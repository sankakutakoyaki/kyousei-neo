"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { createVehicleColumns } from "./columns.js";
import { VehicleRepository } from "../../../repositories/operations/vehicle/VehicleRepository.js";
import { createMasterPage } from "../../../core/page/createMasterPage.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { registerController, getController } from "../../../application/controllerRegistry.js";
import { VehicleDefaultMemberRepository } from "../../../repositories/operations/vehicle/VehicleDefaultMemberRepository.js";
import { DialogService } from "../../../core/ui/dialog/DialogService.js";

// 車両登録フォーム専用の基本乗務員削除情報
let deletedVehicleFormMembers = [];
// 車両フォームを開いた時点の基本配車区分
let initialVehicleDispatchCategory = 0;

export async function init() {
    await initCommon();
    await initPageCache("/api/vehicle/init/cache");
    initFirstRegistrationSelects();
    // 車両フォームで基本乗務員を追加したら保存ボタンを更新
    document.getElementById("vehicle-member-add-btn")?.addEventListener("click", () => {
        addDefaultMemberDraft();
        getController("vehicle")?.getActiveForm()?.updateSubmitState();
    });
    const vehicle = vehiclePage();
    registerController("vehicle", vehicle);
    vehicle.init();
}

export const vehiclePage = () =>
    createMasterPage({
        // 車両ページの識別子
        key: "vehicle",
        // 車両情報の保存先を統合保存Handlerへ変更
        saveHandler: params => VehicleRepository.saveSettings(params),
        buildAdditionalPayload: buildVehicleSettingsPayload,
        // 新規登録では車両の必須入力が完了している場合だけ追加変更を許可
        hasAdditionalChanges: () => {
            const changed = hasVehicleSettingsChanges();
            const form = getController("vehicle")?.getActiveForm();
            if(!form || form.currentEntity?.vehicleId) return changed;
            return changed && form.hasValidInput();
        },
        // 車両登録フォームを開いた際に基本配車設定を読み込む
        onOpen: async data => {
            await loadVehicleDispatchSettings(data);
        },
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        idKey: "vehicleId",
        repository: VehicleRepository,
        columns: createVehicleColumns(),
        components: {combo: true},
        buildDetailParams: (id) => ({state: APP.cache.common.state.INITIAL, vehicleId: id}),
        model: {
            filters: {
                officeId: filterFactory.equals("officeId")
            }
        }
    });

/**
 * 基本乗務員一覧を取得して指定エリアに表示する
 */
// 車両登録フォームの基本乗務員一覧を取得
async function loadDefaultMembers(vehicleId, dispatchCategory){
    const area = document.getElementById("vehicle-form-member-area");
    if(!area){
        return;
    }
    area.replaceChildren();

    // 基本乗務員を取得できない場合も未設定メッセージを表示
    if(!vehicleId || !dispatchCategory){
        const empty = document.createElement("div");
        empty.className = "vehicle-default-member-empty";
        empty.textContent = "基本乗務員は未設定です";
        area.appendChild(empty);
        return;
    }
    const result = await VehicleDefaultMemberRepository.findList({
        vehicleId,
        dispatchCategory,
        state:
            APP.cache.common.state.INITIAL
    });
    const rows = result.data ?? [];
    if(rows.length === 0){
        const empty = document.createElement("div");
        empty.className = "vehicle-default-member-empty";
        empty.textContent = "基本乗務員は未設定です";
        area.appendChild(empty);
        return;
    }
    rows.forEach(row => {
        const item = createDefaultMemberItem({
            vehicleDefaultMemberId: row.vehicleDefaultMemberId,
            employeeId: row.employeeId,
            employeeName: row.employeeName ?? "",
            role: row.role,
            version: row.version,
            isNew: false
        });
        area.appendChild(item);
    });
}

/**
 * 車両登録フォームの基本配車設定を読み込む
 */
async function loadVehicleDispatchSettings(data){
    // 前回の変更情報を初期化
    deletedVehicleFormMembers = [];
    initialVehicleDispatchCategory = 0;

    const categorySelect = document.getElementById("vehicle-dispatch-category");
    const area = document.getElementById("vehicle-form-member-area");
    if(!categorySelect || !area) return;

    categorySelect.value = "";
    area.replaceChildren();

    // フォームを開き直した際に、未確定の乗務員入力を消去
    for(const id of ["vehicle-member-code", "vehicle-member-name",
                    "vehicle-member-employee-id", "vehicle-member-role"]){
        const el = document.getElementById(id);
        if(!el) continue;
        el.value = "";
        if(id === "vehicle-member-code") delete el.dataset.lastId;
    }

    const vehicleId = Number(data?.vehicleId || 0);
    if(!vehicleId) return;

    // 登録済みの基本配車区分を取得
    const result = await VehicleDefaultMemberRepository.findDispatchCategory({
        vehicleId,
        state: APP.cache.common.state.INITIAL
    });

    const currentCategory = Number(result.data?.[0]?.dispatchCategory || 0);
    initialVehicleDispatchCategory = currentCategory;
    categorySelect.value = currentCategory ? String(currentCategory) : "";

    // 登録済みの基本乗務員を表示
    await loadDefaultMembers(vehicleId, currentCategory);
}

/**
 * 車両登録フォームに基本乗務員を追加する
 */
