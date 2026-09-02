"use strict"

import { initCommon } from "../../bootstrap/initPage.js";
import { initPageCache } from "../../bootstrap/initPageCache.js";
import { registerController } from "../../application/controllerRegistry.js";
import { createMasterPage } from "../../core/page/createMasterPage.js";
import { TimeworksRepository } from "../../repositories/management/TimeworksRepository.js";
import { createTimeworksListColumns } from "./columns.js";
import { formatDate } from "../../util/time.js";
import { openMsgDialog } from "../../core/ui/dialog/dialogCore.js";

let selectedEmployee = null;

export async function init() {
    await initCommon();
    await initPageCache("/api/timeworks/init/cache");

    const timeworks = timeworksPage();
    registerController("timeworks", timeworks);
    timeworks.init();
    initClock(timeworks);
    initEmployeeLookup();
    initStampActions(timeworks);
    initManagement();
    resetStampForm();
}

export const timeworksPage = () =>
    createMasterPage({
        key: "timeworks",
        tableId: "table-01",
        footerId: "footer-01",
        idKey: "timeworkId",
        repository: TimeworksRepository,
        columns: createTimeworksListColumns(),
        forms: {},
        checkable: false,
        buildParams: () => ({}),
        onDoubleClick: () => {},
        model: {
            pageSize: 50
        }
    });

function initClock(controller) {
    const date = document.getElementById("stamp-date");
    const time = document.getElementById("stamp-time");
    let currentDateKey = formatDate(new Date(), "yyyy-MM-dd");
    const render = () => {
        const now = new Date();
        if (date) date.textContent = formatDate(now, "yyyy年MM月dd日");
        if (time) time.textContent = formatDate(now, "HH:mm:ss");
        return now;
    };
    render();
    const timerId = window.setInterval(async () => {
        if (!date?.isConnected || !time?.isConnected) {
            window.clearInterval(timerId);
            return;
        }
        const now = render();
        const nextDateKey = formatDate(now, "yyyy-MM-dd");
        if (nextDateKey === currentDateKey) return;

        currentDateKey = nextDateKey;
        resetStampForm();
        try {
            await controller.dataTable.refresh();
        } catch (error) {
            openMsgDialog({message: error.message || "当日の一覧を更新できませんでした。", color: "red"});
        }
    }, 1000);
}

function initEmployeeLookup() {
    const identifier = document.getElementById("stamp-identifier");
    identifier?.addEventListener("keydown", async event => {
        if (event.key !== "Enter") return;
        event.preventDefault();
        await lookupEmployee(identifier.value);
    });
}

function initStampActions(controller) {
    document.querySelectorAll("[data-stamp-type]").forEach(button => {
        button.addEventListener("click", async () => {
            if (!selectedEmployee) return;
            setStampButtonsDisabled(true);
            try {
                await TimeworksRepository.stamp(selectedEmployee.employeeId, button.dataset.stampType);
                await controller.dataTable.refresh();
                resetStampForm();
            } catch (error) {
                openMsgDialog({message: error.message || "打刻に失敗しました。", color: "red"});
                await lookupEmployee(document.getElementById("stamp-identifier")?.value);
            }
        });
    });
}

async function lookupEmployee(identifier) {
    const value = identifier?.trim();
    if (!value) {
        resetStampForm();
        return;
    }
    setStampButtonsDisabled(true);
    selectedEmployee = null;
    setInputValue("stamp-user-name", "");
    try {
        selectedEmployee = await TimeworksRepository.findEmployee(value);
        setInputValue("stamp-user-name", selectedEmployee.fullName);
        focusStampButton(selectedEmployee);
    } catch (error) {
        openMsgDialog({message: error.message || "社員を確認できませんでした。", color: "red"});
        document.getElementById("stamp-identifier")?.select();
    }
}

function focusStampButton(status) {
    const start = document.querySelector('[data-stamp-type="START"]');
    const end = document.querySelector('[data-stamp-type="END"]');
    if (start) start.disabled = !status?.canStart;
    if (end) end.disabled = !status?.canEnd;
    if (status?.canStart) start?.focus();
    else if (status?.canEnd) end?.focus();
    else {
        openMsgDialog({message: "本日の出勤・退勤打刻は完了しています。", color: "blue"});
        document.getElementById("stamp-identifier")?.focus();
    }
}

function setStampButtonsDisabled(disabled) {
    document.querySelectorAll("[data-stamp-type]").forEach(button => button.disabled = disabled);
}

function setInputValue(id, value) {
    const element = document.getElementById(id);
    if (element) element.value = value || "";
}

