"use strict"

import { initCommon } from "../../../core/init/initPage.js";
import { PageController } from "../../../controllers/PageController.js";
import { DataTable } from "../../../core/table/DataTable.js";
import { createPartnerCompanyColumns, createPartnerEmployeeColumns } from "./columns.js";
import { FormController } from "../../../controllers/FormController.js";
import { registerController } from "../../../controllers/controllers.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { api } from "../../../core/api/apiService.js";
import { initPageCache } from "../../../core/init/initPageCache.js";
import { dispatchAction } from "../../../util/actionDispatcher.js";

export async function init() {

    await initCommon();
    await initPageCache("/api/partner/init/cache");

    // tab1
    const company = partnerCompanyPage();
    registerController("partnerCompany", company);

    company.init({
        columns: createPartnerCompanyColumns(company),
        data: [],
        components: { combo: true }
    });
    await company.dataTable.refresh();

    // tab2
    const employee = partnerEmployeePage();
    registerController("partnerEmployee", employee);

    employee.init({
        columns: createPartnerEmployeeColumns(employee),
        data: [],
        components: { combo: true, input: true },
        actions: {
            companyChanged: async (c, payload) => {
                // combo更新
                const list = await api.get("/api/company/partner/combo");
                APP.cache.page.companyComboList = list.data;
                // UI & データ更新
                await c.reset();
            }
        }
    });

    await employee.dataTable.refresh();
}

export const partnerCompanyPage = () => {

    const controller = new PageController({
        key:"partnerCompany",

        onDeleted: () => {
            dispatchAction({
                action: "companyChanged",
                target: "partnerEmployee"
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
                    // select: "partnerCompanyList",
                    // delete: "partnerCompanyDeleteByIds",
                    // download: "partnerCompanyCsv"
                    select: "companyList",
                    delete: "companyDeleteByIds",
                    download: "companyCsv"
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
                        payload.category = APP.cache.common.companyCategory.PARTNER;
                    }
                },
                onSaved: async (id) => {
                    await controller.dataTable.refresh();
                    controller.scrollToRow(id);

                    // window.dispatchEvent(new CustomEvent("partnerCompany:changed", {
                    //     detail: { id }
                    // }));
                    dispatchAction({
                        action: "companyChanged",
                        target: "partnerEmployee",
                        // data: { companyId: id }
                    });
                },
                buildParams: (id) => ({
                    state: APP.cache.common.state.INITIAL,
                    companyId: id
                }),
                api: {
                    request: api.request,
                    // find: "partnerCompanyDetail",
                    // save: "partnerCompanySave"
                    find: "companyDetail",
                    save: "companySave"
                }
            })
        }
    });
    return controller;
};

export const partnerEmployeePage = () => {

    const controller = new PageController({
        key: "partnerEmployee",

        table: {
            create: (controller, columns) => new DataTable({
                controller: controller,
                tableId: "table-02",
                footerId: "footer-02",
                columns,
                idKey: "employeeId",
                checkable: true,
                buildParams: () => ({
                    state: APP.cache.common.state.INITIAL,
                    category: APP.cache.common.employeeCategory.CONSTRUCT,
                }),
                buildCsvParams: () => ({
                    state: APP.cache.common.state.INITIAL
                }),
                api: {
                    request: api.request,
                    select: "employeeList",
                    delete: "employeeDeleteByIds",
                    download: "employeeCsv"
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
                beforeSave: (payload) => {
                    const id = payload[controller.key];
                    if (!id || Number(id) === 0) {
                        payload.category = APP.cache.common.employeeCategory.CONSTRUCT;
                    }
                },
                onSaved: async (id) => {
                    await controller.dataTable.refresh();
                    controller.scrollToRow(id);
                },
                buildParams: (id) => ({
                    state: APP.cache.common.state.INITIAL,
                    employeeId: id                   
                }),
                api: {
                    request: api.request,
                    find: "employeeDetail",
                    save: "employeeSave"
                }
            })
        }
    });
    return controller;
};

// window.addEventListener("load", () => {

//     initCommon();  

//     // tab1
//     const company = partnerCompanyPage();
//     registerController("partnerCompany", company); 
//     company.name = "partnerCompany";   
//     company.init({
//         columns: createPartnerCompanyColumns(company),
//         // data: APP.cache.companyOrigin,
//         data: [],
//         components: {
//             combo: true
//         },
//     });
//     company.dataTable.refresh();

//     //　tab2
//     const employee = partnerEmployeePage();
//     registerController("partnerEmployee", employee);
//     employee.name = "partnerEmployee";
//     employee.init({
//         columns: createPartnerEmployeeColumns(employee),
//         // data: APP.cache.employeeOrigin,
//         data: [],
//         components: {
//             combo: true
//         }
//     });
//     employee.dataTable.refresh();
// });

// function initCsrf() {
//     // return {
//     //     token: document.querySelector('meta[name="_csrf"]').content,
//     //     header: document.querySelector('meta[name="_csrf_header"]').content
//     // };
//     window.APP = {
//         security: {
//             token: document.querySelector('meta[name="_csrf"]').content,
//             header: document.querySelector('meta[name="_csrf_header"]').content
//         }
//     };
// }

// async function initCache() {
//     const res = await fetch("/api/partner/init/cache");
//     const data = await res.json();
//     // window.APP.cache = data;
//     window.APP = {
//         security: {
//             token: document.querySelector('meta[name="_csrf"]').content,
//             header: document.querySelector('meta[name="_csrf_header"]').content
//         },
//         cache: {                    
//             companyComboList: data.companyComboList,
//             genderComboList: data.genderComboList,
//             bloodTypeComboList: data.bloodTypeComboList,
//             state: data.state,
//             clientCategory: data.clientCategory,
//             employeeCategory: data.employeeCategory        
//         }
//     }
// }

// async function initCache() {
//     const res = await fetch("/api/partner/init/cache");
//     const data = await res.json();

//     Object.assign(APP.cache, data);
// }
