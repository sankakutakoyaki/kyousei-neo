"use strict";

import { formatDate } from "../../../util/time.js";
import { toggleScrollbar } from "../../../core/table/tableRender.js";

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

    body.replaceChildren();

    items.forEach(item => {
        const row = document.createElement("tr");
        row.setAttribute("name", "data-row");
        row.dataset.timeworkId = item.timeworkId;
        row.dataset.version = item.version;
        row.dataset.workDate = item.workDate;
        row.dataset.timeworkEditId = item.timeworkEditId ?? "";

        appendTextCell(row, item.workDate);
        appendTextCell(row, item.employeeId);
        appendTextCell(row, item.fullName);
        appendTextCell(row, item.officeName || "-----");

        appendOriginalTimeCell(row, item.originalStartTime, item.workDate);
        appendTimeCell(row, "editStartTime", item.editStartTime || item.originalStartTime);
        appendOriginalTimeCell(row, item.originalEndTime, item.workDate);
        appendTimeCell(row, "editEndTime", item.editEndTime || item.originalEndTime);
        appendNextDayCell(row, item.editEndTime || item.originalEndTime, item.workDate);

        body.appendChild(row);
    });

    window.requestAnimationFrame(() => toggleScrollbar(body));
}

function appendTextCell(row, value) {
    const cell = document.createElement("td");
    cell.textContent = value ?? "";
    row.appendChild(cell);
}

function appendOriginalTimeCell(row, value, workDate) {
    const cell = document.createElement("td");
    cell.className = "management-original-time";

    if (!value) {
        cell.textContent = "-----";
    } else {
        const nextDay = value.slice(0, 10) > workDate;
        cell.textContent = `${nextDay ? "翌日 " : ""}${value.slice(11, 16)}`;
    }

    row.appendChild(cell);
}

function appendTimeCell(row, name, value) {
    const cell = document.createElement("td");
    const area = document.createElement("div");
    const input = document.createElement("input");

    area.className = "management-time-input";
    input.type = "time";
    input.title = "←／→：15分、↑／↓：1分ずつ変更";
    input.name = name;
    input.value = value ? value.slice(11, 16) : "";

    area.appendChild(input);
    cell.appendChild(area);
    row.appendChild(cell);
}

function appendNextDayCell(row, value, workDate) {
    const cell = document.createElement("td");
    const checkbox = document.createElement("input");

    cell.className = "next-day-cell";
    checkbox.type = "checkbox";
    checkbox.name = "endNextDay";
    checkbox.checked = Boolean(value && workDate && value.slice(0, 10) > workDate);
    checkbox.setAttribute("aria-label", "翌日退勤");

    cell.appendChild(checkbox);
    row.appendChild(cell);
}