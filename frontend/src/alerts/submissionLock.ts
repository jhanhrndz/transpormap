import type { SubmissionOwner } from './submissionStore';

export interface LockProvider {
  request<T>(name: string, options: { mode: 'exclusive'; ifAvailable: true },
    callback: (lock: { name: string } | null) => Promise<T>): Promise<T>;
}

type LockResult<T> = { status: 'completed'; value: T } | { status: 'busy' } | { status: 'unavailable' };

export function createSubmissionLock(provider: LockProvider | undefined, digest: SubtleCrypto | undefined) {
  return {
    async run<T>(owner: SubmissionOwner, reportId: string, work: () => Promise<T>): Promise<LockResult<T>> {
      if (!owner.issuer?.trim() || !owner.subject?.trim() || !reportId?.trim()) {
        throw new Error('La identidad de la operación es obligatoria.');
      }
      if (!provider || !digest) return { status: 'unavailable' };

      const bytes = new TextEncoder().encode(JSON.stringify([owner.issuer, owner.subject, reportId]));
      const hash = await digest.digest('SHA-256', bytes);
      const name = 'transpormap-submission-' + Array.from(new Uint8Array(hash), b => b.toString(16).padStart(2, '0')).join('');

      return provider.request<LockResult<T>>(name, { mode: 'exclusive', ifAvailable: true }, async lock => {
        if (!lock) return { status: 'busy' };
        return { status: 'completed', value: await work() };
      });
    },
  };
}

export function createBrowserSubmissionLock() {
  const locks = typeof navigator === 'undefined' ? undefined : navigator.locks;
  const digest = typeof crypto === 'undefined' ? undefined : crypto.subtle;
  return createSubmissionLock(locks, digest);
}
