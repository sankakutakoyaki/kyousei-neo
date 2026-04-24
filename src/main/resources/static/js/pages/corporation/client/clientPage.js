"use strict"

import { initCommon } from "../../../core/init/initPage.js";
import { PageController } from "../../../controllers/PageController.js";
import { DataTable } from "../../../core/table/DataTable.js";
import { createClientCompanyColumns, createOfficeColumns } from "./columns.js";
import { createStaffColumns } from "../office/columns.js";
import { FormController } from "../../../controllers/FormController.js";
import { registerController } from "../../../controllers/controllers.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { api } from "../../../core/api/apiService.js";
import { initPageCache } from "../../../core/init/initPageCache.js";
import { dispatchAction } from "../../../util/actionDispatcher.js";

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
    registerController("clientOffice", office);

    office.init({
        columns: createOfficeColumns(office),
        data: [],
        components: { combo: true, input: true },
        actions: {
            companyChanged: async (c, payload) => {
                const list = await api.get("/api/company/client/combo");
                APP.cache.page.companyComboList = list.data;
                await c.reset();
            }
        }
    });
    await office.dataTable.refresh();

    // tab3
    const staff = clientStaffPage();
    registerController("clientStaff", staff);

    staff.init({
        columns: createStaffColumns(staff),
        data: [],
        components: { combo: true, input: true },
        actions: {
            companyChanged: async (c, payload) => {
                const list = await api.get("/api/company/client/combo");
                APP.cache.page.companyComboList = list.data;
                await c.reset();
            },
            officeChanged: async (c, payload) => {
                const list = await api.get("/api/office/client/combo");
                APP.cache.page.officeComboList = list.data;
                await c.reset();
            }
        }
    });
    await staff.dataTable.refresh();
}

export const clientCompanyPage = () => {

    const controller = new PageController({
        key:"clientCompany",

        onDeleted: () => {
            dispatchAction({
                action: "companyChanged",
                target: "clientOffice"
            });
        },

        table: {
            create: (controller, columns) => new DataTable({
                controller: controller,
                tableId: "table-01",
                footerId: "footer-01",
                columns,
                idKey: "companyId",
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
                businessValidate: (payload) => {
                    if (!payload.category) {
                        throw { message: "分類を選択してください", field: "category" };
                    }
                },
                // beforeSave: (payload) => {
                //     payload.category = document.getElementById('code01')?.value;
                // },
                onSaved: async (id) => {
                    await controller.dataTable.refresh();
                    controller.scrollToRow(id);

                    dispatchAction({
                        action: "companyChanged",
                        target: "clientOffice",
                    });
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
        key:"clientOffice",

        onDeleted: () => {
            dispatchAction({
                action: "officeChanged",
                target: "clientStaff"
            });
        },

        table: {
            create: (controller, columns) => new DataTable({
                controller: controller,
                tableId: "table-02",
                footerId: "footer-02",
                columns,
                idKey: "officeId",
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
                onSaved: async (id) => {
                    await controller.dataTable.refresh();
                    controller.scrollToRow(id);
                    dispatchAction({
                        action: "officeChanged",
                        target: "clientOffice",
                    });
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

export const clientStaffPage = () => {

    const controller = new PageController({
        key:"clientStaff",

        table: {
            create: (controller, columns) => new DataTable({
                controller: controller,
                tableId: "table-03",
                footerId: "footer-03",
                columns,
                idKey: "staffId",
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
                    select: "clientStaffList",
                    delete: "staffDeleteByIds",
                    download: "staffCsv"
                },
                model: {
                    filters: {
                        staffId: filterFactory.equals("staffId")
                    }
                },
                onDoubleClick: (item) => controller.openEdit(item[controller.key]),
            })
        },
        form: {
            create: (controller) => new FormController({
                formId: "form-03",
                key: controller.key,
                controller: controller, 
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
                    find: "staffDetail",
                    save: "staffSave"
                }
            })
        }
    });
    return controller;
};