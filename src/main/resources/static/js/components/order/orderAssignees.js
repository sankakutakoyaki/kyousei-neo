"use strict";

export function orderAssigneesText(order) {
    const names=String(order?.assignedEmployeeNames ?? "").trim();
    const assigned=Number(order?.assignedCrewCount)>0;
    const text=names || (assigned ? "担当者未設定" : "未配車");
    const day=String(order?.assignedWorkDate ?? "");
    return !order?.visitDate && /^\d{4}-\d{2}-\d{2}$/.test(day) && (names || assigned) ? `前回（${day}）：${text}` : text;
}
