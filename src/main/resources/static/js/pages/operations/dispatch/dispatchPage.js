"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { getToday } from "../../../util/time.js";
import { registerController } from "../../../application/controllerRegistry.js";
import { PageController } from "../../../application/PageController.js";
import { DailyCrewRepository } from "../../../repositories/operations/dispatch/DailyCrewRepository.js";
import { DispatchOrderRepository } from "../../../repositories/operations/dispatch/DispatchOrderRepository.js";
import { VehicleDefaultMemberRepository } from "../../../repositories/operations/vehicle/VehicleDefaultMemberRepository.js";
import { DispatchAssignmentRepository } from "../../../repositories/operations/dispatch/DispatchAssignmentRepository.js";
import { groupDispatchCrews, renderDispatchCrews } from "./dispatchCrewView.js";
import { renderDispatchOrders } from "./dispatchOrderView.js";
import { initCrewDrop } from "./dispatchDragDrop.js";
import { DialogService } from "../../../core/ui/dialog/DialogService.js";
import { createCrewMemberForm } from "./dispatchCrewMemberForm.js";
import { createDispatchVehicle } from "./dispatchVehicle.js";
import { initLoginOffice } from "../../../util/office.js";

let dispatchOrders = [];
let dispatchLoadPromise = Promise.resolve();
const expandedCrewIds = new Set();
let crewMemberForm = null;
let dispatchVehicle = null;

export async function init() {
    await initCommon();
    await initPageCache("/api/dispatch/init/cache");

    const dispatch = dispatchPage();
    registerController("dispatch", dispatch);
    dispatch.init();
    crewMemberForm = createCrewMemberForm(dispatch, {afterSave: loadDispatchBoard});
    dispatchVehicle = createDispatchVehicle({onChanged: loadDispatchBoard});
    
    initLoginOffice();
    await loadDispatchBoard({ensureCrews: true});
}

function dispatchPage() {
    return new PageController({
        key: "dispatch",
        autoLoad: false,
        components: { combo: true },
        actions: {
            "select-active-crews": async (controller) => {await openActiveCrewDialog(controller);},
            "change-order-status": async () => {renderOrderList();},
            "reinitialize-crews": async () => {await reinitializeCrews();},
            "reset-assignments": async () => {await resetAssignments();},
            "add-daily-crew": async () => {await dispatchVehicle?.add();},
        },
        onInit: (controller) => {
            controller.state.filters = {officeId: "", dispatchCategory: ""};
            initConditions();
        }
    });
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
        element.addEventListener("change", async () => {
            await loadDispatchBoard({ensureCrews: true});
        });
    });
}

function loadDispatchBoard({ensureCrews = false} = {}) {
    dispatchLoadPromise = dispatchLoadPromise.catch(() => {}).then(() =>
        executeLoadDispatchBoard({ensureCrews})
    );
    return dispatchLoadPromise;
}

async function executeLoadDispatchBoard({ensureCrews = false} = {}) {
    const conditions = getDispatchConditions();
    if(!hasDispatchConditions(conditions)){
        renderDispatchCrews([]);
        dispatchOrders = [];
        renderOrderList();
        return;
    }
    if(ensureCrews){
        await ensureDailyCrews(conditions);
    }
    const crews = await loadDailyCrews(conditions);
    renderCrewBoard(crews);
    await loadDispatchOrders(conditions);
    renderOrderList();

    await dispatchVehicle?.loadCandidates();
}

function getDispatchConditions(){
    return {
        workDate: document.getElementById("dispatch-work-date")?.value ?? "",
        officeId: Number(document.getElementById("dispatch-office")?.value || 0),
        dispatchCategory: Number(document.getElementById("dispatch-category")?.value || 0)
    };
}

function hasDispatchConditions({workDate, officeId, dispatchCategory}) {
    return Boolean(workDate && officeId && dispatchCategory);
}

