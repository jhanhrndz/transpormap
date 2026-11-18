import { renderToStaticMarkup } from 'react-dom/server';
import { expect, test } from 'vitest';
import { App } from './App';

test('identifies the application as a demo without presenting unavailable actions', () => {
  const html = renderToStaticMarkup(<App />);
  expect(html).toContain('TransporMap');
  expect(html).toContain('demostración');
  expect(html).not.toContain('<button');
});
