import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';
import {mapOcrCandidate} from '../../main/resources/static/js/pages/business/order/mapOcrCandidate.js';
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
    const sharedForm = {};
    const context = vm.createContext({
        OrderPdfImportQueue, FormData, console, mapOcrCandidate,
        getController: () => ({openForm: async (name, data) => {form = data;}, getDefaultForm: () => sharedForm}),
        document: {getElementById: get, createElement: () => new Element()},
        apiFetch: async (url, options) => {
            calls.push({url, options});
            if (url.endsWith('/pdf')) return {data: {orderImportId: 12}};
            if (url.endsWith('/hei-wado')) return {data: {ocrLogId: 7, candidates}};
            if (failSave) throw new Error('保存エラー');
            return {data: null};
        },
        openFormDialog: options => {form = options;},
        closeFormDialog: () => closes++,
        openMsgDialog: options => messages.push(options.message)
    });
    vm.runInContext(code, context);
    context.initOrderPdfImport();
    return {get, context, calls, messages, sharedForm, get form() {return form;}, get closes() {return closes;}, failSave: () => failSave = true};
}
const settle = () => new Promise(resolve => setImmediate(resolve));
const file = () => new File(['%PDF-1.7 test'], 'sample.pdf', {type: 'application/pdf'});

test('file selection and drop reject an unselected shipper without API calls', async () => {
    const h = harness();
    h.get('order-pdf-file-input').files = [file()];
    h.get('order-pdf-file-input').emit('change');
    h.get('order-pdf-drop-area').emit('drop', {preventDefault() {}, dataTransfer: {files: [file()]}});
    await settle();
    assert.equal(h.calls.length, 0);
    assert.equal(h.messages.length, 2);
});


test('OCR opens the shared form with log ID, and cancellation keeps the extraction unchanged', async () => {
    const candidates = {customerName: '山田', requestedDate: '2026-09-05', items: '[{"itemModel":"A","itemQuantity":"1"}]'};
    const h = harness(candidates);
    h.get('primeConstractor01').value = '1085';
    h.get('order-pdf-file-input').files = [file()];
    h.get('order-pdf-file-input').emit('change');
    await settle();
    assert.equal(h.form.title, '山田');
    assert.equal(h.form.visitDate, '2026-09-05');
    assert.equal(h.form.ocrLogId, 7);
    assert.equal(h.form.items[0].itemModel, 'A');
    h.sharedForm.finishOcrReview();
    await settle();
    assert.equal(h.calls.length, 2);
    assert.equal(candidates.customerName, '山田');
});

test('missing year is shown for review without guessing a year; fields are mapped independently', () => {
    const raw = {customerName: 'A', requestedDate: '9月5日', address: '住所', items: [{itemModel: 'X', orderItemId: 99}]};
    const mapped = mapOcrCandidate({candidates: raw, ocrLogId: 5, shipperId: '1085'});
    assert.equal(mapped.visitDate, '');
    assert.match(mapped.ocrDateWarning, /9月5日/);
    assert.equal(mapped.fullAddress, '住所');
    assert.equal(mapped.items[0].orderItemId, undefined);
    mapped.items[0].itemModel = 'Y';
    assert.equal(raw.items[0].itemModel, 'X');
});

test('invalid calendar date is never silently rolled forward', () => {
    const result = mapOcrCandidate({candidates: {requestedDate: '2026-02-30'}});
    assert.equal(result.visitDate, '');
    assert.match(result.ocrDateWarning, /2026-02-30/);
});

test('normalizes full-width quantities and comma-separated prices without changing the raw extraction', () => {
    const candidates = {works: [{orderWorkName: '作業', orderWorkQuantity: '２', orderWorkPrice: '１,２００'}]};
    const result = mapOcrCandidate({candidates});
    assert.equal(result.works[0].orderWorkQuantity, '2');
    assert.equal(result.works[0].orderWorkPrice, '1200');
    assert.equal(candidates.works[0].orderWorkPrice, '１,２００');
});

test('unchanged blank postal code keeps the OCR address; clearing a previously resolved code still clears it', async () => {
    const source = readFileSync(new URL('../../main/resources/static/js/core/behavior/DataResolver.js', import.meta.url), 'utf8');
    const method = source.slice(source.indexOf('    async resolve(group, type) {'), source.indexOf('    resolveSelect(select, id) {')).trim().replace(/,$/, '');
    const resolver = vm.runInNewContext(`({${method}, clear(field) { field.value = ''; }})`);
    const input = {value: '', dataset: {lastId: ''}};
    const address = {value: 'OCRの住所'};
    const group = {querySelector: selector => selector === '[data-resolve-id]' ? input : address};
    await resolver.resolve(group, 'postal');
    assert.equal(address.value, 'OCRの住所');
    input.dataset.lastId = '1234567';
    await resolver.resolve(group, 'postal');
    assert.equal(address.value, '');
});
