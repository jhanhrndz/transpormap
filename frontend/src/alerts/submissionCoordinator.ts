import type { createSubmissionLock } from './submissionLock';
import type { createSubmissionStore, SubmissionOwner } from './submissionStore';
import type { PreparedAlert } from './prepareAlert';

type Store = Pick<ReturnType<typeof createSubmissionStore>, 'claim' | 'get' | 'markUncertain'>;
type Lock = ReturnType<typeof createSubmissionLock>;

export function createSubmissionCoordinator(store: Store, lock: Lock) {
  return {
    async run<T>(owner: SubmissionOwner, reportId: string, work: (snapshot: PreparedAlert) => Promise<T>) {
      const identity = { issuer: owner.issuer, subject: owner.subject };
      return lock.run(identity, reportId, async () => {
        if (!await store.claim(identity, reportId)) return { status: 'not-prepared' as const };
        let result: T;
        try {
          const submission = await store.get(identity, reportId);
          if (!submission || submission.status !== 'sending') {
            throw new Error('No se encontró el intento reclamado.');
          }
          const snapshot = Object.freeze({ body: submission.body, idempotencyKey: submission.idempotencyKey });
          result = await work(snapshot);
        } catch (workError) {
          try { await store.markUncertain(identity, reportId); }
          catch (storageError) { throw new AggregateError([workError, storageError], 'El trabajo y la actualización local fallaron.'); }
          throw workError;
        }
        await store.markUncertain(identity, reportId);
        return { status: 'uncertain' as const, value: result };
      });
    },
    async markInterrupted(owner: SubmissionOwner, reportId: string) {
      const identity = { issuer: owner.issuer, subject: owner.subject };
      return lock.run(identity, reportId, async () => {
        const submission = await store.get(identity, reportId);
        if (submission?.status !== 'sending') return { status: 'unchanged' as const };
        await store.markUncertain(identity, reportId);
        return { status: 'uncertain' as const };
      });
    },
  };
}
