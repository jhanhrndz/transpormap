import type { PreparedAlert } from './prepareAlert';

export type SubmissionOwner = Readonly<{ issuer: string; subject: string }>;
export type StoredSubmission = Readonly<{
  issuer: string;
  subject: string;
  clientReportId: string;
  idempotencyKey: string;
  body: string;
  status: 'prepared' | 'sending' | 'uncertain';
  createdAt: string;
  updatedAt: string;
}>;

const STORE = 'submissions';

function validateOwner(owner: SubmissionOwner): void {
  if (!owner.issuer?.trim() || !owner.subject?.trim()) throw new Error('La identidad es obligatoria.');
}

function openDatabase(factory: IDBFactory, name: string): Promise<IDBDatabase> {
  return new Promise((resolve, reject) => {
    const request = factory.open(name, 1);
    let blocked = false;
    request.onupgradeneeded = () => {
      request.result.createObjectStore(STORE, { keyPath: ['issuer', 'subject', 'clientReportId'] });
    };
    request.onsuccess = () => { if (blocked) request.result.close(); else resolve(request.result); };
    request.onerror = () => reject(request.error ?? new Error('No se pudo abrir el almacenamiento.'));
    request.onblocked = () => { blocked = true; reject(new Error('La apertura del almacenamiento está bloqueada.')); };
  });
}

async function transaction<T>(factory: IDBFactory, name: string, mode: IDBTransactionMode,
  run: (store: IDBObjectStore, setResult: (value: T) => void) => void): Promise<T> {
  const database = await openDatabase(factory, name);
  try {
    return await new Promise<T>((resolve, reject) => {
      const tx = database.transaction(STORE, mode);
      let result: T;
      tx.oncomplete = () => resolve(result);
      tx.onabort = () => reject(tx.error ?? new Error('La transacción no se completó.'));
      try { run(tx.objectStore(STORE), value => { result = value; }); }
      catch (error) { tx.abort(); reject(error); }
    });
  } finally {
    database.close();
  }
}

export function createSubmissionStore(factory: IDBFactory = indexedDB, name = 'transpormap-submissions') {
  return {
    async save(owner: SubmissionOwner, prepared: PreparedAlert): Promise<void> {
      validateOwner(owner);
      if (!prepared.idempotencyKey?.trim()) throw new Error('La clave de la operación es obligatoria.');
      let payload: unknown;
      try { payload = JSON.parse(prepared.body); } catch { throw new Error('El cuerpo de la alerta no es válido.'); }
      const reportId = (payload && typeof payload === 'object' && 'clientReportId' in payload)
        ? payload.clientReportId : undefined;
      if (typeof reportId !== 'string' || !reportId.trim()) throw new Error('La identidad del reporte es obligatoria.');
      const time = new Date().toISOString();
      const record: StoredSubmission = { issuer: owner.issuer, subject: owner.subject,
        clientReportId: reportId, idempotencyKey: prepared.idempotencyKey,
        body: prepared.body, status: 'prepared', createdAt: time, updatedAt: time };
      return transaction<void>(factory, name, 'readwrite', (store) => { store.add(record); });
    },
    async get(owner: SubmissionOwner, reportId: string): Promise<StoredSubmission | undefined> {
      validateOwner(owner);
      return transaction(factory, name, 'readonly', (store, setResult) => {
        const request = store.get([owner.issuer, owner.subject, reportId]);
        request.onsuccess = () => setResult(request.result as StoredSubmission | undefined);
      });
    },
    async claim(owner: SubmissionOwner, reportId: string): Promise<boolean> {
      validateOwner(owner);
      return transaction(factory, name, 'readwrite', (store, setResult) => {
        const request = store.get([owner.issuer, owner.subject, reportId]);
        request.onsuccess = () => {
          const value = request.result as StoredSubmission | undefined;
          if (!value || value.status !== 'prepared') { setResult(false); return; }
          store.put({ ...value, status: 'sending', updatedAt: new Date().toISOString() });
          setResult(true);
        };
      });
    },
    async markUncertain(owner: SubmissionOwner, reportId: string): Promise<void> {
      validateOwner(owner);
      return transaction(factory, name, 'readwrite', (store) => {
        const request = store.get([owner.issuer, owner.subject, reportId]);
        request.onsuccess = () => {
          const value = request.result as StoredSubmission | undefined;
          if (!value || value.status !== 'sending') { store.transaction.abort(); return; }
          store.put({ ...value, status: 'uncertain', updatedAt: new Date().toISOString() });
        };
      });
    },
  };
}
