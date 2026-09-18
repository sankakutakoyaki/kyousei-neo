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
        field: "manufacturer",
        label: "メーカー",
        sortable: true,
        default: ""
    },
    {
        field: "modelCode",
        label: "型式",
        sortable: true,
        default: ""
    },
    {
        field: "firstRegistrationYear",
        label: "初年度登録",
        sortable: true,
        render: (item) => {
            if (!item.firstRegistrationYear) return "";

            const month = item.firstRegistrationMonth
                ? String(item.firstRegistrationMonth).padStart(2, "0")
                : "";

            return month
                ? `${item.firstRegistrationYear}/${month}`
                : String(item.firstRegistrationYear);
        }
    },
    {
        field: "inspectionExpirationDate",
        label: "車検期限",
        sortable: true,
        default: ""
    },
    {
        field: "frontTireSize",
        label: "前輪タイヤ",
        sortable: true,
        default: ""
    },
    {
        field: "rearTireSize",
        label: "後輪タイヤ",
        sortable: true,
        default: ""
    },
    {
        field: "vehicleHeight",
        label: "車高",
        sortable: true,
        default: "",
        format: (v) => v == null ? "" : `${v} mm`
    },
    {
        field: "maximumLoad",
        label: "最大積載量",
        sortable: true,
        default: "",
        format: (v) => v == null ? "" : `${v} kg`
    }
];