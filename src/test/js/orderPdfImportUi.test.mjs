import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';
import {OrderPdfImportQueue} from '../../main/resources/static/js/pages/business/order/OrderPdfImportQueue.js';

const code = readFileSync(new URL('../../main/resources/static/js/pages/business/order/orderPdfImport.js', import.meta.url), 'utf8')
    .replace(/^import .*;\n/gm, '').replace('export function initOrderPdfImport', 'function initOrderPdfImport');
class Element {
    constructor() {
        this.children = []; this.events = {}; this.dataset = {}; this.value = ''; this.isConnected = true;
        const classes = new Set();
        this.classList = {add: c => classes.add(c), remove: c => classes.delete(c), toggle: (c, on) => on ? classes.add(c) : classes.delete(c)};
    }
    append(...nodes) { this.children.push(...nodes); }
    querySelectorAll() { return this.children.flatMap(child => child.dataset?.field ? [child] : child.querySelectorAll()); }
    replaceChildren() { this.children = []; }
    addEventListener(name, fn) { (this.events[name] ??= []).push(fn); }
    emit(name, event = {}) { for (const fn of this.events[name] ?? []) fn(event); }
    click() { this.emit('click'); }
}
function harness(candidates = {customerName: '読取候補'}) {
    const elements = new Map();
    const get = id => {if (!elements.has(id)) elements.set(id, new Element()); return elements.get(id);};
    const calls = [], messages = [];
    let form, closes = 0, failSave = false;
    const context = vm.createContext({
        OrderPdfImportQueue, FormData, console,
        document: {getElementById: get, createElement: () => new Element()},
        apiFetch: async (url, options) => {
            calls.push({url, options});
            if (url.endsWith('/pdf')) return {data: {orderImportId: 12}};
            if (url.endsWith('/hei-wado')) return {data: candidates};
            if (failSave) throw new Error('保存エラー');
            return {data: null};
        },
        openFormDialog: options => {form = options;},
        closeFormDialog: () => closes++,
        openMsgDialog: options => messages.push(options.message)
    });
    vm.runInContext(code, context);
    context.initOrderPdfImport();
    return {get, context, calls, messages, get form() {return form;}, get closes() {return closes;}, failSave: () => failSave = true};
}
const settle = () => new Promise(resolve => setImmediate(resolve));
const file = () => new File(['%PDF-1.7 test'], 'sample.pdf', {type: 'application/pdf'});

test('file selection and drop reject an unselected shipper without API calls', async () => {
    const h = harness();
    h.get('order-pdf-file-input').files = [file()];
    h.get('order-pdf-file-input').emit('change');
    h.get('tab-02').emit('drop', {preventDefault() {}, dataTransfer: {files: [file()]}});
    await settle();
    assert.equal(h.calls.length, 0);
    assert.equal(h.messages.length, 2);
});

test('file selection uploads and reads automatically; review remains manual and save closes only on success', async () => {
    const h = harness();
    h.get('primeConstractorImport').value = '1085';
    h.get('primeConstractorImport').selectedOptions = [{textContent: '平和堂'}];
    h.get('order-pdf-file-input').files = [file()];
    h.get('order-pdf-file-input').emit('change');
    await settle();
    assert.equal(h.calls.length, 2);
    assert.ok(h.form);
    assert.equal(h.get("order-pdf-import-items").children.length, 0);
    assert.ok(h.calls.slice(0, 2).every(call => call.options.showProcessing === true));
    assert.equal(h.get('order-pdf-file-input').value, '');

    assert.equal(h.get('ocr-customer-name').value, '読取候補');
    h.get('ocr-customer-name').value = '確認した氏名';
    await h.form.onSubmit();
    await settle();
    assert.equal(h.calls[2].options.showProcessing, false);
    assert.equal(h.calls[2].options.data.customerName, '確認した氏名');
    assert.equal(h.closes, 1);
    assert.equal(h.get('order-pdf-import-items').children[0].children[0].textContent, '受注登録済み');
    assert.equal(h.messages.length, 0); // No completion modal obstructs the next PDF.
    h.context.initOrderPdfImport();
    assert.equal(h.get('order-pdf-import-items').children.length, 0);
    assert.equal(h.calls.length, 3); // Reopening never fetches historical PDFs.
});

test('review blocks the next file; failed save keeps the dialog open without a spinner', async () => {
    const h = harness();
    h.get('primeConstractorImport').value = '1085';
    h.get('tab-02').emit('drop', {preventDefault() {}, dataTransfer: {files: [file(), file()]}});
    await settle();
    assert.equal(h.calls.length, 2);
    assert.equal(h.get('order-pdf-import-items').children.length, 0);
    h.failSave();
    await h.form.onSubmit();
    await settle();
    assert.equal(h.calls.length, 3);
    assert.equal(h.calls[2].options.showProcessing, false);
    assert.equal(h.closes, 0);
    assert.equal(h.get('order-pdf-import-items').children.length, 0);
    assert.ok(h.messages.includes('保存エラー'));
    h.form.onClose();
    await settle();
    assert.equal(h.calls.length, 5);
    assert.equal(h.get('order-pdf-import-items').children.length, 1);
    assert.equal(h.get('order-pdf-import-items').children[0].children[0].textContent, '読取完了・要確認');
    h.form.onClose();
    await settle();
    assert.equal(h.get('order-pdf-import-items').children.length, 2);
});

test('confirmed product and work rows are sent with corrected quantities', async () => {
    const h = harness({customerName: '氏名', items: JSON.stringify([{itemName: 'エアコン', itemModel: 'ABC', itemQuantity: '2'}]),
        works: JSON.stringify([{orderWorkName: 'リサイクル運搬', orderWorkQuantity: '1', orderWorkPrice: ''}])});
    h.get('primeConstractorImport').value = '1085';
    h.get('order-pdf-file-input').files = [file()];
    h.get('order-pdf-file-input').emit('change');
    await settle();
    const inputs = h.get('ocr-item-rows').children[0].querySelectorAll();
    inputs.find(input => input.dataset.field === 'itemQuantity').value = '3';
    await h.form.onSubmit();
    const saved = h.calls.find(call => call.url.endsWith('/candidate')).options.data;
    assert.equal(JSON.parse(saved.items)[0].itemQuantity, '3');
    assert.equal(JSON.parse(saved.works)[0].orderWorkName, 'リサイクル運搬');
    assert.equal(JSON.parse(saved.works)[0].orderWorkPrice, '');
});
