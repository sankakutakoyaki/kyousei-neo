"use strict"

import { initCommon } from "../../../core/init/initPage.js";
import { PageController } from "../../../controllers/PageController.js";
import { DataTable } from "../../../core/table/DataTable.js";
import { createClientCompanyColumns, createOfficeColumns, createStaffColumns } from "./columns.js";
import { FormController } from "../../../controllers/FormController.js";
import { registerController } from "../../../controllers/controllers.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { api } from "../../../core/api/apiService.js";
import { initPageCache } from "../../../core/init/initPageCache.js";
// import { dispatchAction } from "../../../util/actionDispatcher.js";
import { dispatchAction } from "../../../core/events/actionDispatcher.js";
import { initParentChildLink } from "../../../util/link.js";
import { CompanyService } from "../../../services/company/CompanyService.js";
import { OfficeService } from "../../../services/company/OfficeService.js";

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
    // await company.dataTable.refresh();
    await company.refresh();

    // tab2
    const office = clientOfficePage();
    registerController("clientOffice", office);

    office.init({
        columns: createOfficeColumns(office),
        data: [],
        components: { combo: true, input: true },
        actions: {
            // companyChanged: async (controller, payload) => {
            //     const list = await api.get("/api/company/client/combo");
            //     APP.cache.page.companyComboList = list.data;
            //     await controller.reset();
            // }
            companyChanged: async (controller, payload) => {
                await CompanyService.refreshCombo();
                await controller.reset();
            }
        }
    });
    // await office.dataTable.refresh();
    await office.refresh();

    // tab3
    const staff = clientStaffPage();
    registerController("clientStaff", staff);

    staff.init({
        columns: createStaffColumns(staff),
        data: [],
        components: { combo: true, input: true },
        actions: {
            // companyChanged: async (controller, payload) => {
            //     const list = await api.get("/api/company/client/combo");
            //     APP.cache.page.companyComboList = list.data;
            //     await controller.reset();
            // },
            // officeChanged: async (c, payload) => {
            //     const list = await api.get("/api/office/client/combo");
            //     APP.cache.page.officeComboList = list.data;
            //     await c.reset();
            // }
            companyChanged: async (controller, payload) => {
                await CompanyService.refreshCombo();
                await controller.reset();
            },
            officeChanged: async (controller, payload) => {
                await OfficeService.refreshCombo();
                await controller.reset();
            }
        }
    });
    // await staff.dataTable.refresh();
    await staff.refresh();

    initParentChildLink();
}

export const clientCompanyPage = () => {

    const controller = new PageController({
        key:"clientCompany",

        onDeleted: () => {
            dispatchAction({
                action: "companyChanged",
                target: ["clientOffice", "clientStaff"]
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
                onDoubleClick: (item) => controller.openEdit(item.companyId)
            })
        },
        forms: {
            detail: {
                create: (controller) => new FormController({
                    controller: controller,
                    formId: "form-01",
                    key: controller.key,
                    validateBusiness: async (payload) => {
                    // businessValidate: (payload) => {
                        if (!payload.category) {
                            throw { message: "分類を選択してください", field: "category" };
                        }
                    },
                    afterSave: async (id) => {
                    // onSaved: async (id) => {
                        // await controller.dataTable.refresh();
                        // controller.scrollToRow(id);
                        await controller.refresh(id);

                        dispatchAction({
                            action: "companyChanged",
                            target: ["clientOffice", "clientStaff"]
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
                target: ["clientOffice", "clientStaff"]
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
                onDoubleClick: (item) => controller.openEdit(item.officeId),
            })
        },
        forms: {
            detail: {
                create: (controller) => new FormController({
                    formId: "form-02",
                    key: controller.key,
                    controller: controller,
                    afterSave: async (id) => {
                    // onSaved: async (id) => {
                        // await controller.dataTable.refresh();
                        // controller.scrollToRow(id);
                        await controller.refresh(id);
                        dispatchAction({
                            action: "officeChanged",
                            target: ["clientOffice", "clientStaff"]
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
                    select: "staffList",
                    delete: "staffDeleteByIds",
                    download: "staffCsv"
                },
                model: {
                    filters: {
                        companyId: filterFactory.equals("companyId"),
                        officeId: filterFactory.equals("officeId")
                    }
                },
                onDoubleClick: (item) => controller.openEdit(item.staffId),
            })
        },
        forms: {
            detail: {
                create: (controller) => new FormController({
                    formId: "form-03",
                    key: controller.key,
                    controller: controller,
                    afterSave: async (id) => {
                    // onSaved: async (id) => {
                        // await controller.dataTable.refresh();
                        // controller.scrollToRow(id);
                        await controller.refresh(id);
                    },
                    buildParams: (id) => ({
                        state: APP.cache.common.state.INITIAL,
                        staffId: id
                    }),
                    api: {
                        request: api.request,
                        find: "staffDetail",
                        save: "staffSave"
                    }
                })
            }
        }
    });
    return controller;
};