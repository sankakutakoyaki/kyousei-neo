"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { initCombo } from "../../../bootstrap/initCombo.js";
import { EmployeeShiftRepository } from "../../../repositories/operations/shift/EmployeeShiftRepository.js";
import { DialogService } from "../../../core/ui/dialog/DialogService.js";
import { getMonthRange, getShiftMark } from "./employeeShiftUtil.js";
import { appendEmployeeRow, renderMonthHeader, updateRowSummary } from "./employeeShiftTable.js";

const changedShifts = new Map();
let loadedEmployees = [];
let loadedShiftMap = new Map();
let loadedMonthValue = "";

export async function init(){
    await initCommon();
    await initPageCache("/api/employeeshift/init/cache");

    const controller = {key: "employeeShift"};
    initCombo(controller);
    initMonth();
    renderMonthHeader(document.getElementById("shift-month")?.value);

    const monthInput = document.getElementById("shift-month");
    let previousMonth = monthInput?.value ?? "";
    monthInput?.addEventListener("change", async (event) => {
        const nextMonth = event.target.value;
        if(!await confirmDiscardChanges()){
            event.target.value = previousMonth;
            return;
        }
        previousMonth = nextMonth;
        renderMonthHeader(nextMonth);
        const officeId = Number(document.getElementById("shift-office")?.value);
        await loadEmployees(officeId);
    });

    const officeInput = document.getElementById("shift-office");
    let previousOfficeId = officeInput?.value ?? "";
    officeInput?.addEventListener("change", async (event) => {
        const nextOfficeId = event.target.value;
        if(!await confirmDiscardChanges()){
            event.target.value = previousOfficeId;
            return;
        }
        previousOfficeId = nextOfficeId;
        await loadEmployees(Number(nextOfficeId));
    });

    const workCategoryInput = document.getElementById("shift-work-category");
    let previousWorkCategoryId = workCategoryInput?.value ?? "";
    workCategoryInput?.addEventListener("change", async event => {
        const nextWorkCategoryId = event.target.value;
        if(!await confirmDiscardChanges()){
            event.target.value = previousWorkCategoryId;
            return;
        }
        previousWorkCategoryId = nextWorkCategoryId;
        renderEmployees();
    });

    document.getElementById("shift-save-btn")?.addEventListener("click", async () => {
        await saveShifts();
    });
}

async function loadEmployees(officeId){
    const body = document.getElementById("shift-table-body");
    if(!body){
        return;
    }
    body.replaceChildren();
    changedShifts.clear();
    updateSaveButton();

    if(!officeId){
        return;
    }
    const monthValue = document.getElementById("shift-month")?.value;
    const range = getMonthRange(monthValue);
    if(!range){
        return;
    }

    const [employeeResult, shiftResult] = await Promise.all([
            EmployeeShiftRepository.findEmployees({
                state: APP.cache.common.state.INITIAL,
                officeId,
                ownCompanyCategory: APP.cache.common.companyCategory.OWN
            }),
            EmployeeShiftRepository.findMonthList({
                fromDate: range.fromDate,
                toDate: range.toDate,
                officeId,
                state: APP.cache.common.state.INITIAL
            })
        ]);
    const employees = employeeResult.data ?? [];
    const shifts = shiftResult.data ?? [];
    loadedEmployees = employees;
    loadedShiftMap = createShiftMap(shifts);
    loadedMonthValue = monthValue;
    renderEmployees();
}

function createShiftMap(shifts){
    const map = new Map();
    shifts.forEach(shift => {
        const key = `${shift.employeeId}:${shift.workDate}`;
        map.set(key, shift);
    });
    return map;
}

function initMonth(){
    const input = document.getElementById("shift-month");
    if(!input){
        return;
    }
    if(input.value){
        return;
    }
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, "0");
    input.value = `${year}-${month}`;
}

function changeShiftType(button){
    const current = Number(button.dataset.shiftType);
    let next;
    switch(current){
        case 0:
            next = 1;
            break;
        case 1:
            next = 2;
            break;
        case 2:
            next = 3;
            break;
        default:
            next = 0;
            break;
    }
    button.dataset.shiftType = String(next);
    button.textContent = getShiftMark(next);
    markShiftChanged(button);
    updateRowSummary(button.closest("tr"));
}

