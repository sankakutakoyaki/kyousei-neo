"use strict"

import {
    initCommon
} from "../../../bootstrap/initPage.js";

import {
    initPageCache
} from "../../../bootstrap/initPageCache.js";

import {
    initCombo
} from "../../../bootstrap/initCombo.js";

import {
    EmployeeShiftRepository
} from "../../../repositories/operations/shift/EmployeeShiftRepository.js";

const changedShifts =
    new Map();

export async function init(){

    await initCommon();

    await initPageCache(
        "/api/employeeshift/init/cache"
    );

    const controller = {
        key: "employeeShift"
    };

    initCombo(
        controller
    );

    initMonth();

    renderMonthHeader();

    document
        .getElementById(
            "shift-month"
        )
        ?.addEventListener(
            "change",
            async () => {

                renderMonthHeader();

                const officeId =
                    Number(
                        document
                            .getElementById(
                                "shift-office"
                            )
                            ?.value
                    );

                await loadEmployees(
                    officeId
                );
            }
        );

    document
        .getElementById(
            "shift-office"
        )
        ?.addEventListener(
            "change",
            async (event) => {

                const officeId =
                    Number(
                        event.target.value
                    );

                await loadEmployees(
                    officeId
                );
            }
        );

        document
            .getElementById(
                "shift-save-btn"
            )
            ?.addEventListener(
                "click",
                async () => {
                    await saveShifts();
                }
            );
}

async function loadEmployees(
    officeId
){

    const body =
        document.getElementById(
            "shift-table-body"
        );

    if(!body){
        return;
    }

    body.replaceChildren();

    changedShifts.clear();
    updateSaveButton();

    if(!officeId){
        return;
    }

    const range =
        getMonthRange();

    if(!range){
        return;
    }

    const [
        employeeResult,
        shiftResult
    ] =
        await Promise.all([
            EmployeeShiftRepository
                .findEmployees({
                    state:
                        APP.cache.common
                            .state.INITIAL,
                    officeId
                }),

            EmployeeShiftRepository
                .findMonthList({
                    fromDate:
                        range.fromDate,
                    toDate:
                        range.toDate,
                    officeId,
                    state:
                        APP.cache.common
                            .state.INITIAL
                })
        ]);

    const employees =
        employeeResult.data ?? [];

    const shifts =
        shiftResult.data ?? [];

    const shiftMap =
        createShiftMap(
            shifts
        );

    employees.forEach(
        employee => {

            const tr =
                document.createElement(
                    "tr"
                );

            tr.dataset.employeeId =
                employee.employeeId;

            const th =
                document.createElement(
                    "th"
                );

            th.className =
                "shift-employee-column";

            const nameSpan =
                document.createElement(
                    "span"
                );

            nameSpan.textContent =
                employee.fullName;

            const toggleButton =
                document.createElement(
                    "button"
                );

            toggleButton.type =
                "button";

            toggleButton.className =
                "shift-row-btn";

            toggleButton.textContent =
                "全";

            toggleButton.title =
                "全日出勤 / 全クリア";

            toggleButton.addEventListener(
                "click",
                () => {
                    toggleRowShift(
                        tr
                    );
                }
            );

            const nameArea =
                document.createElement(
                    "div"
                );

            nameArea.className =
                "shift-employee-inner";

            nameArea.appendChild(
                nameSpan
            );

            nameArea.appendChild(
                toggleButton
            );

            th.appendChild(
                nameArea
            );

            tr.appendChild(
                th
            );

            appendShiftCells(
                tr,
                shiftMap
            );

            body.appendChild(
                tr
            );
        }
    );
}

function createShiftMap(
    shifts
){

    const map =
        new Map();

    shifts.forEach(
        shift => {

            const key =
                `${shift.employeeId}:${shift.workDate}`;

            map.set(
                key,
                shift
            );
        }
    );

    return map;
}

