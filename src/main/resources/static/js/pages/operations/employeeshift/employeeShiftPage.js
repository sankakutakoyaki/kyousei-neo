"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { initCombo } from "../../../bootstrap/initCombo.js";
import { EmployeeShiftRepository } from "../../../repositories/operations/shift/EmployeeShiftRepository.js";
import { DialogService } from "../../../core/ui/dialog/DialogService.js";
import { appendEmployeeRow, renderMonthHeader, updateRowSummary } from "./employeeShiftTable.js";
import { createDateString, getDateList, getMonthRange, getShiftMark, getWeekRange } from "./employeeShiftUtil.js";

const changedShifts = new Map();
let loadedEmployees = [];
let loadedShiftMap = new Map();
let loadedMonthValue = "";
let loadedHolidays = {};
const holidayCache = new Map();
// スマホで現在表示している週（0から開始）
let currentWeekIndex = 0;
// スマホの週表示専用データ
let mobileShiftMap = new Map();
let mobileHolidays = {};
// 現在の対象月
let previousMonth = "";
// 週間データ取得の競合防止
let mobileRequestId = 0;
// 月間データ取得の競合防止
let monthRequestId = 0;
// 従業員一覧取得の競合防止
let employeeRequestId = 0;
// 画面幅変更の監視
const mobileQuery = window.matchMedia("(max-width: 560px)");
// 未保存の変更がある月
let editingMonth = null;
// 未保存の変更がある営業所
let editingOfficeId = null;
// PC用月間データを取得した営業所
let loadedOfficeId = null;

/**
 * スマホで表示する7日間の日付一覧
 */
function getMobileWeekDates(){
    const monthValue = document.getElementById("shift-month")?.value;
    const range = getWeekRange(monthValue, currentWeekIndex);
    return range ? getDateList(range.fromDate, range.toDate) : [];
}

/**
 * スマホの週表示期間を更新する
 */
function updateMobileWeekPeriod(){
    const period = document.getElementById("shift-week-period");
    const monthValue = document.getElementById("shift-month")?.value;
    const range = getWeekRange(monthValue, currentWeekIndex);
    if(!period || !range){
        return;
    }

    const start = new Date(`${range.fromDate}T00:00:00`);
    const end = new Date(`${range.toDate}T00:00:00`);
    const weekNames = ["日", "月", "火", "水", "木", "金", "土"];

    period.textContent =
        `${start.getMonth() + 1}/${start.getDate()}（${weekNames[start.getDay()]}）` +
        `〜${end.getMonth() + 1}/${end.getDate()}（${weekNames[end.getDay()]}）`;
}

/**
 * シフト画面の状態を初期化する
 * ページ再表示時に前回の状態を引き継がない
 */
function resetPageState(){
    // 未保存の編集情報
    changedShifts.clear();
    editingMonth = null;
    editingOfficeId = null;

    // 月間表示用データ
    loadedEmployees = [];
    loadedShiftMap = new Map();
    loadedMonthValue = "";
    loadedOfficeId = null;
    loadedHolidays = {};

    // スマホの週間表示用データ
    currentWeekIndex = 0;
    mobileShiftMap = new Map();
    mobileHolidays = {};

    // 月選択の状態
    previousMonth = "";

    // 以前の通信結果を無効化
    mobileRequestId++;
    monthRequestId++;
    employeeRequestId++;
}