function resetStampForm() {
    selectedEmployee = null;
    setInputValue("stamp-identifier", "");
    setInputValue("stamp-user-name", "");
    setStampButtonsDisabled(true);
    window.requestAnimationFrame(() => {
        document.getElementById("stamp-identifier")?.focus();
    });
}

function initManagement() {
    const list = document.getElementById("management-list");
    if (!list) return;

    const month = document.getElementById("management-month");
    const office = document.getElementById("management-office");
    month.value = formatDate(new Date(), "yyyy-MM");
    fillOfficeOptions(office, APP.cache.page.officeComboList ?? []);

    document.getElementById("management-search")?.addEventListener("click", refreshManagement);
    document.getElementById("management-csv")?.addEventListener("click", downloadManagementCsv);
    let loaded = false;
    document.querySelector('[data-tab="tab-02"]')?.addEventListener("click", () => {
        if (loaded) return;
        loaded = true;
        refreshManagement();
    });
    list.addEventListener("click", async event => {
        const button = event.target.closest("[data-save-timework]");
        if (!button) return;
        await saveManagementRow(button.closest("tr"));
    });
}

function fillOfficeOptions(select, offices) {
    select.replaceChildren(new Option("すべて", ""));
    offices.forEach(item => select.add(new Option(item.label, item.value)));
}

function managementParams() {
    return {
        targetMonth: document.getElementById("management-month")?.value,
        closingType: document.getElementById("management-closing-type")?.value,
        officeId: document.getElementById("management-office")?.value || null
    };
}

async function refreshManagement() {
    try {
        const items = await TimeworksRepository.searchManagement(managementParams());
        renderManagementList(items);
        renderManagementPeriod();
    } catch (error) {
        openMsgDialog({message: error.message || "勤怠データを取得できませんでした。", color: "red"});
    }
}

function renderManagementPeriod() {
    const {targetMonth, closingType} = managementParams();
    const [year, month] = targetMonth.split("-").map(Number);
    let from;
    let to;
    if (closingType === "FIFTEENTH") {
        from = new Date(year, month - 2, 16);
        to = new Date(year, month - 1, 15);
    } else {
        from = new Date(year, month - 1, 1);
        to = new Date(year, month, 0);
    }
    document.getElementById("management-period").textContent =
        `対象期間：${formatDate(from, "yyyy/MM/dd")} ～ ${formatDate(to, "yyyy/MM/dd")}`;
}

function renderManagementList(items) {
    const body = document.getElementById("management-list");
    body.replaceChildren();
    items.forEach(item => {
        const row = document.createElement("tr");
        row.dataset.timeworkId = item.timeworkId;
        row.dataset.version = item.version;
        appendTextCell(row, item.workDate);
        appendTextCell(row, item.employeeId);
        appendTextCell(row, item.fullName);
        appendTextCell(row, item.officeName || "-----");
        appendDateTimeCell(row, "startTime", item.startTime);
        appendDateTimeCell(row, "endTime", item.endTime);
        const action = document.createElement("td");
        const save = document.createElement("button");
        save.type = "button";
        save.className = "normal-btn";
        save.dataset.saveTimework = "true";
        save.textContent = "保存";
        action.appendChild(save);
        row.appendChild(action);
        body.appendChild(row);
    });
}

function appendTextCell(row, value) {
    const cell = document.createElement("td");
    cell.textContent = value ?? "";
    row.appendChild(cell);
}

function appendDateTimeCell(row, name, value) {
    const cell = document.createElement("td");
    const input = document.createElement("input");
    input.type = "datetime-local";
    input.name = name;
    input.value = value ? value.slice(0, 16) : "";
    cell.appendChild(input);
    row.appendChild(cell);
}

async function saveManagementRow(row) {
    const startTime = row.querySelector('[name="startTime"]').value || null;
    const endTime = row.querySelector('[name="endTime"]').value || null;
    try {
        const result = await TimeworksRepository.updateTimes({
            timeworkId: Number(row.dataset.timeworkId),
            startTime,
            endTime,
            version: Number(row.dataset.version)
        });
        openMsgDialog({message: result.message || "保存しました。", color: "blue"});
        await refreshManagement();
    } catch (error) {
        openMsgDialog({message: error.message || "保存できませんでした。", color: "red"});
    }
}

function downloadManagementCsv() {
    const params = managementParams();
    if (!params.officeId) {
        openMsgDialog({message: "CSVを出力する営業所を選択してください。", color: "red"});
        return;
    }
    const query = new URLSearchParams(params);
    const link = document.createElement("a");
    link.href = `/api/timeworks/admin/csv?${query}`;
    link.click();
}