function initMonth(){

    const input =
        document.getElementById(
            "shift-month"
        );

    if(!input){
        return;
    }

    if(input.value){
        return;
    }

    const now =
        new Date();

    const year =
        now.getFullYear();

    const month =
        String(
            now.getMonth() + 1
        ).padStart(2, "0");

    input.value =
        `${year}-${month}`;
}

function renderMonthHeader(){

    const monthInput =
        document.getElementById(
            "shift-month"
        );

    const header =
        document.getElementById(
            "shift-table-header"
        );

    if(
        !monthInput ||
        !header ||
        !monthInput.value
    ){
        return;
    }

    header.replaceChildren();

    const [
        year,
        month
    ] =
        monthInput.value
            .split("-")
            .map(Number);

    const lastDay =
        new Date(
            year,
            month,
            0
        ).getDate();

    const tr =
        document.createElement("tr");


    // 従業員列
    const employeeTh =
        document.createElement("th");

    employeeTh.textContent =
        "従業員";

    employeeTh.className =
        "shift-employee-column";

    tr.appendChild(
        employeeTh
    );


    // 日付列
    for(
        let day = 1;
        day <= lastDay;
        day++
    ){

        const date =
            new Date(
                year,
                month - 1,
                day
            );

        const th =
            document.createElement("th");

        const dayText =
            document.createElement("div");

        dayText.textContent =
            day;

        const weekText =
            document.createElement("small");

        weekText.textContent =
            getWeekName(
                date.getDay()
            );

        th.appendChild(
            dayText
        );

        th.appendChild(
            weekText
        );

        const weekDay =
            date.getDay();

        if(weekDay === 0){
            th.classList.add(
                "sunday"
            );
        }

        if(weekDay === 6){
            th.classList.add(
                "saturday"
            );
        }

        tr.appendChild(
            th
        );
    }

    header.appendChild(
        tr
    );
}

function appendShiftCells(
    tr,
    shiftMap
){

    const monthInput =
        document.getElementById(
            "shift-month"
        );

    if(
        !monthInput ||
        !monthInput.value
    ){
        return;
    }

    const [
        year,
        month
    ] =
        monthInput.value
            .split("-")
            .map(Number);

    const lastDay =
        new Date(
            year,
            month,
            0
        ).getDate();

    for(
        let day = 1;
        day <= lastDay;
        day++
    ){

        const td =
            document.createElement(
                "td"
            );
            
        const dateObject =
            new Date(
                year,
                month - 1,
                day
            );

        const weekDay =
            dateObject.getDay();

        if(weekDay === 0){
            td.classList.add("sunday");
        }

        if(weekDay === 6){
            td.classList.add("saturday");
        }

        const button =
            document.createElement(
                "button"
            );

        button.type =
            "button";

        button.className =
            "shift-cell";

        const date =
            `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(2, "0")}`;

        button.dataset.date =
            date;

        button.dataset.employeeId =
            tr.dataset.employeeId;

        const key =
            `${tr.dataset.employeeId}:${date}`;

        const currentShift =
            shiftMap.get(key);

        const shiftType =
            Number(
                currentShift?.shiftType ?? 0
            );

        button.dataset.shiftType =
            String(
                shiftType
            );

        button.dataset.originalShiftType =
            String(shiftType);

        button.dataset.employeeShiftId =
            currentShift?.employeeShiftId ?? "";

        button.dataset.version =
            currentShift?.version ?? "";

        button.textContent =
            getShiftMark(
                shiftType
            );

        button.addEventListener(
            "click",
            () => {
                changeShiftType(
                    button
                );
            }
        );

        td.appendChild(
            button
        );

        tr.appendChild(
            td
        );
    }
}

function changeShiftType(
    button
){

    const current =
        Number(
            button.dataset.shiftType
        );

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

    button.dataset.shiftType =
        String(next);

    button.textContent =
        getShiftMark(next);

    markShiftChanged(
        button
    );
}

function getShiftMark(
    shiftType
){

    switch(
        Number(shiftType)
    ){

        case 1:
            return "出";

        case 2:
            return "休";

        case 3:
            return "有";

        default:
            return "--";
    }
}

