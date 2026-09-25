"use strict"

import {
    createDateString,
    getLastDay,
    getShiftMark,
    getWeekName,
    parseMonth
} from "./employeeShiftUtil.js";

/**
 * 月間ヘッダー描画
 */
export function renderMonthHeader(monthValue){
    const header = document.getElementById("shift-table-header");
    if(!header || !monthValue){
        return;
    }

    const parsed = parseMonth(monthValue);
    if(!parsed){
        return;
    }

    const {year, month} = parsed;
    const lastDay = getLastDay(year, month);
    header.replaceChildren();
    const tr = document.createElement("tr");

    // 従業員
    const employeeTh = document.createElement("th");
    employeeTh.textContent = "従業員";
    employeeTh.className = "shift-employee-column";
    tr.appendChild(employeeTh);

    // 日付
    for(let day = 1; day <= lastDay; day++){
        const date = new Date(year, month - 1, day);
        const th = document.createElement("th");
        const dayText = document.createElement("div");
        dayText.textContent = day;
        const weekText = document.createElement("small");
        weekText.textContent = getWeekName(date.getDay());
        th.appendChild(dayText);
        th.appendChild(weekText);
        const weekDay = date.getDay();
        if(weekDay === 0){
            th.classList.add("sunday");
        }
        if(weekDay === 6){
            th.classList.add("saturday");
        }
        tr.appendChild(th);
    }
    appendSummaryHeader(tr);
    header.appendChild(tr);
}

/**
 * シフト日付セル
 */
export function appendShiftCells({tr, shiftMap, monthValue, onChange}){
    if(!tr || !monthValue){
        return;
    }

    const parsed = parseMonth(monthValue);
    if(!parsed){
        return;
    }

    const {year, month} = parsed;
    const lastDay = getLastDay(year, month);
    for(let day = 1; day <= lastDay; day++){
        const td = document.createElement("td");
        const dateObject = new Date(year, month - 1, day);
        const weekDay = dateObject.getDay();
        if(weekDay === 0){
            td.classList.add("sunday");
        }
        if(weekDay === 6){
            td.classList.add("saturday");
        }
        const button = document.createElement("button");
        button.type = "button";
        button.className = "shift-cell";
        const date = createDateString(year, month, day);
        button.dataset.date = date;
        button.dataset.employeeId = tr.dataset.employeeId;
        const key = `${tr.dataset.employeeId}:${date}`;
        const currentShift = shiftMap.get(key);
        const shiftType = Number(currentShift?.shiftType ?? 0);
        button.dataset.shiftType = String(shiftType);
        button.dataset.originalShiftType = String(shiftType);
        button.dataset.employeeShiftId = currentShift?.employeeShiftId ?? "";
        button.dataset.version = currentShift?.version ?? "";
        button.textContent = getShiftMark(shiftType);
        button.addEventListener("click", () => {
            if(onChange){
                onChange(button);
            }
        });
        td.appendChild(button);
        tr.appendChild(td);
    }
}

/**
 * 集計セル
 */
export function appendShiftSummary(tr){
    const workTd = createSummaryCell("work");
    const holidayTd = createSummaryCell("holiday");
    const paidTd = createSummaryCell("paid");
    tr.appendChild(workTd);
    tr.appendChild(holidayTd);
    tr.appendChild(paidTd);
    updateRowSummary(tr);
}

/**
 * 行集計更新
 */
export function updateRowSummary(tr){
    if(!tr){
        return;
    }

    const buttons = tr.querySelectorAll(".shift-cell");
    let workCount = 0;
    let holidayCount = 0;
    let paidCount = 0;
    buttons.forEach(button => {
        const shiftType = Number(button.dataset.shiftType);
        switch(shiftType){
            case 1:
                workCount++;
                break;
            case 2:
                holidayCount++;
                break;
            case 3:
                paidCount++;
                break;
        }
    });
    setSummaryValue(tr, "work", workCount);
    setSummaryValue(tr, "holiday", holidayCount);
    setSummaryValue(tr, "paid", paidCount);
}


function appendSummaryHeader(tr){
    tr.appendChild(createSummaryHeader("出勤", "work"));
    tr.appendChild(createSummaryHeader("公休", "holiday"));
    tr.appendChild(createSummaryHeader("有休", "paid"));
}


function createSummaryHeader(text, type){
    const th = document.createElement("th");
    th.textContent = text;
    th.className =`shift-summary-header ${type}`;
    return th;
}

function createSummaryCell(type){
    const td = document.createElement("td");
    td.className = `shift-summary-cell ${type}`;
    td.dataset.summary = type;
    return td;
}

function setSummaryValue(tr, type, value){
    const cell = tr.querySelector(`[data-summary="${type}"]`);
    if(cell){
        cell.textContent = value;
    }
}

export function appendEmployeeRow({
    body,
    employee,
    shiftMap,
    monthValue,
    onShiftChange,
    onRowToggle
}){
    const tr = document.createElement("tr");
    tr.dataset.employeeId = employee.employeeId;
    const th = document.createElement("th");
    th.className = "shift-employee-column";
    const nameSpan = document.createElement("span");
    nameSpan.textContent = employee.fullName;
    const toggleButton = document.createElement("button");
    toggleButton.type = "button";
    toggleButton.className = "shift-row-btn";
    toggleButton.textContent = "全";
    toggleButton.title = "全日出勤 / 全クリア";
    toggleButton.addEventListener("click", () => {
        if(onRowToggle){
            onRowToggle(tr);
        }
    });
    const nameArea = document.createElement("div");
    nameArea.className = "shift-employee-inner";
    nameArea.appendChild(nameSpan);
    nameArea.appendChild(toggleButton);
    th.appendChild(nameArea);
    tr.appendChild(th);
    appendShiftCells({tr, shiftMap, monthValue, onChange: onShiftChange});
    appendShiftSummary(tr);

    body.appendChild(tr);
}