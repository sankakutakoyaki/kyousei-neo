import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';
import {createOrderItemListColumns} from '../../main/resources/static/js/pages/business/order/columns.js';
import {assertMobileWriteAllowed} from '../../main/resources/static/js/core/access/mobileReadOnly.js';
const source = readFileSync(new URL('../../main/resources/static/js/pages/business/order/orderItemArrivals.js', import.meta.url), 'utf8')
    .replace(/^import .*;\n/gm, '').replaceAll('export ', '');
const remaining = vm.runInNewContext(source+'\nremainingQuantity;');
test('remaining quantity excludes cancelled history and includes partial receipts', () => {
    assert.equal(remaining({itemQuantity:3}, [{quantity:2}, {quantity:1,cancelledAt:'2026-01-01'}]),1);
    assert.equal(remaining({itemQuantity:3}, [{quantity:2},{quantity:1}]),0);
});
test('product list displays partial receipt and completed history actions', () => {
    const render=createOrderItemListColumns().find(c=>c.field==='date').render;
    assert.match(render({orderItemId:1,itemQuantity:3,receivedQuantity:2}), /一部入荷 2 \/ 3/);
    assert.match(render({orderItemId:1,itemQuantity:3,receivedQuantity:2}), />入荷<\/button>/);
    assert.match(render({orderItemId:1,itemQuantity:3,receivedQuantity:3,arrivalDate:'2026-01-01'}), />履歴<\/button>/);
});
test('mobile permits receipt and history lookup but rejects correction and cancellation', () => {
    globalThis.window={matchMedia:()=>({matches:true})};
    for(const queryId of ['orderItemArrival','orderItemArrivalDetail']) assert.doesNotThrow(()=>assertMobileWriteAllowed('/api/query','POST',{queryId}));
    for(const queryId of ['orderItemArrivalCorrect','orderItemArrivalCancel']) assert.throws(()=>assertMobileWriteAllowed('/api/query','POST',{queryId}));
});

test('request IDs can be generated without secure-context randomUUID', () => {
    const make = vm.runInNewContext(source+'\nnewRequestId;', {crypto:{getRandomValues: bytes => { bytes.fill(17); return bytes; }}});
    assert.match(make(), /^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/);
});
