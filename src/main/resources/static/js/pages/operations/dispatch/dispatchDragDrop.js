"use strict"

import { DispatchAssignmentRepository }
    from "../../../repositories/operations/dispatch/DispatchAssignmentRepository.js";


/**
 * 班カードをドロップ対象にする
 */
export function initCrewDrop(
    card,
    {
        onAssigned
    } = {}
){

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


            const dailyCrewId =
                Number(
                    card.dataset.dailyCrewId
                );

            if(!dailyCrewId){
                return;
            }


            // 班内伝票からの移動
            const assignmentData =
                event.dataTransfer.getData(
                    "application/x-dispatch-assignment"
                );

            if(assignmentData){

                const source =
                    JSON.parse(
                        assignmentData
                    );

                if(
                    source.sourceDailyCrewId ===
                    dailyCrewId
                ){
                    return;
                }

                await moveOrderToCrew(
                    source.dispatchAssignmentId,
                    source.orderId,
                    dailyCrewId
                );

                await onAssigned?.({
                    orderId:
                        source.orderId,

                    dailyCrewId
                });

                return;
            }


            // 右側伝票からの配車
            const orderId =
                Number(
                    event.dataTransfer.getData(
                        "text/plain"
                    )
                );

            if(!orderId){
                return;
            }


            const assignedOrderIds =
                JSON.parse(
                    card.dataset.assignedOrderIds
                    || "[]"
                );

            if(
                assignedOrderIds.includes(
                    orderId
                )
            ){
                return;
            }


            await assignOrderToCrew(
                orderId,
                dailyCrewId
            );

            await onAssigned?.({
                orderId,
                dailyCrewId
            });
        }
    );
}


/**
 * 伝票を班へ割当
 */
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
}

async function moveOrderToCrew(
    dispatchAssignmentId,
    orderId,
    dailyCrewId
){

    // 新しい班へ登録
    await DispatchAssignmentRepository.save({
        orderId,
        dailyCrewId,
        visitOrder: 0,
        remarks: "",
        state:
            APP.cache.common.state.INITIAL
    });


    // 元の班との割当を解除
    await DispatchAssignmentRepository.deleteByIds([
        dispatchAssignmentId
    ]);
}