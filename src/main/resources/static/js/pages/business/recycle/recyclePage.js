"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { createRecycleListColumns, createRecycleUseColumns } from "./columns.js";
import { registerController } from "../../../applcation/controllerRegistry.js";
import { initParentChildLink } from "../../../util/link.js";
import { getToday } from "../../../util/time.js";
import { toExclusiveDate } from "../../../util/date.js";
import { RecycleRepository } from "../../../repositories/business/recycle/RecycleRepository.js";
import { createRecyclePage } from "../createRecyclePage.js";

export async function init() {
    await initCommon();
    await initPageCache("/api/recycle/init/cache");

    // tab1
    const list = recycleListPage();
    registerController("recycleList", list);
    list.init();
    await list.executeAction("search");
    
    // tab
    const use = recycleUsePage();
    registerController("recycleUse", use);
    use.init();
    
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
        onInit: () => {
            const today = getToday();
            const from = document.querySelector("[name='date-from']");
            const to = document.querySelector("[name='date-to']");
            if(from && !from.value){
                from.value = today;
            }
            if(to && !to.value){
                to.value = today;
            }
        },
        buildParams: () => {
            const cate = document.querySelector("[name='category01']")?.value;
            const from = document.querySelector("[name='date-from']")?.value;
            const to = document.querySelector("[name='date-to']")?.value;
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
            search: async (controller) => {await controller.refresh();}
        }
    });

export const recycleUsePage = () =>
    createRecyclePage({
        key: "recycleUse",
        components: {combo: true, input: true},
        tableId: "table-02",
        footerId: "footer-02",
        formId: "header-02",
        columns: createRecycleUseColumns(),
        // forms: {
        //     detail: {
        //         create: (controller) => createRecycleUseForm(controller)
        //     }
        // },
        defaultFormName: "inlineUse",
        onInit: () => {
            const today = getToday();
            const from = document.querySelector("[name='use-date']");
            if(from && !from.value){
                from.value = today;
            }
        // },
        // buildParams: () => {
        //     const from = document.querySelector("[name='use-date']")?.value;
        //     return {
        //         state: APP.cache.common.state.INITIAL,
        //         category: String(APP.cache.common.recycleCategory.USE),
        //         dateFrom: from,
        //         dateTo: toExclusiveDate(from)
        //     };
        },
        canSave: () => {
            const recycle = document.querySelector("#header-02 [name='recycle-number']")?.value;
            return recycle?.trim() !== "";
        },
        actions: {
            search: async (controller) => {await controller.refresh();}
        }
    });

// export const recycleListPage = () => {
//     const controller = new PageController({
//         key:"recycleList",
//         onInit: () => {
//             const today = getToday();
//             const from = document.querySelector("[name='date-from']");
//             const to   = document.querySelector("[name='date-to']");
//             if (from && !from.value) from.value = today;
//             if (to && !to.value) to.value = today;
//         },

//         // onDeleted: () => {
//         //     dispatchAction({
//         //         action: "recycleChanged",
//         //         target: ["recycleUse", "recycleDeli", "recycleShip", "recycleLoss"]
//         //     });
//         // },

//         table: {
//             create: (controller, columns) => new DataTable({
//                 controller: controller,
//                 tableId: "table-01",
//                 footerId: "footer-01",
//                 columns,
//                 idKey: "recycleId",
//                 checkable: true,
//                 buildParams: () => {
//                     const cate = document.querySelector("[name='category01']")?.value;
//                     const from = document.querySelector("[name='date-from']")?.value;
//                     const to   = document.querySelector("[name='date-to']")?.value;
//                     return {
//                         state: APP.cache.common.state.INITIAL,
//                         category: cate,
//                         dateFrom: from,
//                         dateTo: toExclusiveDate(to)
//                     };
//                 },
//                 buildCsvParams: () => ({
//                     state: APP.cache.common.state.INITIAL
//                 }),
//                 // api: {
//                 //     request: api.request, // 取得方法定義
//                 //     select: "recycleList",
//                 //     delete: "recycleDeleteByIds",
//                 //     download: "recycleCsv"
//                 // },
//                 repository: RecycleRepository,
//                 // onDoubleClick: (item) => controller.openEdit(item.recycleId)
//                 onDoubleClick:(item) => controller.openForm("detail", item.recycleId, { bulkMode:false })
//             })
//         },
//         forms: {
//             detail: {
//                 create: (controller) =>
//                     createRecycleForm(controller, {
//                         formId: "form-01"
//                         // saveQueryId: "recycleSave"
//                     })
//             },
//             bulk: {
//                 create: (controller) =>
//                     createRecycleForm(controller, {
//                         formId: "form-02"
//                         // // saveQueryId: "recycleBulkUpdate"
//                         // saveQueryId: "recycleSave"
//                     })
//             }
//         }
//     });
//     return controller;
// };

