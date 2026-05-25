"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { createRecycleListColumns, 
         createRecycleUseColumns, 
         createRecycleDeliveryColumns, 
         createRecycleShippingColumns, 
         createRecycleLossColumns } from "./columns.js";
import { registerController } from "../../../applcation/controllerRegistry.js";
import { initParentChildLink } from "../../../util/link.js";
import { getToday } from "../../../util/time.js";
import { toExclusiveDate } from "../../../util/date.js";
import { RecycleRepository } from "../../../repositories/business/recycle/RecycleRepository.js";
import { createRecyclePage } from "../createRecyclePage.js";
import { getController } from "../../../applcation/controllerRegistry.js";

export async function init() {
    await initCommon();
    await initPageCache("/api/recycle/init/cache");

    // tab1
    const list = recycleListPage();
    registerController("recycleList", list);
    list.init();
    await list.executeAction("search");
    
    // tab2
    const use = recycleUsePage();
    registerController("recycleUse", use);
    use.init();

    // tab3
    const delivery = recycleDeliveryPage();
    registerController("recycleDelivery", delivery);
    delivery.init();

    // tab4
    const shipping = recycleShippingPage();
    registerController("recycleShipping", shipping);
    shipping.init();

    // tab5
    const loss = recycleLossPage();
    registerController("recycleLoss", loss);
    loss.init();

    initParentChildLink();
}

export const recycleListPage = () =>
    createRecyclePage({
        key: "recycleList",
        components: {combo: true, input: true},
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        bulkFormId: "form-02",
        columns: createRecycleListColumns(),
        rowClass: (item) => item.lossDate ? "is-loss" : "",
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
            recycleChanged: async (controller) => {await controller.refresh();}
        },
        afterSave: refreshRecycleChildren,
        onDeleted: refreshRecycleChildren
    });

function createInlineRecyclePage({
    key,
    tableId,
    footerId,
    formId,
    columns,
    category,
    inputId,
    formName
}){
    return createRecyclePage({
        key,
        components: {combo: true, input: true},
        tableId,
        footerId,
        formId,
        columns,
        rowClass: (item) => item.lossDate ? "is-loss": "",
        defaultFormName: formName,
        checkable: false,
        onInit: () => initTodayValue(inputId),
        buildParams: () => buildRecycleDateParams({category, inputId}),
        canSave: () => {
            const recycle = document.querySelector(`#${formId} [name='recycle-number']`)?.value;
            return recycle?.trim() !== "";
        },
        actions: {
            search: async (controller) => {
                await controller.refresh();
            },
            recycleChanged: async (controller) => {await controller.refresh();}
        },
        afterSave: refreshRecycleList
    });
}

export const recycleUsePage = () =>
    createInlineRecyclePage({
        key: "recycleUse",
        tableId: "table-02",
        footerId: "footer-02",
        formId: "header-02",
        columns: createRecycleUseColumns(),
        category: APP.cache.common.recycleCategory.USE,
        inputId: "use-date02",
        formName: "inlineUse"
    });

export const recycleDeliveryPage = () =>
    createInlineRecyclePage({
        key: "recycleDelivery",
        tableId: "table-03",
        footerId: "footer-03",
        formId: "header-03",
        columns: createRecycleDeliveryColumns(),
        category: APP.cache.common.recycleCategory.DELIVERY,
        inputId: "delivery-date02",
        formName: "inlineDelivery"
    });

export const recycleShippingPage = () =>
    createInlineRecyclePage({
        key: "recycleShipping",
        tableId: "table-04",
        footerId: "footer-04",
        formId: "header-04",
        columns: createRecycleShippingColumns(),
        category: APP.cache.common.recycleCategory.SHIPPER,
        inputId: "shipping-date02",
        formName: "inlineShipping"
    });

export const recycleLossPage = () =>
    createInlineRecyclePage({
        key: "recycleLoss",
        tableId: "table-05",
        footerId: "footer-05",
        formId: "header-05",
        columns: createRecycleLossColumns(),
        category: APP.cache.common.recycleCategory.LOSS,
        inputId: "loss-date02",
        formName: "inlineLoss"
    });

