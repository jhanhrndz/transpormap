import { describe, expect, it, vi } from 'vitest';
import { createSubmissionLock, type LockProvider } from './submissionLock';

const owner = { issuer: 'https://identity.example', subject: 'sensitive-subject' };
const reportId = '4c5208fa-1cbd-4fa7-aec9-2c306c408f78';

function fakeLockProvider(): LockProvider & { names: string[] } {
  const held = new Set<string>();
  return {
    names: [],
    async request(name, options, callback) {
      this.names.push(name);
      expect(options).toEqual({ mode: 'exclusive', ifAvailable: true });
      if (held.has(name)) return callback(null);
      held.add(name);
      try { return await callback({ name }); } finally { held.delete(name); }
    },
  };
}

describe('submission lock adapter', () => {
  it('excludes a concurrent callback for the same operation until work finishes', async () => {
    const provider = fakeLockProvider();
    const first = createSubmissionLock(provider, crypto.subtle);
    const second = createSubmissionLock(provider, crypto.subtle);
    let unblock!: () => void;
    let entered!: () => void;
    const started = new Promise<void>(resolve => { entered = resolve; });
    const pending = new Promise<void>(resolve => { unblock = resolve; });
    const operation = first.run(owner, reportId, async () => { entered(); await pending; return 'done'; });
    await started;
    const competing = vi.fn();
    expect(await second.run(owner, reportId, competing)).toEqual({ status: 'busy' });
    expect(competing).not.toHaveBeenCalled();
    unblock();
    expect(await operation).toEqual({ status: 'completed', value: 'done' });
    expect(await second.run(owner, reportId, async () => 'next')).toEqual({ status: 'completed', value: 'next' });
  });

  it('uses stable opaque names for the same tuple and different names for different tuples', async () => {
    const provider = fakeLockProvider();
    const lock = createSubmissionLock(provider, crypto.subtle);
    await lock.run(owner, reportId, async () => {});
    await lock.run(owner, reportId, async () => {});
    await lock.run({ ...owner, subject: 'another' }, reportId, async () => {});
    expect(provider.names[0]).toBe(provider.names[1]);
    expect(provider.names[0]).not.toBe(provider.names[2]);
    expect(provider.names[0]).not.toContain(owner.issuer);
    expect(provider.names[0]).not.toContain(owner.subject);
    expect(provider.names[0]).not.toContain(reportId);
  });

  it('does not run the callback when the lock or hashing capability is unavailable', async () => {
    const callback = vi.fn();
    expect(await createSubmissionLock(undefined, crypto.subtle).run(owner, reportId, callback)).toEqual({ status: 'unavailable' });
    expect(await createSubmissionLock(fakeLockProvider(), undefined).run(owner, reportId, callback)).toEqual({ status: 'unavailable' });
    expect(callback).not.toHaveBeenCalled();
  });

  it('propagates failures without silently running work outside the lock', async () => {
    const provider: LockProvider = { request: async () => { throw new Error('lock failure'); } };
    const callback = vi.fn();
    await expect(createSubmissionLock(provider, crypto.subtle).run(owner, reportId, callback)).rejects.toThrow('lock failure');
    expect(callback).not.toHaveBeenCalled();
  });

  it('does not request a lock or run work if hashing fails', async () => {
    const provider = fakeLockProvider();
    const digest = { digest: async () => { throw new Error('hash failure'); } } as unknown as SubtleCrypto;
    const callback = vi.fn();
    await expect(createSubmissionLock(provider, digest).run(owner, reportId, callback)).rejects.toThrow('hash failure');
    expect(provider.names).toEqual([]);
    expect(callback).not.toHaveBeenCalled();
  });

  it('propagates a work failure and permits a later lock attempt', async () => {
    const lock = createSubmissionLock(fakeLockProvider(), crypto.subtle);
    await expect(lock.run(owner, reportId, async () => { throw new Error('work failure'); })).rejects.toThrow('work failure');
    expect(await lock.run(owner, reportId, async () => 'next')).toEqual({ status: 'completed', value: 'next' });
  });
});
