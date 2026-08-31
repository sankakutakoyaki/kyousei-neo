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
import { initOrderPdfImport } from "./orderPdfImport.js";

export async function init() {
    await initCommon();
    await initPageCache("/api/order/init/cache");

    // tab1
    const list = orderListPage();
    registerController("orderList", list);
    list.init();
    await list.executeAction("search");

    // PDF取込
    initOrderPdfImport();

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
