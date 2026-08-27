"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { createMasterPage } from "../../../core/page/createMasterPage.js";
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

export async function init() {
    await initCommon();
    await initPageCache("/api/order/init/cache");

    // tab1
    const list = orderListPage();
    registerController("orderList", list);
    list.init();
    await list.executeAction("search");


    // tab2
    const items = orderItemListPage();
    registerController("orderItemList", items);
    items.init();
    await items.executeAction("search");

    // JANスキャン
    // initBarcodeScanner();
    initBarcodeSearch(items);
    initJanCodeInput(items);
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
            return {
                state: APP.cache.common.state.INITIAL,
                compState: APP.cache.common.state.COMPLETE,
                category: cate,
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
        repository: OrderItemRepository,
        columns: createOrderItemListColumns(),
        submitText: "保存",
        cancelText: "キャンセル",
        components: {combo: true, input: true},
        buildParams: (controller) => ({
            state: APP.cache.common.state.INITIAL,
            itemModel: controller.getFilter("itemModel")
        })
    });

// function initBarcodeScanner() {
//     const scanButton = document.getElementById("btn-barcode-scan");
//     if (!scanButton) {
//         console.warn("JANスキャンボタンが見つかりません。");
//         return;
//     }
//     scanButton.addEventListener("click", () => {
//         BarcodeScanner.open({
//             onScan(code) {
//                 console.log("読み取ったJAN:", code);

//                 const janCode = document.getElementById("jan-code01");
//                 if (janCode) {
//                     janCode.value = code;
//                 }
//             }
//         });
//     });
// }

function initBarcodeSearch(controller) {
    const scanButton = document.getElementById("btn-barcode-scan");
    if(!scanButton){
        console.warn("JANスキャンボタンが見つかりません。");
        return;
    }

    scanButton.addEventListener("click", () => {
        BarcodeScanner.open({
            onScan: async (code) => {
                console.log("読み取ったJAN:", code);
                await searchByJanCode(controller, code);
            }
        });
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

        // const janCode = input.value.trim();
        // if (!janCode) {
        //     return;
        // }
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

        const itemModel = input.value.trim();

        const janInput = document.getElementById("jan-code01");

        if (janInput) {
            janInput.value = "";
        }

        if (!itemModel) {
            controller.setFilter("itemModel", null);
            controller.dataTable.setData([]);
            controller.dataTable.reload();
            return;
        }

        // controller.setFilter("itemModel", itemModel);
        // await controller.refresh();
        controller.setFilter("itemModel", itemModel);
        const data = await controller.refresh();
        if (data.length === 0) {
            openMsgDialog({
                message: `型番「${itemModel}」の受注はありません。`,
                color: "blue"
            });
        }
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

        openMsgDialog({
            message: `JANコード「${janCode}」の商品が登録されていません。`,
            color: "red"
        });
        return;
    }

    console.log("商品マスター:", item);

    if(modelInput){
        modelInput.value = item.itemModel ?? "";
    }

    if(!item.itemModel){
        controller.dataTable.setData([]);
        controller.dataTable.reload();

        openMsgDialog({
            message: "商品マスターに型番が登録されていません。",
            color: "red"
        });
        return;
    }

    // // controller.setFilter(
    // //     "itemModel",
    // //     item.itemModel
    // // );

    // // const data = await OrderItemRepository.searchByItemModel({
    // //     state: APP.cache.common.state.INITIAL,
    // //     itemModel: item.itemModel
    // // });

    // // controller.dataTable.setData(data);
    // // controller.dataTable.reload();
    // controller.setFilter("itemModel", item.itemModel);
    // await controller.refresh();
    controller.setFilter("itemModel", item.itemModel);

    const data = await controller.refresh();

    if (data.length === 0) {
        openMsgDialog({
            message: `型番「${item.itemModel}」の受注はありません。`,
            color: "blue"
        });
    }
}