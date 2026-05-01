"use strict"

export const createRecycleListColumns = (controller) => [
    {
        field: "recycleNumber",
        label: "お問合せ管理票番号",
        sortable: true,
        class: "link-cell",
        format: (v) => {
            const s = String(v ?? "").replace(/\D/g, "").padStart(13, "0");
            return `${s.slice(0,4)}-${s.slice(4,12)}-${s.slice(12)}`;
        }
    },
    {
        field: "date",
        label: "使用日/引渡日",
        sortable: true,
        render: (item) => `
            <span>${item.useDate ?? "-----"}</span><br>
            <span>${item.DeliveryDate ?? "-----"}</span>
        `
    },
    {
        field: "maker",
        label: "品目/製造業者等名",
        sortable: true,
        render: (item) => `
            <span>${item.itemName ?? "-----"}</span><br>
            <span>${item.makerName ?? "-----"}</span>
        `
    },
    {
        field: "shipper",
        label: "小売業者",
        sortable: true,
        render: (item) => `
            <span>${item.companyName ?? "-----"}</span><br>
            <span>${item.officeName ?? "-----"}</span>
        `
    }
];