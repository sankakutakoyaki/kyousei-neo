"use strict"

import { cloneTemplate } from "../../../core/dom/cloneTemplate.js";
import { DialogService } from "../../../core/ui/dialog/DialogService.js";

/**
 * Query結果を班単位にまとめる
 */
export function groupDispatchCrews(rows){
    const map = new Map();
    rows.forEach(row => {
        const dailyCrewId = row.dailyCrewId;
        if(!map.has(dailyCrewId)){
            map.set(
                dailyCrewId,
                {
                    dailyCrewId,
                    vehicleId: row.vehicleId,
                    vehicleName: row.vehicleName,
                    registrationArea: row.registrationArea,
                    registrationClass: row.registrationClass,
                    registrationKana: row.registrationKana,
                    registrationNumber: row.registrationNumber,
                    members: []
                }
            );
        }
        if(row.employeeId){
            map.get(dailyCrewId)
                .members
                .push({
                    dailyCrewMemberId: row.dailyCrewMemberId,
                    employeeId: row.employeeId,
                    employeeName: row.employeeName,
                    role: row.role,
                    shiftType: row.shiftType,
                    startTime: row.startTime,
                    endTime: row.endTime
                });
        }
    });

    return [
        ...map.values()
    ];
}

/**
 * 班一覧描画
 */
export function renderDispatchCrews(
    crews,
    {
        initCrewDrop,
        onReorder,
        expandedCrewIds,
        isActive = () => true
    } = {}
){
    const area = document.getElementById("dispatch-crew-list");
    if(!area) return;
    area.replaceChildren();

    crews.forEach(crew => {area.appendChild(
        createCrewCard(
            crew,
            {
                initCrewDrop,
                onReorder,
                expandedCrewIds,
                isActive
            }
        ));
    });
}

/**
 * 班カード
 */
function createCrewCard(
    crew,
    {
        initCrewDrop,
        onReorder,
        expandedCrewIds,
        isActive = () => true
    } = {}
){
    const card = cloneTemplate("dispatch-crew-card-template");
    card.dataset.dailyCrewId = crew.dailyCrewId;
    card.dataset.assignedOrderIds =
        JSON.stringify(
            (crew.assignments ?? []).map(assignment => assignment.orderId)
        );

    card.querySelector('[data-field="vehicleName"]').textContent = crew.vehicleName ?? "";
    card.querySelector('[data-field="registration"]').textContent = [
        crew.registrationArea, crew.registrationClass, crew.registrationKana, crew.registrationNumber
    ].filter(Boolean).join(" ");
    card.querySelector('[data-action="delete-daily-crew"]')._crew = crew;
    const members = card.querySelector(".dispatch-crew-members");
    const rows = crew.members.length ? crew.members : [null];
    rows.forEach((member, index) => {
        const row = createCrewMember(member);
        const edit = row.querySelector('[data-action="edit-crew-members"]');
        if(index === rows.length - 1) edit._crew = crew;
        else edit.remove();
        members.appendChild(row);
    });

    const assignmentCount = crew.assignments?.length ?? 0;
    const orders = card.querySelector(".dispatch-assignment-list");
    const expanded = expandedCrewIds?.has(crew.dailyCrewId) ?? false;
    orders.className = expanded
        ? "dispatch-assignment-list"
        : "dispatch-assignment-list collapsed";

    renderCrewAssignments(
        orders,
        crew.assignments ?? [],
        {
            onReorder,
            dailyCrewId: crew.dailyCrewId,
            isActive
        }
    );

    card._orders = orders;

    const badge = card.querySelector(".dispatch-assignment-badge");
    if(assignmentCount > 0) badge.textContent = assignmentCount;
    else badge.remove();

    if(initCrewDrop){
        initCrewDrop(card);
    }

    return card;
}

/**
 * 班員
 */
