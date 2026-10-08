"use strict"

export const createVehicleColumns = () => [
    {
        field: "vehicleId",
        label: "ID",
        sortable: true,
        class: "link-cell",
        format: (v) => String(v).padStart(4, "0")
    },
    {
        field: "officeName",
        label: "営業所",
        sortable: true,
        default: "登録なし"
    },
    {
        field: "registrationNumber",
        label: "車番",
        sortable: true,
        render: (item) => {
            const area = item.registrationArea ?? "";
            const classification = item.registrationClass ?? "";
            const kana = item.registrationKana ?? "";
            const number = item.registrationNumber ?? "";

            return `${area} ${classification} ${kana} ${number}`.trim();
        }
    },
    {
        field: "vehicleName",
        label: "車種",
        sortable: true,
        default: ""
    },
    {
        field: "inspectionExpirationDate",
        label: "車検期限",
        sortable: true,
        default: ""
    },
    {
        field: "defaultDriverNames",
        label: "初期担当",
        sortable: false,
        default: "未設定"
    },
    {
        field: "defaultAssistantNames",
        label: "初期助手",
        sortable: false,
        default: "未設定"
    }
];