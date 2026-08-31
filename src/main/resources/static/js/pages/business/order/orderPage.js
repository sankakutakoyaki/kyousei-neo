"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { createMasterPage } from "../../../core/page/createMasterPage.js";
import { createCrudPage } from "../../../core/page/createCrudPage.js";
import { OrderRepository } from "../../../repositories/business/order/OrderRepository.js";
import { OrderItemRepository } from "../../../repositories/business/order/OrderItemRepository.js";
import { createOrderListColumns, createOrderItemListColumns } from "./columns.js";
import { registerController } from "../../../applcation/controllerRegistry.js";
import { initParentChildLink } from "../../../util/link.js";
import { getToday } from "../../../util/time.js";
import { toExclusiveDate } from "../../../util/date.js";
import { createOrderPage } from "../createOrderPage.js";
import { getController } from "../../../applcation/controllerRegistry.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { BarcodeScanner } from "../../../util/barcodeScanner.js";
import { ItemMasterRepository } from "../../../repositories/master/item/itemMasterRepository.js";
import { DataTable } from "../../../core/table/DataTable.js";
import { openMsgDialog } from "../../../core/ui/dialog/dialogCore.js";
import { FormController } from "../../../applcation/FormController.js";
import { apiFetch } from "../../../core/api/apiFetch.js";
import { closeFormDialog, openFormDialog } from "../../../core/ui/dialog/dialogCore.js";

let latestOrderPdfImportId = null;

export async function init() {
    await initCommon();
    await initPageCache("/api/order/init/cache");

    // tab1
    const list = orderListPage();
    registerController("orderList", list);
    list.init();
    await list.executeAction("search");

    // PDF取込
    initOrderPdfDrop(list);
    initOrderPdfImportList();
    initOrderOcrLayoutEditor();

    // tab2
    const items = orderItemListPage();
    registerController("orderItemList", items);
    items.init();
    await items.executeAction("search");

    // JANスキャン
    initBarcodeSearch(items);
    initJanCodeInput(items);
    initItemMakerInput(items);
    initItemNameInput(items);
    initItemModelInput(items);

    initParentChildLink();    
}

export const orderListPage = () =>
    createOrderPage({
        key: "orderList",
        idKey: "orderId",
        components: {combo: true, input: true},
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        columns: createOrderListColumns(),
        submitText: "保存",
        cancelText: "キャンセル",
        onInit: () => {
            const today = getToday();
            const from = document.getElementById("date-from01");
            const to = document.getElementById("date-to01");
            if(from && !from.value){
                from.value = today;
            }
            if(to && !to.value){
                to.value = today;
            }
        },
        buildParams: () => {
            const cate = document.getElementById("category01")?.value;
            const from = document.getElementById("date-from01")?.value;
            const to = document.getElementById("date-to01")?.value;
            const primeConstractorId = document.getElementById("primeConstractor01")?.value;
            const primeConstractorOfficeId = document.getElementById("primeConstractorOffice01")?.value;
            return {
                state: APP.cache.common.state.INITIAL,
                compState: APP.cache.common.state.COMPLETE,
                category: cate,
                primeConstractorId: primeConstractorId,
                primeConstractorOfficeId: primeConstractorOfficeId,
                dateFrom: from,
                dateTo: toExclusiveDate(to)
            };
        },
        buildCsvParams: () => ({
            state: APP.cache.common.state.INITIAL
        }),
        actions: {
            search: async (controller) => {await controller.refresh();},
            orderChanged: async (controller) => {await controller.refresh();}
        },
    });

