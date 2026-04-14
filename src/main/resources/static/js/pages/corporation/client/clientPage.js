"use strict"

import { initCommon } from "../../../core/init/initPage.js";
import { PageController } from "../../../controllers/PageController.js";
import { DataTable } from "../../../core/table/DataTable.js";
import { createClientCompanyColumns, createOfficeColumns } from "./columns.js";
import { FormController } from "../../../controllers/FormController.js";
import { registerController } from "../../../controllers/controllers.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { api } from "../../../core/api/apiService.js";
import { initPageCache } from "../../../core/init/initPageCache.js";

export async function init() {

    await initCommon();
    await initPageCache("/api/client/init/cache");

    // tab1
    const company = clientCompanyPage();
    registerController("clientCompany", company);

    company.init({
        columns: createClientCompanyColumns(company),
        data: [],
        components: { combo: true }
    });
    await company.dataTable.refresh();

    // tab2
    const office = clientOfficePage();
    registerController("office", office);

    office.init({
        columns: createOfficeColumns(office),
        data: [],
        components: { combo: true }
    });
    await office.dataTable.refresh();
}

export const clientCompanyPage = () => {

    const controller = new PageController({
        key:"companyId",

        table: {
            create: (controller, columns) => new DataTable({
                controller: controller,
                tableId: "table-01",
                footerId: "footer-01",
                columns,
                idKey: controller.key,
                checkable: true,
                buildParams: () => ({
                    state: APP.cache.common.state.INITIAL,
                    category: APP.cache.common.companyCategory.PARTNER
                }),
                buildCsvParams: () => ({
                    state: APP.cache.common.state.INITIAL
                }),
                api: {
                    request: api.request, // 取得方法定義
                    select: "clientList",
                    delete: "companyDeleteByIds",
                    download: "companyCsv"
                },
                model: {
                    filters: {
                        category: filterFactory.equals("category")
                    }
                },
                onDoubleClick: (item) => controller.openEdit(item[controller.key])
            })
        },
        form: {
            create: (controller) => new FormController({
                controller: controller,
                formId: "form-01",
                key: controller.key,
                beforeSave: (payload) => {
                    const id = payload[controller.key];
                    if (!id || Number(id) === 0) {
                        payload.category = document.getElementById('code01');
                    }
                },
                onSaved: async (id) => {
                    await controller.dataTable.refresh();
                    controller.scrollToRow(id);
                },
                buildParams: (id) => ({
                    state: APP.cache.common.state.INITIAL,
                    companyId: id
                }),
                api: {
                    request: api.request,
                    find: "companyDetail",
                    save: "companySave"
                }
            })
        }
    });
    return controller;
};

export const clientOfficePage = () => {

    const controller = new PageController({
        key:"officeId",

        table: {
            create: (controller, columns) => new DataTable({
                controller: controller,
                tableId: "table-02",
                footerId: "footer-02",
                columns,
                idKey: controller.key,
                checkable: true,
                buildParams: () => ({
                    state: APP.cache.common.state.INITIAL,
                    category: APP.cache.common.companyCategory.PARTNER
                }),
                buildCsvParams: () => ({
                    state: APP.cache.common.state.INITIAL
                }),
                api: {
                    request: api.request,
                    select: "clientOfficeList",
                    delete: "officeDeleteByIds",
                    download: "officeCsv"
                },
                model: {
                    filters: {
                        companyId: filterFactory.equals("companyId")
                    }
                },
                onDoubleClick: (item) => controller.openEdit(item[controller.key]),
            })
        },
        form: {
            create: (controller) => new FormController({
                formId: "form-02",
                key: controller.key,
                controller: controller, 
                // beforeSave: (payload) => {
                //     const id = payload[controller.key];
                //     if (!id || Number(id) === 0) {
                //         payload.category = APP.cache.common.employeeCategory.CONSTRUCT;
                //     }
                // },
                onSaved: async (id) => {
                    await controller.dataTable.refresh();
                    controller.scrollToRow(id);
                },
                buildParams: (id) => ({
                    state: APP.cache.common.state.INITIAL,
                    officeId: id                   
                }),
                api: {
                    request: api.request,
                    find: "officeDetail",
                    save: "officeSave"
                }
            })
        }
    });
    return controller;
};