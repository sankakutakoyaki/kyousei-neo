import {installOrderEnumCache} from './fixtures/orderEnumCache.mjs';
import {orderEnumLabel,workResultLabel,orderStatusLabel,photoCategoryLabel,isSimpleWorkResult} from '../../main/resources/static/js/components/order/orderEnums.js';
installOrderEnumCache();
import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';
const code=readFileSync(new URL('../../main/resources/static/js/components/order/workResultSummary.js',import.meta.url),'utf8').replace(/^import .*;\n/gm,'').replace(/export function /g,'function ');
const context=vm.createContext({workResultLabel,orderStatusLabel});
vm.runInContext(code+'\nglobalThis.summary=workResultSummaryHtml;globalThis.assignment=renderAssignmentResult;globalThis.current=orderCurrentStatusHtml;',context);

test('status labels omit crew counts and retain visibility of legacy mixed results',()=>{
    const html=context.summary({completedCount:1,unreportedCount:2,failedCount:0,postponedCount:1});
    assert.match(html,/完了/);assert.match(html,/未報告/);assert.match(html,/日延/);
    assert.doesNotMatch(html,/搬入不可/);assert.doesNotMatch(html,/>[^<]*[0-9]/);
    assert.equal(context.summary({}),'');
});
test('assignment status falls back to unreported and reason is assigned as plain text',()=>{
    const root={dataset:{}};
    context.assignment(root,{resultType:'DELIVERY_FAILED',resultReason:'<理由>'});
    assert.equal(root.textContent,'搬入不可');assert.equal(root.title,'<理由>');
    context.assignment(root,{});
    assert.equal(root.textContent,'未報告');assert.equal(root.dataset.resultType,'UNREPORTED');assert.equal(root.title,'');
});

test('order-level pending and cancellation statuses override old crew counts',()=>{
    const html=context.summary({orderStatus:'NEEDS_ACTION',outcomeType:'POSTPONED',completedCount:1});
    assert.match(html,/日延・要対応/);assert.doesNotMatch(html,/完了/);
    assert.match(context.summary({orderStatus:'CANCELLED',completedCount:1}),/キャンセル/);
});

test('current status is independent of same-day counts and absence includes a safe reported time',()=>{
    assert.match(context.current({orderStatus:'ACTIVE',completedCount:2}),/未完了/);
    const html=context.current({orderStatus:'ABSENT',outcomeReportedAt:'2026-11-01 15:00:12'});
    assert.match(html,/不在 11\/01 15:00:12/);
    assert.doesNotMatch(context.current({orderStatus:'ABSENT',outcomeReportedAt:'<script>'}),/<script>/);
});