export const orderItemListPage = () =>
    createMasterPage({
        key: "orderItemList",
        tableId: "table-02",
        footerId: "footer-02",
        formId: "form-02",
        idKey: "orderItemId",
        components: {combo: true, input: true},
        repository: OrderItemRepository,
        columns: createOrderItemListColumns(),
        submitText: "保存",
        cancelText: "キャンセル",
        model: {
            filters: {
                category: filterFactory.nullState("arrivalDate"),
                primeConstractorId: filterFactory.equals("primeConstractorId"),
                primeConstractorOfficeId: filterFactory.equals("primeConstractorOfficeId")
            }
        },
        actions: {
            "arrival-item": async (c, el) => {
                const orderItemId = el.dataset.id;
                await OrderItemRepository.arrival({
                    orderItemId,
                    state: APP.cache.common.state.INITIAL
                });
                await c.refresh();
            },
            "create": async (c) => {
                // まずフォームを開く
                await c.openForm("orderItem", null);
                // 検索画面の値を取得
                const janCode = document.getElementById("jan-code01")?.value?.trim() ?? "";
                const itemMaker = document.getElementById("item-maker01")?.value?.trim() ?? "";
                const itemName = document.getElementById("item-name01")?.value?.trim() ?? "";
                const itemModel = document.getElementById("item-model01")?.value?.trim() ?? "";
                // 新規フォーム
                const form = document.getElementById("form-04");
                if (!form) {
                    console.warn("form-04 が見つかりません。");
                    return;
                }
                // 検索条件をフォームへコピー
                const janInput = form.querySelector('[name="jan-code"]');
                const makerInput = form.querySelector('[name="item-maker"]');
                const nameInput = form.querySelector('[name="item-name"]');
                const modelInput = form.querySelector('[name="item-model"]');
                if (janInput) {
                    janInput.value = janCode;
                }
                if (makerInput) {
                    makerInput.value = itemMaker;
                }
                if (nameInput) {
                    nameInput.value = itemName;
                }
                if (modelInput) {
                    modelInput.value = itemModel;
                }
                // 数量は初期値1
                const quantityInput = form.querySelector('[name="item-quantity"]');

                if (quantityInput) {
                    quantityInput.value = "1";
                }
                // 自動入力した値を保存ボタンの判定に反映
                modelInput?.dispatchEvent(new Event("input", { bubbles: true }));
            },
        },
        buildParams: (controller) => ({
            state: APP.cache.common.state.INITIAL,
            category: controller.getFilter("category"),
            primeConstractorId: controller.getFilter("prime-constractor-id"),
            primeConstractorOfficeId: controller.getFilter("prime-constractor-office-id"),
            janCode: controller.getFilter("janCode"),
            itemMaker: controller.getFilter("itemMaker"),
            itemName: controller.getFilter("itemName"),
            itemModel: controller.getFilter("itemModel")
        }),
        forms: {
            // 商品マスター登録
            itemMaster: {
                create: (controller) => new FormController({
                    controller,
                    formId: "form-03",
                    key: "itemMasterId",
                    idKey: "itemMasterId",
                    repository: ItemMasterRepository,
                    saveHandler: ItemMasterRepository.save,
                    submitText: "保存",
                    cancelText: "キャンセル",
                    initialFocusSelector: '[name="item-model"]',
                    afterSave: async () => {
                        const janInput = document.getElementById("jan-code01");
                        const janCode = janInput?.value?.trim();
                        if (!janCode) {
                            return;
                        }
                        await searchByJanCode(controller, janCode);
                    }
                })
            },
            // 受注商品登録
            orderItem: {
                create: (controller) => new FormController({
                    controller,
                    formId: "form-04",
                    key: "orderItemId",
                    idKey: "orderItemId",
                    repository: OrderItemRepository,
                    saveHandler: OrderItemRepository.create,
                    submitText: "保存",
                    cancelText: "キャンセル",
                    validInputSelector: '[name="item-model"]',
                    initialFocusSelector: '[name="item-model"]',
                    afterSave: async () => {
                        await controller.refresh();
                    }
                })
            }
        },
    });

async function initBarcodeSearch(controller) {
    document.getElementById("btn-barcode-scan")?.addEventListener("click", async () => {
        document.getElementById("barcode-scan-dialog")?.classList.remove("none");
        await BarcodeScanner.open({
            onScan: async (code) => {await searchByJanCode(controller, code);}
        });
    });
    document.getElementById("barcode-scan-cancel")?.addEventListener("click", () => {
        BarcodeScanner.close();
    });
}

function initJanCodeInput(controller) {
    const input = document.getElementById("jan-code01");
    if (!input) {
        return;
    }

    input.addEventListener("keydown", async (e) => {
        if (e.key !== "Enter") {
            return;
        }
        e.preventDefault();

        const janCode = input.value.trim();
        if (!janCode) {
            const modelInput = document.getElementById("item-model01");
            if (modelInput) {
                modelInput.value = "";
            }
            controller.setFilter("itemModel", null);
            controller.dataTable.setData([]);
            controller.dataTable.reload();
            return;
        }
        await searchByJanCode(controller, janCode);
    });
}
function initItemMakerInput(controller) {
    const input = document.getElementById("item-maker01");
    if (!input) {
        return;
    }

    input.addEventListener("keydown", async (e) => {
        if (e.key !== "Enter") {
            return;
        }
        e.preventDefault();
        await searchOrderItems(controller);
    });
}

function initItemNameInput(controller) {
    const input = document.getElementById("item-name01");
    if (!input) {
        return;
    }

    input.addEventListener("keydown", async (e) => {
        if (e.key !== "Enter") {
            return;
        }
        e.preventDefault();
        await searchOrderItems(controller);
    });
}

