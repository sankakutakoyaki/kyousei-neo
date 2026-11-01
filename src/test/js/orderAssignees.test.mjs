import test from 'node:test';
import assert from 'node:assert/strict';
import {orderAssigneesText} from '../../main/resources/static/js/components/order/orderAssignees.js';

test('all assigned names are preserved for detail and tooltip',()=>{
    assert.equal(orderAssigneesText({assignedEmployeeNames:'山田・佐藤',assignedCrewCount:2}),'山田・佐藤');
});
test('unassigned orders and crews with no valid members are distinguished',()=>{
    assert.equal(orderAssigneesText({assignedCrewCount:0}),'未配車');
    assert.equal(orderAssigneesText({assignedCrewCount:1,assignedEmployeeNames:' '}),'担当者未設定');
});

test('undated followups identify the previous visit explicitly while scheduled visits show their own crew',()=>{
    assert.equal(orderAssigneesText({assignedEmployeeNames:'山田',assignedCrewCount:1,visitDate:null,assignedWorkDate:'2026-11-01'}),'前回（2026-11-01）：山田');
    assert.equal(orderAssigneesText({assignedEmployeeNames:'佐藤',assignedCrewCount:1,visitDate:'2026-11-03',assignedWorkDate:'2026-11-03'}),'佐藤');
});
