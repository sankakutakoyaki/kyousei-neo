"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { createOrderListColumns } from "./columns.js";
import { registerController } from "../../../applcation/controllerRegistry.js";
import { initParentChildLink } from "../../../util/link.js";
import { getToday } from "../../../util/time.js";
import { toExclusiveDate } from "../../../util/date.js";
import { OrderRepository } from "../../../repositories/business/order/OrderRepository.js";
import { createOrderPage } from "../createOrderPage.js";
import { getController } from "../../../applcation/controllerRegistry.js";
import { filterFactory } from "../../../util/filterFactory.js";

export async function init() {
    await initCommon();
    await initPageCache("/api/order/init/cache");

    // tab1
    const list = orderListPage();
    registerController("orderList", list);
    list.init();
    await list.executeAction("search");

    initParentChildLink();
}

export const orderListPage = () =>
    createOrderPage({
        key: "orderList",
        components: {combo: true, input: true},
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        columns: createOrderListColumns(),
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
        // afterSave: refreshOrderChildren,
        // onDeleted: refreshOrderChildren
    });

// function createInlineOrderPage({
//     key,
//     tableId,
//     footerId,
//     formId,
//     columns,
//     category,
//     inputId,
//     formName
// }){
//     return createOrderPage({
//         key,
//         components: {combo: true, input: true},
//         tableId,
//         footerId,
//         formId,
//         columns,
//         defaultFormName: formName,
//         checkable: false,
//         onInit: () => initTodayValue(inputId),
//         buildParams: () => buildRecycleDateParams({category, inputId}),
//         actions: {
//             search: async (controller) => {
//                 await controller.refresh();
//             },
//             orderChanged: async (controller) => {await controller.refresh();}
//         },
//         afterSave: refreshOrderList
//     });
// }

// async function refreshOrderList(){
//     const order = getController("orderList");
//     await order?.executeAction("orderChanged");
// }

// async function refreshOrderChildren(){
//     const keys = [
//         "recycleUse",
//         "recycleDelivery",
//         "recycleShipping",
//         "recycleLoss"
//     ];
//     for(const key of keys){
//         const controller = getController(key);
//         await controller?.executeAction("recycleChanged");
//     }
// }

// function buildOrderDateParams({category, inputId}){
//     const from = document.getElementById(inputId)?.value;
//     return {
//         state: APP.cache.common.state.INITIAL,
//         category: String(category),
//         dateFrom: from,
//         dateTo: toExclusiveDate(from)
//     };
// }

// function initTodayValue(inputId){
//     const today = getToday();
//     const el = document.getElementById(inputId);
//     if(el && !el.value){
//         el.value = today;
//     }
// }