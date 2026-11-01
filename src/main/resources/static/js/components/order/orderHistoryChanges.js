"use strict";
import { orderEnumLabel, workResultLabel, orderOutcomeLabel } from "./orderEnums.js";

const fields = [
    ["RequestNumber", "受注番号"], ["Title", "お客様名・件名"],
    ["PostalCode", "郵便番号"], ["FullAddress", "住所"],
    ["VisitDate", "訪問日"], ["VisitTime", "訪問時間"],
    ["ContactInformation", "連絡先"], ["Remarks", "備考"],
    ["OrderStatus", "対応状況"], ["OutcomeType", "報告結果"], ["OutcomeReason", "理由・備考"], ["CancelReason", "キャンセル理由"], ["OutcomeReportedAt", "報告日時"], ["DispatchCategory", "配車区分"]
];

export function orderHistoryChanges(row) {
    if (["WORK_COMPLETED", "DELIVERY_FAILED", "POSTPONED", "ABSENT", "WORK_RESULT_CORRECTED"].includes(row.eventType)) return workResultChanges(row);
    if (["DISPATCH_CREATED", "DISPATCH_MOVED", "DISPATCH_REMOVED", "DISPATCH_RESET", "DISPATCH_REORDERED"].includes(row.eventType)) {
        return dispatchChanges(row);
    }
    if (row.eventType !== "ORDER_SAVED") return {changes: [], message: ""};
    if (Number(row.beforeSnapshotCount) !== 1 || Number(row.afterSnapshotCount) !== 1) {
        return {changes: [], message: "変更前後の記録が揃っていないため、変更内容を確認できません。"};
    }
    const changes = [];
    for (const [key, label] of fields) {
        const groups={DispatchCategory:"dispatchCategory",OrderStatus:"orderStatus",OutcomeType:"workResult",CancelReason:"cancelReason"};
        const group=groups[key];
        const rawBefore=String(row[`before${key}`] ?? ""),rawAfter=String(row[`after${key}`] ?? "");
        const before=key==="OutcomeType"?orderOutcomeLabel(rawBefore):group?orderEnumLabel(group,rawBefore):rawBefore;
        const after=key==="OutcomeType"?orderOutcomeLabel(rawAfter):group?orderEnumLabel(group,rawAfter):rawAfter;
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

function workResultChanges(row) {
    const corrected = row.eventType === "WORK_RESULT_CORRECTED";
    if (Number(row.afterResultCount) !== 1 || (corrected && Number(row.beforeResultCount) !== 1))
        return {changes: [], message: "作業結果の記録が揃っていないため、内容を確認できません。"};
    const changes = [];
    for (const [key, label] of [["ResultType", "作業結果"], ["ResultReason", "理由・備考"], ["ResultWorkDate", "訪問日"]]) {
        let before = corrected ? String(row[`before${key}`] ?? "") : "";
        let after = String(row[`after${key}`] ?? "");
        if (key === "ResultType") {before = workResultLabel(before); after = workResultLabel(after);}
        if (before !== after) changes.push({label, before, after});
    }
    return {changes, message: changes.length ? "" : "作業結果を報告しました。"};
}