function getMonthRange(){

    const value =
        document.getElementById(
            "shift-month"
        )?.value;

    if(!value){
        return null;
    }

    const [
        year,
        month
    ] =
        value
            .split("-")
            .map(Number);

    const fromDate =
        `${year}-${String(month).padStart(2, "0")}-01`;

    const next =
        new Date(
            year,
            month,
            1
        );

    const toDate =
        `${next.getFullYear()}-${String(
            next.getMonth() + 1
        ).padStart(2, "0")}-01`;

    return {
        fromDate,
        toDate
    };
}

function markShiftChanged(
    button
){

    const employeeId =
        Number(
            button.dataset.employeeId
        );

    const workDate =
        button.dataset.date;

    const shiftType =
        Number(
            button.dataset.shiftType
        );

    const originalShiftType =
        Number(
            button.dataset.originalShiftType ?? 0
        );

    const employeeShiftId =
        Number(
            button.dataset.employeeShiftId || 0
        );

    const version =
        Number(
            button.dataset.version || 0
        );

    if(
        !employeeId ||
        !workDate
    ){
        return;
    }

    const key =
        `${employeeId}:${workDate}`;

    // 元の状態に戻った
    if(
        shiftType === originalShiftType
    ){
        changedShifts.delete(
            key
        );

        button.classList.remove(
            "changed"
        );

        updateSaveButton();

        return;
    }

    changedShifts.set(
        key,
        {
            employeeShiftId:
                employeeShiftId || null,
            employeeId,
            workDate,
            shiftType,
            version
        }
    );

    button.classList.add(
        "changed"
    );

    updateSaveButton();
}

function setRowShift(
    tr,
    shiftType
){

    const buttons =
        tr.querySelectorAll(
            ".shift-cell"
        );

    buttons.forEach(
        button => {

            const current =
                Number(
                    button.dataset.shiftType
                );

            if(
                current === shiftType
            ){
                return;
            }

            button.dataset.shiftType =
                String(
                    shiftType
                );

            button.textContent =
                getShiftMark(
                    shiftType
                );

            markShiftChanged(
                button
            );
        }
    );
}

function toggleRowShift(
    tr
){

    const buttons =
        [
            ...tr.querySelectorAll(
                ".shift-cell"
            )
        ];

    if(
        buttons.length === 0
    ){
        return;
    }

    const allWorking =
        buttons.every(
            button =>
                Number(
                    button.dataset.shiftType
                ) === 1
        );

    setRowShift(
        tr,
        allWorking ? 0 : 1
    );
}

async function saveShifts(){

    if(
        changedShifts.size === 0
    ){
        return;
    }

    const officeId =
        Number(
            document
                .getElementById(
                    "shift-office"
                )
                ?.value
        );

    if(!officeId){
        return;
    }

    const rows =
        [...changedShifts.values()];

    for(
        const row of rows
    ){

        // 新規なのに -- の場合は保存不要
        if(
            !row.employeeShiftId &&
            row.shiftType === 0
        ){
            continue;
        }

        const payload = {
            employeeId:
                row.employeeId,

            workDate:
                row.workDate,

            officeId,

            shiftType:
                row.shiftType,

            state:
                row.shiftType === 0
                    ? APP.cache.common.state.DELETE
                    : APP.cache.common.state.INITIAL
        };


        // 既存データの更新
        if(row.employeeShiftId){

            payload.employeeShiftId =
                row.employeeShiftId;

            payload.version =
                row.version;
        }

        await EmployeeShiftRepository
            .save(
                payload
            );
    }


    changedShifts.clear();

    updateSaveButton();


    // 保存結果をDBから再取得
    await loadEmployees(
        officeId
    );
}

function updateSaveButton(){

    const button =
        document.getElementById(
            "shift-save-btn"
        );

    if(!button){
        return;
    }

    button.disabled =
        changedShifts.size === 0;
}

function getWeekName(day){

    return [
        "日",
        "月",
        "火",
        "水",
        "木",
        "金",
        "土"
    ][day];
}