// // tab1フォーム共通処理
// const createRecycleForm = (controller, options = {}) =>
//     new FormController({
//         controller,
//         formId: options.formId,
//         key: controller.key,
//         afterSave: async (id) => {
//         // onSaved: async (id) => {
//             // await controller.dataTable.refresh();
//             // controller.scrollToRow(id);
//             await controller.refresh(id);
//         },
//         buildParams: (id) => ({
//             state:APP.cache.common.state.INITIAL,
//             recycleId: id
//         }),
//         validateBusiness: async (payload) => {
//         // businessValidate:
//             // async (payload) => {
//             const table =  controller.dataTable;
//             // const ids = controller.state.bulkMode ? table.model.getSelectedIds(): [payload.recycleId];
//             const ids = controller.isBulkMode() ? table.getSelectedIds(): [payload.recycleId];

//             for(const id of ids){
//                 // const origin = table.model.findOriginById(id);
//                 const origin = table.findOriginById(id);
//                 validatePersisted(payload, origin, "useDate", "使用日");
//                 validatePersisted(payload, origin, "deliveryDate", "引渡日");
//                 validatePersisted(payload, origin, "shippingDate", "発送日");
//             }
//         },
//         repository: RecycleRepository,
//         // api: {
//         //     request: api.request,
//         //     find: "recycleDetail",
//         //     save: options.saveQueryId
//         // }
//     });

// export const recycleUsePage = () => {
//     const controller = new PageController({
//         key:"recycleUse",

//         onInit: () => {
//             const today = getToday();
//             const from = document.querySelector("[name='use-date']");
//             if (from && !from.value) from.value = today;
//         },

//         table: {
//             create: (controller, columns) => new DataTable({
//                 controller: controller,
//                 tableId: "table-02",
//                 footerId: "footer-02",
//                 columns,
//                 idKey: "recycleId",
//                 checkable: true,
//                 buildParams: () => {
//                     const from = document.querySelector("[name='use-date']")?.value;
//                     return {
//                         state: APP.cache.common.state.INITIAL,
//                         category: String(APP.cache.common.recycleCategory.USE),
//                         dateFrom: from,
//                         dateTo: toExclusiveDate(from)
//                     };
//                 },
//                 repository: RecycleRepository,
//                 // api: {
//                 //     request: api.request, // 取得方法定義
//                 //     select: "recycleList"
//                 // },
//                 // ボタン disabled 制御
//                 canSave: () => {
//                     const recycle = document.querySelector("#header-02 [name='recycle-number']")?.value;
//                     return recycle?.trim() !== "";
//                 },
//                 onDoubleClick:(item) => controller.openForm("detail", item.recycleId, { bulkMode:false })
//             })
//         }
//     });
//     return controller;
// };

// function validatePersisted(
//     payload,
//     origin,
//     field,
//     label
// ){
//     // 未変更
//     if(!Object.hasOwn(payload, field)){
//         return;
//     }
//     // 空更新は許可
//     if(payload[field] == null || payload[field] === ""){
//         return;
//     }
//     // 元データなし
//     if(!origin?.[field]){
//         throw {
//             message:
//                 `${label}が未登録のため変更できません`,
//             field:
//                 convertKey(field, "camel", "kebab")
//         };
//     }
// }