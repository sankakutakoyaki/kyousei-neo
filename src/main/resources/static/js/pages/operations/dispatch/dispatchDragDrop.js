"use strict"

import { DispatchAssignmentRepository }
    from "../../../repositories/operations/dispatch/DispatchAssignmentRepository.js";

/**
 * 班カードをドロップ対象にする
 */
import { DialogService } from "../../../core/ui/dialog/DialogService.js";

const busyAssignments = new Set();
export function initCrewDrop(card, {session, onAssigned} = {}){
    let dropping = false;
    card.addEventListener(
        "dragover",
        event => {
            event.preventDefault();
            event.dataTransfer.dropEffect = "move";
            card.classList.add("drag-over");
        }
    );

    card.addEventListener(
        "dragleave",
        event => {
            if(card.contains(event.relatedTarget)){
                return;
            }
            card.classList.remove("drag-over");
        }
    );

    card.addEventListener(
        "drop",
        async event => {
            event.preventDefault();
            card.classList.remove("drag-over");
            if(dropping || !session.isActive()) return;
            dropping = true;
            let operationKey;
            try {
                const dailyCrewId = Number(card.dataset.dailyCrewId);
                if(!dailyCrewId){
                    return;
                }

                // 班内伝票からの移動
                const assignmentData = event.dataTransfer.getData("application/x-dispatch-assignment");
                if(assignmentData){
                    const source = JSON.parse(assignmentData);
                    // 同じ班なら何もしない
                    if(source.sourceDailyCrewId === dailyCrewId){
                        return;
                    }

                    // 移動先に同じ伝票がすでに配車されていないか確認
                    const assignedOrderIds = JSON.parse(card.dataset.assignedOrderIds || "[]");
                    if(assignedOrderIds.includes(source.orderId)){
                        return;
                    }

                    operationKey = `assignment:${source.dispatchAssignmentId}`;
                    if(busyAssignments.has(operationKey)) {operationKey = null; return;}
                    busyAssignments.add(operationKey);
                    await moveOrderToCrew(source, dailyCrewId);
                    await onAssigned?.({orderId: source.orderId, dailyCrewId});
                    return;
                }

                // 右側伝票からの配車
                const orderId = Number(event.dataTransfer.getData("text/plain"));
                if(!orderId){
                    return;
                }

                const assignedOrderIds = JSON.parse(card.dataset.assignedOrderIds || "[]");
                if(assignedOrderIds.includes(orderId)){
                    return;
                }

                operationKey = `order:${orderId}:crew:${dailyCrewId}`;
                if(busyAssignments.has(operationKey)) {operationKey = null; return;}
                busyAssignments.add(operationKey);
                await assignOrderToCrew(orderId, dailyCrewId);
                await onAssigned?.({orderId, dailyCrewId});
            } catch(error) {
                DialogService.error(error.message || "配車の更新に失敗しました。");
            } finally {
                if(operationKey) busyAssignments.delete(operationKey);
                dropping = false;
            }
        }
    );
}


/**
 * 伝票を班へ割当
 */
async function assignOrderToCrew(orderId, dailyCrewId){
    const visitOrder = await getNextVisitOrder(dailyCrewId);
    await DispatchAssignmentRepository.save({
        orderId,
        dailyCrewId,
        visitOrder,
        remarks: "",
        state: APP.cache.common.state.INITIAL
    });
}

export async function moveOrderToCrew(source, dailyCrewId) {
    if(!Number.isInteger(source.dispatchAssignmentId) || source.dispatchAssignmentId <= 0
        || !Number.isInteger(source.version) || source.version < 0) {
        throw new Error("配車情報が古くなっています。再読み込みしてください。");
    }
    const visitOrder = await getNextVisitOrder(dailyCrewId);
    await DispatchAssignmentRepository.save({
        dispatchAssignmentId: source.dispatchAssignmentId,
        version: source.version,
        dailyCrewId,
        visitOrder
    });
}

async function getNextVisitOrder(dailyCrewId){
    const result = await DispatchAssignmentRepository.findNextVisitOrder({
        dailyCrewId,
        state: APP.cache.common.state.INITIAL
    });

    return Number(result.data?.[0]?.nextVisitOrder ?? 1);
}