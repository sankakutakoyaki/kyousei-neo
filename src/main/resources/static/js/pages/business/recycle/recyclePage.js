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
import { dispatchAction } from "../../../util/actionDispatcher.js";
import { initParentChildLink } from "../../../util/link.js";

export async function init() {

    await initCommon();
    await initPageCache("/api/recycle/init/cache");

    // tab1
    const list = recycleListPage();
    registerController("recycleList", list);

    list.init({
        columns: createRecycleListColumns(list),
        data: [],
        components: { combo: true, input: true },
        // actions: {
        //     companyChanged: async (c, payload) => {
        //         const list = await api.get("/api/company/client/combo");
        //         APP.cache.page.companyComboList = list.data;
        //         await c.reset();
        //     },
        //     officeChanged: async (c, payload) => {
        //         const list = await api.get("/api/office/client/combo");
        //         APP.cache.page.officeComboList = list.data;
        //         await c.reset();
        //     }
        // }
    });
    await list.dataTable.refresh();
    
    initParentChildLink();
}

export const recycleListPage = () => {

    const controller = new PageController({
        key:"recycleList",

        onDeleted: () => {
            dispatchAction({
                action: "recycleChanged",
                target: ["recycleUse", "recycleDeli", "recycleShip", "recycleLoss"]
            });
        },

        table: {
            create: (controller, columns) => new DataTable({
                controller: controller,
                tableId: "table-01",
                footerId: "footer-01",
                columns,
                idKey: "recycleId",
                checkable: true,
                buildParams: () => ({
                    state: APP.cache.common.state.INITIAL,
                    // category: controller.selectedCategory,
                    // date: controller.selectedDate
                    category: 'use_date',
                    date: '2026-1-1'
                }),
                buildCsvParams: () => ({
                    state: APP.cache.common.state.INITIAL
                }),
                api: {
                    request: api.request, // 取得方法定義
                    select: "recycleList",
                    delete: "recycleDeleteByIds",
                    download: "recycleCsv"
                },
                model: {
                    filters: {
                        category: filterFactory.equals("category")
                    }
                },
                onDoubleClick: (item) => controller.openEdit(item.recycleId)
            })
        },
        form: {
            create: (controller) => new FormController({
                controller: controller,
                formId: "form-01",
                key: controller.key,
                // businessValidate: (payload) => {
                //     if (!payload.category) {
                //         throw { message: "分類を選択してください", field: "category" };
                //     }
                // },
                onSaved: async (id) => {
                    await controller.dataTable.refresh();
                    controller.scrollToRow(id);

                    dispatchAction({
                        action: "recycleChanged",
                        target: ["recycleUse", "recycleDeli", "recycleShip", "recycleLoss"]
                    });
                },
                buildParams: (id) => ({
                    state: APP.cache.common.state.INITIAL,
                    recycleId: id
                }),
                api: {
                    request: api.request,
                    find: "recycleDetail",
                    save: "recycleSave"
                }
            })
        }
    });
    return controller;
};