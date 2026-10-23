"use strict";

import { initCommon } from "../../../bootstrap/initPage.js";
import { registerController } from "../../../application/controllerRegistry.js";
import { getToday } from "../../../util/time.js";
import { FormController } from "../../../application/FormController.js";

export async function init() {
    await initCommon();

    const controller = new MyOrderPageController();
    registerController("myOrders", controller);

    controller.init();
    await controller.refresh();
}

class MyOrderPageController {
    constructor() {
        this.root = document.querySelector(".myorder-page");
        this.dateInput = this.root.querySelector("#myorder-date");
        this.list = this.root.querySelector("#myorder-list");
        this.count = this.root.querySelector("#myorder-count");
        this.requestId = 0;
        this.detailForm = new FormController({
            formId: "myorder-form",
            key: "orderId",
            idKey: "orderId",
            controller: this,
            submitText: "保存",
            cancelText: "閉じる"
        });
    }

    init() {
        this.dateInput.value = getToday();
        this.dateInput.addEventListener("change", () => this.refresh());
        this.list.addEventListener("click", async (event) => {
            const button = event.target.closest("[data-myorder-detail]");
            if (!button) return;
            const orderId = Number(button.dataset.myorderDetail);
            if (!Number.isSafeInteger(orderId) || orderId <= 0) return;
            await this.openDetail(orderId);
        });
    }

    async moveDate(days) {
        const date = this.dateInput.valueAsDate;
        if (!date) return;

        date.setUTCDate(date.getUTCDate() + days);
        this.dateInput.valueAsDate = date;

        await this.refresh();
    }

    async refresh() {
        const requestId = ++this.requestId;
        const date = this.dateInput.value;
        if (!date) return;

        this.list.replaceChildren();
        this.list.appendChild(this.message("読み込み中..."));

        try {
            const response = await fetch(
                `/api/my-orders?date=${encodeURIComponent(date)}`,
                { credentials: "same-origin" }
            );
            if (!response.ok) {throw new Error(`HTTP ${response.status}`);}
            const orders = await response.json();
            // 日付変更中に古いリクエストが返ってきた場合は無視
            if (requestId !== this.requestId || !this.root.isConnected) {return;}
            this.render(orders);
        } catch (error) {
            if (requestId !== this.requestId || !this.root.isConnected) {return;}
            console.error("担当伝票取得エラー:", error);
            this.count.textContent = "取得失敗";
            this.list.replaceChildren();
            this.list.appendChild(this.message("担当伝票を取得できませんでした。"));
        }
    }
    render(orders) {
        this.list.replaceChildren();
        this.count.textContent = `${orders.length}件`;

        if (orders.length === 0) {
            this.list.appendChild(this.message("この日の担当伝票はありません。"));
            return;
        }
        const crews = new Map();
        for (const order of orders) {
            if (!crews.has(order.dailyCrewId)) {
                crews.set(order.dailyCrewId, []);
            }
            crews.get(order.dailyCrewId).push(order);
        }
        for (const [crewId, crewOrders] of crews) {
            this.list.appendChild(this.createCrewSection(crewId, crewOrders));
        }
    }
    createCrewSection(crewId, orders) {
        const section = document.createElement("section");
        section.className = "myorder-crew";

        const header = document.createElement("div");
        header.className = "myorder-crew-header";

        const vehicleId = orders[0].vehicleId;
        header.textContent = `車両ID：${vehicleId ?? "未設定"}`;
        section.appendChild(header);

        orders.sort((a, b) => (a.visitOrder ?? 0) - (b.visitOrder ?? 0));
        for (const order of orders) {
            section.appendChild(this.createOrderCard(order));
        }
        return section;
    }

    createOrderCard(order) {
        const card = document.createElement("article");
        card.className = "myorder-card";

        const top = document.createElement("div");
        top.className = "myorder-card-top";

        const orderNumber = document.createElement("span");
        orderNumber.className = "myorder-number";
        orderNumber.textContent = `${order.visitOrder ?? "-"}件目`;

        const visitTime = document.createElement("span");
        visitTime.className = "myorder-time";
        visitTime.textContent = order.visitTime || "時間未設定";

        top.append(orderNumber, visitTime);

        const title = document.createElement("div");
        title.className = "myorder-title";
        title.textContent = order.title || "お客様名未設定";

        const address = document.createElement("div");
        address.className = "myorder-address";
        address.textContent = order.fullAddress || "住所未設定";

        const remarks = document.createElement("div");
        remarks.className = "myorder-remarks";
        remarks.textContent = order.remarks || "";

        card.append(top, title, address);

        if (order.remarks) {
            card.appendChild(remarks);
        }

        const detailButton = document.createElement("button");
        detailButton.type = "button";
        detailButton.className = "normal-btn";
        detailButton.textContent = "詳細を見る";
        detailButton.dataset.myorderDetail = order.orderId;

        card.appendChild(detailButton);

        return card;
    }

    message(text) {
        const p = document.createElement("p");
        p.className = "myorder-empty";
        p.textContent = text;
        return p;
    }

    async openDetail(orderId) {
        try {
            const [detailResponse, linesResponse] = await Promise.all([
                fetch(`/api/my-orders/${orderId}`, {
                    credentials: "same-origin"
                }),
                fetch(`/api/my-orders/${orderId}/lines`, {
                    credentials: "same-origin"
                })
            ]);

            if (!detailResponse.ok || !linesResponse.ok) {
                throw new Error("担当伝票の取得に失敗しました。");
            }

            const [order, lines] = await Promise.all([
                detailResponse.json(),
                linesResponse.json()
            ]);

            if (!this.root.isConnected) return;

            this.renderDetailLines(lines);

            await this.detailForm.open(order, {
                readOnly: true
            });

        } catch (error) {
            console.error(error);
            alert(error.message || "担当伝票を取得できませんでした。");
        }
    }

    async executeAction(action, el) {
        switch (action) {
            case "previous-date":
                await this.moveDate(-1);
                break;
            case "next-date":
                await this.moveDate(1);
                break;
            case "today":
                this.dateInput.value = getToday();
                await this.refresh();
                break;
        }
    }

    renderDetailLines(lines) {
        const itemList = document.getElementById("myorder-item-list");
        const workList = document.getElementById("myorder-work-list");
        if (!itemList || !workList) return;

        this.renderLineList(
            itemList,
            lines.items ?? [],
            "itemName",
            "itemQuantity",
            ["itemMaker", "itemModel", "janCode", "remarks"]
        );

        this.renderLineList(
            workList,
            lines.works ?? [],
            "orderWorkName",
            "orderWorkQuantity",
            ["orderWorkCode", "remarks"]
        );
    }

    renderLineList(container, rows, nameKey, quantityKey, extraKeys) {
        container.replaceChildren();

        if (rows.length === 0) {
            container.appendChild(this.message("登録されていません。"));
            return;
        }

        for (const row of rows) {
            const card = document.createElement("div");
            card.className = "myorder-detail-card";

            const title = document.createElement("div");
            title.className = "myorder-detail-card-title";
            title.textContent = row[nameKey] || "名称未設定";

            const quantity = document.createElement("div");
            quantity.className = "myorder-detail-card-quantity";
            quantity.textContent = `数量：${row[quantityKey] ?? "-"}`;

            card.append(title, quantity);

            for (const key of extraKeys) {
                const value = row[key];
                if (value == null || value === "") continue;

                const line = document.createElement("div");
                line.className = "myorder-detail-card-extra";
                line.textContent = String(value);
                card.appendChild(line);
            }

            container.appendChild(card);
        }
    }
}