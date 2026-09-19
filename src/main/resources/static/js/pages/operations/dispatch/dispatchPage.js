"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { DataTable } from "../../../core/table/DataTable.js";
import { openFormDialog } from "../../../core/ui/dialog/dialogCore.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { getToday } from "../../../util/time.js";
import { VehicleRepository } from "../../../repositories/operations/vehicle/VehicleRepository.js";
import { createDispatchVehicleColumns } from "./columns.js";
import { registerController } from "../../../application/controllerRegistry.js";
import { PageController } from "../../../application/PageController.js";
import { DailyCrewRepository } from "../../../repositories/operations/dispatch/DailyCrewRepository.js";
import { DispatchOrderRepository } from "../../../repositories/operations/dispatch/DispatchOrderRepository.js";
import { VehicleDefaultMemberRepository } from "../../../repositories/operations/vehicle/VehicleDefaultMemberRepository.js";
import { DispatchAssignmentRepository } from "../../../repositories/operations/dispatch/DispatchAssignmentRepository.js";

let vehicleTable;
let dispatchOrders = [];
let dispatchLoadPromise = Promise.resolve();

export async function init() {
    await initCommon();
    await initPageCache("/api/dispatch/init/cache");

    const dispatch = dispatchPage();
    registerController("dispatch", dispatch);
    dispatch.init();
}

function dispatchPage() {
    return new PageController({
        key: "dispatch",
        autoLoad: false,
        components: { combo: true },
        actions: {
            "select-active-crews": async (controller) => {
                await openActiveCrewDialog(controller);
            },
            "change-order-status": async () => {
                renderDispatchOrders();
            }
        },
        onInit: (controller) => {
            controller.state.filters = {
                officeId: "",
                dispatchCategory: ""
            };
            initConditions();
            initVehicleTable(controller);
        }
    });
}

/**
 * 初期条件
 */
function initConditions() {

    const workDate =
        document.getElementById(
            "dispatch-work-date"
        );

    if(workDate && !workDate.value){
        workDate.value = getToday();
    }


    const targets = [
        "dispatch-work-date",
        "dispatch-office",
        "dispatch-category"
    ];

    targets.forEach(id => {

        const element =
            document.getElementById(id);

        if(!element) return;

        element.addEventListener(
            "change",
            async () => {
                await loadDispatchBoard();
            }
        );
    });
}

/**
 * 稼働車両選択用テーブル
 */
function initVehicleTable(controller) {
    vehicleTable = new DataTable({
        tableId: "table-dispatch-vehicle",
        idKey: "vehicleId",
        repository: VehicleRepository,
        controller,
        columns: createDispatchVehicleColumns(),
        checkable: true,
        infiniteScroll: false,
        pageTopButton: false,
        buildParams: () => ({
            state: APP.cache.common.state.INITIAL
        }),
        model: {
            pageSize: 100,
            filters: {
                officeId: filterFactory.equals("officeId")
            }
        },
        onRendered: () => {
            updateSelectedCount();
        }
    });
}

/**
 * 稼働班選択
 */
async function openActiveCrewDialog(controller) {
    const officeId =  document.getElementById("dispatch-office")?.value ?? "";
    controller.state.filters.officeId = officeId;

    if (vehicleTable) {
        await vehicleTable.reload();
    }

    openFormDialog({
        dialogId: "dispatch-vehicle-dialog",
        submitText: "決定",
        cancelText: "キャンセル",
        onSubmit: async () => {
            const vehicleIds = vehicleTable.getSelectedIds();
            if (vehicleIds.length === 0) {
                return false;
            }
            return true;
        }
    });
}

/**
 * 選択台数表示
 */
function updateSelectedCount() {
    const count = vehicleTable?.getSelectedIds()?.length ?? 0;
    const area = document.getElementById("dispatch-vehicle-selected-count");
    if (area) {
        area.textContent = `${count}台選択`;
    }
}

function loadDispatchBoard() {

    dispatchLoadPromise =
        dispatchLoadPromise
            .catch(() => {
                // 前回失敗していても次は実行する
            })
            .then(() =>
                executeLoadDispatchBoard()
            );

    return dispatchLoadPromise;
}