async function refreshRecycleList(){
    const recycle = getController("recycleList");
    await recycle?.executeAction("recycleChanged");
}

async function refreshRecycleChildren(){
    const keys = [
        "recycleUse",
        "recycleDelivery",
        "recycleShipping",
        "recycleLoss"
    ];
    for(const key of keys){
        const controller = getController(key);
        await controller?.executeAction("recycleChanged");
    }
}

function buildRecycleDateParams({category, inputId}){
    const from = document.getElementById(inputId)?.value;
    return {
        state: APP.cache.common.state.INITIAL,
        category: String(category),
        dateFrom: from,
        dateTo: toExclusiveDate(from)
    };
}

function initTodayValue(inputId){
    const today = getToday();
    const el = document.getElementById(inputId);
    if(el && !el.value){
        el.value = today;
    }
}


// export const recycleUsePage = () =>
//     createRecyclePage({
//         key: "recycleUse",
//         components: {combo: true, input: true},
//         tableId: "table-02",
//         footerId: "footer-02",
//         formId: "header-02",
//         columns: createRecycleUseColumns(),
//         rowClass: (item) => item.lossDate ? "is-loss" : "",
//         defaultFormName: "inlineUse",
//         checkable:false,
//         // onInit: () => {
//         //     const today = getToday();
//         //     // const from = document.querySelector("[name='use-date']");
//         //     const from = document.getElementById("use-date02");
//         //     if(from && !from.value){
//         //         from.value = today;
//         //     }
//         // },
//         // buildParams: () => {
//         //     // const from = document.querySelector("[name='use-date']")?.value;
//         //     const from = document.getElementById("use-date02")?.value;
//         //     return {
//         //         state: APP.cache.common.state.INITIAL,
//         //         category: String(APP.cache.common.recycleCategory.USE),
//         //         dateFrom: from,
//         //         dateTo: toExclusiveDate(from)
//         //     };
//         // },
//         onInit: () => initTodayValue("use-date02"),
//         buildParams: () =>
//             buildRecycleDateParams({
//                 category: APP.cache.common.recycleCategory.USE,
//                 inputId: "use-date02"
//             }),
//         canSave: () => {
//             const recycle = document.querySelector("#header-02 [name='recycle-number']")?.value;
//             return recycle?.trim() !== "";
//         },
//         actions: {
//             search: async (controller) => {await controller.refresh();},
//             recycleChanged: async (controller) => {await controller.refresh();}
//         },
//         // afterSave: async () => {
//         //     const recycle = getController("recycleList");
//         //     await recycle?.executeAction("recycleChanged");
//         // }
//         afterSave: refreshRecycleList
//     });

// export const recycleDeliveryPage = () =>
//     createRecyclePage({
//         key: "recycleDelivery",
//         components: {combo: true, input: true},
//         tableId: "table-03",
//         footerId: "footer-03",
//         formId: "header-03",
//         columns: createRecycleDeliveryColumns(),
//         rowClass: (item) => item.lossDate ? "is-loss" : "",
//         defaultFormName: "inlineDelivery",
//         checkable:false,
//         // onInit: () => {
//         //     const today = getToday();
//         //     // const from = document.querySelector("[name='delivery-date']");
//         //     const from = document.getElementById("delivery-date02");
//         //     if(from && !from.value){
//         //         from.value = today;
//         //     }
//         // },
//         // buildParams: () => {
//         //     // const from = document.querySelector("[name='delivery-date']")?.value;
//         //     const from = document.getElementById("delivery-date02")?.value;
//         //     return {
//         //         state: APP.cache.common.state.INITIAL,
//         //         category: String(APP.cache.common.recycleCategory.DELIVERY),
//         //         dateFrom: from,
//         //         dateTo: toExclusiveDate(from)
//         //     };
//         // },
//         onInit: () => initTodayValue("delivery-date02"),
//         buildParams: () =>
//             buildRecycleDateParams({
//                 category:
//                     APP.cache.common
//                         .recycleCategory
//                         .DELIVERY,

