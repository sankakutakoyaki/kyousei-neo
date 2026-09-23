"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { createVehicleColumns } from "./columns.js";
import { VehicleRepository } from "../../../repositories/operations/vehicle/VehicleRepository.js";
import { createMasterPage } from "../../../core/page/createMasterPage.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { registerController, getController } from "../../../application/controllerRegistry.js";
import { FormController } from "../../../application/FormController.js";
import { VehicleDefaultMemberRepository } from "../../../repositories/operations/vehicle/VehicleDefaultMemberRepository.js";
import { DialogService } from "../../../core/ui/dialog/DialogService.js";

let deletedDefaultMembers = [];

export async function init() {
    await initCommon();
    await initPageCache("/api/vehicle/init/cache");

    const vehicle = vehiclePage();
    registerController("vehicle", vehicle);
    vehicle.init();
    await vehicle.refresh();
}

export const vehiclePage = () =>
    createMasterPage({
        key: "vehicle",
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        idKey: "vehicleId",
        repository: VehicleRepository,
        columns: createVehicleColumns(),
        components: {
            combo: true
        },
        forms: {
            dispatchSetting: { 
                create: (controller) =>
                    new FormController({
                        controller,
                        formId: "dispatch-setting-dialog",
                        key: "vehicle-id",
                        idKey: "vehicleId",

                        repository:
                            VehicleRepository,

                        buildParams: (id) => ({
                            state:
                                APP.cache.common.state.INITIAL,
                            vehicleId:
                                id
                        }),
                        onOpen: async (data, formController) => {
                            const vehicleId =
                                data.vehicleId;

                            const vehicleName =
                                document.getElementById(
                                    "dispatch-setting-vehicle-name"
                                );

                            const plateNumber =
                                document.getElementById(
                                    "dispatch-setting-plate-number"
                                );

                            if(vehicleName){
                                vehicleName.value =
                                    data.vehicleName ?? "";
                            }

                            if(plateNumber){
                                plateNumber.value =
                                    [
                                        data.registrationArea,
                                        data.registrationClass,
                                        data.registrationKana,
                                        data.registrationNumber
                                    ]
                                    .filter(Boolean)
                                    .join(" ");
                            }

                            const dispatchCategory =
                                document.getElementById(
                                    "dispatch-setting-category"
                                );

                            const memberCategory =
                                document.getElementById(
                                    "dispatch-member-category"
                                );

                            if(
                                !dispatchCategory ||
                                !memberCategory
                            ){
                                return;
                            }

                            const result =
                                await VehicleDefaultMemberRepository
                                    .findDispatchCategory({
                                        vehicleId: data.vehicleId,
                                        state:
                                            APP.cache.common.state.INITIAL
                                    });

                            const rows =
                                result.data ?? [];

                            const currentCategory =
                                rows.length > 0
                                    ? Number(
                                        rows[0].dispatchCategory
                                    )
                                    : 0;

                            dispatchCategory.value =
                                currentCategory
                                    ? String(currentCategory)
                                    : "";

                            // 基本乗務員も最初は車両の基本区分を表示
                            memberCategory.value =
                                currentCategory
                                    ? String(currentCategory)
                                    : "";

                            deletedDefaultMembers = [];

                            await loadDefaultMembers(
                                data.vehicleId,
                                currentCategory
                            );

                            let currentMemberCategory =
                                Number(memberCategory.value || 0);

                            memberCategory.onchange =
                                async () => {

                                    const nextCategory =
                                        Number(
                                            memberCategory.value || 0
                                        );

                                    if(hasDefaultMemberChanges()){

                                        memberCategory.value =
                                            currentMemberCategory
                                                ? String(currentMemberCategory)
                                                : "";

                                        DialogService.error(
                                            "基本乗務員の変更が未保存です。保存してから対象区分を切り替えてください。"
                                        );

                                        return;
                                    }

                                    currentMemberCategory =
                                        nextCategory;

                                    deletedDefaultMembers = [];

                                    await loadDefaultMembers(
                                        data.vehicleId,
                                        nextCategory
                                    );

                                    formController
                                        .updateSubmitState();
                                };

                            formController
                                .updateSubmitState();
                        },
                        buildAdditionalPayload:
                            () =>
                                buildDefaultMemberPayload(
                                    controller
                                        .getCurrentRowId()
                                ),

                        hasAdditionalChanges:
                            () =>
                                hasDefaultMemberChanges(),

                        resetAdditional:
                            () => {
                                deletedDefaultMembers = [];
                            },

                        saveHandler:
                            async (payload) =>
                                VehicleDefaultMemberRepository
                                    .saveSettings(payload),
                                    
                        validateBusiness: async () => {

                            const category =
                                Number(
                                    document.getElementById(
                                        "dispatch-setting-category"
                                    )?.value
                                );

                            if(!category){
                                throw new Error(
                                    "配車区分を選択してください。"
                                );
                            }
                        },
                    })
            }
        },
        actions: {
            "open-dispatch-setting":
                async (c) => {

                    const vehicleId =
                        c.getCurrentRowId();

                    if(!vehicleId){
                        return;
                    }

                    const vehicle =
                        c.dataTable.findOriginById(
                            vehicleId
                        );

                    if(!vehicle){
                        return;
                    }

                    await c.openForm(
                        "dispatchSetting",
                        vehicle,
                        { bulkMode: false }
                    );
                }
        },
        conditions: {
            "open-dispatch-setting":
                c => c.hasCurrentRow()
        },
        buildDetailParams: (id) => ({
            state: APP.cache.common.state.INITIAL,
            vehicleId: id
        }),
        model: {
            filters: {
                officeId: filterFactory.equals("officeId")
            }
        }
    });

