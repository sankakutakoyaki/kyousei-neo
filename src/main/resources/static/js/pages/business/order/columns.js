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

export const createOrderItemListColumns = () => [
    {
        field: "itemCode",
        label: "JANコード/商品名",
        render: (item) => `
            <span>${item.janCode ?? "-----"}</span><br>
            <span>${item.itemName ?? "-----"}</span>
        `
    },
    {
        field: "itemName",
        label: "メーカー/型番",
        render: (item) => `
            <span>${item.itemMaker ?? "-----"}</span><br>
            <span>${item.itemModel ?? "-----"}</span>
        `
    },
    {
        field: "itemQuantity",
        label: "数量",
        render: (item) => `
            <span>${item.itemQuantity ?? "-----"}</span>
        `
        // render: (item) => `
        //     <input
        //         class="normal-input frameless text-right"
        //         data-id="${item._tempId}"
        //         value="${item.quantity ?? 1}"
        //         min="1">
        // `
    },
    {    
        field: "action",
        label: "",
        render: (item) => `
            <button
                type="button"
                class="img-btn"
                data-action="delete-order-item"
                data-id="${item._tempId}">
                <img src="/icons/dust.png">
            </button>
        `
    }
];

export const createOrderWorkListColumns = () => [
    {
        field: "orderWorkName",
        label: "作業名",
        render: (item) => `
            <span>${item.orderWorkName ?? "-----"}</span>
        `
    },
    {
        field: "orderWorkPrice",
        label: "金額",
        render: (item) => `
            <span>${item.orderWorkPrice ?? "-----"}</span>
        `
    },
    {
        field: "orderWorkQuantity",
        label: "数量",
        render: (item) => `
            <span>${item.orderWorkQuantity ?? "-----"}</span>
        `
    },
    {    
        field: "action",
        label: "",
        render: (item) => `
            <button
                type="button"
                class="img-btn"
                data-action="delete-order-work"
                data-id="${item._tempId}">
                <img src="/icons/dust.png">
            </button>
        `
    }
];