async function ensureDailyCrews({workDate, officeId, dispatchCategory}) {
    const result = await VehicleDefaultMemberRepository.findDispatchDefaultList({
        workDate,
        officeId,
        dispatchCategory,
        shiftType: 1,
        state: APP.cache.common.state.INITIAL
    });
    const vehicleIds = [
        ...new Set((result.data ?? []).map(row => row.vehicleId).filter(id => id != null))
    ];
    if(vehicleIds.length === 0){
        return;
    }
    await DailyCrewRepository.bulkCreate({
        workDate,
        officeId,
        dispatchCategory,
        vehicleIds
    });
}

async function loadDailyCrews({workDate, officeId, dispatchCategory}) {
    const result = await DailyCrewRepository.findBoardList({
        workDate,
        officeId,
        dispatchCategory,
        state: APP.cache.common.state.INITIAL
    });
    const crews = groupDispatchCrews(result.data ?? []);
    await loadCrewAssignments(crews);
    crews.forEach(crew => {
        if((crew.assignments?.length ?? 0) === 0){
            expandedCrewIds.delete(crew.dailyCrewId);
        }
    });
    return crews;
}

function renderCrewBoard(crews){
    renderDispatchCrews(
        crews,
        {
            expandedCrewIds,
            initCrewDrop: card => {
                initCrewDrop(
                    card,
                    {
                        onAssigned: async ({dailyCrewId}) => {
                            expandedCrewIds.add(dailyCrewId);
                            await loadDispatchBoard();
                        }
                    }
                );
            },
            onUnassign: async assignment => {
                await DispatchAssignmentRepository.deleteByIds([
                    assignment.dispatchAssignmentId
                ]);
                await loadDispatchBoard();
            },
            onReorder: async items => {
                await DispatchAssignmentRepository.reorder(items);
                await loadDispatchBoard();
            },
            onDeleteCrew: async crew => {
                if((crew.assignments?.length ?? 0) > 0){
                    DialogService.error("配車済みの伝票があります。先に配車を解除してください。");
                    return;
                }
                const confirmed = await DialogService.confirm(`${crew.vehicleName ?? "この車両"}を配車ボードから削除しますか？`);
                if(!confirmed){
                    return;
                }
                await DailyCrewRepository.remove({dailyCrewId: crew.dailyCrewId});
                await loadDispatchBoard();
            },
            onEditMembers: async crew => {await crewMemberForm?.open(crew);}
        }
    );
}

async function loadDispatchOrders({workDate, officeId, dispatchCategory}) {
    const result = await DispatchOrderRepository.findList({
        workDate,
        officeId,
        dispatchCategory,
        state: APP.cache.common.state.INITIAL
    });
    dispatchOrders = result.data ?? [];
}

async function loadCrewAssignments(crews){
    if(!crews?.length){
        return;
    }
    const results = await Promise.all(crews.map(crew =>
        DispatchAssignmentRepository.findList({
            dailyCrewId: crew.dailyCrewId,
            state: APP.cache.common.state.INITIAL
        }))
    );
    crews.forEach((crew, index) => {crew.assignments = results[index]?.data ?? [];});
}

function renderOrderList(){
    const status = document.getElementById("dispatch-order-status")?.value ?? "unassigned";
    renderDispatchOrders(dispatchOrders, status);
}

async function reinitializeCrews() {
    const conditions = getDispatchConditions();
    if(!hasDispatchConditions(conditions)){
        return;
    }
    const confirmed = await DialogService.confirm("現在の当日班を削除し、最新の基本設定から再初期化します。よろしいですか？");
    if(!confirmed){
        return;
    }
    try {
        await DailyCrewRepository.reinitialize(conditions);
        await loadDispatchBoard();
    } catch(error) {
        DialogService.error(error.message ?? "再初期化に失敗しました。");
    }
}

async function resetAssignments() {
    const conditions = getDispatchConditions();
    if(!hasDispatchConditions(conditions)){
        return;
    }
    const confirmed = await DialogService.confirm("この日の配車をすべて解除します。よろしいですか？");
    if(!confirmed){
        return;
    }
    try {
        await DispatchAssignmentRepository.reset(conditions);
        await loadDispatchBoard();
    } catch(error) {
        DialogService.error(error.message ?? "配車解除に失敗しました。");
    }
}