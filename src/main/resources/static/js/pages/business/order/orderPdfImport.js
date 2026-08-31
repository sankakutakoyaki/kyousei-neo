"use strict"

import { apiFetch } from "../../../core/api/apiFetch.js";
import { closeFormDialog, openFormDialog, openMsgDialog } from "../../../core/ui/dialog/dialogCore.js";

let latestOrderPdfImportId = null;

export function initOrderPdfImport() {
    initOrderPdfDrop();
    initOrderPdfImportList();
    initOrderOcrLayoutEditor();
}

function initOrderPdfDrop() {
    const dropArea = document.getElementById("tab-02");
    if (!dropArea) return;

    let dragDepth = 0;
    dropArea.addEventListener("dragenter", (event) => {
        event.preventDefault();
        dragDepth += 1;
        dropArea.classList.add("drag-over");
    });
    dropArea.addEventListener("dragover", (event) => {
        event.preventDefault();
        dropArea.classList.add("drag-over");
    });
    dropArea.addEventListener("dragleave", () => {
        dragDepth = Math.max(0, dragDepth - 1);
        if (dragDepth === 0) dropArea.classList.remove("drag-over");
    });
    dropArea.addEventListener("drop", async (event) => {
        event.preventDefault();
        dragDepth = 0;
        dropArea.classList.remove("drag-over");
        const file = event.dataTransfer?.files?.[0];
        if (file) await importOrderPdf(file);
    });
}

async function importOrderPdf(file) {
    const primeConstractorId = document.getElementById("primeConstractorImport")?.value?.trim();
    if (!primeConstractorId || primeConstractorId === "0") {
        openMsgDialog({message: "荷主を選択してください。", color: "red"});
        return;
    }
    if (file.type && file.type !== "application/pdf") {
        openMsgDialog({message: "PDFファイルを選択してください。", color: "red"});
        return;
    }

    const formData = new FormData();
    formData.append("primeConstractorId", primeConstractorId);
    formData.append("file", file);

    try {
        const result = await apiFetch("/api/order/import/pdf", {method: "POST", data: formData});
        openMsgDialog({message: result.message || "PDFを保存しました。", color: "blue"});
        await refreshOrderPdfImportList();
    } catch (error) {
        openMsgDialog({message: error.message || "PDFの保存に失敗しました。", color: "red"});
    }
}

function initOrderPdfImportList() {
    document.getElementById("primeConstractorImport")?.addEventListener("change", refreshOrderPdfImportList);
}

async function refreshOrderPdfImportList() {
    const primeConstractorId = document.getElementById("primeConstractorImport")?.value?.trim();
    const message = document.getElementById("order-pdf-import-message");
    const items = document.getElementById("order-pdf-import-items");
    if (!message || !items) return;

    if (!primeConstractorId || primeConstractorId === "0") {
        message.textContent = "荷主を選択すると、取り込んだPDFを表示します。";
        items.replaceChildren();
        items.classList.add("none");
        latestOrderPdfImportId = null;
        return;
    }

    try {
        const result = await apiFetch(`/api/order/import?primeConstractorId=${encodeURIComponent(primeConstractorId)}`, {method: "GET", showProcessing: false});
        renderOrderPdfImportList(result.data ?? [], message, items);
    } catch (error) {
        message.textContent = error.message || "取込PDFの取得に失敗しました。";
        items.replaceChildren();
        items.classList.add("none");
    }
}

function renderOrderPdfImportList(imports, message, items) {
    items.replaceChildren();
    if (imports.length === 0) {
        message.textContent = "取込済みのPDFはありません。";
        items.classList.add("none");
        latestOrderPdfImportId = null;
        return;
    }

    message.textContent = `${imports.length}件の取込PDFがあります。`;
    latestOrderPdfImportId = imports[0].orderImportId;
    for (const imported of imports) {
        const item = document.createElement("li");
        const link = document.createElement("a");
        link.href = `/api/order/import/${encodeURIComponent(imported.orderImportId)}/file`;
        link.target = "_blank";
        link.rel = "noopener";
        link.textContent = `${formatOrderPdfImportDate(imported.registDate)}  ${imported.originalFileName} (${formatFileSize(imported.fileSize)})`;
        item.append(link);

        const ocrButton = document.createElement("button");
        ocrButton.type = "button";
        ocrButton.className = "normal-btn";
        ocrButton.textContent = "OCR実行";
        ocrButton.addEventListener("click", () => executeOrderPdfOcr(imported.orderImportId));
        item.append(ocrButton);
        items.append(item);
    }
    items.classList.remove("none");
}

