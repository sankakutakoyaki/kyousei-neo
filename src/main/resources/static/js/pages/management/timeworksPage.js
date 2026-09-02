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
let employeeComboRequest = 0;
const mobileMedia = window.matchMedia("(max-width: 560px)");
let rolePersonalMode = false;
let isPersonalMode = mobileMedia.matches;
let responsiveChangeHandler = null;
let managementInitialized = false;

export async function init() {
    managementInitialized = false;
    await initCommon();
    await initPageCache("/api/timeworks/init/cache");
    rolePersonalMode = APP.cache.page.personalMode === true;

    const timeworks = timeworksPage();
    registerController("timeworks", timeworks);
    initClock(timeworks);
    initEmployeeLookup();
    initStampActions(timeworks);
    initResponsiveStampMode(timeworks);
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
        if (isPersonalMode) await loadSelfStatus();
        else resetStampForm();
        try {
            await controller.dataTable?.refresh();
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
                if (isPersonalMode) await TimeworksRepository.stampSelf(button.dataset.stampType);
                else await TimeworksRepository.stamp(selectedEmployee.employeeId, button.dataset.stampType);
                await controller.dataTable?.refresh();
                if (isPersonalMode) await loadSelfStatus();
                else resetStampForm();
            } catch (error) {
                openMsgDialog({message: error.message || "打刻に失敗しました。", color: "red"});
                if (isPersonalMode) await loadSelfStatus();
                else await lookupEmployee(document.getElementById("stamp-identifier")?.value);
            }
        });
    });
}

function initResponsiveStampMode(controller) {
    const applyMode = async event => {
        isPersonalMode = rolePersonalMode || event.matches;
        document.querySelector("main[data-page$='timeworksPage.js']")
            ?.classList.toggle("personal-timeworks-mode", isPersonalMode);
        if (isPersonalMode) await loadSelfStatus();
        else {
            ensureDesktopPage(controller);
            resetStampForm();
        }
    };
    if (responsiveChangeHandler) mobileMedia.removeEventListener("change", responsiveChangeHandler);
    responsiveChangeHandler = applyMode;
    mobileMedia.addEventListener("change", responsiveChangeHandler);
    applyMode(mobileMedia);
}

function ensureDesktopPage(controller) {
    if (!controller.dataTable) controller.init();
    if (!managementInitialized) {
        initManagement();
        managementInitialized = true;
    }
}

async function loadSelfStatus() {
    setStampButtonsDisabled(true);
    selectedEmployee = null;
    try {
        selectedEmployee = await TimeworksRepository.findSelf();
        focusStampButton(selectedEmployee);
    } catch (error) {
        openMsgDialog({message: error.message || "本人の勤務状態を取得できませんでした。", color: "red"});
    }
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
        if (isPersonalMode) return;
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
    loadManagementEmployees();

    document.getElementById("management-search")?.addEventListener("click", refreshManagement);
    document.getElementById("management-csv")?.addEventListener("click", downloadManagementCsv);
    office.addEventListener("change", loadManagementEmployees);
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

async function loadManagementEmployees() {
    const requestId = ++employeeComboRequest;
    const employee = document.getElementById("management-employee");
    const officeId = document.getElementById("management-office")?.value || null;
    employee.replaceChildren(new Option("読み込み中", ""));
    employee.disabled = true;
    try {
        const employees = await TimeworksRepository.findEmployeeCombo(officeId);
        if (requestId !== employeeComboRequest) return;
        employee.replaceChildren(new Option("すべて", ""));
        employees.forEach(item => employee.add(new Option(item.label, item.value)));
    } catch (error) {
        if (requestId !== employeeComboRequest) return;
        employee.replaceChildren(new Option("取得できませんでした", ""));
        openMsgDialog({message: error.message || "従業員を取得できませんでした。", color: "red"});
    } finally {
        if (requestId === employeeComboRequest) employee.disabled = false;
    }
}

function managementParams() {
    return {
        targetMonth: document.getElementById("management-month")?.value,
        closingType: document.getElementById("management-closing-type")?.value,
        officeId: document.getElementById("management-office")?.value || null,
        employeeId: document.getElementById("management-employee")?.value || null
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
    const query = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
        if (value !== null && value !== undefined && value !== "") query.set(key, value);
    });
    const link = document.createElement("a");
    link.href = `/api/timeworks/admin/csv?${query}`;
    link.click();
}