//                 inputId:
//                     "delivery-date02"
//             }),
//         canSave: () => {
//             const recycle = document.querySelector("#header-03 [name='recycle-number']")?.value;
//             return recycle?.trim() !== "";
//         },
//         actions: {
//             search: async (controller) => {await controller.refresh();},
//             recycleChanged: async (controller) => {await controller.refresh();}
//         },
//         // afterSave: async () => {
//         //     const recycle = getController("recycleList");
//         //     await recycle?.executeAction("recycleChanged");
//         // }
//         afterSave: refreshRecycleList
//     });

// export const recycleShippingPage = () =>
//     createRecyclePage({
//         key: "recycleShipping",
//         components: {combo: true, input: true},
//         tableId: "table-04",
//         footerId: "footer-04",
//         formId: "header-04",
//         columns: createRecycleShippingColumns(),
//         rowClass: (item) => item.lossDate ? "is-loss" : "",
//         defaultFormName: "inlineShipping",
//         checkable:false,
//         // onInit: () => {
//         //     const today = getToday();
//         //     // const from = document.querySelector("[name='shipping-date']");
//         //     const from = document.getElementById("shipping-date02");
//         //     if(from && !from.value){
//         //         from.value = today;
//         //     }
//         // },
//         // buildParams: () => {
//         //     // const from = document.querySelector("[name='shipping-date']")?.value;
//         //     const from = document.getElementById("shipping-date02")?.value;
//         //     return {
//         //         state: APP.cache.common.state.INITIAL,
//         //         category: String(APP.cache.common.recycleCategory.SHIPPER),
//         //         dateFrom: from,
//         //         dateTo: toExclusiveDate(from)
//         //     };
//         // },
//         onInit: () => initTodayValue("shipping-date02"),
//         buildParams: () =>
//             buildRecycleDateParams({
//                 category:
//                     APP.cache.common
//                         .recycleCategory
//                         .SHIPPER,

//                 inputId:
//                     "shipping-date02"
//             }),
//         canSave: () => {
//             const recycle = document.querySelector("#header-04 [name='recycle-number']")?.value;
//             return recycle?.trim() !== "";
//         },
//         actions: {
//             search: async (controller) => {await controller.refresh();},
//             recycleChanged: async (controller) => {await controller.refresh();}
//         },
//         // afterSave: async () => {
//         //     const recycle = getController("recycleList");
//         //     await recycle?.executeAction("recycleChanged");
//         // }
//         afterSave: refreshRecycleList
//     });

// export const recycleLossPage = () =>
//     createRecyclePage({
//         key: "recycleLoss",
//         components: {combo: true, input: true},
//         tableId: "table-05",
//         footerId: "footer-05",
//         formId: "header-05",
//         columns: createRecycleLossColumns(),
//         rowClass: (item) => item.lossDate ? "is-loss" : "",
//         defaultFormName: "inlineLoss",
//         checkable:false,
//         // onInit: () => {
//         //     const today = getToday();
//         //     // const from = document.querySelector("[name='loss-date']");
//         //     const from = document.getElementById("loss-date02");
//         //     if(from && !from.value){
//         //         from.value = today;
//         //     }
//         // },
//         // buildParams: () => {
//         //     // const from = document.querySelector("[name='loss-date']")?.value;
//         //     const from = document.getElementById("loss-date02")?.value;
//         //     return {
//         //         state: APP.cache.common.state.INITIAL,
//         //         category: String(APP.cache.common.recycleCategory.LOSS),
//         //         dateFrom: from,
//         //         dateTo: toExclusiveDate(from)
//         //     };
//         // },
//         onInit: () => initTodayValue("loss-date02"),
//         buildParams: () =>
//             buildRecycleDateParams({
//                 category:
//                     APP.cache.common
//                         .recycleCategory
//                         .LOSS,

//                 inputId:
//                     "loss-date02"
//             }),
//         canSave: () => {
//             const recycle = document.querySelector("#header-05 [name='recycle-number']")?.value;
//             return recycle?.trim() !== "";
//         },
//         actions: {
//             search: async (controller) => {await controller.refresh();},
//             recycleChanged: async (controller) => {await controller.refresh();}
//         },
//         // afterSave: async () => {
//         //     const recycle = getController("recycleList");
//         //     await recycle?.executeAction("recycleChanged");
//         // }
//         afterSave: refreshRecycleList
//     });