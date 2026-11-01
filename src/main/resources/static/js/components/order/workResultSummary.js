"use strict";
import { workResultLabel, orderStatusLabel } from "./orderEnums.js";

const statuses = [
    ["UNREPORTED", "unreportedCount"],
    ["COMPLETED", "completedCount"],
    ["DELIVERY_FAILED", "failedCount"],
    ["POSTPONED", "postponedCount"],
    ["ABSENT", "absentCount"]
];

export function workResultSummaryHtml(row) {
    const lifecycle={ABSENT:absenceLabel(row.outcomeReportedAt),NEEDS_ACTION:orderStatusLabel("NEEDS_ACTION"),COMPLETED:orderStatusLabel("COMPLETED"),CANCELLED:orderStatusLabel("CANCELLED")};
    if(row.orderStatus==="NEEDS_ACTION")lifecycle.NEEDS_ACTION=["DELIVERY_FAILED","POSTPONED"].includes(row.outcomeType)?`${workResultLabel(row.outcomeType)}・${orderStatusLabel("NEEDS_ACTION")}`:orderStatusLabel("NEEDS_ACTION");
    if(lifecycle[row.orderStatus])return `<span class="work-result-badge" data-result-type="${row.orderStatus}">${lifecycle[row.orderStatus]}</span>`;
    return statuses.filter(([, field]) => Number(row[field]) > 0).map(([type, field]) =>
        `<span class="work-result-badge" data-result-type="${type}">${workResultLabel(type)}</span>`
    ).join(" ");
}

export function renderWorkResultSummary(root, row) {
    root.innerHTML = workResultSummaryHtml(row);
}

export function renderAssignmentResult(root, assignment) {
    const status = statuses.find(([type]) => type === assignment.resultType) ?? statuses[0];
    root.dataset.resultType = status[0];
    root.textContent = status[0] === "ABSENT" ? absenceLabel(assignment.resultReportedAt) : workResultLabel(status[0]);
    root.title = assignment.resultReason || "";
}

export function absenceLabel(reportedAt) {
    const time=String(reportedAt ?? "").match(/^(\d{4})-(\d{2})-(\d{2})[T ](\d{2}:\d{2}:\d{2})/);
    return time ? `${workResultLabel("ABSENT")} ${time[2]}/${time[3]} ${time[4]}` : workResultLabel("ABSENT");
}
export function orderCurrentStatusHtml(row) {
    if(["COMPLETED","CANCELLED","NEEDS_ACTION","ABSENT"].includes(row.orderStatus))return workResultSummaryHtml(row);
    return `<span class="work-result-badge" data-result-type="UNREPORTED">${orderStatusLabel("ACTIVE")}</span>`;
}
