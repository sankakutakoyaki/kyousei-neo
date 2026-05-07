"use strict"

import { initCommon } from "../../../core/init/initPage.js";
import { PageController } from "../../../controllers/PageController.js";
import { DataTable } from "../../../core/table/DataTable.js";
import { createRecycleListColumns } from "./columns.js";
import { FormController } from "../../../controllers/FormController.js";
import { registerController } from "../../../controllers/controllers.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { api } from "../../../core/api/apiService.js";
import { initPageCache } from "../../../core/init/initPageCache.js";
import { dispatchAction } from "../../../core/events/actionDispatcher.js";
import { initParentChildLink } from "../../../util/link.js";
import { getToday } from "../../../util/time.js";
import { toExclusiveDate } from "../../../util/date.js";

export async function init() {

    await initCommon();
    await initPageCache("/api/recycle/init/cache");

    // tab1
    const list = recycleListPage();
    registerController("recycleList", list);

    await list.init({
        columns: createRecycleListColumns(list),
        data: [],
        components: { combo: true, input: true },
        actions: {
            search: async (c) => {
                await c.dataTable.refresh();
            }
        }
    });
    await list.actions.search(list);
    
    initParentChildLink();
}

export const recycleListPage = () => {

    const controller = new PageController({
        key:"recycleList",

        onInit: () => {
            const today = getToday();
            const from = document.querySelector("[name='dateFrom']");
            const to   = document.querySelector("[name='dateTo']");
            if (from && !from.value) from.value = today;
            if (to && !to.value) to.value = today;
        },

        // onDeleted: () => {
        //     dispatchAction({
        //         action: "recycleChanged",
        //         target: ["recycleUse", "recycleDeli", "recycleShip", "recycleLoss"]
        //     });
        // },

        table: {
            create: (controller, columns) => new DataTable({
                controller: controller,
                tableId: "table-01",
                footerId: "footer-01",
                columns,
                idKey: "recycleId",
                checkable: true,
                buildParams: () => {
                    const cate = document.querySelector("[name='category01']")?.value;
                    const from = document.querySelector("[name='dateFrom']")?.value;
                    const to   = document.querySelector("[name='dateTo']")?.value;
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
                api: {
                    request: api.request, // 取得方法定義
                    select: "recycleList",
                    delete: "recycleDeleteByIds",
                    download: "recycleCsv"
                },
                onDoubleClick: (item) => controller.openEdit(item.recycleId)
            })
        },
        form: {
            create: (controller) => new FormController({
                controller: controller,
                formId: "form-01",
                key: controller.key,
                onSaved: async (id) => {
                    await controller.dataTable.refresh();
                    controller.scrollToRow(id);

                    // dispatchAction({
                    //     action: "recycleChanged",
                    //     target: ["recycleUse", "recycleDeli", "recycleShip", "recycleLoss"]
                    // });
                },
                buildParams: (id) => ({
                    state: APP.cache.common.state.INITIAL,
                    recycleId: id
                }),
                businessValidate: async (payload) => {
                    const table = controller.dataTable;
                    const ids = controller.state.bulkMode ? table.model.getSelectedIds(): [payload.recycleId];
                    for(const id of ids){
                        const origin = table.model.findOriginById(id);
                        validatePersisted(
                            payload,
                            origin,
                            "useDate",
                            "使用日"
                        );
                        validatePersisted(
                            payload,
                            origin,
                            "deliveryDate",
                            "引渡日"
                        );
                        validatePersisted(
                            payload,
                            origin,
                            "shippingDate",
                            "発送日"
                        );
                    }
                },
                api: {
                    request: api.request,
                    find: "recycleDetail",
                    save: "recycleBulkUpdate"
                }
            })
        }
    });
    return controller;
};

function validatePersisted(
    payload,
    origin,
    field,
    label
){
    // 未変更
    if(!Object.hasOwn(payload, field)){
        return;
    }
    // 空更新は許可
    if(payload[field] == null || payload[field] === ""){
        return;
    }
    // 元データなし
    if(!origin?.[field]){
        throw {
            message:
                `${label}が未登録のため変更できません`,
            field:
                convertKey(field, "camel", "kebab")
        };
    }
}