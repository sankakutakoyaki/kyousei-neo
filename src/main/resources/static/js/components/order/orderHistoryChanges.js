"use strict";

const fields = [
    ["RequestNumber", "受注番号"], ["Title", "お客様名・件名"],
    ["PostalCode", "郵便番号"], ["FullAddress", "住所"],
    ["VisitDate", "訪問日"], ["VisitTime", "訪問時間"],
    ["ContactInformation", "連絡先"], ["Remarks", "備考"]
];

export function orderHistoryChanges(row) {
    if (["DISPATCH_CREATED", "DISPATCH_MOVED", "DISPATCH_REMOVED", "DISPATCH_RESET", "DISPATCH_REORDERED"].includes(row.eventType)) {
        return dispatchChanges(row);
    }
    if (row.eventType !== "ORDER_SAVED") return {changes: [], message: ""};
    if (Number(row.beforeSnapshotCount) !== 1 || Number(row.afterSnapshotCount) !== 1) {
        return {changes: [], message: "変更前後の記録が揃っていないため、変更内容を確認できません。"};
    }
    const changes = [];
    for (const [key, label] of fields) {
        const before = String(row[`before${key}`] ?? "");
        const after = String(row[`after${key}`] ?? "");
        if (before !== after) changes.push({label, before, after});
    }
    return {changes, message: changes.length ? "" : "住所・訪問日時など、表示対象の項目に変更はありません。"};
}

function dispatchChanges(row) {
    const created = row.eventType === "DISPATCH_CREATED";
    const removed = ["DISPATCH_REMOVED", "DISPATCH_RESET"].includes(row.eventType);
    if (Number(row.afterDispatchCount) !== 1 || (!created && Number(row.beforeDispatchCount) !== 1)) {
        return {changes: [], message: "配車の記録が揃っていないため、変更内容を確認できません。"};
    }
    const changes = [];
    for (const [key, label] of [["DailyCrewId", "配車先（班ID）"], ["VisitOrder", "訪問順"]]) {
        const before = created ? "" : String(row[`before${key}`] ?? "");
        const after = removed ? "" : String(row[`after${key}`] ?? "");
        if (before !== after) changes.push({label, before, after});
    }
    return {changes, message: changes.length ? "" : "配車先・訪問順に変更はありません。"};
}
