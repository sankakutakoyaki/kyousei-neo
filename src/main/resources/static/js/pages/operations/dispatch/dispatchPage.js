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
            },
            "reinitialize-crews": async () => {
                await reinitializeCrews();
            },
            "reset-assignments": async () => {
                await resetAssignments();
            },
            "add-daily-crew": async () => {
                await addDailyCrew();
            },
        },
        onInit: (controller) => {
            controller.state.filters = {officeId: "", dispatchCategory: ""};
            initConditions();
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

function loadDispatchBoard() {
    // 前回失敗していても次は実行する
    dispatchLoadPromise = dispatchLoadPromise.catch(() => {}).then(() => executeLoadDispatchBoard());
    return dispatchLoadPromise;
}

async function executeLoadDispatchBoard() {
    const workDate = document.getElementById("dispatch-work-date")?.value ?? "";
    const officeId = Number(document.getElementById("dispatch-office")?.value || 0);
    const dispatchCategory = Number(document.getElementById("dispatch-category")?.value || 0);
    if(!workDate || !officeId || !dispatchCategory){
        renderDispatchCrews([]);
        dispatchOrders = [];
        renderOrderList();
        return;
    }

    // 左：配車班
    // 基本設定から対象車両を取得
    const defaultResult = await VehicleDefaultMemberRepository.findDispatchDefaultList({
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
            initCrewDrop: card => { initCrewDrop(
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
            onReorder: async items => {
                await DispatchAssignmentRepository.reorder(items);
                await loadDispatchBoard();
            },
            onAddMember: openAddMember
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
    await loadVehicleCandidates();
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

async function reinitializeCrews() {
    const workDate = document.getElementById("dispatch-work-date")?.value ?? "";
    const officeId = Number(document.getElementById("dispatch-office")?.value || 0);
    const dispatchCategory = Number(document.getElementById("dispatch-category")?.value || 0);
    if(!workDate || !officeId || !dispatchCategory){
        return;
    }

    const confirmed = await DialogService.confirm("現在の当日班を削除し、最新の基本設定から再初期化します。よろしいですか？");
    if(!confirmed){
        return;
    }

    try {
        await DailyCrewRepository.reinitialize({
            workDate,
            officeId,
            dispatchCategory
        });
        await loadDispatchBoard();
    } catch(error) {
        DialogService.error(error.message ?? "再初期化に失敗しました。");
    }
}

async function resetAssignments() {
    const workDate = document.getElementById("dispatch-work-date")?.value ?? "";
    const officeId = Number(document.getElementById("dispatch-office")?.value || 0);
    const dispatchCategory = Number(document.getElementById("dispatch-category")?.value || 0);
    if(!workDate || !officeId || !dispatchCategory){
        return;
    }

    const confirmed = await DialogService.confirm("この日の配車をすべて解除します。よろしいですか？");
    if(!confirmed){
        return;
    }

    try {
        await DispatchAssignmentRepository.reset({
            workDate,
            officeId,
            dispatchCategory
        });
        await loadDispatchBoard();        
    } catch(error) {
        DialogService.error(error.message ?? "再初期化に失敗しました。");
    }
}

async function loadVehicleCandidates() {
    const select = document.getElementById("dispatch-add-vehicle");
    if(!select){
        return;
    }

    select.replaceChildren();
    const emptyOption = document.createElement("option");
    emptyOption.value = "";
    emptyOption.textContent = "車両を選択";
    select.appendChild(
        emptyOption
    );
    const workDate = document.getElementById("dispatch-work-date")?.value ?? "";
    const officeId = Number(document.getElementById("dispatch-office")?.value || 0);
    const dispatchCategory = Number(document.getElementById("dispatch-category")?.value || 0);
    if(!workDate || !officeId || !dispatchCategory){
        return;
    }
    const result = await VehicleDefaultMemberRepository.findDispatchCandidateList({
        workDate,
        officeId,
        dispatchCategory,
        state: APP.cache.common.state.INITIAL
    });
    const rows = result.data ?? [];
    rows.forEach(vehicle => {
        const option = document.createElement("option");
        option.value = vehicle.vehicleId;
        option.textContent = [
            vehicle.vehicleName,
            vehicle.registrationArea,
            vehicle.registrationClass,
            vehicle.registrationKana,
            vehicle.registrationNumber
        ].filter(Boolean).join(" ");
        select.appendChild(option);
    });
    select.disabled = rows.length === 0;
}

async function addDailyCrew() {
    const workDate = document.getElementById("dispatch-work-date")?.value ?? "";
    const officeId = Number(document.getElementById("dispatch-office")?.value || 0);
    const dispatchCategory = Number(document.getElementById("dispatch-category")?.value || 0);
    const vehicleId = Number(document.getElementById("dispatch-add-vehicle")?.value || 0);
    if(!workDate || !officeId || !dispatchCategory || !vehicleId){
        return;
    }
    
    try {
        await DailyCrewRepository.bulkCreate({
            workDate,
            officeId,
            dispatchCategory,
            vehicleIds: [vehicleId],
            // 手動追加なので
            // 基本乗務員はコピーしない
            copyDefaultMembers: false
        });
        await loadDispatchBoard();
    } catch(error) {
        DialogService.error(error.message ?? "車両の追加に失敗しました。");
    }
}


async function openAddMember(crew) {

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

    if(
        !crew?.dailyCrewId ||
        !workDate ||
        !officeId
    ){
        return;
    }

    try {

        const result =
            await DailyCrewRepository
                .findMemberCandidates({
                    dailyCrewId:
                        crew.dailyCrewId,

                    workDate,

                    officeId,

                    shiftType: 1,

                    state:
                        APP.cache.common.state.INITIAL
                });

        const candidates =
            result.data ?? [];

        console.log(
            "member candidates:",
            candidates
        );

    } catch(error) {

        console.error(error);

        DialogService.error(
            error.message
            ?? "乗務員候補の取得に失敗しました。"
        );
    }
}