function markShiftChanged(button){
    const employeeId = Number(button.dataset.employeeId);
    const workDate = button.dataset.date;
    const shiftType = Number(button.dataset.shiftType);
    const originalShiftType = Number(button.dataset.originalShiftType ?? 0);
    const employeeShiftId = Number(button.dataset.employeeShiftId || 0);
    const version = Number(button.dataset.version || 0);
    if(!employeeId || !workDate){
        return;
    }
    const key = `${employeeId}:${workDate}`;
    // 元の状態に戻った
    if(shiftType === originalShiftType){
        changedShifts.delete(key);
        button.classList.remove("changed");
        updateSaveButton();
        return;
    }
    changedShifts.set(key, {
        employeeShiftId: employeeShiftId || null,
        employeeId,
        workDate,
        shiftType,
        version
    });
    button.classList.add("changed");
    updateSaveButton();
}

function setRowShift(tr, shiftType){
    const buttons = tr.querySelectorAll(".shift-cell");
    buttons.forEach(button => {
        const current = Number(button.dataset.shiftType);
        if(current === shiftType){
            return;
        }
        button.dataset.shiftType = String(shiftType);
        button.textContent = getShiftMark(shiftType);
        markShiftChanged(button);
    });
    updateRowSummary(tr);
}

function toggleRowShift(tr){
    const buttons = [...tr.querySelectorAll(".shift-cell")];
    if(buttons.length === 0){
        return;
    }
    const allWorking = buttons.every(button => Number(button.dataset.shiftType) === 1);
    setRowShift(tr, allWorking ? 0 : 1);
}

async function saveShifts(){
    if(changedShifts.size === 0){
        return;
    }
    const officeId = Number(document.getElementById("shift-office")?.value);
    if(!officeId){
        return;
    }
    const rows = [...changedShifts.values()].filter(row =>
        row.employeeShiftId || row.shiftType !== 0).map(row => {
            const item = {
                employeeId: row.employeeId,
                workDate: row.workDate,
                officeId, 
                shiftType: row.shiftType,
                state: row.shiftType === 0 ? APP.cache.common.state.DELETE: APP.cache.common.state.INITIAL
            };
            if(row.employeeShiftId){
                item.employeeShiftId = row.employeeShiftId;
                item.version = row.version;
            }
            return item;
        }
    );
    if(rows.length === 0){
        changedShifts.clear();
        updateSaveButton();
        return;
    }
    await EmployeeShiftRepository.batchSave({rows});
    changedShifts.clear();
    updateSaveButton();
    DialogService.info("保存しました");
    await loadEmployees(officeId);
}

function updateSaveButton(){
    const button = document.getElementById("shift-save-btn");
    if(!button){
        return;
    }
    button.disabled = changedShifts.size === 0;
}

async function confirmDiscardChanges(){
    if(changedShifts.size === 0){
        return true;
    }
    return await DialogService.confirm(
        "保存していない変更があります。\n変更を破棄して移動しますか？"
    );
}

function renderEmployees(){
    const body = document.getElementById("shift-table-body");
    if(!body){
        return;
    }

    body.replaceChildren();
    const workCategoryId = Number(document.getElementById("shift-work-category")?.value || 0);
    const employees =
        loadedEmployees.filter(employee => {
            if(!workCategoryId){
                return true;
            }
            const ids = String(employee.workCategoryIds ?? "").split(",").map(Number);
            return ids.includes(workCategoryId);
        });

    let previousCompanyId = undefined;

    employees.forEach(employee => {
        if(previousCompanyId !== employee.companyId){
            appendCompanyHeader(body, employee.companyCategory, employee.companyName);
            previousCompanyId = employee.companyId;
        }
        appendEmployeeRow({
            body,
            employee,
            shiftMap: loadedShiftMap,
            monthValue: loadedMonthValue,
            onShiftChange: changeShiftType,
            onRowToggle: toggleRowShift
        });
    });
}

function appendCompanyHeader(body, companyCategory, companyName){
    const tr = document.createElement("tr");
    tr.className = "shift-company-row";
    const th = document.createElement("th");
    th.textContent =
        Number(companyCategory) === Number(APP.cache.common.companyCategory.OWN) ? "社員": companyName || "会社未設定";
    tr.appendChild(th);
    body.appendChild(tr);
}