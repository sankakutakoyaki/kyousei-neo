"use strict";

import { TimeworksRepository } from "../../../repositories/management/TimeworksRepository.js";
import { formatDate } from "../../../util/time.js";
import { openMsgDialog } from "../../../core/ui/dialog/dialogCore.js";
import { renderManagementList, renderManagementPeriod} from "./managementRenderer.js";

export function createManagement(session) {
    let employeeComboRequest = 0;
    let managementInitialized = false;

    const rowSaveTimers = new Map();
    const rowSaveRevisions = new WeakMap();
    const savedTimers = new Set();
    let managementRequest = 0;
    session.onDispose(() => {
        employeeComboRequest++;
        managementRequest++;
        savedTimers.forEach(timer => window.clearTimeout(timer));
        for(const [row, pending] of rowSaveTimers) {
            window.clearTimeout(pending.timer);
            enqueueSave(row, pending.payload, pending.revision);
        }
        rowSaveTimers.clear();
    });
    const rowSaveChains = new WeakMap();

    function initManagement() {
        if(!session.isActive() || managementInitialized){return;}
        const list = session.byId("management-list");
        if(!list){return;}
        managementInitialized = true;

        loadManagementEmployees().then(() => refreshManagement()).catch(showError);

        initManagementTableEvents();
    }

    async function loadManagementEmployees() {
        if(!session.isActive()) return;
        const requestId = ++employeeComboRequest;
        const employee = session.byId("management-employee");
        if(!employee){return;}

        const officeId = session.byId("management-office")?.value || null;
        employee.replaceChildren(new Option("読み込み中", ""));
        employee.disabled = true;
        try {
            const employees = await TimeworksRepository.findEmployeeCombo(officeId);
            if(!session.isActive() || requestId !== employeeComboRequest){return;}
            employee.replaceChildren(new Option("すべて", ""));
            employees.forEach(item => {employee.add(new Option(item.label, item.value));});
        }catch(error){
            if(!session.isActive() || requestId !== employeeComboRequest){return;}
            employee.replaceChildren(new Option("取得できませんでした", ""));
            openMsgDialog({
                message: error.message || "従業員を取得できませんでした。",
                color: "red"
            });
        }finally{
            if(session.isActive() && requestId === employeeComboRequest){employee.disabled = false;}
        }
    }

    function managementParams() {
        return {
            targetMonth: session.byId("management-month")?.value,
            closingType: session.byId("management-closing-type")?.value,
            officeId: session.byId("management-office")?.value || null,
            employeeId: session.byId("management-employee")?.value || null
        };
    }

    async function refreshManagement() {
        if(!session.isActive()) return;
        const request = ++managementRequest;
        try {
            const params = managementParams();
            const items = await TimeworksRepository.searchManagement(params);

            if(!session.isActive() || request !== managementRequest) return;
            renderManagementList(items);
            renderManagementPeriod(params);
        } catch (error) {
            if(!session.isActive() || request !== managementRequest) return;
            openMsgDialog({
                message: error.message || "勤怠データを取得できませんでした。",
                color: "red"
            });
        }
    }

    function scheduleManagementSave(row) {
        if(!session.isActive()) return;
        const current = rowSaveTimers.get(row);
        if(current) window.clearTimeout(current.timer);
        const payload = buildRowPayload(row);
        const revision = (rowSaveRevisions.get(row) ?? 0) + 1;
        rowSaveRevisions.set(row, revision);
        row.classList.remove("is-saved", "is-save-error");
        row.classList.add("is-saving");
        const timer = window.setTimeout(() => {
            rowSaveTimers.delete(row);
            enqueueSave(row, payload, revision);
        }, 400);
        rowSaveTimers.set(row, {timer, payload, revision});
    }

    function enqueueSave(row, payload, revision) {
        const previous = rowSaveChains.get(row) ?? Promise.resolve();
        const next = previous.catch(() => {}).then(() => saveManagementRow(row, payload, revision));
        rowSaveChains.set(row, next);
        // Save errors are reported by saveManagementRow, including after departure.
        next.catch(() => {});
    }

    function buildRowPayload(row) {
        const workDate = row.dataset.workDate;
        return {
            timeworkId: Number(row.dataset.timeworkId),
            timeworkEditId: row.dataset.timeworkEditId ? Number(row.dataset.timeworkEditId) : null,
            editStartTime: toDateTimeValue(workDate, row.querySelector('[name="editStartTime"]')?.value || ""),
            editEndTime: toDateTimeValue(workDate, row.querySelector('[name="editEndTime"]')?.value || "",
                row.querySelector('[name="endNextDay"]')?.checked === true)
        };
    }

    async function saveManagementRow(row, payload, revision) {
        const canRender = () => session.isActive() && row.isConnected
            && rowSaveRevisions.get(row) === revision;
        try {
            await TimeworksRepository.updateTimes(payload);
            if(!canRender()) return;
            row.classList.remove("is-saving", "is-save-error");
            row.classList.add("is-saved");
            const timer = window.setTimeout(() => {
                savedTimers.delete(timer);
                if(canRender()) row.classList.remove("is-saved");
            }, 1200);
            savedTimers.add(timer);
        } catch(error) {
            if(canRender()) {
                row.classList.remove("is-saving", "is-saved");
                row.classList.add("is-save-error");
            }
            openMsgDialog({message: error.message || `勤怠 ${payload.timeworkId} を保存できませんでした。`, color: "red"});
            throw error;
        }
    }

    function showError(error) {
        if(session.isActive()) openMsgDialog({message: error.message || "勤怠を取得できませんでした。", color: "red"});
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

    function downloadManagementCsv() {
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
        const list = session.byId("management-list");
        if(!list){return;}

        session.onDispose(() => {
            list.removeEventListener("change", handleManagementChange);
            list.removeEventListener("keydown", handleManagementKeydown);
        });
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
    return {initManagement, loadManagementEmployees, refreshManagement, downloadManagementCsv};
}
