import { expect, test } from '@playwright/test';

test.beforeEach(async ({ context }) => {
  await context.route('**/*', route => {
    if (new URL(route.request().url()).origin === 'http://127.0.0.1:4179') return route.continue();
    return route.abort();
  });
});

test('another tab cannot recover an attempt while the first tab holds its lock', async ({ context }) => {
  const first = await context.newPage();
  const second = await context.newPage();
  await Promise.all([first.goto('/e2e/submission-fixture.html'), second.goto('/e2e/submission-fixture.html')]);
  await expect(first.locator('[data-ready]')).toHaveText('ready');
  await expect(second.locator('[data-ready]')).toHaveText('ready');

  await first.evaluate(() => window.harness.save());
  await first.evaluate(() => window.harness.start());
  await first.evaluate(() => window.harness.waitForStart());
  expect(await second.evaluate(() => window.harness.status())).toBe('sending');
  expect(await second.evaluate(() => window.harness.recover())).toEqual({ status: 'busy' });
  expect(await second.evaluate(() => window.harness.status())).toBe('sending');
  await first.evaluate(() => window.harness.release());
  expect(await first.evaluate(() => window.harness.result())).toEqual({
    status: 'completed', value: { status: 'uncertain', value: 'done' },
  });
  expect(await second.evaluate(() => window.harness.status())).toBe('uncertain');
});

test('closing the tab holding a lock lets another tab recover its interrupted attempt', async ({ context }) => {
  const first = await context.newPage();
  const second = await context.newPage();
  await Promise.all([first.goto('/e2e/submission-fixture.html'), second.goto('/e2e/submission-fixture.html')]);
  await expect(first.locator('[data-ready]')).toHaveText('ready');
  await expect(second.locator('[data-ready]')).toHaveText('ready');

  await first.evaluate(() => window.harness.save());
  await first.evaluate(() => window.harness.start());
  await first.evaluate(() => window.harness.waitForStart());
  expect(await second.evaluate(() => window.harness.recover())).toEqual({ status: 'busy' });
  await first.close();
  await expect.poll(async () => second.evaluate(() => window.harness.recover())).toEqual({
    status: 'completed', value: { status: 'uncertain' },
  });
  expect(await second.evaluate(() => window.harness.status())).toBe('uncertain');
});
