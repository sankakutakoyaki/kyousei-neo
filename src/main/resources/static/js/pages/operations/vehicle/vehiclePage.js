"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { createVehicleColumns } from "./columns.js";
import { VehicleRepository } from "../../../repositories/operations/vehicle/VehicleRepository.js";
import { createMasterPage } from "../../../core/page/createMasterPage.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { registerController } from "../../../application/controllerRegistry.js";
import { FormController } from "../../../application/FormController.js";
import { VehicleDefaultMemberRepository } from "../../../repositories/operations/vehicle/VehicleDefaultMemberRepository.js";

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
            dispatchSetting: { create: (controller) =>
                new FormController({
                    controller,
                    formId: "dispatch-setting-dialog",
                    key: "vehicle-id",
                    idKey: "vehicleId",
                    repository: VehicleRepository,
                    buildParams: (id) => ({
                        state: APP.cache.common.state.INITIAL,
                        vehicleId: id
                    }),
                    onOpen:
                        async (data) => {

                            deletedDefaultMembers = [];

                            const category =
                                document.getElementById(
                                    "dispatch-setting-category"
                                );

                            const addButton =
                                document.getElementById(
                                    "dispatch-member-add-btn"
                                );

                            if(category){

                                category.onchange =
                                    async () => {

                                        await loadDefaultMembers(
                                            data.vehicleId,
                                            Number(category.value)
                                        );
                                    };

                                await loadDefaultMembers(
                                    data.vehicleId,
                                    Number(category.value)
                                );
                            }


                            if(addButton){

                                addButton.onclick =
                                    () => {

                                        addDefaultMemberDraft();
                                    };
                            }
                        }
                })
            }
        },
        actions: {
            "open-dispatch-setting":
                async (c) => {
                    const vehicleId = c.getSelectedId();
                    if(!vehicleId){return;}
                    await c.openForm("dispatchSetting", vehicleId, {bulkMode: false});
                }
        },
        conditions: {
            "open-dispatch-setting": c => c.getSelectedIds().length === 1
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

            // DB登録済みなら削除対象として保持
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
        };


    item.appendChild(text);
    item.appendChild(removeButton);

    return item;
}

function buildDefaultMemberPayload(vehicleId){

    const category =
        Number(
            document.getElementById(
                "dispatch-setting-category"
            )?.value
        );

    const area =
        document.getElementById(
            "vehicle-default-member-area"
        );

    if(
        !vehicleId ||
        !category ||
        !area
    ){
        return {
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
                    ? Number(
                        item.dataset.vehicleDefaultMemberId
                    )
                    : null,

            vehicleId,

            dispatchCategory:
                category,

            employeeId:
                Number(
                    item.dataset.employeeId
                ),

            role:
                Number(
                    item.dataset.role
                ),

            displayOrder:
                index + 1,

            version:
                Number(
                    item.dataset.version ?? 0
                ),

            state:
                APP.cache.common.state.INITIAL
        }));


    return {
        members,
        deletedMembers:
            [...deletedDefaultMembers]
    };
}