function initOrderOcrLayoutEditor() {
    document.getElementById("order-ocr-layout-button")?.addEventListener("click", async () => {
        const primeConstractorId = document.getElementById("primeConstractorImport")?.value?.trim();
        if (!latestOrderPdfImportId) {
            openMsgDialog({message: "帳票設定に使う取込PDFを選択してください。", color: "red"});
            return;
        }
        if (!primeConstractorId || primeConstractorId === "0") {
            openMsgDialog({message: "荷主を選択してください。", color: "red"});
            return;
        }

        const preview = document.getElementById("order-ocr-layout-preview");
        preview.replaceChildren();
        openFormDialog({
            dialogId: "order-ocr-layout-form",
            submitText: "保存",
            cancelText: "閉じる",
            onSubmit: async () => {
                const layouts = [...preview.querySelectorAll(".ocr-layout-box")].map((box) => ({
                    fieldKey: box.dataset.fieldKey,
                    x: Math.round(Number(box.dataset.x)),
                    y: Math.round(Number(box.dataset.y)),
                    width: Math.round(Number(box.dataset.width)),
                    height: Math.round(Number(box.dataset.height))
                }));
                await apiFetch(`/api/order/ocr-layout?primeConstractorId=${encodeURIComponent(primeConstractorId)}`, {method: "POST", data: layouts});
                closeFormDialog("order-ocr-layout-form");
                openMsgDialog({message: "帳票設定を保存しました。", color: "blue"});
                return false;
            }
        });

        const saved = await apiFetch(`/api/order/ocr-layout?primeConstractorId=${encodeURIComponent(primeConstractorId)}`, {method: "GET", showProcessing: false});
        const hasSavedLayout = (saved.data?.length ?? 0) > 0;
        const layouts = hasSavedLayout ? saved.data : (primeConstractorId === "1085" ? defaultHeiwadoLayouts() : []);
        const image = document.createElement("img");
        image.alt = "帳票プレビュー";
        image.addEventListener("load", () => renderOcrLayoutBoxes(preview, image, layouts), {once: true});
        image.src = `/api/order/import/${encodeURIComponent(latestOrderPdfImportId)}/preview`;
        preview.append(image);
        document.getElementById("order-ocr-layout-add").onclick = () => {
            const fieldKey = document.getElementById("order-ocr-layout-field").value;
            if (preview.querySelector(`[data-field-key="${fieldKey}"]`)) return;
            const current = [...preview.querySelectorAll(".ocr-layout-box")].map((box) => ({
                fieldKey: box.dataset.fieldKey,
                x: Number(box.dataset.x),
                y: Number(box.dataset.y),
                width: Number(box.dataset.width),
                height: Number(box.dataset.height)
            }));
            current.push({fieldKey, x: 100, y: 100, width: 500, height: 160});
            renderOcrLayoutBoxes(preview, image, current);
        };
        if (!hasSavedLayout && primeConstractorId !== "1085") {
            openMsgDialog({message: "この荷主には帳票設定がありません。読取項目の枠を新規登録してください。", color: "blue"});
        }
    });
}

function defaultHeiwadoLayouts() {
    return [
        {fieldKey: "customerName", x: 800, y: 1220, width: 1050, height: 180},
        {fieldKey: "mobilePhone", x: 1540, y: 1180, width: 820, height: 210},
        {fieldKey: "address", x: 420, y: 1440, width: 1900, height: 430}
    ];
}

