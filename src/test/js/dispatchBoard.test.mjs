import test from 'node:test';
import assert from 'node:assert/strict';
import {assignmentPair} from '../../main/resources/static/js/pages/operations/dispatchAssignment.js';
import {qualificationStatus} from '../../main/resources/static/js/pages/operations/qualificationViews.js';
test('vehicle/order pairs work in both drag directions and reject same-kind drops',()=>{
 const vehicle={kind:'vehicle',code:'V1'},order={kind:'order',id:11};
 assert.deepEqual(assignmentPair(vehicle,order),{orderId:11,vehicleCode:'V1'});
 assert.deepEqual(assignmentPair(order,vehicle),assignmentPair(vehicle,order));
 assert.equal(assignmentPair(vehicle,vehicle),null);
 assert.equal(assignmentPair(null,order),null);
});
test('one qualification list labels overdue, renewal and valid qualifications',()=>{
 const today='2026-09-15';
 assert.equal(qualificationStatus({expiryDate:'2026-09-14'},today),'期限切れ');
 assert.equal(qualificationStatus({expiryDate:today},today),'更新予定');
 assert.equal(qualificationStatus({expiryDate:'2027-09-15'},today),'有効');
 assert.equal(qualificationStatus({},today),'期限設定なし');
});
