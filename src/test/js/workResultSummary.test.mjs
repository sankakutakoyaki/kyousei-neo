import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';
const code=readFileSync(new URL('../../main/resources/static/js/components/order/workResultSummary.js',import.meta.url),'utf8').replace(/export function /g,'function ');
const context=vm.createContext({});
vm.runInContext(code+'\nglobalThis.summary=workResultSummaryHtml;globalThis.assignment=renderAssignmentResult;',context);

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
