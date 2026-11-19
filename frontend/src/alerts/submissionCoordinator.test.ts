import { describe, expect, it, vi } from 'vitest';
import { IDBFactory } from 'fake-indexeddb';
import { createSubmissionStore } from './submissionStore';
import { createSubmissionLock, type LockProvider } from './submissionLock';
import { createSubmissionCoordinator } from './submissionCoordinator';

const owner = { issuer: 'https://identity.example', subject: 'one' };
const id = '4c5208fa-1cbd-4fa7-aec9-2c306c408f78';
const prepared = { idempotencyKey: 'key', body: JSON.stringify({ clientReportId: id }) };

function locks(): LockProvider {
  const held = new Set<string>();
  return { async request(name, _options, callback) {
    if (held.has(name)) return callback(null);
    held.add(name);
    try { return await callback({ name }); } finally { held.delete(name); }
  } };
}

describe('submission coordinator', () => {
  it('holds the operation lock through async work and the uncertain transition', async () => {
    const factory = new IDBFactory();
    const store = createSubmissionStore(factory, 'test');
    const provider = locks();
    const first = createSubmissionCoordinator(store, createSubmissionLock(provider, crypto.subtle));
    const second = createSubmissionCoordinator(store, createSubmissionLock(provider, crypto.subtle));
    await store.save(owner, prepared);
    let entered!: () => void;
    let release!: () => void;
    const started = new Promise<void>(resolve => { entered = resolve; });
    const wait = new Promise<void>(resolve => { release = resolve; });
    const operation = first.run(owner, id, async () => { entered(); await wait; return 'work completed'; });
    await started;
    expect((await store.get(owner, id))?.status).toBe('sending');
    expect(await second.markInterrupted(owner, id)).toEqual({ status: 'busy' });
    expect((await store.get(owner, id))?.status).toBe('sending');
    release();
    expect(await operation).toEqual({ status: 'completed', value: { status: 'uncertain', value: 'work completed' } });
    expect((await store.get(owner, id))?.status).toBe('uncertain');
  });

  it('does not mutate data or invoke work if locking is unavailable', async () => {
    const store = createSubmissionStore(new IDBFactory(), 'test');
    await store.save(owner, prepared);
    const coordinator = createSubmissionCoordinator(store, createSubmissionLock(undefined, crypto.subtle));
    const callback = vi.fn(async () => {});
    expect(await coordinator.run(owner, id, callback)).toEqual({ status: 'unavailable' });
    expect(await coordinator.markInterrupted(owner, id)).toEqual({ status: 'unavailable' });
    expect(callback).not.toHaveBeenCalled();
    expect((await store.get(owner, id))?.status).toBe('prepared');
  });

  it('makes failed work uncertain and never invokes work for an already claimed operation', async () => {
    const store = createSubmissionStore(new IDBFactory(), 'test');
    await store.save(owner, prepared);
    const coordinator = createSubmissionCoordinator(store, createSubmissionLock(locks(), crypto.subtle));
    await expect(coordinator.run(owner, id, async () => { throw new Error('work failed'); })).rejects.toThrow('work failed');
    expect((await store.get(owner, id))?.status).toBe('uncertain');
    const callback = vi.fn(async () => {});
    expect(await coordinator.run(owner, id, callback)).toEqual({ status: 'completed', value: { status: 'not-prepared' } });
    expect(callback).not.toHaveBeenCalled();
  });

  it('retains both work and persistence failures without claiming a durable transition', async () => {
    const store = createSubmissionStore(new IDBFactory(), 'test');
    await store.save(owner, prepared);
    const coordinator = createSubmissionCoordinator({ ...store, markUncertain: async () => { throw new Error('storage failed'); } }, createSubmissionLock(locks(), crypto.subtle));
    await expect(coordinator.run(owner, id, async () => { throw new Error('work failed'); })).rejects.toBeInstanceOf(AggregateError);
    expect((await store.get(owner, id))?.status).toBe('sending');
  });

  it('rejects after successful work when persisting uncertainty fails and does not repeat work', async () => {
    const store = createSubmissionStore(new IDBFactory(), 'test');
    await store.save(owner, prepared);
    const failingStore = { ...store, markUncertain: async () => { throw new Error('storage failed'); } };
    const coordinator = createSubmissionCoordinator(failingStore, createSubmissionLock(locks(), crypto.subtle));
    const work = vi.fn(async () => 'completed');
    await expect(coordinator.run(owner, id, work)).rejects.toThrow('storage failed');
    expect((await store.get(owner, id))?.status).toBe('sending');
    expect(await coordinator.run(owner, id, work)).toEqual({ status: 'completed', value: { status: 'not-prepared' } });
    expect(work).toHaveBeenCalledTimes(1);
  });

  it('keeps the lock until the state transition has committed', async () => {
    const store = createSubmissionStore(new IDBFactory(), 'test');
    await store.save(owner, prepared);
    let entered!: () => void;
    let release!: () => void;
    const started = new Promise<void>(resolve => { entered = resolve; });
    const wait = new Promise<void>(resolve => { release = resolve; });
    const delayedStore = { ...store, markUncertain: async (...args: Parameters<typeof store.markUncertain>) => {
      entered();
      await wait;
      await store.markUncertain(...args);
    } };
    const provider = locks();
    const first = createSubmissionCoordinator(delayedStore, createSubmissionLock(provider, crypto.subtle));
    const second = createSubmissionCoordinator(store, createSubmissionLock(provider, crypto.subtle));
    const operation = first.run(owner, id, async () => 'finished');
    await started;
    expect(await second.markInterrupted(owner, id)).toEqual({ status: 'busy' });
    expect((await store.get(owner, id))?.status).toBe('sending');
    release();
    await operation;
    expect((await store.get(owner, id))?.status).toBe('uncertain');
  });

  it('marks an explicitly identified interrupted attempt uncertain under the lock', async () => {
    const store = createSubmissionStore(new IDBFactory(), 'test');
    await store.save(owner, prepared);
    await store.claim(owner, id);
    const coordinator = createSubmissionCoordinator(store, createSubmissionLock(locks(), crypto.subtle));
    expect(await coordinator.markInterrupted(owner, id)).toEqual({ status: 'completed', value: { status: 'uncertain' } });
    expect((await store.get(owner, id))?.status).toBe('uncertain');
  });
});
