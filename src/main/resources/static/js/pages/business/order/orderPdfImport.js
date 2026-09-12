"use strict"

import { getController } from "../../../application/controllerRegistry.js";
import { mapOcrCandidate } from "./mapOcrCandidate.js";
import { apiFetch } from "../../../core/api/apiFetch.js";
import { openMsgDialog } from "../../../core/ui/dialog/dialogCore.js";

import { OrderPdfImportQueue } from "./OrderPdfImportQueue.js";


let importQueue = null;

export function initOrderPdfImport() {
    importQueue?.dispose();

    const panel = document.getElementById("order-pdf-drop-area");
    const queue = new OrderPdfImportQueue({
        upload: async (file, shipperId) => {
            const data = new FormData();
            data.append("primeConstractorId", shipperId);
            data.append("file", file);
            const result = await apiFetch("/api/order/import/pdf", {
                method: "POST", data, timeout: 120000, showProcessing: true
            });
            return result.data;
        },
        recognize: async (id) => {
            const result = await apiFetch(`/api/order/import/${encodeURIComponent(id)}/ocr/hei-wado`, {
                method: "POST", timeout: 300000, showProcessing: true
            });
            return result.data;
        },
        review: entry => {
            if (importQueue !== queue || !panel?.isConnected) { queue.dispose(); return; }
            return openOcrCandidateForm(entry);
        },
        onChange: () => {
            if (importQueue !== queue) return;
            if (!panel?.isConnected) { queue.dispose(); return; }
            renderOrderPdfImportList();
        }
    });
    importQueue = queue;
    initOrderPdfDrop();
    document.getElementById("primeConstractor01")?.addEventListener("change", renderOrderPdfImportList);
    const fileInput = document.getElementById("order-pdf-file-input");
    document.getElementById("order-pdf-file-button")?.addEventListener("click", () => fileInput?.click());
    fileInput?.addEventListener("change", () => {
        const files = Array.from(fileInput.files ?? []);
        fileInput.value = "";
        enqueueOrderPdfs(files);
    });
    renderOrderPdfImportList();

}

function initOrderPdfDrop() {
    const dropArea = document.getElementById("order-pdf-drop-area");
    if (!dropArea) return;
    let dragDepth = 0;
    dropArea.addEventListener("dragenter", event => {
        event.preventDefault();
        dragDepth += 1;
        dropArea.classList.add("drag-over");
    });
    dropArea.addEventListener("dragover", event => event.preventDefault());
    dropArea.addEventListener("dragleave", () => {
        dragDepth = Math.max(0, dragDepth - 1);
        if (!dragDepth) dropArea.classList.remove("drag-over");
    });
    dropArea.addEventListener("drop", event => {
        event.preventDefault();
        dragDepth = 0;
        dropArea.classList.remove("drag-over");
        enqueueOrderPdfs(Array.from(event.dataTransfer?.files ?? []));
    });
}

function enqueueOrderPdfs(files) {
    if (!files.length) return;
    const select = document.getElementById("primeConstractor01");
    const shipperId = select?.value?.trim();
    const shipperName = select?.selectedOptions?.[0]?.textContent?.trim() || shipperId;
    try {
        // Freeze the shipper at submission, even if the selection changes later.
        void importQueue.enqueue(files, shipperId, shipperName);
    } catch (error) {
        openMsgDialog({message: error.message, color: "red"});
    }
}

function renderOrderPdfImportList() {
    const message = document.getElementById("order-pdf-import-message");
    const items = document.getElementById("order-pdf-import-items");
    if (!message || !items || !importQueue) return;
    const queue = importQueue;
    const completed = queue.entries.filter(entry => entry.status === "completed").length;
    const failed = queue.entries.filter(entry => entry.status === "failed").length;
    const confirmed = queue.entries.filter(entry => entry.confirmed).length;
    message.textContent = queue.active
        ? `${queue.active.status === "reviewing" ? "確認・修正中" : "読取中"}：${queue.active.name} ／ 待機 ${queue.pending.length}件 ／ 読取完了 ${completed}件（確認済み ${confirmed}件）／ 失敗 ${failed}件`
        : queue.entries.length
            ? `読取完了 ${completed}件（確認済み ${confirmed}件）／ 失敗 ${failed}件`
            : "今回の取込結果はまだありません。荷主を選択してPDFを追加してください。";
    items.replaceChildren();
    for (const entry of queue.entries) {
        const item = document.createElement("li");
        item.className = `order-pdf-result ${entry.status}`;
        const status = document.createElement("strong");
        status.textContent = entry.status === "failed" ? "失敗" : entry.confirmed ? "受注登録済み" : "読取完了・要確認";
        const detail = document.createElement("div");
        detail.className = "order-pdf-result-detail";
        const name = document.createElement(entry.orderImportId ? "a" : "span");
        if (entry.orderImportId) {
            name.href = `/api/order/import/${encodeURIComponent(entry.orderImportId)}/file`;
            name.target = "_blank";
            name.rel = "noopener";
        }
        name.textContent = `${entry.name} (${formatFileSize(entry.size)})`;
        const metadata = document.createElement("small");
        metadata.textContent = `${entry.shipperName} ／ ${formatOrderPdfImportDate(entry.completedAt)}${entry.orderId ? ` ／ 受注番号 ${entry.orderId}` : ""}`;
        detail.append(name, metadata);
        if (entry.error) {
            const error = document.createElement("p");
            error.textContent = entry.error;
            detail.append(error);
        }
        const button = document.createElement("button");
        button.type = "button";
        button.className = "normal-btn";
        button.disabled = entry.confirmed || queue.running;
        button.textContent = entry.status === "failed" ? "再試行" : entry.confirmed ? "受注登録済み" : "確認・修正";
        button.addEventListener("click", () => {
            if (queue.running) return;
            if (entry.status === "failed") void queue.retry(entry);
            else void openOcrCandidateForm(entry).catch(error => openMsgDialog({message: error.message, color: "red"}));
        });
        item.append(status, detail, button);
        items.append(item);
    }
    items.classList.toggle("none", queue.entries.length === 0);
}

async function openOcrCandidateForm(entry) {
    const controller = getController("orderList");
    const data = mapOcrCandidate(entry);
    await controller.openForm("detail", data, {bulkMode: false});
    const form = controller.getDefaultForm();
    return new Promise(resolve => {
        form.finishOcrReview = id => {
            form.finishOcrReview = null;
            if (id) { entry.orderId = id; entry.confirmed = true; }
            renderOrderPdfImportList();
            resolve();
        };
    });
}

function formatOrderPdfImportDate(value) {
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? "" : date.toLocaleString("ja-JP");
}
function formatFileSize(size) { return `${(size / 1024 / 1024).toFixed(1)} MB`; }
