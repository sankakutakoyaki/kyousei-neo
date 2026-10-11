"use strict";

import { TimeworksRepository } from "../../../repositories/management/TimeworksRepository.js";
import { formatDate } from "../../../util/time.js";
import { openMsgDialog } from "../../../core/ui/dialog/dialogCore.js";
import { renderManagementList, renderManagementPeriod} from "./managementRenderer.js";

let employeeComboRequest = 0;
let managementInitialized = false;

const rowSaveTimers = new WeakMap();
const rowSaveChains = new WeakMap();

export function initManagement() {
    if(managementInitialized){return;}
    const list = document.getElementById("management-list");
    if(!list){return;}
    managementInitialized = true;

    loadManagementEmployees();
    refreshManagement();

    initManagementTableEvents();
}

export async function loadManagementEmployees() {
    const requestId = ++employeeComboRequest;
    const employee = document.getElementById("management-employee");
    if(!employee){return;}

    const officeId = document.getElementById("management-office")?.value || null;
    employee.replaceChildren(new Option("読み込み中", ""));
    employee.disabled = true;
    try {
        const employees = await TimeworksRepository.findEmployeeCombo(officeId);
        if(requestId !== employeeComboRequest){return;}
        employee.replaceChildren(new Option("すべて", ""));
        employees.forEach(item => {employee.add(new Option(item.label, item.value));});
    }catch(error){
        if(requestId !== employeeComboRequest){return;}
        employee.replaceChildren(new Option("取得できませんでした", ""));
        openMsgDialog({
            message: error.message || "従業員を取得できませんでした。",
            color: "red"
        });
    }finally{
        if(requestId === employeeComboRequest){employee.disabled = false;}
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

export async function refreshManagement() {
    try {
        const params = managementParams();
        const items = await TimeworksRepository.searchManagement(params);

        renderManagementList(items);
        renderManagementPeriod(params);
    } catch (error) {
        openMsgDialog({
            message: error.message || "勤怠データを取得できませんでした。",
            color: "red"
        });
    }
}

function scheduleManagementSave(row){
    const currentTimer = rowSaveTimers.get(row);
    if(currentTimer){window.clearTimeout(currentTimer);}

    row.classList.remove("is-saved", "is-save-error");
    row.classList.add("is-saving");

    const timer = window.setTimeout(() => {
        const previous = rowSaveChains.get(row) ?? Promise.resolve();
        const next = previous.then(() => saveManagementRow(row)).catch(() => {});
        rowSaveChains.set(row, next);
    }, 400);
    rowSaveTimers.set(row, timer);
}

async function saveManagementRow(row){
    row.classList.remove("is-saved", "is-save-error");
    row.classList.add("is-saving");
    const workDate = row.dataset.workDate;
    const startValue = row.querySelector('[name="editStartTime"]')?.value || "";
    const endValue = row.querySelector('[name="editEndTime"]')?.value || "";
    const endNextDay = row.querySelector('[name="endNextDay"]')?.checked === true;
    const editStartTime = toDateTimeValue(workDate, startValue);
    const editEndTime = toDateTimeValue(workDate, endValue, endNextDay);
    try {
        await TimeworksRepository.updateTimes({
            timeworkId: Number(row.dataset.timeworkId),
            timeworkEditId:row.dataset.timeworkEditId ? Number(row.dataset.timeworkEditId): null,
            editStartTime,
            editEndTime
        });
        row.classList.remove("is-saving", "is-save-error");
        row.classList.add("is-saved");
        window.setTimeout(() => row.classList.remove("is-saved"), 1200);
    }catch(error){
        row.classList.remove("is-saving", "is-saved");
        row.classList.add("is-save-error");
        openMsgDialog({
            message: error.message || "保存できませんでした。",
            color: "red"
        });
        throw error;
    }
}

function toDateTimeValue(workDate, time, nextDay = false){
    if(!workDate || !time){return null;}
    let date = workDate;
    if(nextDay){
        const next = new Date(`${workDate}T00:00:00`);
        next.setDate(next.getDate() + 1);
        date = formatDate(next, "yyyy-MM-dd");
    }
    return `${date}T${time}:00`;
}

export function downloadManagementCsv() {
    const params = managementParams();
    if(!params.officeId){
        openMsgDialog({
            message: "CSVを出力する営業所を選択してください。",
            color: "red"
        });
        return;
    }

    const query = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
        if(value !== null && value !== undefined && value !== ""){query.set(key, value);}
    });
    const link = document.createElement("a");
    link.href = `/api/timeworks/admin/csv?${query}`;
    link.click();
}

function initManagementTableEvents() {
    const list = document.getElementById("management-list");
    if(!list){return;}

    list.addEventListener("change", handleManagementChange);
    list.addEventListener("keydown", handleManagementKeydown);
}

function handleManagementChange(event){
    const target = event.target;
    if(!target.matches('[name="editStartTime"], ' + '[name="editEndTime"], ' + '[name="endNextDay"]')){return;}
    const row = target.closest("tr");
    if(!row){return;}
    scheduleManagementSave(row);
}

function handleManagementKeydown(event){
    const input = event.target;
    if(!input.matches('[name="editStartTime"], ' + '[name="editEndTime"]')){return;}
    if(event.altKey || event.ctrlKey || event.metaKey || event.isComposing){return;}

    const delta = {ArrowLeft: -15, ArrowRight: 15, ArrowUp: 1, ArrowDown: -1}[event.key];
    if(delta === undefined){return;}
    event.preventDefault();
    event.stopPropagation();
    adjustManagementTime(input, delta);
}


function adjustManagementTime(input, delta){
    if(!input.value){return;}
    const [hours, minutes] = input.value.split(":").map(Number);
    const row = input.closest("tr");
    if(!row){return;}

    const nextDay = row.querySelector('[name="endNextDay"]');
    const isEnd = input.name === "editEndTime";
    const total = hours * 60 + minutes + (isEnd && nextDay?.checked ? 1440: 0);
    const updated = Math.max(0, Math.min(isEnd ? 2879: 1439, total + delta));
    if(updated === total){return;}

    const clockMinutes = updated % 1440;
    input.value = `${String(Math.floor(clockMinutes / 60)).padStart(2, "0")}:${String(clockMinutes % 60).padStart(2, "0")}`;
    if(isEnd && nextDay){
        nextDay.checked = updated >= 1440;
    }
    input.dispatchEvent(new Event("change", {bubbles: true}));
}