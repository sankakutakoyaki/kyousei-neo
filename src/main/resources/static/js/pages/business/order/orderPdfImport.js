"use strict"

import { formatters } from "../../../core/behavior/formatters.js";
import { apiFetch } from "../../../core/api/apiFetch.js";
import { closeFormDialog, openFormDialog, openMsgDialog } from "../../../core/ui/dialog/dialogCore.js";

import { OrderPdfImportQueue } from "./OrderPdfImportQueue.js";

let latestOrderPdfImportId = null;
let importQueue = null;

export function initOrderPdfImport() {
    importQueue?.dispose();
    latestOrderPdfImportId = null;
    const panel = document.getElementById("tab-02");
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
            return openOcrCandidateForm(entry, true);
        },
        onChange: () => {
            if (importQueue !== queue) return;
            if (!panel?.isConnected) { queue.dispose(); return; }
            renderOrderPdfImportList();
        }
    });
    importQueue = queue;
    initOrderPdfDrop();
    document.getElementById("primeConstractorImport")?.addEventListener("change", renderOrderPdfImportList);
    const fileInput = document.getElementById("order-pdf-file-input");
    document.getElementById("order-pdf-file-button")?.addEventListener("click", () => fileInput?.click());
    fileInput?.addEventListener("change", () => {
        const files = Array.from(fileInput.files ?? []);
        fileInput.value = "";
        enqueueOrderPdfs(files);
    });
    renderOrderPdfImportList();
    initOrderOcrLayoutEditor();
}

function initOrderPdfDrop() {
    const dropArea = document.getElementById("tab-02");
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
    const select = document.getElementById("primeConstractorImport");
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
    const shipperId = document.getElementById("primeConstractorImport")?.value?.trim();
    latestOrderPdfImportId = [...queue.entries].reverse().find(entry => entry.shipperId === shipperId && entry.orderImportId)?.orderImportId ?? null;
    const completed = queue.entries.filter(entry => entry.status === "completed").length;
    const failed = queue.entries.filter(entry => entry.status === "failed").length;
    const confirmed = queue.entries.filter(entry => entry.confirmed).length;
    message.textContent = queue.active
        ? `${queue.active.status === "reviewing" ? "確認・修正中" : "読取中"}：${queue.active.name} ／ 待機 ${queue.pending.length}件 ／ 読取完了 ${completed}件（確認済み ${confirmed}件）／ 失敗 ${failed}件`
        : queue.entries.length
            ? `読取完了 ${completed}件（確認済み ${confirmed}件）／ 失敗 ${failed}件`
            : "今回の取込結果はまだありません。荷主を選択してPDFを追加してください。";
    const layoutButton = document.getElementById("order-ocr-layout-button");
    if (layoutButton) layoutButton.disabled = queue.running;
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
            else openOcrCandidateForm(entry);
        });
        item.append(status, detail, button);
        items.append(item);
    }
    items.classList.toggle("none", queue.entries.length === 0);
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

