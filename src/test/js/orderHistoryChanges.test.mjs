import test from 'node:test';
import assert from 'node:assert/strict';
import {orderHistoryChanges} from '../../main/resources/static/js/components/order/orderHistoryChanges.js';
const snapshot = values => ({eventType: 'ORDER_SAVED', beforeSnapshotCount: 1, afterSnapshotCount: 1, ...values});

test('only changed fields are listed, including cleared values and date/time', () => {
    const result = orderHistoryChanges(snapshot({beforeFullAddress: '大阪市1-1', afterFullAddress: '大阪市2-2',
        beforeVisitDate: '2026-10-30', afterVisitDate: '2026-10-31', beforeVisitTime: '午前', afterVisitTime: '午後',
        beforeRemarks: '連絡希望', afterRemarks: null, beforeTitle: '同じ名前', afterTitle: '同じ名前'}));
    assert.deepEqual(result.changes.map(c => c.label), ['住所', '訪問日', '訪問時間', '備考']);
    assert.equal(result.changes.at(-1).after, '');
    assert.equal(result.message, '');
});
test('null and empty are both unset while spaces and newlines remain meaningful', () => {
    const result = orderHistoryChanges(snapshot({beforeTitle: null, afterTitle: '', beforeRemarks: 'A\nB', afterRemarks: 'A B'}));
    assert.equal(result.changes.length, 1);
    assert.equal(result.changes[0].before, 'A\nB');
});
test('incomplete or duplicate snapshots never produce inferred differences', () => {
    for (const [before, after] of [[null, 1], [1, null], [0, 1], [2, 1], [1, 2]]) {
        const result = orderHistoryChanges(snapshot({beforeSnapshotCount: before, afterSnapshotCount: after, beforeTitle: '旧', afterTitle: '新'}));
        assert.deepEqual(result.changes, []);
        assert.match(result.message, /確認できません/);
    }
});
test('creation and unknown events do not invent a comparison', () => {
    for (const eventType of ['ORDER_CREATED', 'UNKNOWN']) {
        assert.deepEqual(orderHistoryChanges(snapshot({eventType, beforeTitle: null, afterTitle: '新'})), {changes: [], message: ''});
    }
});
test('detail-only save has a scoped no-difference message', () => {
    const result = orderHistoryChanges(snapshot({beforeTitle: '同じ', afterTitle: '同じ'}));
    assert.deepEqual(result.changes, []);
    assert.match(result.message, /表示対象の項目に変更はありません/);
});

test('dispatch creation, move, removal and reset use assignment snapshots without order changes', () => {
    const assignment = {beforeDispatchCount: 1, afterDispatchCount: 1,
        beforeDailyCrewId: 5, afterDailyCrewId: 8, beforeVisitOrder: 2, afterVisitOrder: 4,
        beforeTitle: 'old', afterTitle: 'new', beforeSnapshotCount: 1, afterSnapshotCount: 1};
    const created = orderHistoryChanges({...assignment, beforeDispatchCount: null, eventType: 'DISPATCH_CREATED'});
    assert.equal(created.changes[0].before, '');
    assert.equal(created.changes[0].after, '8');
    const moved = orderHistoryChanges({...assignment, eventType: 'DISPATCH_MOVED'});
    assert.deepEqual(moved.changes.map(c => c.label), ['配車先（班ID）', '訪問順']);
    assert.equal(moved.changes[0].before, '5');
    for (const eventType of ['DISPATCH_REMOVED', 'DISPATCH_RESET']) {
        const removed = orderHistoryChanges({...assignment, eventType});
        assert.equal(removed.changes[0].before, '5');
        assert.equal(removed.changes[0].after, '');
    }
});

test('dispatch missing or duplicate logs are not compared and no-op reorder is scoped', () => {
    for (const count of [null, 0, 2]) {
        const result = orderHistoryChanges({eventType: 'DISPATCH_MOVED', beforeDispatchCount: count, afterDispatchCount: 1});
        assert.deepEqual(result.changes, []);
        assert.match(result.message, /確認できません/);
    }
    const result = orderHistoryChanges({eventType: 'DISPATCH_REORDERED', beforeDispatchCount: 1, afterDispatchCount: 1,
        beforeDailyCrewId: 8, afterDailyCrewId: 8, beforeVisitOrder: 2, afterVisitOrder: 2});
    assert.deepEqual(result.changes, []);
    assert.match(result.message, /配車先・訪問順に変更はありません/);
});