function initItemModelInput(controller) {
    const input = document.getElementById("item-model01");
    if (!input) {
        return;
    }

    input.addEventListener("keydown", async (e) => {
        if (e.key !== "Enter") {
            return;
        }
        e.preventDefault();
        await searchOrderItems(controller);
    });
}

async function searchByJanCode(controller, janCode) {
    const janInput = document.getElementById("jan-code01");
    const modelInput = document.getElementById("item-model01");
    if(janInput){
        janInput.value = janCode;
    }
    // 前回の商品情報をクリア
    if(modelInput){
        modelInput.value = "";
    }
    const item = await ItemMasterRepository.findByJanCode({
        state: APP.cache.common.state.INITIAL,
        janCode
    });
    if(!item){
        if(modelInput){
            modelInput.value = "";
        }

        controller.dataTable.setData([]);
        controller.dataTable.reload();

        // 商品マスター登録フォームを開く
        await controller.openForm("itemMaster", null);
        // JANコードを登録フォームへセット
        const form = document.getElementById("form-03");
        if(form){
            const janInput = form.querySelector('[name="jan-code"]');
            if(janInput){
                janInput.value = janCode;
            }
        }
        return;
    }

    const makerInput = document.getElementById("item-maker01");
    const nameInput = document.getElementById("item-name01");
    if(makerInput){
        makerInput.value = item.itemMaker ?? "";
    }
    if(nameInput){
        nameInput.value = item.itemName ?? "";
    }
    if(modelInput){
        modelInput.value = item.itemModel ?? "";
    }
    controller.setFilter("janCode", janCode);
    controller.setFilter("itemMaker", null);
    controller.setFilter("itemName", null);
    controller.setFilter("itemModel", null);

    const data = await controller.refresh();
    if (data.length === 0) {
        openMsgDialog({
            message: `型番「${item.itemModel}」の受注はありません。`,
            color: "blue"
        });
    }
}

async function searchOrderItems(controller) {
    const category = document.getElementById("category02")?.value.trim() ?? "";
    const janCode = document.getElementById("jan-code01")?.value.trim() ?? "";
    const itemMaker = document.getElementById("item-maker01")?.value.trim() ?? "";
    const itemName = document.getElementById("item-name01")?.value.trim() ?? "";
    const itemModel = document.getElementById("item-model01")?.value.trim() ?? "";

    controller.setFilter("category", category || null);
    controller.setFilter("janCode", janCode || null);
    controller.setFilter("itemMaker", itemMaker || null);
    controller.setFilter("itemName", itemName || null);
    controller.setFilter("itemModel", itemModel || null);

    const data = await controller.refresh();
    if (data.length === 0) {
        openMsgDialog({
            message: "条件に一致する受注商品がありません。",
            color: "blue"
        });
    }
    return data;
}

function initOrderPdfDrop(controller) {
    const dropArea = document.getElementById("tab-02");
    if (!dropArea) {
        return;
    }
    let dragDepth = 0;
    dropArea.addEventListener("dragenter", (e) => {
        e.preventDefault();
        dragDepth += 1;
        dropArea.classList.add("drag-over");
    });
    dropArea.addEventListener("dragover", (e) => {
        e.preventDefault();
        dropArea.classList.add("drag-over");
    });
    // ドラッグ終了
    dropArea.addEventListener("dragleave", () => {
        dragDepth -= 1;
        if (dragDepth === 0) dropArea.classList.remove("drag-over");
    });
    // ドロップ
    dropArea.addEventListener("drop", async (e) => {
        e.preventDefault();
        dragDepth = 0;
        dropArea.classList.remove("drag-over");
        const files = e.dataTransfer?.files;
        if (!files || files.length === 0) {
            return;
        }
        const file = files[0];
        await importOrderPdf(controller, file);
    });
}

async function importOrderPdf(controller, file) {
    // 荷主
    const primeConstractorId = document.getElementById("primeConstractorImport")?.value?.trim();
    // 荷主未選択
    if (!primeConstractorId || primeConstractorId === "0") {
        openMsgDialog({message: "荷主を選択してください。", color: "red"});
        return;
    }
    // PDFチェック
    if (file.type !== "application/pdf") {
        openMsgDialog({message: "PDFファイルを選択してください。", color: "red"});
        return;
    }
    console.log("PDF取込開始");
    console.log("荷主ID:", primeConstractorId);
    console.log("ファイル:", file.name);

    const formData = new FormData();
    formData.append("primeConstractorId", primeConstractorId);
    formData.append("file", file);

    try {
        const result = await apiFetch("/api/order/import/pdf", {
            method: "POST",
            data: formData
        });

        openMsgDialog({
            message: result.message || "PDFを保存しました。",
            color: "blue"
        });
        await refreshOrderPdfImportList();
    } catch (error) {
        openMsgDialog({
            message: error.message || "PDFの保存に失敗しました。",
            color: "red"
        });
    }
}

