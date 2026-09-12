"use strict"

import { getController } from "../../../application/controllerRegistry.js";
import { mapOcrCandidate } from "./mapOcrCandidate.js";
import { apiFetch } from "../../../core/api/apiFetch.js";
import { openMsgDialog, closeMsgDialog } from "../../../core/ui/dialog/dialogCore.js";

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
        },
        onError: entry => {
            if (importQueue !== queue || !panel?.isConnected) { queue.dispose(); return; }
            return new Promise(resolve => openMsgDialog({
                message: `${entry.name} の取込に失敗しました。\n${entry.error}`,
                color: "red",
                onClose: () => { closeMsgDialog(); resolve(); }
            }));
        }
    });
    importQueue = queue;
    initOrderPdfDrop();
    const fileInput = document.getElementById("order-pdf-file-input");
    document.getElementById("order-pdf-file-button")?.addEventListener("click", () => fileInput?.click());
    fileInput?.addEventListener("change", () => {
        const files = Array.from(fileInput.files ?? []);
        fileInput.value = "";
        enqueueOrderPdfs(files);
    });

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

async function openOcrCandidateForm(entry) {
    const controller = getController("orderList");
    const data = mapOcrCandidate(entry);
    await controller.openForm("detail", data, {bulkMode: false});
    const form = controller.getDefaultForm();
    return new Promise(resolve => {
        form.finishOcrReview = id => {
            form.finishOcrReview = null;
            if (id) { entry.orderId = id; entry.confirmed = true; }
            resolve();
        };
    });
}
