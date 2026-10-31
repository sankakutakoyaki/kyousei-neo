"use strict";

import { OrderRepository } from "../../repositories/business/order/OrderRepository.js";
import { formatDate } from "../../util/time.js";

import { orderHistoryChanges } from "./orderHistoryChanges.js";

const eventLabels = {
    ORDER_CREATED: "受注を登録", ORDER_SAVED: "受注を保存",
    DISPATCH_CREATED: "配車を登録", DISPATCH_MOVED: "配車を移動",
    DISPATCH_REMOVED: "配車を解除", DISPATCH_RESET: "配車を一括解除",
    DISPATCH_REORDERED: "訪問順を保存"
};

export class OrderHistoryList {
    constructor(root, tab) {
        this.root = root;
        this.status = root?.querySelector("[data-order-history-status]");
        this.list = root?.querySelector("[data-order-history-list]");
        this.refresh = root?.querySelector('[data-order-history-action="refresh"]');
        this.more = root?.querySelector('[data-order-history-action="more"]');
        tab?.addEventListener("click", () => {
            if (!this.loaded) void this.load();
        });
        tab?.addEventListener("keydown", event => {
            if (event.key === "Enter" || event.key === " ") {
                event.preventDefault();
                tab.click();
            }
        });
        this.refresh?.addEventListener("click", () => void this.load(true));
        this.more?.addEventListener("click", () => void this.load());
        this.generation = 0;
    }

    open(orderId, canApply = () => true) {
        this.generation++;
        this.orderId = orderId || null;
        this.canApply = canApply;
        this.loaded = false;
        this.busy = false;
        this.hasMore = false;
        this.cursor = null;
        this.count = 0;
        this.list?.replaceChildren();
        this.message(this.orderId ? "履歴タブを開くと、受注の登録・保存履歴を表示します。" : "保存後に履歴を確認できます。");
        this.updateButtons();
    }

    close() {
        this.open(null);
    }

    message(text, error = false) {
        if (!this.status) return;
        this.status.textContent = text;
        this.status.classList.toggle("order-history-error", error);
    }

    updateButtons() {
        this.root?.setAttribute("aria-busy", String(this.busy));
        if (this.refresh) this.refresh.disabled = !this.orderId || this.busy;
        if (this.more) {
            this.more.hidden = !this.hasMore;
            this.more.disabled = this.busy;
        }
    }

    async load(reset = false) {
        if (!this.root || !this.orderId || this.busy || !this.canApply?.()) return;
        if (!reset && this.loaded && !this.hasMore) return;
        const generation = this.generation;
        const current = () => generation === this.generation && this.root.isConnected && this.canApply();
        this.busy = true;
        this.message("履歴を読み込んでいます…");
        this.updateButtons();
        try {
            const rows = await OrderRepository.findHistories({
                orderId: this.orderId,
                state: APP.cache.common.state.INITIAL,
                beforeHistoryId: reset ? null : this.cursor
            });
            if (!current()) return;
            if (reset) {
                this.list.replaceChildren();
                this.count = 0;
            }
            for (const row of rows) this.list.append(this.renderRow(row));
            this.count += rows.length;
            this.cursor = rows.at(-1)?.orderHistoryId ?? (reset ? null : this.cursor);
            this.hasMore = rows.length === 100;
            this.loaded = true;
            this.message(this.count ? `${this.count}件の履歴を表示しています。` : "受注の登録・保存履歴はまだありません。");
        } catch {
            if (current()) this.message("履歴を取得できませんでした。「更新」または「続きを表示」で再試行してください。", true);
        } finally {
            if (current()) {
                this.busy = false;
                this.updateButtons();
            }
        }
    }

    renderRow(row) {
        const item = document.createElement("li");
        const title = document.createElement("strong");
        title.textContent = eventLabels[row.eventType] ?? "受注の履歴";
        const time = document.createElement("time");
        time.textContent = row.occurredAt ? formatDate(row.occurredAt, "yyyy/MM/dd HH:mm:ss") : "日時不明";
        const editor = document.createElement("span");
        editor.textContent = `操作者：${row.editor || "不明"}`;
        item.append(title, time, editor);
        const {changes, message} = orderHistoryChanges(row);
        if (message) {
            const note = document.createElement("p");
            note.textContent = message;
            item.append(note);
        }
        if (changes.length) {
            const list = document.createElement("dl");
            list.className = "order-history-changes";
            for (const change of changes) {
                const group = document.createElement("div");
                const label = document.createElement("dt");
                label.textContent = change.label;
                const values = document.createElement("dd");
                const before = document.createElement("span");
                const after = document.createElement("span");
                before.textContent = `変更前：${change.before || "（未設定）"}`;
                after.textContent = `変更後：${change.after || "（未設定）"}`;
                values.append(before, after);
                group.append(label, values);
                list.append(group);
            }
            item.append(list);
        }
        return item;
    }
}
