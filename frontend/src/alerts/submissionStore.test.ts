import { beforeEach, describe, expect, it } from 'vitest';
import { IDBFactory } from 'fake-indexeddb';
import { createSubmissionStore } from './submissionStore';

const account = { issuer: 'https://identity.example', subject: 'one' };
const otherAccount = { issuer: 'https://identity.example', subject: 'two' };
const otherIssuer = { issuer: 'https://other.example', subject: 'one' };
const prepared = {
  idempotencyKey: 'key-original',
  body: JSON.stringify({ clientReportId: 'report-1', title: 'Alerta original' }),
};
let factory: IDBFactory;

beforeEach(() => { factory = new IDBFactory(); });

describe('IndexedDB submission store', () => {
  it('retains the exact request and separates records by their composite key', async () => {
    const first = createSubmissionStore(factory, 'test');
    await first.save(account, prepared);
    const reopened = createSubmissionStore(factory, 'test');
    const saved = await reopened.get(account, 'report-1');
    expect(saved).toMatchObject({ ...prepared, clientReportId: 'report-1', status: 'prepared' });
    expect(await reopened.get(otherAccount, 'report-1')).toBeUndefined();
    expect(await reopened.get(otherIssuer, 'report-1')).toBeUndefined();
  });

  it('does not overwrite an existing request with the same identity', async () => {
    const store = createSubmissionStore(factory, 'test');
    await store.save(account, prepared);
    await expect(store.save(account, { ...prepared, body: JSON.stringify({ clientReportId: 'report-1', title: 'Distinto' }) })).rejects.toThrow();
    expect((await store.get(account, 'report-1'))?.body).toBe(prepared.body);
  });

  it('permits one concurrent claim and allows another store to mark it uncertain', async () => {
    const first = createSubmissionStore(factory, 'test');
    const second = createSubmissionStore(factory, 'test');
    await first.save(account, prepared);
    const claims = await Promise.all([first.claim(account, 'report-1'), second.claim(account, 'report-1')]);
    expect(claims.filter(Boolean)).toHaveLength(1);
    const anotherStore = createSubmissionStore(factory, 'test');
    await anotherStore.markUncertain(account, 'report-1');
    expect(await anotherStore.get(account, 'report-1')).toMatchObject({ ...prepared, status: 'uncertain' });
    expect(await anotherStore.claim(account, 'report-1')).toBe(false);
  });

  it('does not claim or change a record when a different composite key is supplied', async () => {
    const store = createSubmissionStore(factory, 'test');
    await store.save(account, prepared);
    expect(await store.claim(otherAccount, 'report-1')).toBe(false);
    await expect(store.markUncertain(otherAccount, 'report-1')).rejects.toThrow();
    expect((await store.get(account, 'report-1'))?.status).toBe('prepared');
  });

  it('rejects requests without an identity or invalid body', async () => {
    const store = createSubmissionStore(factory, 'test');
    await expect(store.save({ issuer: '', subject: 'one' }, prepared)).rejects.toThrow();
    await expect(store.save(account, { idempotencyKey: '', body: '{}' })).rejects.toThrow();
    await expect(store.save(account, { idempotencyKey: 'k', body: 'not json' })).rejects.toThrow();
  });
});