function initOrderPdfImportList() {
    document.getElementById("primeConstractorImport")?.addEventListener("change", () => {
        refreshOrderPdfImportList();
    });
}

async function refreshOrderPdfImportList() {
    const primeConstractorId = document.getElementById("primeConstractorImport")?.value?.trim();
    const message = document.getElementById("order-pdf-import-message");
    const items = document.getElementById("order-pdf-import-items");

    if (!message || !items) {
        return;
    }
    if (!primeConstractorId || primeConstractorId === "0") {
        message.textContent = "荷主を選択すると、取り込んだPDFを表示します。";
        items.replaceChildren();
        items.classList.add("none");
        return;
    }

    try {
        const result = await apiFetch(
            `/api/order/import?primeConstractorId=${encodeURIComponent(primeConstractorId)}`,
            {method: "GET", showProcessing: false}
        );
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
        return;
    }

    message.textContent = `${imports.length}件の取込PDFがあります。`;
    latestOrderPdfImportId = imports[0].orderImportId;
    for (const imported of imports) {
        const link = document.createElement("a");
        link.href = `/api/order/import/${encodeURIComponent(imported.orderImportId)}/file`;
        link.target = "_blank";
        link.rel = "noopener";
        link.textContent = `${formatOrderPdfImportDate(imported.registDate)}  ${imported.originalFileName} (${formatFileSize(imported.fileSize)})`;

        const item = document.createElement("li");
        item.append(link);

        const ocrButton = document.createElement("button");
        ocrButton.type = "button";
        ocrButton.className = "normal-btn";
        ocrButton.textContent = "OCR実行";
        ocrButton.addEventListener("click", async () => {
            await executeOrderPdfOcr(imported.orderImportId);
        });
        item.append(" ", ocrButton);
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
        openFormDialog({dialogId: "order-ocr-layout-form", submitText: "保存", cancelText: "閉じる", onSubmit: async () => {
            const layouts = [...preview.querySelectorAll(".ocr-layout-box")].map(box => ({fieldKey: box.dataset.fieldKey, x: Math.round(Number(box.dataset.x)), y: Math.round(Number(box.dataset.y)), width: Math.round(Number(box.dataset.width)), height: Math.round(Number(box.dataset.height))}));
            await apiFetch(`/api/order/ocr-layout?primeConstractorId=${encodeURIComponent(primeConstractorId)}`, {method: "POST", data: layouts});
            closeFormDialog("order-ocr-layout-form");
            openMsgDialog({message: "帳票設定を保存しました。", color: "blue"});
            return false;
        }});
        const saved = await apiFetch(`/api/order/ocr-layout?primeConstractorId=${encodeURIComponent(primeConstractorId)}`, {method: "GET", showProcessing: false});
        const hasSavedLayout = (saved.data?.length ?? 0) > 0;
        const layouts = hasSavedLayout
            ? saved.data
            : (primeConstractorId === "1085" ? defaultHeiwadoLayouts() : []);
        const image = document.createElement("img");
        image.alt = "帳票プレビュー";
        const render = () => renderOcrLayoutBoxes(preview, image, layouts);
        image.addEventListener("load", render, {once: true});
        image.src = `/api/order/import/${encodeURIComponent(latestOrderPdfImportId)}/preview`;
        preview.append(image);
        document.getElementById("order-ocr-layout-add").onclick = () => {
            const fieldKey = document.getElementById("order-ocr-layout-field").value;
            if (preview.querySelector(`[data-field-key="${fieldKey}"]`)) return;
            const current = [...preview.querySelectorAll(".ocr-layout-box")].map(box => ({fieldKey: box.dataset.fieldKey, x: Number(box.dataset.x), y: Number(box.dataset.y), width: Number(box.dataset.width), height: Number(box.dataset.height)}));
            current.push({fieldKey, x: 100, y: 100, width: 500, height: 160});
            renderOcrLayoutBoxes(preview, image, current);
        };
        if (!hasSavedLayout && primeConstractorId !== "1085") {
            openMsgDialog({message: "この荷主には帳票設定がありません。読取項目の枠を新規登録してください。", color: "blue"});
        }
    });
}

