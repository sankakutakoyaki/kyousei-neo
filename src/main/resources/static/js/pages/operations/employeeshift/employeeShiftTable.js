"use strict"

import {
    createDateString,
    getDateList,
    getLastDay,
    getShiftMark,
    getWeekName,
    parseMonth
} from "./employeeShiftUtil.js";

/**
 * 指定月の日付一覧を取得する
 */
function getMonthDates(monthValue){
    const parsed = parseMonth(monthValue);
    if(!parsed){
        return [];
    }

    const {year, month} = parsed;
    const lastDay = getLastDay(year, month);

    return getDateList(
        createDateString(year, month, 1),
        createDateString(year, month, lastDay)
    );
}

/**
 * 日付ヘッダー描画
 * datesを省略した場合は月間表示
 */
export function renderMonthHeader(monthValue, holidays = {}, dates = null){
    const header = document.getElementById("shift-table-header");
    if(!header || !monthValue){
        return;
    }

    const dateList = dates ?? getMonthDates(monthValue);
    header.replaceChildren();

    const tr = document.createElement("tr");
    const employeeTh = document.createElement("th");
    employeeTh.className = "shift-employee-column";
    tr.appendChild(employeeTh);

    dateList.forEach(dateString => {
        const date = new Date(`${dateString}T00:00:00`);
        const day = date.getDate();
        const weekDay = date.getDay();

        const th = document.createElement("th");
        th.dataset.date = dateString;

        const dayText = document.createElement("div");
        dayText.textContent = day;

        const weekText = document.createElement("small");
        weekText.textContent = getWeekName(weekDay);

        th.appendChild(dayText);
        th.appendChild(weekText);

        const holidayName = holidays[dateString];
        if(holidayName){
            th.classList.add("sunday");
            th.title = holidayName;
        }else if(weekDay === 0){
            th.classList.add("sunday");
        }else if(weekDay === 6){
            th.classList.add("saturday");
        }

        tr.appendChild(th);
    });

    appendSummaryHeader(tr);
    header.appendChild(tr);
}

/**
 * シフト日付セル
 * datesを省略した場合は月間表示
 */
export function appendShiftCells({tr, shiftMap, monthValue, holidays = {}, dates = null, onChange}){
    if(!tr || !monthValue){
        return;
    }

    const dateList = dates ?? getMonthDates(monthValue);

    dateList.forEach(dateString => {
        const date = new Date(`${dateString}T00:00:00`);
        const weekDay = date.getDay();

        const td = document.createElement("td");
        td.dataset.date = dateString;

        const holidayName = holidays[dateString];
        if(holidayName){
            td.classList.add("sunday");
            td.title = holidayName;
        }else if(weekDay === 0){
            td.classList.add("sunday");
        }else if(weekDay === 6){
            td.classList.add("saturday");
        }

        const button = document.createElement("button");
        button.type = "button";
        button.className = "shift-cell";
        button.dataset.date = dateString;
        button.dataset.employeeId = tr.dataset.employeeId;

        const key = `${tr.dataset.employeeId}:${dateString}`;
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
    });
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
    holidays = {},
    dates = null,
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
    appendShiftCells({tr, shiftMap, monthValue, holidays, dates, onChange: onShiftChange});
    appendShiftSummary(tr);

    body.appendChild(tr);
}