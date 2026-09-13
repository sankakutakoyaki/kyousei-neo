import test from 'node:test';
import assert from 'node:assert/strict';
import {isWriteRequest, assertMobileWriteAllowed, refreshMobileReadOnly, isMobileReadOnly, isWriteAction} from '../../main/resources/static/js/core/access/mobileReadOnly.js';

test('mobile permits searches/details/downloads and rejects all existing write query kinds', () => {
    globalThis.window = {matchMedia: () => ({matches: true})};
    for (const queryId of ['orderList', 'orderDetail', 'orderItemFormList', 'orderWorkFormList', 'companyCsv', 'itemMasterFindByJanCode', 'orderItemListByItemModel']) {
        assert.doesNotThrow(() => assertMobileWriteAllowed('/api/query', 'POST', {queryId}));
    }
    for (const queryId of ['orderSave', 'companyDeleteByIds', 'orderItemCreate', 'unknown']) {
        assert.throws(() => assertMobileWriteAllowed('/api/query', 'POST', {queryId}), /閲覧/);
    }
    for (const [url, method] of [['/api/order/import/pdf','POST'], ['/api/attachments/files/1','DELETE'], ['/api/attachments/groups/1','PATCH'], ['/api/timeworks/admin/update','POST']]) {
        assert.throws(() => assertMobileWriteAllowed(url, method), /閲覧/);
    }
    assert.equal(isWriteRequest('/api/attachments/ORDER/1/groups', 'GET'), false);
});

test('desktop retains write access', () => {
    globalThis.window = {matchMedia: () => ({matches: false})};
    assert.doesNotThrow(() => assertMobileWriteAllowed('/api/query','POST',{queryId:'orderSave'}));
    assert.doesNotThrow(() => assertMobileWriteAllowed('/api/attachments/files/1','DELETE'));
});

test('switching to mobile disables detail fields and restores original disabled states on desktop', () => {
    let mobile = true;
    globalThis.window = {matchMedia: () => ({matches: mobile})};
    const fields = [{disabled:false}, {disabled:true}];
    globalThis.document = {documentElement:{classList:{toggle(){}}}, querySelectorAll: () => fields};
    refreshMobileReadOnly();
    assert.deepEqual(fields.map(f=>f.disabled), [true,true]);
    refreshMobileReadOnly();
    mobile = false;
    refreshMobileReadOnly();
    assert.deepEqual(fields.map(f=>f.disabled), [false,true]);
});

test('mobile permits arrival and operational recycle writes but not product edits or recycle master maintenance', () => {
    globalThis.window = {matchMedia: () => ({matches: true})};
    for (const queryId of ['orderItemArrival', 'recycleSave', 'recycleDeliverySave', 'recycleShippingSave', 'recycleLossSave'])
        assert.doesNotThrow(() => assertMobileWriteAllowed('/api/query','POST',{queryId}));
    for (const queryId of ['recycleDeleteByIds', 'orderItemSave', 'orderItemCreate', 'orderItemDeleteByIds', 'recycleMakerSave', 'recyclePriceSave', 'orderSave'])
        assert.throws(() => assertMobileWriteAllowed('/api/query','POST',{queryId}), /閲覧/);
    assert.equal(isWriteAction('arrival-item'), false);
    for (const key of ['recycleUse','recycleDelivery','recycleShipping','recycleLoss'])
        assert.equal(isMobileReadOnly(key), false);
    for (const key of ['recycleList','orderItemList','orderList','recycleMaker','employeeList'])
        assert.equal(isMobileReadOnly(key), true);
});

test('recycle form fields stay editable while unrelated fields remain protected', () => {
    globalThis.window = {matchMedia: () => ({matches: true})};
    const recycleField = {disabled: false, closest: () => ({})};
    const orderField = {disabled: false, closest: () => null};
    globalThis.document = {documentElement:{classList:{toggle(){}}}, querySelectorAll: () => [recycleField,orderField]};
    refreshMobileReadOnly();
    assert.equal(recycleField.disabled, false);
    assert.equal(orderField.disabled, true);
});

test('mobile rejects editing existing recycle records but permits new registration', () => {
    globalThis.window = {matchMedia: () => ({matches: true})};
    assert.throws(() => assertMobileWriteAllowed('/api/query', 'POST', {queryId:'recycleSave', params:{recycleId:123}}), /閲覧/);
    assert.doesNotThrow(() => assertMobileWriteAllowed('/api/query', 'POST', {queryId:'recycleSave', params:{recycleId:0}}));
    const listField = {closest: selector => selector.startsWith('main') ? {} : null};
    assert.equal(isMobileReadOnly(listField), true);
});

test('mobile allows own start/end stamps but protects administrative attendance writes', () => {
    globalThis.window = {matchMedia: () => ({matches: true})};
    for (const stampType of ['START', 'END'])
        assert.doesNotThrow(() => assertMobileWriteAllowed('/api/timeworks/stamp/self','POST',{stampType}));
    for (const path of ['/api/timeworks/stamp', '/api/timeworks/admin/update'])
        assert.throws(() => assertMobileWriteAllowed(path,'POST',{stampType:'START'}));
    assert.throws(() => assertMobileWriteAllowed('/api/timeworks/stamp/self','DELETE',{stampType:'START'}));
});

test('dispatch mobile edit is scoped to management users and does not enable order edits', () => {
    globalThis.window = {matchMedia: () => ({matches: true})};
    globalThis.document = {querySelector: () => null};
    assert.equal(isMobileReadOnly('dispatch'), true);
    assert.throws(() => assertMobileWriteAllowed('/api/query','POST',{queryId:'dispatchSave'}));
    for (const queryId of ['dispatchList','dispatchDetail','dispatchEmployeeList'])
        assert.doesNotThrow(() => assertMobileWriteAllowed('/api/query','POST',{queryId}));
    globalThis.document = {querySelector: selector => selector.includes('data-dispatch-manager') ? {} : null};
    assert.equal(isMobileReadOnly('dispatch'), false);
    assert.equal(isMobileReadOnly('orderList'), true);
    assert.doesNotThrow(() => assertMobileWriteAllowed('/api/query','POST',{queryId:'dispatchSave'}));
    assert.throws(() => assertMobileWriteAllowed('/api/query','POST',{queryId:'orderSave'}));
    delete globalThis.document;
});