function defaultHeiwadoLayouts() { return [{fieldKey:"customerName",x:800,y:1220,width:1050,height:180},{fieldKey:"mobilePhone",x:1540,y:1180,width:820,height:210},{fieldKey:"address",x:420,y:1440,width:1900,height:430}]; }

function renderOcrLayoutBoxes(preview, image, layouts) {
    preview.querySelectorAll(".ocr-layout-box").forEach(box => box.remove());
    const scale = image.clientWidth / image.naturalWidth;
    for (const layout of layouts) {
        const box = document.createElement("div");
        box.className = "ocr-layout-box";
        box.dataset.fieldKey = layout.fieldKey; box.dataset.x = layout.x; box.dataset.y = layout.y; box.dataset.width = layout.width; box.dataset.height = layout.height;
        box.textContent = ocrFieldLabel(layout.fieldKey);
        Object.assign(box.style, {left: `${layout.x * scale}px`, top: `${layout.y * scale}px`, width: `${layout.width * scale}px`, height: `${layout.height * scale}px`});
        let startX, startY, left, top;
        box.addEventListener("pointerdown", e => { e.stopPropagation(); startX=e.clientX; startY=e.clientY; left=Number(box.dataset.x); top=Number(box.dataset.y); box.setPointerCapture(e.pointerId); });
        box.addEventListener("pointermove", e => { if (startX == null) return; const x=Math.max(0, left+(e.clientX-startX)/scale), y=Math.max(0, top+(e.clientY-startY)/scale); box.dataset.x=x; box.dataset.y=y; box.style.left=`${x*scale}px`; box.style.top=`${y*scale}px`; });
        box.addEventListener("pointerup", () => { startX = null; });
        const resizeHandle = document.createElement("span");
        resizeHandle.className = "ocr-layout-resize-handle";
        let resizeStartX, resizeStartY, originalWidth, originalHeight;
        resizeHandle.addEventListener("pointerdown", e => { e.preventDefault(); e.stopPropagation(); resizeStartX=e.clientX; resizeStartY=e.clientY; originalWidth=Number(box.dataset.width); originalHeight=Number(box.dataset.height); resizeHandle.setPointerCapture(e.pointerId); });
        resizeHandle.addEventListener("pointermove", e => { if (resizeStartX == null) return; const width=Math.max(40, originalWidth+(e.clientX-resizeStartX)/scale), height=Math.max(30, originalHeight+(e.clientY-resizeStartY)/scale); box.dataset.width=width; box.dataset.height=height; box.style.width=`${width*scale}px`; box.style.height=`${height*scale}px`; });
        resizeHandle.addEventListener("pointerup", () => { resizeStartX = null; });
        box.append(resizeHandle);
        preview.append(box);
    }
}

async function executeOrderPdfOcr(orderImportId) {
    try {
        const result = await apiFetch(
            `/api/order/import/${encodeURIComponent(orderImportId)}/ocr/hei-wado`,
            {method: "POST", timeout: 60000}
        );
        const candidates = result.data ?? {};
        openOcrCandidateForm(orderImportId, candidates);
    } catch (error) {
        openMsgDialog({
            message: error.message || "OCR処理に失敗しました。",
            color: "red"
        });
    }
}

function openOcrCandidateForm(orderImportId, candidates) {
    const fields = {
        customerName: "ocr-customer-name",
        mobilePhone: "ocr-mobile-phone",
        address: "ocr-address",
        itemModel1: "ocr-item-model-1",
        itemModel2: "ocr-item-model-2",
        requestedDate: "ocr-requested-date",
        contactNote: "ocr-contact-note"
    };
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
            for (const [key, id] of Object.entries(fields)) {
                candidate[key] = document.getElementById(id)?.value?.trim() ?? "";
            }
            await apiFetch(`/api/order/import/${encodeURIComponent(orderImportId)}/candidate`, {
                method: "POST",
                data: candidate
            });
            openMsgDialog({message: "受注候補を保存しました。", color: "blue"});
            return true;
        }
    });
}

function ocrFieldLabel(key) {
    const labels = {
        customerName: "氏名",
        mobilePhone: "携帯電話",
        address: "住所",
        itemModel1: "商品型番1",
        itemModel2: "商品型番2",
        requestedDate: "希望日",
        contactNote: "連絡事項"
    };
    return labels[key] ?? key;
}

function formatOrderPdfImportDate(value) {
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? "" : date.toLocaleString("ja-JP");
}

function formatFileSize(size) {
    if (size < 1024) {
        return `${size} B`;
    }
    return `${(size / 1024 / 1024).toFixed(1)} MB`;
}
