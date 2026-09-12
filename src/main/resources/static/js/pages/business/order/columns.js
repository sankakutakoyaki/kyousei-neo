"use strict";
const escapeHtml = value => String(value ?? "-----").replace(/[&<>"']/g, char => ({"&":"&amp;", "<":"&lt;", ">":"&gt;", '"':"&quot;", "'":"&#39;"}[char]));

export const createOrderListColumns = (controller) => [
    {
        field: "orderId",
        label: "受注ID/発注番号",
        sortable: true,
        render: (item) => `
            <span>${String(item.orderId).padStart(6, "0") ?? "-----"}</span><br>
            <span>${escapeHtml(item.requestNumber)}</span>
        `
    },
    {
        field: "date",
        label: "訪問日/時間",
        sortable: true,
        render: (item) => `
            <span>${escapeHtml(item.visitDate)}</span><br>
            <span>${escapeHtml(item.visitTime)}</span>
        `
    },
    {
        field: "shipper",
        label: "小売業者",
        sortable: true,
        render: (item) => `
            <span>${escapeHtml(item.primeConstractorName)}</span><br>
            <span>${escapeHtml(item.primeConstractorOfficeName)}</span>
        `
    },
    {
        field: "title",
        label: "住所/件名",
        sortable: true,
        render: (item) => `
            <span>${escapeHtml(item.fullAddress)}</span><br>
            <span>${escapeHtml(item.title)}</span>
        `
    },
    {
        field: "mobileAction",
        label: "",
        class: "mobile-order-detail",
        render: item => `<button type="button" class="normal-btn" data-action="view-order" data-id="${Number(item.orderId)}">詳細</button>`
    }
];

export const createOrderItemFormListColumns = () => [
    {
        field: "itemCode",
        label: "JANコード/商品名",
        render: (item) => `
            <span>${escapeHtml(item.janCode)}</span><br>
            <span>${escapeHtml(item.itemName)}</span>
        `
    },
    {
        field: "itemName",
        label: "メーカー/型番",
        render: (item) => `
            <span>${escapeHtml(item.itemMaker)}</span><br>
            <span>${escapeHtml(item.itemModel)}</span>
        `
    },
    {
        field: "itemQuantity",
        label: "数量",
        render: (item) => `
            <span>${escapeHtml(item.itemQuantity)}</span>
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

export const createOrderWorkFormListColumns = () => [
    {
        field: "orderWorkName",
        label: "作業名",
        render: (item) => `
            <span>${escapeHtml(item.orderWorkName)}</span>
        `
    },
    {
        field: "orderWorkPrice",
        label: "金額",
        render: (item) => `
            <span>${escapeHtml(item.orderWorkPrice)}</span>
        `
    },
    {
        field: "orderWorkQuantity",
        label: "数量",
        render: (item) => `
            <span>${escapeHtml(item.orderWorkQuantity)}</span>
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

export const createOrderItemListColumns = () => [
    {
        field: "orderId",
        label: "受注ID/発注番号",
        sortable: true,
        render: (item) => `
            <span>${Number(item.orderId) > 0 ? String(item.orderId).padStart(6, "0") : "-----"}</span><br>
            <span>${escapeHtml(item.requestNumber)}</span>
        `
    },
    {
        field: "shipper",
        label: "小売業者",
        sortable: true,
        render: (item) => `
            <span>${escapeHtml(item.primeConstractorName)}</span><br>
            <span>${escapeHtml(item.primeConstractorOfficeName)}</span>
        `
    },
    {
        field: "janCode",
        label: "JANコード/商品名",
        render: (item) => `
            <span>${escapeHtml(item.janCode)}</span><br>
            <span>${escapeHtml(item.itemName)}</span>
        `
    },
    {
        field: "itemName",
        label: "メーカー/型番",
        render: (item) => `
            <span>${escapeHtml(item.itemMaker)}</span><br>
            <span>${escapeHtml(item.itemModel)}</span>
        `
    },
    {
        field: "itemQuantity",
        label: "数量",
        class: "text-right",
        render: (item) => `
            <span>${escapeHtml(item.itemQuantity)}</span>
        `
    },
    {
        field: "date",
        label: "入荷状況 / 予定日",
        sortable: true,
        render: (item) => {
            const received = Number(item.receivedQuantity ?? 0);
            const total = Number(item.itemQuantity);
            const complete = total > 0 && received >= total;
            const status = complete ? escapeHtml(item.arrivalDate) : received > 0 ? `一部入荷 ${received} / ${total}` : "未入荷";
            return `<div class="arrival-status-row"><div><span>${status}</span><br>
                <small>予定：${escapeHtml(item.expectedArrivalDate)}</small></div>
                <button type="button" class="normal-btn" data-action="arrival-item" data-id="${Number(item.orderItemId)}">${complete ? "履歴" : "入荷"}</button></div>`;
        }
    }
];