function createCrewMember(member) {
    const row = cloneTemplate("dispatch-crew-member-template");
    const role = row.querySelector('[data-field="role"]');
    const name = row.querySelector('[data-field="employeeName"]');
    const warning = row.querySelector(".dispatch-member-warning");
    if(!member) {
        role.classList.remove("dispatch-member-role");
        name.classList.add("dispatch-member-unassigned");
        name.textContent = "乗務員未設定";
        warning.remove();
    } else {
        role.textContent = member.role === 1 ? "担当" : "助手";
        name.textContent = member.employeeName ?? "";
        if(member.shiftType !== 1) warning.textContent = getShiftStatusText(member.shiftType);
        else warning.remove();
    }
    return row;
}

/**
 * シフト状態
 */
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

/**
 * 班内伝票
 */
function renderCrewAssignments(area, assignments, {onReorder, dailyCrewId, isActive} = {}){
    area.replaceChildren();
    assignments.forEach(assignment => {
        const item = cloneTemplate("dispatch-assignment-template");
        item.dataset.dispatchAssignmentId = assignment.dispatchAssignmentId;
        item.dataset.orderId = assignment.orderId;
        item.dataset.version = assignment.version;
        item.addEventListener("dragstart",
            event => {
                event.stopPropagation();
                event.dataTransfer.effectAllowed = "move";
                event.dataTransfer.setData(
                    "application/x-dispatch-assignment",
                    JSON.stringify({
                        dispatchAssignmentId: assignment.dispatchAssignmentId,
                        orderId: assignment.orderId,
                        sourceDailyCrewId: dailyCrewId,
                        version: assignment.version
                    })
                );
                item.classList.add("dragging");
            }
        );
        item.addEventListener("dragend", () => {item.classList.remove("dragging");});
        item.addEventListener("dragover", event => {
            const assignmentData = event.dataTransfer.getData("application/x-dispatch-assignment");
            if(!assignmentData){
                return;
            }
            const source = JSON.parse(assignmentData);
            // 別班の伝票はここでは並べ替えない
            if(source.sourceDailyCrewId !== dailyCrewId){
                return;
            }
            event.preventDefault();
            item.classList.add("drag-over-order");
        });
        item.addEventListener("dragleave", () => {item.classList.remove("drag-over-order");});
        item.addEventListener("drop", async event => {
            if(!isActive() || area.dataset.reordering === "true") return;
            try {
                const assignmentData = event.dataTransfer.getData("application/x-dispatch-assignment");
                if(!assignmentData){
                    return;
                }
                const source = JSON.parse(assignmentData);
                // 同じ班内だけ並べ替え
                if(source.sourceDailyCrewId !== dailyCrewId){
                    return;
                }
                event.preventDefault();
                event.stopPropagation();
                item.classList.remove("drag-over-order");
                const sourceItem = area.querySelector(`[data-dispatch-assignment-id="${source.dispatchAssignmentId}"]`);
                if(!sourceItem || sourceItem === item){
                    return;
                }
                const rect = item.getBoundingClientRect();
                const insertAfter = event.clientY > rect.top + rect.height / 2;
                if(insertAfter){
                    item.after(sourceItem);
                } else {
                    item.before(sourceItem);
                }
                const items = [...area.querySelectorAll(".dispatch-assignment-item")].map((element, index) => ({
                    dispatchAssignmentId: Number(element.dataset.dispatchAssignmentId),
                    visitOrder: index + 1,
                    version: Number(element.dataset.version)
                }));
                area.dataset.reordering = "true";
                await onReorder?.(items);
            } catch(error) {
                DialogService.error(error.message || "配車順の更新に失敗しました。再読み込みしてください。");
            } finally {delete area.dataset.reordering;}
        });
        item.querySelector('[data-field="title"]').textContent = assignment.title || "名称未設定";
        item.querySelector('[data-field="fullAddress"]').textContent = assignment.fullAddress || "";
        item.querySelector('[data-action="unassign-order"]')._assignment = assignment;
        area.appendChild(item);
    });
}