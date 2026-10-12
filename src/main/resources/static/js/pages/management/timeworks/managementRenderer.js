"use strict";

import { formatDate } from "../../../util/time.js";
import { toggleScrollbar } from "../../../core/table/tableRender.js";
import { cloneTemplate } from "../../../core/dom/cloneTemplate.js";

export function renderManagementPeriod({ targetMonth, closingType }) {
    if (!targetMonth) return;

    const [year, month] = targetMonth.split("-").map(Number);
    let from, to;

    if (closingType === "FIFTEENTH") {
        from = new Date(year, month - 2, 16);
        to = new Date(year, month - 1, 15);
    } else {
        from = new Date(year, month - 1, 1);
        to = new Date(year, month, 0);
    }

    const period = document.getElementById("management-period");
    if (!period) return;

    period.textContent =
        `対象期間：${formatDate(from, "yyyy/MM/dd")} ～ ${formatDate(to, "yyyy/MM/dd")}`;
}

export function renderManagementList(items) {
    const body = document.getElementById("management-list");
    if (!body) return;

    const fragment = document.createDocumentFragment();

    items.forEach(item => {
        fragment.appendChild(createManagementRow(item));
    });

    body.replaceChildren(fragment);

    window.requestAnimationFrame(() => toggleScrollbar(body));
}

function createManagementRow(item) {
    const row = cloneTemplate("timeworks-management-row-template");

    row.dataset.timeworkId = item.timeworkId;
    row.dataset.version = item.version;
    row.dataset.workDate = item.workDate;
    row.dataset.timeworkEditId = item.timeworkEditId ?? "";

    setText(row, "workDate", item.workDate);
    setText(row, "employeeId", item.employeeId);
    setText(row, "fullName", item.fullName);
    setText(row, "officeName", item.officeName || "-----");

    setText(
        row,
        "originalStartTime",
        formatOriginalTime(item.originalStartTime, item.workDate)
    );

    setText(
        row,
        "originalEndTime",
        formatOriginalTime(item.originalEndTime, item.workDate)
    );

    const start = item.editStartTime || item.originalStartTime;
    const end = item.editEndTime || item.originalEndTime;

    row.querySelector('[name="editStartTime"]').value =
        start ? start.slice(11, 16) : "";

    row.querySelector('[name="editEndTime"]').value =
        end ? end.slice(11, 16) : "";

    row.querySelector('[name="endNextDay"]').checked =
        Boolean(
            end &&
            item.workDate &&
            end.slice(0, 10) > item.workDate
        );

    return row;
}

function setText(row, field, value) {
    const element = row.querySelector(`[data-field="${field}"]`);
    if (element) element.textContent = value ?? "";
}

function formatOriginalTime(value, workDate) {
    if (!value) return "-----";

    const nextDay = value.slice(0, 10) > workDate;

    return `${nextDay ? "翌日 " : ""}${value.slice(11, 16)}`;
}