async function loadDefaultMembers(
    vehicleId,
    dispatchCategory
){

    const area =
        document.getElementById(
            "vehicle-default-member-area"
        );

    if(!area){
        return;
    }

    area.replaceChildren();


    if(
        !vehicleId ||
        !dispatchCategory
    ){
        return;
    }


    const result =
        await VehicleDefaultMemberRepository
            .findList({
                vehicleId,
                dispatchCategory,
                state:
                    APP.cache.common.state.INITIAL
            });


    const rows =
        result.data ?? [];


    if(rows.length === 0){

        const empty =
            document.createElement("div");

        empty.className =
            "vehicle-default-member-empty";

        empty.textContent =
            "基本乗務員は未設定です";

        area.appendChild(
            empty
        );

        return;
    }

    rows.forEach(row => {

        const item =
            createDefaultMemberItem({
                vehicleDefaultMemberId:
                    row.vehicleDefaultMemberId,

                employeeId:
                    row.employeeId,

                employeeName:
                    row.employeeName ?? "",

                role:
                    row.role,

                version:
                    row.version,

                isNew:
                    false
            });

        area.appendChild(item);
    });
}

function addDefaultMemberDraft(){

    const employeeIdInput =
        document.getElementById(
            "dispatch-member-employee-id"
        );

    const employeeNameInput =
        document.getElementById(
            "dispatch-member-name"
        );

    const employeeCodeInput =
        document.getElementById(
            "dispatch-member-code"
        );

    const roleInput =
        document.getElementById(
            "dispatch-member-role"
        );

    const area =
        document.getElementById(
            "vehicle-default-member-area"
        );


    if(
        !employeeIdInput ||
        !employeeNameInput ||
        !roleInput ||
        !area
    ){
        return;
    }


    const employeeId =
        Number(employeeIdInput.value);

    const employeeName =
        employeeNameInput.value.trim();

    const role =
        Number(roleInput.value);


    if(
        !employeeId ||
        !employeeName ||
        !role
    ){
        return;
    }


    // 同じ社員の二重追加を防止
    const exists =
        area.querySelector(
            `[data-employee-id="${employeeId}"]`
        );

    if(exists){
        return;
    }


    // 「未設定です」を消す
    area.querySelector(
        ".vehicle-default-member-empty"
    )?.remove();

    const item =
        createDefaultMemberItem({
            employeeId,
            employeeName,
            role,
            isNew: true
        });

    area.appendChild(item);


    // 入力欄クリア
    employeeIdInput.value = "";

    employeeNameInput.value = "";

    if(employeeCodeInput){
        employeeCodeInput.value = "";
        delete employeeCodeInput.dataset.lastId;
        employeeCodeInput.focus();
    }

    roleInput.value = "";
}