export async function init(){
    // ページ再表示時に前回の状態を初期化
    resetPageState();

    await initCommon();
    await initPageCache("/api/employeeshift/init/cache");

    const controller = {key: "employeeShift"};
    initCombo(controller);
    initMonth();
    const initialMonth = document.getElementById("shift-month")?.value;
    loadedHolidays = await loadHolidays(initialMonth);
    renderMonthHeader(initialMonth, loadedHolidays);

    const monthInput = document.getElementById("shift-month");
    previousMonth = monthInput?.value ?? "";
    // 対象月の変更
    monthInput?.addEventListener("change", async event => {
        const nextMonth = event.target.value;

        if(!await confirmDiscardChanges()){
            event.target.value = previousMonth;
            return;
        }

        // 確認後に未保存の変更を破棄
        clearChangedShifts();
        await changeTargetMonth(nextMonth);
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
        clearChangedShifts();
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
        clearChangedShifts();
        renderEmployees();
    });

    document.getElementById("shift-save-btn")?.addEventListener("click", async () => {
        await saveShifts();
    });

    const officeId = Number(document.getElementById("shift-office")?.value);
    if(officeId){
        await loadEmployees(officeId);
    }else{
        await applyMobileWeek();
    }

    // PC・スマホの表示切り替えに対応
    mobileQuery.removeEventListener("change", applyMobileWeek);
    mobileQuery.addEventListener("change", applyMobileWeek);

    // スマホの週移動操作を初期化
    initMobileWeekSwipe();

    // 週移動ボタン
    document.querySelector('[data-action="previous-week"]')?.addEventListener("click", () => moveMobileWeek(-1));
    document.querySelector('[data-action="next-week"]')?.addEventListener("click", () => moveMobileWeek(1));
}

/**
 * 対象月の変更を反映する
 * resetWeek: true=第1週へ戻す、false=現在の週を維持
 */
async function changeTargetMonth(nextMonth, resetWeek = true){
    const monthInput = document.getElementById("shift-month");
    if(!monthInput || !nextMonth){
        return;
    }

    // 対象月と週位置を更新
    monthInput.value = nextMonth;
    previousMonth = nextMonth;
    if(resetWeek){
        currentWeekIndex = 0;
    }

    const isMobile = mobileQuery.matches;

    if(isMobile){
        // スマホでは月間データを取得せず、週間データだけ更新
        await applyMobileWeek();
    }else{
        // PCでは月間シフトと祝日をまとめて取得
        const officeId = Number(document.getElementById("shift-office")?.value);
        await loadEmployees(officeId);
    }
}

/**
 * 指定営業所の従業員一覧を取得する
 * 古い通信結果は反映しない
 */
async function loadEmployeeList(officeId){
    const requestId = ++employeeRequestId;

    if(!officeId){
        loadedEmployees = [];
        return false;
    }

    const result = await EmployeeShiftRepository.findEmployees({
        state: APP.cache.common.state.INITIAL,
        officeId,
        ownCompanyCategory: APP.cache.common.companyCategory.OWN,
        fulltimeCategory: APP.cache.common.employeeCategory.FULLTIME,
        parttimeCategory: APP.cache.common.employeeCategory.PARTTIME
    });

    if(requestId !== employeeRequestId){
        return false;
    }

    loadedEmployees = result.data ?? [];
    return true;
}


/**
 * PC用の月間シフトを取得する
 * 古い通信結果は反映しない
 */
async function loadMonthShifts(officeId){
    const requestId = ++monthRequestId;
    const monthValue = document.getElementById("shift-month")?.value;
    const range = getMonthRange(monthValue);

    if(!officeId || !range){
        loadedShiftMap = new Map();
        loadedMonthValue = monthValue ?? "";
        return false;
    }

    // 月間シフトと対象年の祝日を並行取得
    const [result, holidays] = await Promise.all([
        EmployeeShiftRepository.findMonthList({
            fromDate: range.fromDate,
            toDate: range.toDate,
            officeId,
            state: APP.cache.common.state.INITIAL
        }),
        loadHolidays(monthValue)
    ]);

    if(requestId !== monthRequestId){
        return false;
    }

    loadedShiftMap = createShiftMap(result.data ?? []);
    loadedHolidays = holidays;
    loadedMonthValue = monthValue;
    loadedOfficeId = officeId;
    return true;
}

/**
 * 営業所に応じて従業員とシフトを取得する
 * PC：従業員一覧＋月間シフト
 * スマホ：従業員一覧＋週間シフト
 */
