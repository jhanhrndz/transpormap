import { test } from 'node:test';
import assert from 'node:assert/strict';
import { prepareAlert, type AlertDraft } from './prepareAlert.ts';

const report = () => ({
  title: 'Cierre en vía',
  description: 'Cierre total por obras.',
  category: 'full_closure' as const,
  impact: 'closed' as const,
  severity: 'unknown' as const,
  location: { latitude: 10.99, longitude: -74.8 },
  observedAt: '2026-01-02T14:30:00Z',
});

test('captures a stable request even if the draft is edited after preparation', () => {
  const draft = report();
  const ids = ['4c5208fa-1cbd-4fa7-aec9-2c306c408f78', '88c73c43-e110-4cd8-9091-31445c858070'];
  const prepared = prepareAlert(draft, () => ids.shift()!);
  draft.title = 'Otro título';
  draft.location.latitude = 0;
  assert.equal(prepared.idempotencyKey, '88c73c43-e110-4cd8-9091-31445c858070');
  assert.deepEqual(JSON.parse(prepared.body), {
    clientReportId: '4c5208fa-1cbd-4fa7-aec9-2c306c408f78',
    title: 'Cierre en vía', description: 'Cierre total por obras.',
    category: 'full_closure', impact: 'closed', severity: 'unknown',
    location: { latitude: 10.99, longitude: -74.8 },
    observedAt: '2026-01-02T14:30:00Z', photoIntent: 'none',
  });
});

test('rejects titles and descriptions outside contract limits', () => {
  assert.throws(() => prepareAlert({ ...report(), title: '1234' }));
  assert.throws(() => prepareAlert({ ...report(), title: 'x'.repeat(101) }));
  assert.throws(() => prepareAlert({ ...report(), description: '123456789' }));
  assert.throws(() => prepareAlert({ ...report(), description: 'x'.repeat(501) }));
});

test('rejects non-finite or out-of-range coordinates', () => {
  for (const location of [
    { latitude: 90.1, longitude: 0 }, { latitude: 0, longitude: -180.1 },
    { latitude: Number.NaN, longitude: 0 }, { latitude: 0, longitude: Infinity },
  ]) assert.throws(() => prepareAlert({ ...report(), location }));
});

test('does not silently alter the observed time', () => {
  const observedAt = '2026-01-02T09:30:00-05:00';
  const result = prepareAlert({ ...report(), observedAt });
  assert.equal(JSON.parse(result.body).observedAt, observedAt);
});

test('rejects unknown category, impact and severity at runtime', () => {
  for (const field of ['category', 'impact', 'severity'] as const) {
    assert.throws(() => prepareAlert({ ...report(), [field]: 'invalid' } as AlertDraft));
  }
});

test('counts Unicode code points for text limits', () => {
  assert.throws(() => prepareAlert({ ...report(), title: '🚚'.repeat(4) }));
  assert.doesNotThrow(() => prepareAlert({ ...report(), title: '🚚'.repeat(5) }));
  assert.doesNotThrow(() => prepareAlert({ ...report(), title: '🚚'.repeat(100) }));
  assert.throws(() => prepareAlert({ ...report(), title: '🚚'.repeat(101) }));
});

test('rejects an empty observed time without substituting another one', () => {
  assert.throws(() => prepareAlert({ ...report(), observedAt: '' }));
  assert.throws(() => prepareAlert({ ...report(), observedAt: '   ' }));
});
