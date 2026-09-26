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
import { groupDispatchCrews, renderDispatchCrews } from "./dispatchCrewView.js";
import { renderDispatchOrders } from "./dispatchOrderView.js";
import { initCrewDrop } from "./dispatchDragDrop.js";
import { DispatchEmployeeRepository } from "../../../repositories/operations/dispatch/DispatchEmployeeRepository.js";

let vehicleTable;
let dispatchOrders = [];
let dispatchLoadPromise = Promise.resolve();
const expandedCrewIds = new Set();

export async function init() {
    await initCommon();
    await initPageCache("/api/dispatch/init/cache");

    const dispatch = dispatchPage();
    registerController("dispatch", dispatch);
    dispatch.init();

    setInitialOffice();
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
                renderOrderList();
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

function setInitialOffice(){
    const office = document.getElementById("dispatch-office");
    if(!office) return;

    const loginOfficeId = APP.cache.page?.loginOfficeId;
    if(!loginOfficeId) return;

    office.value = String(loginOfficeId);
}

/**
 * 初期条件
 */
function initConditions() {
    const workDate = document.getElementById("dispatch-work-date");
    if(workDate && !workDate.value){
        workDate.value = getToday();
    }

    const targets = [
        "dispatch-work-date",
        "dispatch-office",
        "dispatch-category"
    ];
    targets.forEach(id => {
        const element = document.getElementById(id);
        if(!element) return;

        element.addEventListener("change",
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
    // 前回失敗していても次は実行する
    dispatchLoadPromise = dispatchLoadPromise.catch(() => {}).then(() => executeLoadDispatchBoard());
    return dispatchLoadPromise;
}

async function executeLoadDispatchBoard() {
    const workDate = document.getElementById("dispatch-work-date")?.value ?? "";
    const officeId = Number(document.getElementById("dispatch-office")?.value || 0);
    const dispatchCategory = Number(document.getElementById("dispatch-category")?.value || 0);
    const employeeResult = await DispatchEmployeeRepository.findCandidateList({
        workDate, officeId, state: APP.cache.common.state.INITIAL
    });

console.log(
    "dispatch employees",
    employeeResult.data
);
    if(!workDate || !officeId || !dispatchCategory){
        renderDispatchCrews([]);
        dispatchOrders = [];
        renderOrderList();
        return;
    }

    // 左：配車班
    // 基本設定から対象車両を取得
    const defaultResult =
        await VehicleDefaultMemberRepository
            .findDispatchDefaultList({
                workDate,
                officeId,
                dispatchCategory,
                shiftType: 1,
                state: APP.cache.common.state.INITIAL
            });
    const defaultRows = defaultResult.data ?? [];

    // 車両IDを重複除去
    const vehicleIds = [
        ...new Set(
            defaultRows.map(row => row.vehicleId).filter(id => id != null)
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
            state: APP.cache.common.state.INITIAL
        });
    const crewRows = crewResult.data ?? [];

    // Query結果を班単位にまとめる
    const crews = groupDispatchCrews(crewRows);

    // 班ごとの配車済み伝票を取得
    await loadCrewAssignments(crews);

    crews.forEach(crew => {
        if((crew.assignments?.length ?? 0) === 0){
            expandedCrewIds.delete(crew.dailyCrewId);
        }
    });

    // 左側描画
    renderDispatchCrews(
        crews,
        {
            expandedCrewIds,
            initCrewDrop: card => {
                initCrewDrop(
                    card,
                    {
                        onAssigned: async ({ dailyCrewId }) => {
                            expandedCrewIds.add(dailyCrewId);
                            await loadDispatchBoard();
                        }
                    }
                );},
            onUnassign: async assignment => {
                    await DispatchAssignmentRepository.deleteByIds([assignment.dispatchAssignmentId]);
                    await loadDispatchBoard();
                },
            onReorder:
                async items => {
                    await DispatchAssignmentRepository.reorder(items);
                    await loadDispatchBoard();
                },
        }
    );

    // 右：伝票
    const orderResult = await DispatchOrderRepository.findList({
        workDate,
        officeId,
        dispatchCategory,
        state: APP.cache.common.state.INITIAL
    });
    dispatchOrders = orderResult.data ?? [];
    renderOrderList();
}

async function loadCrewAssignments(crews){
    for(const crew of crews){
        const result = await DispatchAssignmentRepository.findList({
            dailyCrewId: crew.dailyCrewId,
            state: APP.cache.common.state.INITIAL
        });
        crew.assignments = result.data ?? [];
    }
}

function renderOrderList(){
    const status = document.getElementById("dispatch-order-status")?.value ?? "unassigned";
    renderDispatchOrders(dispatchOrders, status);
}