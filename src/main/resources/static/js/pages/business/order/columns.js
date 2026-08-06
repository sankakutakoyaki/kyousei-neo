"use strict"

export const createOrderListColumns = (controller) => [
    {
        field: "orderId",
        label: "受注ID/発注番号",
        sortable: true,
        render: (item) => `
            <span>${String(item.orderId).padStart(6, "0") ?? "-----"}</span><br>
            <span>${item.requestNumber ?? "-----"}</span>
        `
    },
    {
        field: "date",
        label: "訪問日/時間",
        sortable: true,
        render: (item) => `
            <span>${item.visitDate ?? "-----"}</span><br>
            <span>${item.visitTime ?? "-----"}</span>
        `
    },
    {
        field: "shipper",
        label: "小売業者",
        sortable: true,
        render: (item) => `
            <span>${item.primeConstractorName ?? "-----"}</span><br>
            <span>${item.primeConstractorOfficeName ?? "-----"}</span>
        `
    },
    {
        field: "title",
        label: "住所/件名",
        sortable: true,
        render: (item) => `
            <span>${item.fullAddress ?? "-----"}</span><br>
            <span>${item.title ?? "-----"}</span>
        `
    }
];