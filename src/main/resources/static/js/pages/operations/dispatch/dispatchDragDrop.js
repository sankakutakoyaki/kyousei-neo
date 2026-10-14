"use strict"

/**
 * 班カードをドロップ対象にする
 */

import { DispatchAssignmentRepository } from "../../../repositories/operations/dispatch/DispatchAssignmentRepository.js";
import { DialogService } from "../../../core/ui/dialog/DialogService.js";

const busyAssignments = new Set();
export function initCrewDrop(card, {session, onAssigned} = {}){
    let dropping = false;
    card.addEventListener("dragover", event => {
        event.preventDefault();
        event.dataTransfer.dropEffect = "move";
        card.classList.add("drag-over");
    });
    card.addEventListener("dragleave", event => {
        if(card.contains(event.relatedTarget)){return;}
        card.classList.remove("drag-over");
    });
    card.addEventListener("drop", async event => {
        event.preventDefault();
        card.classList.remove("drag-over");
        if(dropping || !session.isActive()) return;
        dropping = true;
        let operationKey;
        try {
            const dailyCrewId = Number(card.dataset.dailyCrewId);
            if(!dailyCrewId){return;}

            // 班内伝票からの移動
            const assignmentData = event.dataTransfer.getData("application/x-dispatch-assignment");
            if(assignmentData){
                const source = JSON.parse(assignmentData);
                // 同じ班なら何もしない
                if(source.sourceDailyCrewId === dailyCrewId){return;}
                // 移動先に同じ伝票がすでに配車されていないか確認
                const assignedOrderIds = JSON.parse(card.dataset.assignedOrderIds || "[]");
                if(assignedOrderIds.includes(source.orderId)){return;}

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
            const saved = await assignOrderToCrew(orderId, dailyCrewId, session);
            if (saved && session.isActive()) {await onAssigned?.({orderId, dailyCrewId});}
        } catch(error) {
            DialogService.error(error.message || "配車の更新に失敗しました。");
        } finally {
            if(operationKey) busyAssignments.delete(operationKey);
            dropping = false;
        }
    });
}

/**
 * 伝票を班へ割当
 */
async function assignOrderToCrew(orderId, dailyCrewId, session) {
    // 最新の配車状況を取得
    const result = await DispatchAssignmentRepository.findByOrderId(orderId);
    if (!session.isActive()) {return false;}

    const assignments = result.data ?? [];
    // 別の班に登録されている受注を抽出
    const others = assignments.filter(
        item => Number(item.dailyCrewId) !== dailyCrewId
    );
    if (others.length > 0) {
        const vehicles = others.map(item => {
            return item.vehicleName || `班ID:${item.dailyCrewId}`;
        });
        const message =
            `この伝票は、すでに別の車両に配車されています。\n\n` +
            `登録済み：${vehicles.join("、")}\n\n` +
            `この車両にも追加登録しますか？`;
        const confirmed = await DialogService.confirm(message);
        if (!confirmed || !session.isActive()) {return false;}
    }
    // 保存直前に訪問順序を取得
    await DispatchAssignmentRepository.create({
        orderId,
        dailyCrewId,
        remarks: "",
        state: APP.cache.common.state.INITIAL
    });
    return true;
}

export async function moveOrderToCrew(source, dailyCrewId) {
    if (!Number.isInteger(source.dispatchAssignmentId) ||
        source.dispatchAssignmentId <= 0 ||
        !Number.isInteger(source.version) ||
        source.version < 0) {
        throw new Error("配車情報が古くなっています。再読み込みしてください。");
    }
    await DispatchAssignmentRepository.move({
        dispatchAssignmentId: source.dispatchAssignmentId,
        version: source.version,
        dailyCrewId
    });
}