async function executeLoadDispatchBoard() {

    const workDate =
        document.getElementById(
            "dispatch-work-date"
        )?.value ?? "";

    const officeId =
        Number(
            document.getElementById(
                "dispatch-office"
            )?.value || 0
        );

    const dispatchCategory =
        Number(
            document.getElementById(
                "dispatch-category"
            )?.value || 0
        );


    if(
        !workDate ||
        !officeId ||
        !dispatchCategory
    ){
        renderDispatchCrews([]);

        dispatchOrders = [];
        renderDispatchOrders();

        return;
    }


    // ==============================
    // 左：配車班
    // ==============================

    // 基本設定から対象車両を取得
    const defaultResult =
        await VehicleDefaultMemberRepository
            .findDispatchDefaultList({
                workDate,
                officeId,
                dispatchCategory,
                state:
                    APP.cache.common.state.INITIAL
            });

    const defaultRows =
        defaultResult.data ?? [];


    // 車両IDを重複除去
    const vehicleIds = [
        ...new Set(
            defaultRows
                .map(row => row.vehicleId)
                .filter(id => id != null)
        )
    ];


    // 当日班を初期生成
    if(vehicleIds.length > 0){

        await DailyCrewRepository.bulkCreate({
            workDate,
            officeId,
            dispatchCategory,
            vehicleIds
        });
    }


    // 実際の当日班を取得
    const crewResult =
        await DailyCrewRepository.findBoardList({
            workDate,
            officeId,
            dispatchCategory,
            state:
                APP.cache.common.state.INITIAL
        });

    const crewRows =
        crewResult.data ?? [];


    // Query結果を班単位にまとめる
    const crews =
        groupDispatchCrews(
            crewRows
        );


    // 班ごとの配車済み伝票を取得
    await loadCrewAssignments(
        crews
    );


    // 左側描画
    renderDispatchCrews(
        crews
    );


    // ==============================
    // 右：伝票
    // ==============================

    const orderResult =
        await DispatchOrderRepository
            .findList({
                workDate,
                officeId,
                dispatchCategory,
                state:
                    APP.cache.common.state.INITIAL
            });

    dispatchOrders =
        orderResult.data ?? [];

    renderDispatchOrders();
}

function groupDispatchCrews(rows){

    const map =
        new Map();

    rows.forEach(row => {

        const dailyCrewId =
            row.dailyCrewId;

        if(!map.has(dailyCrewId)){

            map.set(
                dailyCrewId,
                {
                    dailyCrewId,
                    vehicleId:
                        row.vehicleId,

                    vehicleName:
                        row.vehicleName,

                    registrationArea:
                        row.registrationArea,

                    registrationClass:
                        row.registrationClass,

                    registrationKana:
                        row.registrationKana,

                    registrationNumber:
                        row.registrationNumber,

                    members: []
                }
            );
        }


        if(row.employeeId){

            map.get(dailyCrewId)
                .members
                .push({
                    dailyCrewMemberId:
                        row.dailyCrewMemberId,

                    employeeId:
                        row.employeeId,

                    employeeName:
                        row.employeeName,

                    role:
                        row.role,

                    shiftType:
                        row.shiftType,

                    startTime:
                        row.startTime,

                    endTime:
                        row.endTime
                });
        }
    });

    return [
        ...map.values()
    ];
}

function renderDispatchCrews(crews){

    const area =
        document.getElementById(
            "dispatch-crew-list"
        );

    if(!area) return;

    area.replaceChildren();

    crews.forEach(crew => {

        area.appendChild(
            createCrewCard(crew)
        );
    });
}

function createCrewCard(crew){

    const card =
        document.createElement("div");

    card.className =
        "dispatch-crew-card";

    card.dataset.dailyCrewId =
        crew.dailyCrewId;

    const header =
        document.createElement("div");

    header.className =
        "dispatch-crew-card-header";


    const name =
        document.createElement("strong");

    name.textContent =
        crew.vehicleName;


    const number =
        document.createElement("span");

    number.textContent =
        [
            crew.registrationArea,
            crew.registrationClass,
            crew.registrationKana,
            crew.registrationNumber
        ]
        .filter(Boolean)
        .join(" ");


    header.append(
        name,
        number
    );


    const members =
        document.createElement("div");

    members.className =
        "dispatch-crew-members";


    if(crew.members.length === 0){

        const empty =
            document.createElement("div");

        empty.className =
            "dispatch-member-unassigned";

        empty.textContent =
            "乗務員未設定";

        members.appendChild(empty);

    } else {

        crew.members.forEach(member => {

            members.appendChild(
                createCrewMember(member)
            );
        });
    }

    const assignmentHeader =
        document.createElement("div");

    assignmentHeader.className =
        "dispatch-assignment-header";


    const assignmentTitle =
        document.createElement("span");

    assignmentTitle.textContent =
        `伝票 ${crew.assignments?.length ?? 0}件`;


    const toggle =
        document.createElement("button");

    toggle.type = "button";
    toggle.className =
        "dispatch-assignment-toggle";

    toggle.textContent = "▼";


    assignmentHeader.append(
        assignmentTitle,
        toggle
    );

    const orders =
        document.createElement("div");

    orders.className =
        "dispatch-assignment-list collapsed";

    renderCrewAssignments(
        orders,
        crew.assignments ?? []
    );

    toggle.addEventListener(
        "click",
        event => {

            event.stopPropagation();

            const collapsed =
                orders.classList.toggle(
                    "collapsed"
                );

            toggle.textContent =
                collapsed
                    ? "▶"
                    : "▼";
        }
    );

    card.append(
        header,
        members,
        assignmentHeader,
        orders
    );

    initCrewDrop(card);

    return card;
}

function createCrewMember(member){

    const row =
        document.createElement("div");

    row.className =
        "dispatch-crew-member";


    const role =
        member.role === 1
            ? "担当"
            : "助手";


    const roleSpan =
        document.createElement("span");

    roleSpan.className =
        "dispatch-member-role";

    roleSpan.textContent =
        role;


    const name =
        document.createElement("span");

    name.textContent =
        member.employeeName;


    row.append(
        roleSpan,
        name
    );


    if(member.shiftType !== 1){

        const status =
            document.createElement("span");

        status.className =
            "dispatch-member-warning";

        status.textContent =
            getShiftStatusText(
                member.shiftType
            );

        row.appendChild(status);
    }

    return row;
}

