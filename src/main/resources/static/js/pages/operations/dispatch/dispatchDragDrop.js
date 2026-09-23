"use strict"

import { DispatchAssignmentRepository }
    from "../../../repositories/operations/dispatch/DispatchAssignmentRepository.js";

/**
 * 班カードをドロップ対象にする
 */
export function initCrewDrop(card, {onAssigned} = {}){
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

                await moveOrderToCrew(source.dispatchAssignmentId, source.orderId, dailyCrewId);
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

            await assignOrderToCrew(orderId, dailyCrewId);
            await onAssigned?.({orderId, dailyCrewId});
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

async function moveOrderToCrew(dispatchAssignmentId, orderId, dailyCrewId){
    const visitOrder = await getNextVisitOrder(dailyCrewId);

    // 移動先の最後へ追加
    await DispatchAssignmentRepository.save({
        orderId,
        dailyCrewId,
        visitOrder,
        remarks: "",
        state: APP.cache.common.state.INITIAL
    });

    // 元の班から解除
    await DispatchAssignmentRepository.deleteByIds([dispatchAssignmentId]);
}

async function getNextVisitOrder(dailyCrewId){
    const result = await DispatchAssignmentRepository.findNextVisitOrder({
        dailyCrewId,
        state: APP.cache.common.state.INITIAL
    });

    return Number(result.data?.[0]?.nextVisitOrder ?? 1);
}