function openOcrCandidateForm(entry, sequential = false) {
    const fields = {customerName: "ocr-customer-name", mobilePhone: "ocr-mobile-phone", postalCode: "ocr-postal-code", address: "ocr-address", requestedDate: "ocr-requested-date", contactNote: "ocr-contact-note"};
    for (const [key, id] of Object.entries(fields)) {
        const input = document.getElementById(id);
        if (input) input.value = entry.candidates[key] ?? "";
    }
    const dateInput = document.getElementById("ocr-requested-date");
    const dateHint = document.getElementById("ocr-requested-date-hint");
    const rawDate = String(entry.candidates.requestedDate ?? "").trim();
    if (dateHint) {
        const needsReview = rawDate !== "" && !dateInput?.value;
        dateHint.hidden = !needsReview;
        dateHint.textContent = needsReview ? `読取値：${rawDate}。年を含めて日付を選択してください。` : "";
    }
    const postalInput = document.getElementById("ocr-postal-code");
    if (postalInput) {
        // Avoid replacing a full OCR address with a town-only lookup on an unchanged value.
        postalInput.dataset.lastId = postalInput.value.trim();
    }
    const parseRows = value => {
        if (!value) return [];
        try { const rows = typeof value === "string" ? JSON.parse(value) : value; if (!Array.isArray(rows)) throw new Error(); return rows; }
        catch { throw new Error("商品・作業項目の読取形式が不正です。再試行してください。"); }
    };
    let itemRows = parseRows(entry.candidates.items);
    if (!itemRows.length) itemRows = [entry.candidates.itemModel1, entry.candidates.itemModel2].filter(Boolean).map(itemModel => ({itemModel}));
    const itemContainer = document.getElementById("ocr-item-rows");
    const workContainer = document.getElementById("ocr-work-rows");
    itemContainer.replaceChildren();
    workContainer.replaceChildren();
    const itemFields = {itemName: "商品名", itemModel: "型番", itemQuantity: "数量"};
    const workFields = {orderWorkName: "作業名", orderWorkQuantity: "数量", orderWorkPrice: "単価"};
    for (const item of itemRows) addCandidateRow(itemContainer, itemFields, item);
    for (const work of parseRows(entry.candidates.works)) addCandidateRow(workContainer, workFields, work);
    document.getElementById("ocr-add-item").onclick = () => addCandidateRow(itemContainer, itemFields, {});
    document.getElementById("ocr-add-work").onclick = () => addCandidateRow(workContainer, workFields, {});
    const title = document.getElementById("order-ocr-candidate-source");
    if (title) title.textContent = `${entry.shipperName} ／ ${entry.name}：原本と照合し、必要な修正をして保存してください。`;
    const sourceLink = document.getElementById("order-ocr-candidate-pdf");
    if (sourceLink) sourceLink.href = `/api/order/import/${encodeURIComponent(entry.orderImportId)}/file`;
    let saving = false;
    return new Promise(resolve => {
        openFormDialog({
            dialogId: "order-ocr-candidate-form",
            submitText: "確認して受注登録",
            cancelText: sequential ? "未確認で次へ" : "閉じる",
            onClose: () => {
                if (saving) return;
                closeFormDialog("order-ocr-candidate-form");
                resolve();
            },
            onSubmit: async () => {
                if (saving || entry.confirmed) return false;
                saving = true;
                try {
                    const candidate = {};
                    for (const [key, id] of Object.entries(fields)) candidate[key] = document.getElementById(id)?.value?.trim() ?? "";
                    candidate.items = JSON.stringify(readCandidateRows(itemContainer));
                    candidate.works = JSON.stringify(readCandidateRows(workContainer));
                    const result = await apiFetch(`/api/order/import/${encodeURIComponent(entry.orderImportId)}/candidate`, {method: "POST", data: candidate, showProcessing: false});
                    entry.orderId = result.data?.orderId;
                    entry.candidates = candidate;
                    entry.confirmed = true;
                    closeFormDialog("order-ocr-candidate-form");
                    renderOrderPdfImportList();
                    if (!sequential) openMsgDialog({message: "受注・商品・作業項目を登録しました。", color: "blue"});
                    resolve();
                } catch (error) {
                    openMsgDialog({message: error.message || "保存に失敗しました。入力内容を確認して再度保存してください。", color: "red"});
                } finally {
                    saving = false;
                }
                return false;
            }
        });
    });
}

function addCandidateRow(container, fields, values) {
    const row = document.createElement("div");
    row.className = "ocr-detail-row";
    for (const [key, title] of Object.entries(fields)) {
        const label = document.createElement("label");

        const input = document.createElement("input");
        input.className = "normal-input";
        input.dataset.field = key;
        input.setAttribute("aria-label", title);
        input.value = values?.[key] ?? "";
        if (key.endsWith("Quantity") || key.endsWith("Price")) {
            input.inputMode = "numeric";
            input.value = normalizeCandidateNumber(input.value);
            input.addEventListener("input", event => {
                if (!event.isComposing) input.value = normalizeCandidateNumber(input.value);
            });
            input.addEventListener("compositionend", () => { input.value = normalizeCandidateNumber(input.value); });
            input.addEventListener("blur", () => { input.value = normalizeCandidateNumber(input.value); });
            input.classList.add("ocr-numeric-input");
        }
        if (key.endsWith("Quantity")) label.classList.add("ocr-quantity-field");
        if (key.endsWith("Price")) {
            input.value = formatCandidatePrice(input.value);
            input.addEventListener("focus", () => { input.value = input.value.replace(/,/g, ""); });
            input.addEventListener("blur", () => { input.value = formatCandidatePrice(input.value); });
        }
        label.append(input);
        row.append(label);
    }
    const remove = document.createElement("button");
    remove.type = "button";
    remove.className = "img-btn";
    remove.title = "削除";
    remove.setAttribute("aria-label", "削除");
    const removeIcon = document.createElement("img");
    removeIcon.src = "/icons/dust.png";
    removeIcon.alt = "";
    remove.append(removeIcon);
    remove.onclick = () => row.remove();
    row.append(remove);
    container.append(row);
}

function normalizeCandidateNumber(value) {
    return formatters.code(value).replace(/[^0-9]/g, "");
}

function formatCandidatePrice(value) {
    const digits = normalizeCandidateNumber(value);
    return /^\d+$/.test(digits) ? digits.replace(/\B(?=(\d{3})+(?!\d))/g, ",") : digits;
}

function readCandidateRows(container) {
    return [...container.children].map(row => Object.fromEntries(
        [...row.querySelectorAll("input[data-field]")].map(input => [input.dataset.field, (input.dataset.field.endsWith("Price") || input.dataset.field.endsWith("Quantity")) ? normalizeCandidateNumber(input.value) : input.value.trim()])
    ));
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
