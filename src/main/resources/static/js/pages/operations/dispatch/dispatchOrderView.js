"use strict"

import { cloneTemplate } from "../../../core/dom/cloneTemplate.js";

/**
 * 伝票一覧描画
 */
export function renderDispatchOrders(orders, status, {onDragStart, onDragEnd} = {}){
    const area = document.getElementById("dispatch-order-list");
    if(!area) return;
    area.replaceChildren();

    const list = orders.filter(order => {
        if(status === "unassigned"){
            return order.assignmentCount === 0;
        }
        if(status === "assigned"){
            return order.assignmentCount > 0;
        }
        return true;
    });


    if(list.length === 0){
        const empty = document.createElement("div");
        empty.className = "dispatch-empty";

        if(status === "assigned"){
            empty.textContent = "配車済み伝票はありません";
        } else if(status === "all"){
            empty.textContent = "伝票はありません";
        } else {
            empty.textContent = "未配車伝票はありません";
        }

        area.appendChild(empty);
        return;
    }

    list.forEach(order => {
        area.appendChild(createDispatchOrderCard(order, {onDragStart, onDragEnd}));
    });
}

/**
 * 伝票カード
 */
function createDispatchOrderCard(order, {onDragStart, onDragEnd} = {}){
    const card = cloneTemplate("dispatch-order-card-template");
    card.dataset.orderId = order.orderId;
    card.addEventListener(
        "dragstart",
        event => {
            event.dataTransfer.effectAllowed = "move";
            event.dataTransfer.setData("text/plain", String(order.orderId));
            card.classList.add("dragging");
            onDragStart?.(order, event);
        }
    );
    card.addEventListener(
        "dragend",
        event => {
            card.classList.remove("dragging");
            onDragEnd?.(order, event);
        }
    );

    card.querySelector('[data-field="title"]').textContent = order.title || "名称未設定";
    card.querySelector('[data-field="visitTime"]').textContent = order.visitTime || "";
    card.querySelector('[data-field="fullAddress"]').textContent = order.fullAddress || "";
    const assigned = card.querySelector(".dispatch-order-assigned");
    if(order.assignmentCount > 0) assigned.textContent = `配車済み ${order.assignmentCount}班`;
    else assigned.remove();

    return card;
}