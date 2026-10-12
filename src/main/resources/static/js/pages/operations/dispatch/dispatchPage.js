"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
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
import { createDispatchVehicle } from "./dispatchVehicle.js";
import { createCrewMemberForm } from "./dispatchCrewMemberForm.js";
import { createPageSession } from "../../../core/page/PageSession.js";
import { getDispatchConditions as readConditions, hasDispatchConditions } from "./dispatchConditions.js";

export async function init() {
    const session = createPageSession(document.getElementById("dispatch-page"));
    const page = createDispatchPage(session);
    registerController("dispatch", page);
    try {
        await initCommon();
        await initPageCache("/api/dispatch/init/cache", {isActive: session.isActive});
        if(!session.isActive()) return;
        page.init();
        await page.loadBoard({ensureCrews: true});
    } catch(error) {
        page.dispose();
        throw error;
    }
}

function createDispatchPage(session) {
    let dispatchOrders = [];
    let dispatchLoadPromise = Promise.resolve();
    let boardRequest = 0;
    const expandedCrewIds = new Set();
    let crewMemberForm = null;
    let dispatchVehicle = null;
    const getDispatchConditions = () => readConditions(session.root);

    function dispatchPage() {
        const controller = new PageController({
            key: "dispatch",
            autoLoad: false,
            defaultFormName: "members",
            components: { combo: true },
            actions: {
                "toggle-crew-orders": (controller, card) => {
                    if(!session.isActive() || card.classList.contains("drag-over")) return;
                    const collapsed = card._orders?.classList.toggle("collapsed");
                    const id = Number(card.dataset.dailyCrewId);
                    if(collapsed) expandedCrewIds.delete(id);
                    else expandedCrewIds.add(id);
                },
                "reload-board": async () => {await loadDispatchBoard({ensureCrews: true});},
                "change-order-status": async () => {renderOrderList();},
                "reinitialize-crews": async () => {await reinitializeCrews();},
                "reset-assignments": async () => {await resetAssignments();},
                "add-daily-crew": async () => {await dispatchVehicle?.add();},
                "add-crew-member": () => crewMemberForm?.addMember(),
                "remove-crew-member": (controller, button) => {crewMemberForm?.removeMember(button);},
                "crew-role-change": () => crewMemberForm?.updateSubmitState(),
                "delete-daily-crew": async (controller, button) => {
                    const crew = button._crew;
                    if (!crew) return;

                    if ((crew.assignments?.length ?? 0) > 0) {
                        DialogService.error("配車済みの伝票があります。先に配車を解除してください。");
                        return;
                    }

                    const confirmed = await DialogService.confirm(
                        `${crew.vehicleName ?? "この車両"}を配車ボードから削除しますか？`
                    );
                    if (!confirmed || !session.isActive()) return;

                    await DailyCrewRepository.remove({dailyCrewId: crew.dailyCrewId});
                    await loadDispatchBoard();
                },

                "edit-crew-members": async (controller, button) => {
                    if (button._crew) await crewMemberForm?.open(button._crew);
                },

                "unassign-order": async (controller, button) => {
                    const assignment = button._assignment;
                    if (!assignment) return;

                    await DispatchAssignmentRepository.deleteByIds([
                        assignment.dispatchAssignmentId
                    ]);
                    await loadDispatchBoard();
                }
            },
            onInit: (controller) => {
                controller.state.filters = {officeId: "", dispatchCategory: ""};
                crewMemberForm = createCrewMemberForm(controller, {session, afterSave: loadDispatchBoard});
                controller.forms.members = crewMemberForm;
                dispatchVehicle = createDispatchVehicle({session, onChanged: loadDispatchBoard});
            }
        });
        const dispose = controller.dispose.bind(controller);
        controller.dispose = () => {session.dispose(); boardRequest++; dispose();};
        controller.loadBoard = loadDispatchBoard;
        return controller;
    }

    const controller = dispatchPage();
    return controller;

    function loadDispatchBoard({ensureCrews = false} = {}) {
        const request = ++boardRequest;
        const conditions = getDispatchConditions();
        const canApply = () => session.isActive() && request === boardRequest
            && JSON.stringify(conditions) === JSON.stringify(getDispatchConditions());
        dispatchLoadPromise = dispatchLoadPromise.catch(() => {}).then(() => {
            if(!canApply()) return;
            return executeLoadDispatchBoard({ensureCrews, conditions, canApply});
        });
        return dispatchLoadPromise;
    }

    async function executeLoadDispatchBoard({ensureCrews, conditions, canApply}) {
        if(!hasDispatchConditions(conditions)){
            renderDispatchCrews([]);
            dispatchOrders = [];
            renderOrderList();
            return;
        }
        if(ensureCrews){
            await ensureDailyCrews(conditions);
        }
        if(!canApply()) return;
        const crews = await loadDailyCrews(conditions);
        if(!canApply()) return;
        const orders = await loadDispatchOrders(conditions);
        if(!canApply()) return;
        dispatchOrders = orders;
        renderCrewBoard(crews);
        renderOrderList();

        await dispatchVehicle?.loadCandidates();
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
        if(!session.isActive()) return;
        renderDispatchCrews(
            crews,
            {
                expandedCrewIds,
                isActive: session.isActive,
                initCrewDrop: card => {
                    initCrewDrop(
                        card,
                        {
                            session,
                            onAssigned: async ({dailyCrewId}) => {
                                expandedCrewIds.add(dailyCrewId);
                                await loadDispatchBoard();
                            }
                        }
                    );
                },
                onReorder: async items => {
                    if(!session.isActive()) return;
                    try {await DispatchAssignmentRepository.reorder(items);}
                    catch(error) {await loadDispatchBoard().catch(() => {}); throw error;}
                    await loadDispatchBoard();
                }
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
        return result.data ?? [];
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
        if(!session.isActive()) return;
        const status = document.getElementById("dispatch-order-status")?.value ?? "unassigned";
        renderDispatchOrders(dispatchOrders, status);
    }

    async function reinitializeCrews() {
        const conditions = getDispatchConditions();
        if(!hasDispatchConditions(conditions)){
            return;
        }
        const confirmed = await DialogService.confirm("現在の当日班を削除し、最新の基本設定から再初期化します。よろしいですか？");
        if(!confirmed || !session.isActive()){
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
        if(!confirmed || !session.isActive()){
            return;
        }
        try {
            await DispatchAssignmentRepository.reset(conditions);
            await loadDispatchBoard();
        } catch(error) {
            DialogService.error(error.message ?? "配車解除に失敗しました。");
        }
    }
}
