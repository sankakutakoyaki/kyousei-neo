import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';

class Element {
    constructor(field) {
        this.dataset = field ? {field} : {};
        this.children = []; this.attributes = {}; this.events = {};
        const classes = new Set();
        this.classList = {add: c => classes.add(c), contains: c => classes.has(c),
            toggle: (c, on) => on ? classes.add(c) : classes.delete(c)};
    }
    appendChild(child) { this.children.push(child); }
    setAttribute(k, v) { this.attributes[k] = v; }
    getAttribute(k) { return this.attributes[k]; }
    removeAttribute(k) { delete this.attributes[k]; }
    closest() { return null; }
    addEventListener(k, fn) { this.events[k] = fn; }
    querySelectorAll() { return this.children.filter(e => e.dataset.field); }
    querySelector(selector) { return selector.includes('first') ? this.children[0] : this.children.at(-1); }
}
function setup(media = {matches: true, addEventListener() {}}) {
    const rows = [];
    const context = vm.createContext({window: {matchMedia: () => media}, document: {querySelectorAll: () => rows}});
    const source = readFileSync(new URL('../../main/resources/static/js/core/table/mobileCards.js', import.meta.url), 'utf8');
    vm.runInContext(source.replace('export function', 'function'), context);
    const make = (page, id, fields) => {
        const row = new Element();
        fields.forEach(f => {const cell = new Element(f); cell.children = [new Element(), new Element()]; row.appendChild(cell);});
        context.attachMobileCard({id, closest: () => ({dataset: {page}})}, row);
        rows.push(row);
        return row;
    };
    return make;
}
test('order summary keeps date and title; independent cards toggle without row events', () => {
    const make = setup();
    const fields = ['orderId','date','shipper','title'];
    const a = make('/js/orderPage.js','table-01',fields);
    const b = make('/js/orderPage.js','table-01',fields);
    assert(a.children[0].classList.contains('mobile-card-detail'));
    assert(!a.children[1].classList.contains('mobile-card-detail'));
    assert(a.children[3].children[0].classList.contains('mobile-card-secondary'));
    assert.equal(a.children.length, 4);
    const button = a;
    assert.equal(button.getAttribute('aria-expanded'), 'false');
    let stopped = 0;
    button.events.click({target: a, stopPropagation: () => stopped++});
    assert(a.classList.contains('mobile-card-expanded'));
    assert(!b.classList.contains('mobile-card-expanded'));
    assert.equal(button.getAttribute('aria-expanded'), 'true');
    button.events.click({target: a, stopPropagation: () => stopped++});
    button.events.dblclick({stopPropagation: () => stopped++});
    assert(!a.classList.contains('mobile-card-expanded'));
    assert.equal(stopped, 3);
    const ids = button.getAttribute('aria-controls').split(' ');
    assert.equal(new Set(ids).size, ids.length);
    assert(!b.getAttribute('aria-controls').split(' ').some(id => ids.includes(id)));
});
test('all recycle tabs keep number and item, loss keeps its date', () => {
    const make = setup();
    for (let n=1;n<=5;n++) {
        const row = make('/js/recyclePage.js',`table-0${n}`,n===5 ? ['recycleNumber','date','remarks'] : ['recycleNumber','date','maker','shipper']);
        assert(!row.children[0].classList.contains('mobile-card-detail'));
        assert.equal(row.children[1].classList.contains('mobile-card-detail'), n!==5);
        if(n!==5) assert(row.children[2].children[1].classList.contains('mobile-card-secondary'));
    }
});
test('product and unrelated lists have no collapse controls', () => {
    const make = setup();
    for(const [page,id] of [['/js/orderPage.js','table-02'],['/js/employeePage.js','table-01']]) {
        const row = make(page,id,['title']);
        assert.equal(row.children.length,1);
        assert(!row.classList.contains('mobile-collapsible-card'));
    }
});

test('desktop clicks keep existing behavior; resizing updates keyboard access', () => {
    let changed;
    const media = {matches: false, addEventListener: (_, fn) => changed=fn};
    const make = setup(media);
    const row = make('/js/orderPage.js','table-01',['orderId','date','title']);
    let stopped = 0;
    row.events.click({target: row, stopPropagation: () => stopped++});
    row.events.dblclick({stopPropagation: () => stopped++});
    assert.equal(stopped, 0);
    assert.equal(row.getAttribute('role'), undefined);
    media.matches = true; changed();
    assert.equal(row.getAttribute('role'), 'button');
    let prevented = false;
    row.events.keydown({target: row,key: ' ',preventDefault:()=>prevented=true,stopPropagation:()=>{}});
    assert(prevented);
    assert.equal(row.getAttribute('aria-expanded'), 'true');
    row.events.click({target:{closest:()=>({})},stopPropagation:()=>{}});
    assert.equal(row.getAttribute('aria-expanded'), 'true');
    media.matches = false; changed();
    assert.equal(row.getAttribute('tabindex'), undefined);
});