function addDefaultMemberDraft(){
    const employeeIdInput = document.getElementById("vehicle-member-employee-id");
    const employeeNameInput = document.getElementById("vehicle-member-name");
    const employeeCodeInput = document.getElementById("vehicle-member-code");
    const roleInput = document.getElementById("vehicle-member-role");
    const area = document.getElementById("vehicle-form-member-area");

    if(!employeeIdInput || !employeeNameInput || !roleInput || !area) return;

    const employeeId = Number(employeeIdInput.value);
    const employeeName = employeeNameInput.value.trim();
    const role = Number(roleInput.value);
    if(!employeeId || !employeeName || !role) return;

    // 同じ従業員の二重追加を防止
    if(area.querySelector(`[data-employee-id="${employeeId}"]`)) return;

    area.querySelector(".vehicle-default-member-empty")?.remove();
    area.appendChild(createDefaultMemberItem({
        employeeId, employeeName, role, isNew: true
    }));

    // 追加後の入力欄を初期化
    employeeIdInput.value = "";
    employeeNameInput.value = "";
    roleInput.value = "";
    if(employeeCodeInput){
        employeeCodeInput.value = "";
        delete employeeCodeInput.dataset.lastId;
        employeeCodeInput.focus();
    }
}

function createDefaultMemberItem({
    vehicleDefaultMemberId = null,
    employeeId,
    employeeName,
    role,
    version = 0,
    isNew = false
}){
    const item = document.createElement("div");
    item.className = "vehicle-default-member-item button-hover-group";
    item.dataset.employeeId = employeeId;
    item.dataset.role = role;
    item.dataset.version = version ?? 0;

    if(vehicleDefaultMemberId){
        item.dataset.vehicleDefaultMemberId = vehicleDefaultMemberId;
    }
    if(isNew){
        item.dataset.new = "true";
    }
    // 基本乗務員をカード形式で表示
    const roleName = Number(role) === 1 ? "担当" : "助手";
    const text = document.createElement("div");
    text.className = "vehicle-member-info";

    const nameEl = document.createElement("span");
    nameEl.className = "vehicle-member-name";
    nameEl.textContent = employeeName;

    const detailEl = document.createElement("span");
    detailEl.className = "vehicle-member-role";
    detailEl.textContent = roleName;

    text.append(nameEl, detailEl);
    const removeButton = document.createElement("button");

    removeButton.type = "button";
    removeButton.className = "button-icon-remove";
    const delIcon = document.createElement("img");
    delIcon.src = "/icons/close-s.png";
    delIcon.alt = "";
    removeButton.appendChild(delIcon);
    removeButton.title = "この担当者を基本乗務員から削除";
    removeButton.onclick = () => {
        if(item.dataset.vehicleDefaultMemberId && item.dataset.new !== "true"){
            // 登録済み乗務員の削除情報を保持
            deletedVehicleFormMembers.push({
                vehicleDefaultMemberId: Number(item.dataset.vehicleDefaultMemberId),
                version: Number(item.dataset.version)
            });
        }
        item.remove();

        // 最後の乗務員を削除した場合は未設定メッセージを表示
        const area = document.getElementById("vehicle-form-member-area");
        if(area && !area.querySelector(".vehicle-default-member-item")){
            const empty = document.createElement("div");
            empty.className = "vehicle-default-member-empty";
            empty.textContent = "基本乗務員は未設定です";
            area.appendChild(empty);
        }

        const controller = getController("vehicle");
        controller?.getActiveForm()?.updateSubmitState();
    };

    item.appendChild(text);
    item.appendChild(removeButton);

    return item;
}

/**
 * 車両登録フォームの基本配車設定を保存用データにまとめる
 */
function buildVehicleSettingsPayload(){
    const dispatchCategory = Number(document.getElementById("vehicle-dispatch-category")?.value || 0);
    const area = document.getElementById("vehicle-form-member-area");
    const members = [...(area?.querySelectorAll(".vehicle-default-member-item") ?? [])]
        .map((item, index) => ({
            vehicleDefaultMemberId: item.dataset.vehicleDefaultMemberId ? Number(item.dataset.vehicleDefaultMemberId) : null,
            employeeId: Number(item.dataset.employeeId),
            role: Number(item.dataset.role),
            displayOrder: index + 1,
            version: Number(item.dataset.version ?? 0),
            // 新規追加した基本乗務員かどうかを送信
            isNew: item.dataset.new === "true",
            state: APP.cache.common.state.INITIAL
        }));

    return {
        dispatchCategory,
        members,
        deletedMembers: [...deletedVehicleFormMembers]
    };
}

function initFirstRegistrationSelects() {
    const yearSelect = document.getElementById("vehicle-first-registration-year");
    const monthSelect = document.getElementById("vehicle-first-registration-month");
    if(yearSelect){
        const currentYear = new Date().getFullYear();
        yearSelect.replaceChildren(new Option("年", ""));
        for(let year = currentYear; year >= 1990; year--){
            yearSelect.add(new Option(`${year}年`, year));
        }
    }
    if(monthSelect){
        monthSelect.replaceChildren(new Option("月", ""));
        for(let month = 1; month <= 12; month++){
            monthSelect.add(new Option(`${month}月`, month));
        }
    }
}

/**
 * 基本配車設定の変更有無を判定する
 */
function hasVehicleSettingsChanges(){
    const category = Number(document.getElementById("vehicle-dispatch-category")?.value || 0);
    const area = document.getElementById("vehicle-form-member-area");
    const hasNew = area?.querySelector('.vehicle-default-member-item[data-new="true"]') != null;

    return category !== initialVehicleDispatchCategory
        || hasNew
        || deletedVehicleFormMembers.length > 0;
}