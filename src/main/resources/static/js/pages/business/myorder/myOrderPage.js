"use strict";

import { initCommon } from "../../../bootstrap/initPage.js";
import { registerController } from "../../../application/controllerRegistry.js";
import { getToday } from "../../../util/time.js";

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
    }
    init() {
        this.dateInput.value = getToday();
        this.root.addEventListener("click", async (event) => {
            const button = event.target.closest("button[data-action]");
            if (!button || !this.root.contains(button)) return;
            switch (button.dataset.action) {
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
        });
        this.dateInput.addEventListener("change", () => this.refresh());
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
        if (order.remarks) {card.appendChild(remarks);}
        return card;
    }

    message(text) {
        const p = document.createElement("p");
        p.className = "myorder-empty";
        p.textContent = text;
        return p;
    }
}