function renderOcrLayoutBoxes(preview, image, layouts) {
    preview.querySelectorAll(".ocr-layout-box").forEach((box) => box.remove());
    const scale = image.clientWidth / image.naturalWidth;
    for (const layout of layouts) {
        const box = document.createElement("div");
        box.className = "ocr-layout-box";
        Object.assign(box.dataset, layout);
        box.textContent = ocrFieldLabel(layout.fieldKey);
        Object.assign(box.style, {left: `${layout.x * scale}px`, top: `${layout.y * scale}px`, width: `${layout.width * scale}px`, height: `${layout.height * scale}px`});

        let startX;
        let startY;
        let left;
        let top;
        box.addEventListener("pointerdown", (event) => {
            event.stopPropagation();
            startX = event.clientX;
            startY = event.clientY;
            left = Number(box.dataset.x);
            top = Number(box.dataset.y);
            box.setPointerCapture(event.pointerId);
        });
        box.addEventListener("pointermove", (event) => {
            if (startX == null) return;
            const x = Math.max(0, left + (event.clientX - startX) / scale);
            const y = Math.max(0, top + (event.clientY - startY) / scale);
            box.dataset.x = x;
            box.dataset.y = y;
            box.style.left = `${x * scale}px`;
            box.style.top = `${y * scale}px`;
        });
        box.addEventListener("pointerup", () => { startX = null; });

        const resizeHandle = document.createElement("span");
        resizeHandle.className = "ocr-layout-resize-handle";
        let resizeStartX;
        let resizeStartY;
        let originalWidth;
        let originalHeight;
        resizeHandle.addEventListener("pointerdown", (event) => {
            event.preventDefault();
            event.stopPropagation();
            resizeStartX = event.clientX;
            resizeStartY = event.clientY;
            originalWidth = Number(box.dataset.width);
            originalHeight = Number(box.dataset.height);
            resizeHandle.setPointerCapture(event.pointerId);
        });
        resizeHandle.addEventListener("pointermove", (event) => {
            if (resizeStartX == null) return;
            const width = Math.max(40, originalWidth + (event.clientX - resizeStartX) / scale);
            const height = Math.max(30, originalHeight + (event.clientY - resizeStartY) / scale);
            box.dataset.width = width;
            box.dataset.height = height;
            box.style.width = `${width * scale}px`;
            box.style.height = `${height * scale}px`;
        });
        resizeHandle.addEventListener("pointerup", () => { resizeStartX = null; });
        box.append(resizeHandle);
        preview.append(box);
    }
}

async function executeOrderPdfOcr(orderImportId) {
    try {
        const result = await apiFetch(`/api/order/import/${encodeURIComponent(orderImportId)}/ocr/hei-wado`, {method: "POST", timeout: 60000});
        openOcrCandidateForm(orderImportId, result.data ?? {});
    } catch (error) {
        openMsgDialog({message: error.message || "OCR処理に失敗しました。", color: "red"});
    }
}

function openOcrCandidateForm(orderImportId, candidates) {
    const fields = {customerName: "ocr-customer-name", mobilePhone: "ocr-mobile-phone", address: "ocr-address", itemModel1: "ocr-item-model-1", itemModel2: "ocr-item-model-2", requestedDate: "ocr-requested-date", contactNote: "ocr-contact-note"};
    for (const [key, id] of Object.entries(fields)) {
        const input = document.getElementById(id);
        if (input) input.value = candidates[key] ?? "";
    }
    openFormDialog({
        dialogId: "order-ocr-candidate-form",
        submitText: "保存",
        cancelText: "閉じる",
        onSubmit: async () => {
            const candidate = {};
            for (const [key, id] of Object.entries(fields)) candidate[key] = document.getElementById(id)?.value?.trim() ?? "";
            await apiFetch(`/api/order/import/${encodeURIComponent(orderImportId)}/candidate`, {method: "POST", data: candidate});
            openMsgDialog({message: "受注候補を保存しました。", color: "blue"});
            return true;
        }
    });
}

function ocrFieldLabel(key) {
    return ({customerName: "氏名", mobilePhone: "携帯電話", address: "住所", itemModel1: "商品型番1", itemModel2: "商品型番2", requestedDate: "希望日", contactNote: "連絡事項"})[key] ?? key;
}

function formatOrderPdfImportDate(value) {
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? "" : date.toLocaleString("ja-JP");
}

function formatFileSize(size) {
    return size < 1024 ? `${size} B` : `${(size / 1024 / 1024).toFixed(1)} MB`;
}