function getShiftStatusText(shiftType){

    switch(shiftType){

        case 2:
            return "休み";

        case 3:
            return "有休";

        case 4:
            return "午前休";

        case 5:
            return "午後休";

        default:
            return "シフト未登録";
    }
}

function renderDispatchOrders(){

    const area =
        document.getElementById(
            "dispatch-order-list"
        );

    if(!area) return;

    area.replaceChildren();


    const status =
        document.getElementById(
            "dispatch-order-status"
        )?.value ?? "unassigned";


    const list =
        dispatchOrders.filter(order => {

            if(status === "unassigned"){
                return order.assignmentCount === 0;
            }

            if(status === "assigned"){
                return order.assignmentCount > 0;
            }

            return true;
        });


    if(list.length === 0){

        const empty =
            document.createElement("div");

        empty.className =
            "dispatch-empty";

        if(status === "assigned"){
            empty.textContent =
                "配車済み伝票はありません";
        } else if(status === "all"){
            empty.textContent =
                "伝票はありません";
        } else {
            empty.textContent =
                "未配車伝票はありません";
        }

        area.appendChild(empty);

        return;
    }


    list.forEach(order => {

        area.appendChild(
            createDispatchOrderCard(order)
        );
    });
}

function createDispatchOrderCard(order){

    const card =
        document.createElement("div");

    card.className =
        "dispatch-order-card";

    card.dataset.orderId =
        order.orderId;

    card.draggable = true;
    card.addEventListener(
        "dragstart",
        event => {

            event.dataTransfer.effectAllowed =
                "move";

            event.dataTransfer.setData(
                "text/plain",
                String(order.orderId)
            );

            card.classList.add(
                "dragging"
            );
        }
    );

    card.addEventListener(
        "dragend",
        () => {

            card.classList.remove(
                "dragging"
            );
        }
    );

    const header =
        document.createElement("div");

    header.className =
        "dispatch-order-card-header";


    const title =
        document.createElement("strong");

    title.textContent =
        order.title || "名称未設定";


    const time =
        document.createElement("span");

    time.textContent =
        order.visitTime || "";


    header.append(
        title,
        time
    );


    const address =
        document.createElement("div");

    address.className =
        "dispatch-order-address";

    address.textContent =
        order.fullAddress || "";


    card.append(
        header,
        address
    );


    if(order.assignmentCount > 0){

        const assigned =
            document.createElement("div");

        assigned.className =
            "dispatch-order-assigned";

        assigned.textContent =
            `配車済み ${order.assignmentCount}班`;

        card.appendChild(assigned);
    }


    return card;
}

function initCrewDrop(card){

    card.addEventListener(
        "dragover",
        event => {

            event.preventDefault();

            event.dataTransfer.dropEffect =
                "move";

            card.classList.add(
                "drag-over"
            );
        }
    );


    card.addEventListener(
        "dragleave",
        event => {

            if(
                card.contains(
                    event.relatedTarget
                )
            ){
                return;
            }

            card.classList.remove(
                "drag-over"
            );
        }
    );


    card.addEventListener(
        "drop",
        async event => {

            event.preventDefault();

            card.classList.remove(
                "drag-over"
            );


            const orderId =
                Number(
                    event.dataTransfer.getData(
                        "text/plain"
                    )
                );

            const dailyCrewId =
                Number(
                    card.dataset.dailyCrewId
                );


            if(
                !orderId ||
                !dailyCrewId
            ){
                return;
            }


            await assignOrderToCrew(
                orderId,
                dailyCrewId
            );
        }
    );
}

async function assignOrderToCrew(
    orderId,
    dailyCrewId
){

    await DispatchAssignmentRepository.save({
        orderId,
        dailyCrewId,
        visitOrder: 0,
        remarks: "",
        state:
            APP.cache.common.state.INITIAL
    });

    await loadDispatchBoard();
}

async function loadCrewAssignments(crews){

    for(const crew of crews){

        const result =
            await DispatchAssignmentRepository
                .findList({
                    dailyCrewId:
                        crew.dailyCrewId,

                    state:
                        APP.cache.common.state.INITIAL
                });

        crew.assignments =
            result.data ?? [];
    }
}

function renderCrewAssignments(
    area,
    assignments
){

    area.replaceChildren();

    assignments.forEach(
        assignment => {

            const item =
                document.createElement("div");

            item.className =
                "dispatch-assignment-item";

            item.dataset
                .dispatchAssignmentId =
                    assignment
                        .dispatchAssignmentId;

            item.dataset.orderId =
                assignment.orderId;


            const title =
                document.createElement("strong");

            title.textContent =
                assignment.title
                || "名称未設定";


            const address =
                document.createElement("span");

            address.textContent =
                assignment.fullAddress
                || "";


            item.append(
                title,
                address
            );

            area.appendChild(
                item
            );
        }
    );
}