"use strict";
import { initCommon } from "../../../bootstrap/initPage.js";
import { RequestClient } from "../../../core/api/RequestClient.js";
import { FormController } from "../../../application/FormController.js";
import { DialogService } from "../../../core/ui/dialog/DialogService.js";
import { isMobileDevice, isDispatchManager, refreshMobileReadOnly } from "../../../core/access/mobileReadOnly.js";

const query = async (queryId, params = {}) => RequestClient.request({queryId, params});
const text = (tag, value) => { const el = document.createElement(tag); el.textContent = value ?? ""; return el; };
const roleName = role => role === "DELIVERY" ? "配送" : "工事";
export async function init() {
    await initCommon();
    const search = document.getElementById("dispatch-search");
    const formEl = document.getElementById("dispatch-form");
    const members = document.getElementById("dispatch-members");
    const status = document.getElementById("dispatch-status");
    status.setAttribute("role", "status");
    for (const [value, label] of [["all","すべて"], ["none","両方未割り当て"], ["delivery","配送未割り当て"], ["install","工事未割り当て"]]) {
        const option = text("option", label); option.value = value;
        search.elements.assignmentFilter.append(option);
    }
    const heading = text("tr", ""); heading.setAttribute("name", "table-header");
    for (const title of ["受注番号・件名", "訪問日・時間", "荷主", "住所", "配送担当", "工事担当", "操作"]) {
        const cell = text("th", title); cell.scope = "col"; heading.append(cell);
    }
    document.getElementById("dispatch-table-header").replaceChildren(heading);
    let current = null, original = "", rows = [], generation = 0;
    const editable = () => Number(current?.state) === 0 && (!isMobileDevice() || isDispatchManager());
    const selections = () => [...members.querySelectorAll("input:checked")].map(el => ({employeeId:Number(el.value), role:el.dataset.role}));
    const signature = () => JSON.stringify(selections());
    const renderMembers = (data, employees) => {
        members.replaceChildren();
        for (const role of ["DELIVERY", "INSTALL"]) {
            const fieldset = document.createElement("fieldset");
            fieldset.append(text("legend", roleName(role)));
            const selected = data.assignments.filter(a => a.role === role);
            const options = [...employees];
            for (const a of selected) if (!options.some(e => Number(e.employeeId) === Number(a.employeeId)))
                options.push({employeeId:a.employeeId, fullName:a.employeeName + "（無効・解除のみ）", inactive:true});
            for (const e of options) {
                const label = text("label", "");
                const input = document.createElement("input");
                input.type = "checkbox"; input.value = e.employeeId; input.dataset.role = role;
                input.checked = selected.some(a => Number(a.employeeId) === Number(e.employeeId));
                input.disabled = Number(data.state) !== 0;
                // 無効な従業員の新規選択はAPIでも拒否される。
                label.append(input, text("span", `${e.fullName} ${e.companyName ?? ""}`));
                fieldset.append(label);
            }
            if (!options.length) fieldset.append(text("p", "割り当て可能な従業員がありません。"));
            members.append(fieldset);
        }
        original = signature();
    };
    let employees = [];
    const form = new FormController({
        formId:"dispatch-form", key:"orderId", idKey:"orderId",
        changeTargetSelector:"#dispatch-members",
        controller:{key:"dispatch", setBulkMode() {}},
        submitText:"保存", cancelText:"閉じる",
        hasAdditionalChanges:() => editable() && signature() !== original,
        buildAdditionalPayload:() => ({assignments:selections(), dispatchVersion:current.dispatchVersion, mobile:isMobileDevice()}),
        resetAdditional:() => { if(current) renderMembers(current, employees); },
        onOpen:async data => {
            current = data;
            employees = (await query("dispatchEmployeeList")).data ?? [];
            document.getElementById("dispatch-order-summary").textContent = `${data.requestNumber ?? ""} ${data.title ?? ""} ／ ${data.visitDate ?? "日程未定"} ${data.visitTime ?? ""}`;
            document.getElementById("dispatch-readonly").hidden = editable();
            renderMembers(data, employees);
            const history = document.getElementById("dispatch-history");
            history.replaceChildren(...data.history.map(a => text("p", `${roleName(a.role)}：${a.employeeName} ／ 登録 ${a.assignedAt} (${a.assignedBy})${a.cancelledAt ? ` ／ 解除 ${a.cancelledAt} (${a.cancelledBy})` : ""}`)));
            if (!data.history.length) history.append(text("p", "履歴はありません。"));
        },
        saveHandler:params => {
            if (!editable()) throw new Error("配車を編集できません。");
            return query("dispatchSave", params);
        },
        afterSave:() => load()
    });
    form.confirmSave = () => DialogService.confirm("配送・工事担当の割り当てを保存しますか？");
    const render = () => {
        const term = search.elements.keyword.value.trim().toLowerCase();
        const filter = search.elements.assignmentFilter.value;
        const filtered = rows.filter(r => (filter !== "none" || (!r.deliveryNames && !r.installNames))
            && (filter !== "delivery" || !r.deliveryNames) && (filter !== "install" || !r.installNames)
            && [r.requestNumber,r.title,r.fullAddress,r.companyName,r.deliveryNames,r.installNames].join(" ").toLowerCase().includes(term));
        const container = document.getElementById("dispatch-results");
        container.replaceChildren();
        for (const row of filtered) {
            const card = text("tr", ""); card.setAttribute("name", "data-row");
            const values = [
                ["受注番号・件名", `${row.requestNumber ?? ""} ${row.title ?? ""}`],
                ["訪問日・時間", `${row.visitDate ?? "日程未定"} ${row.visitTime ?? ""}${Number(row.state) === 2 ? "（完了）" : ""}`],
                ["荷主", row.companyName], ["住所", row.fullAddress],
                ["配送担当", row.deliveryNames || "未割り当て"], ["工事担当", row.installNames || "未割り当て"]
            ];
            for (const [label, value] of values) {
                const cell = text("td", value); cell.dataset.label = label; card.append(cell);
            }
            const button = text("button", "担当者・履歴を開く"); button.type = "button"; button.className = "normal-btn";
            button.addEventListener("click", async () => {
                button.disabled = true;
                try { await form.open((await query("dispatchDetail", {orderId:row.orderId})).data); refreshMobileReadOnly(); }
                catch(e) { DialogService.error(e.message); } finally { button.disabled = false; }
            });
            const actions = text("td", ""); actions.append(button); card.append(actions); container.append(card);
        }
        status.textContent = `${filtered.length}件${filtered.length ? "" : "：該当する受注はありません。"}`;
    };
    const load = async () => {
        const request = ++generation;
        status.textContent = "読み込み中…";
        try {
            const result = await query("dispatchList", {dateFrom:search.elements.dateFrom.value, dateTo:search.elements.dateTo.value, includeUndated:search.elements.includeUndated.checked});
            if (request !== generation || !search.isConnected) return;
            rows = result.data ?? []; render();
        } catch(e) { if(request === generation) { status.textContent = "読み込みに失敗しました。"; DialogService.error(e.message); } }
    };
    formEl.addEventListener("submit", e => { e.preventDefault(); form.save(formEl); });
    search.addEventListener("submit", e => { e.preventDefault(); load(); });
    search.elements.keyword.addEventListener("input", render);
    search.elements.assignmentFilter.addEventListener("change", render);
    const today = new Date();
    const localDate = `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,"0")}-${String(today.getDate()).padStart(2,"0")}`;
    search.elements.dateFrom.value = localDate; search.elements.dateTo.value = localDate;
    await load();
}
