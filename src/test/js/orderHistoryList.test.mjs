import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';

const source = readFileSync(new URL('../../main/resources/static/js/components/order/OrderHistoryList.js', import.meta.url), 'utf8')
    .replace(/^import .*;\n/gm, '').replace('export class OrderHistoryList', 'class OrderHistoryList');
class Element {
    constructor() {
        this.children = []; this.listeners = {}; this.textContent = ''; this.isConnected = true;
        this.hidden = false; this.disabled = false; this.attributes = {};
        this.classes = new Set();
        this.classList = {toggle: (name, enabled) => enabled ? this.classes.add(name) : this.classes.delete(name)};
    }
    append(...children) { this.children.push(...children); }
    replaceChildren() { this.children = []; }
    addEventListener(name, callback) { this.listeners[name] = callback; }
    click() { this.listeners.click?.(); }
    setAttribute(key, value) { this.attributes[key] = value; }
}
function harness(find = async () => []) {
    const elements = new Map();
    const root = new Element(), tab = new Element(), calls = [];
    root.querySelector = selector => {
        if (!elements.has(selector)) elements.set(selector, new Element());
        return elements.get(selector);
    };
    const context = vm.createContext({
        document: {createElement: () => new Element()},
        APP: {cache: {common: {state: {INITIAL: 0}}}},
        OrderRepository: {findHistories: params => {calls.push({...params}); return find(params);}},
        formatDate: value => `日時:${value}`
    });
    vm.runInContext(`${source}\nglobalThis.create = (root, tab) => new OrderHistoryList(root, tab);`, context);
    return {view: context.create(root, tab), root, tab, calls};
}
const row = id => ({orderHistoryId: id, eventType: 'ORDER_SAVED', editor: '担当者', occurredAt: '2026-10-31T12:00:00'});
const deferred = () => {let resolve, reject; const promise = new Promise((a,b) => {resolve=a;reject=b;}); return {promise, resolve, reject};};
const settle = () => new Promise(resolve => setImmediate(resolve));

test('new and OCR-unsaved orders never request histories; saved orders load on tab selection', async () => {
    const h = harness();
    h.view.open(null);
    h.tab.click(); await settle();
    assert.equal(h.calls.length, 0);
    assert.match(h.view.status.textContent, /保存後/);
    h.view.open(12);
    assert.equal(h.calls.length, 0);
    h.tab.click(); await settle();
    assert.equal(h.calls[0].orderId, 12);
    assert.equal(h.calls[0].state, 0);
    assert.equal(h.calls[0].beforeHistoryId, null);
    assert.match(h.view.status.textContent, /まだありません/);
    h.tab.click(); await settle();
    assert.equal(h.calls.length, 1);
});

test('100-row page loads continuation with last ID and refresh starts from newest', async () => {
    const h = harness(async params => params.beforeHistoryId ? [row(99)] : Array.from({length: 100}, (_, i) => row(200 - i)));
    h.view.open(12);
    await h.view.load();
    assert.equal(h.view.list.children.length, 100);
    assert.equal(h.view.more.hidden, false);
    await h.view.load();
    assert.equal(h.calls[1].beforeHistoryId, 101);
    assert.equal(h.view.list.children.length, 101);
    assert.equal(h.view.more.hidden, true);
    await h.view.load(true);
    assert.equal(h.calls[2].beforeHistoryId, null);
    assert.equal(h.view.list.children.length, 100);
});

test('failed continuation keeps rows and cursor so retry neither skips nor duplicates rows', async () => {
    let fail = true;
    const h = harness(async params => {
        if (!params.beforeHistoryId) return Array.from({length: 100}, (_, i) => row(200 - i));
        if (fail) throw new Error('private server information');
        return [row(100)];
    });
    h.view.open(12);
    await h.view.load(); await h.view.load();
    assert.equal(h.view.list.children.length, 100);
    assert.equal(h.view.busy, false);
    assert.equal(h.view.more.disabled, false);
    assert.match(h.view.status.textContent, /取得できません/);
    assert.doesNotMatch(h.view.status.textContent, /private/);
    fail = false;
    await h.view.load();
    assert.equal(h.calls[1].beforeHistoryId, h.calls[2].beforeHistoryId);
    assert.equal(h.view.list.children.length, 101);
});

test('an old order response cannot overwrite a newly opened order or release its busy state', async () => {
    const old = deferred(), next = deferred();
    const h = harness(params => params.orderId === 12 ? old.promise : next.promise);
    h.view.open(12); const first = h.view.load();
    h.view.open(13); const second = h.view.load();
    old.resolve([row(999)]); await first;
    assert.equal(h.view.list.children.length, 0);
    assert.equal(h.view.busy, true);
    next.resolve([row(10)]); await second;
    assert.equal(h.view.list.children.length, 1);
    assert.equal(h.view.cursor, 10);
    assert.equal(h.view.busy, false);
});

test('close, detached view and invalid form session discard pending results', async () => {
    for (const invalidate of [h => h.view.close(), h => {h.root.isConnected = false;}, h => {h.active = false;}]) {
        const pending = deferred(); const h = harness(() => pending.promise);
        h.active = true; h.view.open(12, () => h.active);
        const loading = h.view.load(); invalidate(h);
        pending.resolve([row(1)]); await loading;
        assert.equal(h.view.list.children.length, 0);
    }
});

test('double clicks issue one request, errors allow refresh, and initial errors do not block form opening', async () => {
    const pending = deferred();
    const h = harness(() => pending.promise);
    h.view.open(12); const loading = h.view.load();
    await h.view.load(true);
    assert.equal(h.calls.length, 1);
    assert.equal(h.view.refresh.disabled, true);
    pending.reject(new Error('unavailable')); await loading;
    assert.equal(h.view.refresh.disabled, false);
    assert.equal(h.root.attributes['aria-busy'], 'false');
    await h.view.load(true);
    assert.equal(h.calls.length, 2);
});

test('labels and editor names are rendered as text, including unknown events', async () => {
    const h = harness(async () => [{...row(1), eventType: 'ORDER_CREATED', editor: '<img src=x onerror=alert(1)>'}, {...row(2), eventType: '<script>'}]);
    h.view.open(12); await h.view.load();
    const [created, unknown] = h.view.list.children;
    assert.equal(created.children[0].textContent, '受注を登録');
    assert.equal(created.children[2].textContent, '操作者：<img src=x onerror=alert(1)>');
    assert.equal(unknown.children[0].textContent, '受注の履歴');
    assert.equal(created.children.length, 3);
});

test('keyboard activates the history tab without submitting the edit form', async () => {
    const h = harness();
    h.view.open(12);
    let prevented = false;
    h.tab.listeners.keydown({key: ' ', preventDefault() {prevented = true;}});
    await settle();
    assert.equal(prevented, true);
    assert.equal(h.calls.length, 1);
    h.tab.listeners.keydown({key: 'ArrowRight', preventDefault() {assert.fail('unrelated key');}});
    assert.equal(h.calls.length, 1);
});