async function loadEmployees(officeId){
    const body = document.getElementById("shift-table-body");
    if(!body){
        return;
    }

    // 再取得時は未保存の変更を維持する
    body.replaceChildren();

    const isMobile = mobileQuery.matches;

    if(isMobile){
        // スマホでは従業員一覧を取得してから週間シフトを取得
        monthRequestId++;

        const updated = await loadEmployeeList(officeId);
        if(!updated){
            return;
        }

        await applyMobileWeek();
    }else{
        // PCでは従業員一覧と月間シフトを並行取得
        const [employeesUpdated, shiftsUpdated] = await Promise.all([
            loadEmployeeList(officeId),
            loadMonthShifts(officeId)
        ]);

        if(employeesUpdated && shiftsUpdated){
            renderEmployees();
        }
    }
}

/**
 * スマホの週表示に必要なシフト・祝日を取得する
 * 古い通信結果は画面に反映しない
 */
async function loadMobileWeekData(){
    const requestId = ++mobileRequestId;
    const monthValue = document.getElementById("shift-month")?.value;
    const officeId = Number(document.getElementById("shift-office")?.value);
    const range = getWeekRange(monthValue, currentWeekIndex);
    if(!range || !officeId){
        mobileShiftMap = new Map();
        mobileHolidays = {};
        return false;
    }

    // SQLの終了日は含まれないため、翌日を指定
    const end = new Date(`${range.toDate}T00:00:00`);
    end.setDate(end.getDate() + 1);
    const toDate = createDateString(
        end.getFullYear(), end.getMonth() + 1, end.getDate()
    );

    // シフトと祝日を並行して取得
    const years = [...new Set(
        getDateList(range.fromDate, range.toDate).map(date => date.slice(0, 4))
    )];

    const [result, ...holidayResults] = await Promise.all([
        EmployeeShiftRepository.findMonthList({
            fromDate: range.fromDate,
            toDate,
            officeId,
            state: APP.cache.common.state.INITIAL
        }),
        ...years.map(year => loadHolidays(`${year}-01`))
    ]);

    // より新しい取得処理が開始されていたら破棄
    if(requestId !== mobileRequestId){
        return false;
    }

    mobileShiftMap = createShiftMap(result.data ?? []);
    mobileHolidays = Object.assign({}, ...holidayResults);
    return true;
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

async function loadHolidays(monthValue){
    if(!monthValue){
        return {};
    }
    const year = Number(monthValue.slice(0, 4));
    if(!Number.isInteger(year) || year < 1955 || year > 2100){
        return {};
    }
    if(!holidayCache.has(year)){
        const response = await fetch(`/api/employeeshift/holidays?year=${year}`);
        if(!response.ok){
            throw new Error(`祝日データの取得に失敗しました: ${response.status}`);
        }
        holidayCache.set(year, await response.json());
    }
    return holidayCache.get(year);
}

/**
 * シフト変更
 * 通常画面では「未設定 → 出勤 → 休日」のみ切り替える
 * 有給は専用登録フォームで管理する
 */
function changeShiftType(button){
    // スマホでは編集不可
    if(mobileQuery.matches){
        return;
    }

    const current = Number(button.dataset.shiftType);

    // 有給など、専用フォームで管理する区分は変更しない
    if(current > 2){
        return;
    }

    const next = current === 0 ? 1 : current === 1 ? 2 : 0;
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

        // 未保存の変更がなくなったら編集情報を解除
        if(changedShifts.size === 0){
            editingMonth = null;
            editingOfficeId = null;
        }

        updateSaveButton();
        return;
    }
    // 最初の変更時に編集対象月・営業所を記憶する
    if(changedShifts.size === 0){
        editingMonth = workDate.slice(0, 7);
        editingOfficeId = Number(document.getElementById("shift-office")?.value);
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

/**
 * 再描画後に未保存のシフト変更を復元する
 */
function restoreChangedShifts(){
    document.querySelectorAll("#shift-table-body .shift-cell").forEach(button => {
        const key = `${button.dataset.employeeId}:${button.dataset.date}`;
        const changed = changedShifts.get(key);
        if(!changed){
            return;
        }

        button.dataset.shiftType = String(changed.shiftType);
        button.textContent = getShiftMark(changed.shiftType);
        button.classList.add("changed");
    });

    // 復元後に各行の集計を更新
    document.querySelectorAll("#shift-table-body tr[data-employee-id]").forEach(tr => {
        updateRowSummary(tr);
    });
}

function setRowShift(tr, shiftType){
    const buttons = tr.querySelectorAll(".shift-cell");
    buttons.forEach(button => {
        const current = Number(button.dataset.shiftType);
        // 有給などの専用登録区分は一括変更しない
        if(current > 2 || current === shiftType){
            return;
        }
        button.dataset.shiftType = String(shiftType);
        button.textContent = getShiftMark(shiftType);
        markShiftChanged(button);
    });
    updateRowSummary(tr);
}

/**
 * 行単位で出勤・未設定を切り替える
 * 有給などの専用登録区分は判定対象から除外する
 */
function toggleRowShift(tr){
    const buttons = [...tr.querySelectorAll(".shift-cell")]
        .filter(button => Number(button.dataset.shiftType) <= 2);

    if(buttons.length === 0){
        return;
    }

    const allWorking = buttons.every(button =>
        Number(button.dataset.shiftType) === 1
    );

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

    // 編集開始時と異なる営業所への保存を防止
    if(editingOfficeId !== null && officeId !== editingOfficeId){
        DialogService.error("編集中に営業所が変更されています。元の営業所に戻してから保存してください。");
        return;
    }
    const rows = [...changedShifts.values()].filter(row =>
        row.employeeShiftId || row.shiftType !== 0).map(row => {
            const item = {
                employeeId: row.employeeId,
                workDate: row.workDate,
                officeId,
                shiftType: row.shiftType,
                state: row.shiftType === 0 ? APP.cache.common.state.DELETE : APP.cache.common.state.INITIAL,
                // 更新時のみIDとバージョンを追加
                ...(row.employeeShiftId ? {
                    employeeShiftId: row.employeeShiftId,
                    version: row.version
                } : {})
            };
            return item;
        }
    );
    if(rows.length === 0){
        clearChangedShifts();
        return;
    }
    await EmployeeShiftRepository.batchSave({rows});
    clearChangedShifts();
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

/**
 * 未保存のシフト変更を破棄する
 */
function clearChangedShifts(){
    changedShifts.clear();
    editingMonth = null;
    editingOfficeId = null;
    updateSaveButton();
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

    // スマホでは7日分、PCでは従来どおり月間表示
    const isMobile = mobileQuery.matches;
    const dates = isMobile ? getMobileWeekDates() : null;
    // PCとスマホで使用するデータを切り替える
    const shiftMap = isMobile ? mobileShiftMap : loadedShiftMap;
    const holidays = isMobile ? mobileHolidays : loadedHolidays;

    // 表示期間に合わせてヘッダーを描画
    renderMonthHeader(
        isMobile ? document.getElementById("shift-month")?.value : loadedMonthValue,
        holidays,
        dates
    );

    employees.forEach(employee => {
        if(previousCompanyId !== employee.companyId){
            appendCompanyHeader(body, employee.companyCategory, employee.companyName);
            previousCompanyId = employee.companyId;
        }
        appendEmployeeRow({
            body,
            employee,
            shiftMap,
            monthValue: isMobile
                ? document.getElementById("shift-month")?.value
                : loadedMonthValue,
            holidays,
            dates,
            onShiftChange: changeShiftType,
            onRowToggle: toggleRowShift
        });
    });
    
    // 未保存の変更を再描画後に復元
    restoreChangedShifts();

    // スマホの表示期間を更新
    if(isMobile){
        updateMobileWeekPeriod();
    }
}

function appendCompanyHeader(body, companyCategory, companyName){
    const tr = document.createElement("tr");
    tr.className = "shift-company-row";
    const th = document.createElement("th");
    th.className = "shift-company-header";
    th.colSpan = document.querySelectorAll("#shift-table-header tr > th").length;
    tr.appendChild(th);
    body.appendChild(tr);
}

/**
 * PC・スマホの表示切り替え
 */
async function applyMobileWeek(){
    // シフト画面以外では処理しない
    if(!document.getElementById("employee-shift-page")){
        return;
    }

    const isMobile = mobileQuery.matches;

    if(isMobile){
        // PC側で進行中の月間データ取得を無効化
        monthRequestId++;
        // スマホでは週間データを取得
        const updated = await loadMobileWeekData();
        if(!updated){
            return;
        }
    }else{
        // PC表示では進行中の週間データ取得を無効化
        mobileRequestId++;

        const monthInput = document.getElementById("shift-month");
        const officeId = Number(document.getElementById("shift-office")?.value);

        // 未保存の変更がある場合は編集していた月に戻す
        if(changedShifts.size > 0 && editingMonth){
            monthInput.value = editingMonth;
            previousMonth = editingMonth;
        }

        // 対象月または営業所が異なる場合は月間データを再取得
        if(monthInput.value !== loadedMonthValue || officeId !== loadedOfficeId){
            await loadEmployees(officeId);
            return;
        }
    }

    renderEmployees();
}

/**
 * スマホの表示週を移動する
 * direction: 1=翌週、-1=前週
 */
async function moveMobileWeek(direction){
    const monthValue = document.getElementById("shift-month")?.value;
    const range = getWeekRange(monthValue, currentWeekIndex + direction);
    if(!range){
        return;
    }

    // 表示週の木曜日を基準に対象月を判断
    const thursday = new Date(`${range.fromDate}T00:00:00`);
    thursday.setDate(thursday.getDate() + 4);

    const nextMonth = createDateString(
        thursday.getFullYear(),
        thursday.getMonth() + 1,
        1
    ).slice(0, 7);

    // 新しい対象月を基準に週番号を計算
    const firstWeek = getWeekRange(nextMonth, 0);
    const currentStart = new Date(`${range.fromDate}T00:00:00`);
    const firstStart = new Date(`${firstWeek.fromDate}T00:00:00`);

    currentWeekIndex = Math.round(
        (currentStart - firstStart) / (7 * 24 * 60 * 60 * 1000)
    );

    if(nextMonth !== monthValue){
        // 月が変わった場合はPC用データも更新
        await changeTargetMonth(nextMonth, false);
    }else{
        // 同じ月なら週データだけ更新
        await applyMobileWeek();
    }
}

/**
 * スマホの左右フリックで週を切り替える
 * 縦スクロールは通常どおり使用できる
 */
function initMobileWeekSwipe(){
    const wrapper = document.querySelector("#employee-shift-page > .table-wrapper");
    if(!wrapper){
        return;
    }

    let startX = 0;
    let startY = 0;

    wrapper.addEventListener("touchstart", event => {
        if(!mobileQuery.matches){
            return;
        }
        if(event.touches.length !== 1){
            return;
        }
        startX = event.touches[0].clientX;
        startY = event.touches[0].clientY;
    }, {passive: true});

    wrapper.addEventListener("touchend", event => {
        if(!mobileQuery.matches){
            return;
        }
        if(event.changedTouches.length !== 1){
            return;
        }

        const diffX = event.changedTouches[0].clientX - startX;
        const diffY = event.changedTouches[0].clientY - startY;

        // 横方向に50px以上、かつ縦方向より大きく動いた場合だけ週移動
        if(Math.abs(diffX) < 50 || Math.abs(diffX) <= Math.abs(diffY)){
            return;
        }

        // 左フリックで翌週、右フリックで前週
        moveMobileWeek(diffX < 0 ? 1 : -1);
    }, {passive: true});
}