function createDefaultMemberItem({
    vehicleDefaultMemberId = null,
    employeeId,
    employeeName,
    role,
    version = 0,
    isNew = false
}) {

    const item =
        document.createElement("div");

    item.className =
        "vehicle-default-member-item";

    item.dataset.employeeId =
        employeeId;

    item.dataset.role =
        role;

    item.dataset.version =
        version ?? 0;

    if(vehicleDefaultMemberId){
        item.dataset.vehicleDefaultMemberId =
            vehicleDefaultMemberId;
    }

    if(isNew){
        item.dataset.new = "true";
    }


    const roleName =
        Number(role) === 1
            ? "担当"
            : "助手";


    const text =
        document.createElement("span");

    text.textContent =
        `${roleName}　${employeeName}`;


    const removeButton =
        document.createElement("button");

    removeButton.type = "button";
    removeButton.textContent = "×";
    removeButton.className =
        "vehicle-default-member-remove";


    removeButton.onclick =
        () => {

            if(
                item.dataset.vehicleDefaultMemberId &&
                item.dataset.new !== "true"
            ){
                deletedDefaultMembers.push({
                    vehicleDefaultMemberId:
                        Number(
                            item.dataset.vehicleDefaultMemberId
                        ),

                    version:
                        Number(
                            item.dataset.version
                        )
                });
            }

            item.remove();

            const controller =
                getController("vehicle");

            controller
                ?.getActiveForm()
                ?.updateSubmitState();
        };


    item.appendChild(text);
    item.appendChild(removeButton);

    return item;
}

function buildDefaultMemberPayload(vehicleId){

    const dispatchCategory =
        Number(
            document.getElementById(
                "dispatch-setting-category"
            )?.value
        );

    const memberDispatchCategory =
        Number(
            document.getElementById(
                "dispatch-member-category"
            )?.value
        );

    const area =
        document.getElementById(
            "vehicle-default-member-area"
        );

    if(
        !vehicleId ||
        !dispatchCategory ||
        !memberDispatchCategory ||
        !area
    ){
        return {
            vehicleId,
            dispatchCategory,
            memberDispatchCategory,
            members: [],
            deletedMembers: []
        };
    }

    const members =
        [
            ...area.querySelectorAll(
                ".vehicle-default-member-item"
            )
        ]
        .map((item, index) => ({
            vehicleDefaultMemberId:
                item.dataset.vehicleDefaultMemberId
                    ? Number(item.dataset.vehicleDefaultMemberId)
                    : null,

            vehicleId,

            // ★ 基本乗務員はこちら
            dispatchCategory:
                memberDispatchCategory,

            employeeId:
                Number(item.dataset.employeeId),

            role:
                Number(item.dataset.role),

            displayOrder:
                index + 1,

            version:
                Number(item.dataset.version ?? 0),

            state:
                APP.cache.common.state.INITIAL
        }));

    return {
        vehicleId,

        // ★ vehicle_dispatch_categories 用
        dispatchCategory,

        // ★ どの乗務員区分を編集しているか
        memberDispatchCategory,

        members,
        deletedMembers:
            [...deletedDefaultMembers]
    };
}

function hasDefaultMemberChanges(){

    const area =
        document.getElementById(
            "vehicle-default-member-area"
        );

    if(!area){
        return false;
    }

    const hasNew =
        area.querySelector(
            '.vehicle-default-member-item[data-new="true"]'
        ) != null;

    const hasDeleted =
        deletedDefaultMembers.length > 0;

    return hasNew || hasDeleted;
}