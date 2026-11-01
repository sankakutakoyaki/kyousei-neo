"use strict";

const statuses = [
    ["UNREPORTED", "未報告", "unreportedCount"],
    ["COMPLETED", "完了", "completedCount"],
    ["DELIVERY_FAILED", "搬入不可", "failedCount"],
    ["POSTPONED", "日延", "postponedCount"]
];

export function workResultSummaryHtml(row) {
    return statuses.filter(([, , field]) => Number(row[field]) > 0).map(([type, label, field]) =>
        `<span class="work-result-badge" data-result-type="${type}">${label}</span>`
    ).join(" ");
}

export function renderWorkResultSummary(root, row) {
    root.innerHTML = workResultSummaryHtml(row);
}

export function renderAssignmentResult(root, assignment) {
    const status = statuses.find(([type]) => type === assignment.resultType) ?? statuses[0];
    root.dataset.resultType = status[0];
    root.textContent = status[1];
    root.title = assignment.resultReason || "";
}
