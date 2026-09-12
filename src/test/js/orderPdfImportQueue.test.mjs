import test from 'node:test';
import assert from 'node:assert/strict';
import { OrderPdfImportQueue } from '../../main/resources/static/js/pages/business/order/OrderPdfImportQueue.js';
const pdf = name => ({name, type: 'application/pdf', size: 100});
const deferred = () => {let resolve; const promise = new Promise(r => resolve = r); return {promise, resolve};};

test('missing shipper rejects the entire batch before any upload', () => {
    let calls = 0;
    const queue = new OrderPdfImportQueue({upload: () => calls++});
    for (const id of ['', '0', undefined, '-1']) assert.throws(() => queue.enqueue([pdf('a.pdf')], id, ''), /荷主/);
    assert.equal(calls, 0);
    assert.equal(queue.pending.length, 0);
});

test('uploads and reads serially, including batches added during reading; completed rows appear incrementally', async () => {
    const gate = deferred();
    const calls = [];
    const queue = new OrderPdfImportQueue({
        upload: async (file, id) => { calls.push(`upload:${file.name}:${id}`); return {orderImportId: file.name}; },
        recognize: async id => { calls.push(`read:${id}`); if (id === 'a.pdf') await gate.promise; return {ocrLogId: id, candidates: {customerName: id}}; }
    });
    const run = queue.enqueue([pdf('a.pdf'), pdf('b.pdf')], '1085', '平和堂');
    await Promise.resolve();
    queue.enqueue([pdf('c.pdf')], '2000', '別荷主');
    assert.deepEqual(calls, ['upload:a.pdf:1085', 'read:a.pdf']);
    assert.equal(queue.entries.length, 0);
    gate.resolve();
    await run;
    assert.deepEqual(calls, ['upload:a.pdf:1085', 'read:a.pdf', 'upload:b.pdf:1085', 'read:b.pdf', 'upload:c.pdf:2000', 'read:c.pdf']);
    assert.deepEqual(queue.entries.map(e => e.shipperId), ['1085', '1085', '2000']);
    assert.ok(queue.entries.every(e => e.status === 'completed' && !e.confirmed && e.file === null));
});

test('failed OCR does not stop following files; retry reuses saved PDF', async () => {
    let uploads = 0, attempts = 0;
    const queue = new OrderPdfImportQueue({
        upload: async () => ({orderImportId: ++uploads}),
        recognize: async id => { if (id === 1 && attempts++ === 0) throw new Error('読取失敗'); return {ocrLogId: id, candidates: {}}; }
    });
    await queue.enqueue([pdf('a.pdf'), pdf('b.pdf')], '1085', '平和堂');
    const failed = queue.entries[0];
    assert.deepEqual(queue.entries.map(e => e.status), ['failed', 'completed']);
    await queue.retry(failed);
    assert.equal(uploads, 2);
    assert.equal(failed.status, 'completed');
    assert.equal(failed.confirmed, false);
});

test('invalid files and upload failures are listed, while valid files continue', async () => {
    let calls = 0;
    const queue = new OrderPdfImportQueue({
        upload: async file => { calls++; if (file.name === 'bad.pdf') throw new Error('保存失敗'); return {orderImportId: 1}; },
        recognize: async () => ({ocrLogId: 1, candidates: {}})
    });
    await queue.enqueue([{...pdf('empty.pdf'), size: 0}, pdf('wrong.txt'), pdf('bad.pdf'), pdf('ok.pdf')], '1085', '平和堂');
    assert.deepEqual(queue.entries.map(e => e.status), ['failed', 'failed', 'failed', 'completed']);
    assert.equal(calls, 2);
});

test('new page gets an empty queue; disposing old page cancels waiting files', async () => {
    const gate = deferred();
    let uploads = 0;
    const old = new OrderPdfImportQueue({upload: async () => ({orderImportId: ++uploads}), recognize: async () => {await gate.promise; return {ocrLogId: 1, candidates: {}};}});
    const run = old.enqueue([pdf('a.pdf'), pdf('b.pdf')], '1085', '平和堂');
    old.dispose();
    const fresh = new OrderPdfImportQueue({});
    gate.resolve();
    await run;
    assert.equal(uploads, 1);
    assert.equal(fresh.entries.length, 0);
});

test('review must finish before a result is listed or the next upload starts', async () => {
    const gate = deferred();
    let uploads = 0;
    const queue = new OrderPdfImportQueue({
        upload: async () => ({orderImportId: ++uploads}),
        recognize: async () => ({ocrLogId: 1, candidates: {}}),
        review: async entry => {if (entry.orderImportId === 1) await gate.promise; entry.confirmed = true;}
    });
    const run = queue.enqueue([pdf('a.pdf'), pdf('b.pdf')], '1085', '平和堂');
    await new Promise(resolve => setImmediate(resolve));
    assert.equal(uploads, 1);
    assert.equal(queue.active.status, 'reviewing');
    assert.equal(queue.entries.length, 0);
    gate.resolve();
    await run;
    assert.equal(uploads, 2);
    assert.ok(queue.entries.every(entry => entry.confirmed));
});
