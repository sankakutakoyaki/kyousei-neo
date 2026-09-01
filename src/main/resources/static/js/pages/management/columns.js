"use strict"

export const createTimeworksListColumns = () => [
    {
        field: "fullName",
        label: "担当者名",
        sortable: true,
        default: "-----"
    },
    {
        field: "startTime",
        label: "出勤",
        sortable: true,
        default: "-----",
        format: formatTime
    },
    {
        field: "endTime",
        label: "退勤",
        sortable: true,
        default: "-----",
        format: formatTime
    },
    {
        field: "officeName",
        label: "営業所",
        sortable: true,
        default: "-----"
    }
];

function formatTime(value) {
    if (!value) return "-----";
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return "-----";
    return `${String(date.getHours()).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}`;
}
