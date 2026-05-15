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
// import { dispatchAction } from "../../../util/actionDispatcher.js";
// import { dispatchAction } from "../../../core/events/actionDispatcher.js";
import { CompanyService } from "../../../services/corporation/company/CompanyService.js";
import { EmployeeRepository } from "../../../repositories/prsonnel/employee/EmployeeRepository.js";
import { createCompanyPage } from "../createCompanyPage.js";
import { createEmployeePage } from "../../personnel/createEmployeePage.js";
import { getController } from "../../../controllers/controllers.js";

export async function init() {
    await initCommon();
    await initPageCache("/api/partner/init/cache");

    // tab1
    const company = partnerCompanyPage();
    registerController("partnerCompany", company);
    company.init();
    // company.init({
    //     columns: createPartnerCompanyColumns(),
    //     data: [],
    //     components: { combo: true }
    // });
    // await company.dataTable.refresh();
    await company.refresh();

    // tab2
    const employee = partnerEmployeePage();
    registerController("partnerEmployee", employee);
    employee.init();
    // employee.init({
    //     // columns: createPartnerEmployeeColumns(employee),
    //     columns: createPartnerEmployeeColumns(),
    //     data: [],
    //     components: { combo: true, input: true },
    //     actions: {
    //         // companyChanged: async (controller, payload) => {
    //         //     // combo更新
    //         //     const list = await api.get("/api/company/partner/combo");
    //         //     APP.cache.page.companyComboList = list.data;
    //         //     // UI & データ更新
    //         //     await controller.reset();
    //         // }
    //         companyChanged: async (controller, payload) => {
    //             await CompanyService.refreshCombo();
    //             await controller.reset();
    //         }
    //     }
    // });
    // await employee.dataTable.refresh();
    await employee.refresh();
}

export const partnerCompanyPage = () =>
    createCompanyPage({
        key: "partnerCompany",
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        service: CompanyService,
        category: APP.cache.common.companyCategory.PARTNER,
        columns: createPartnerCompanyColumns(),
        afterSave: async () => {
            const employee = getController("partnerEmployee");
            await employee?.executeAction("companyChanged");
        }
    });

export const partnerEmployeePage = () =>
    createEmployeePage({
        key: "partnerEmployee",
        components: {combo: true, input: true},
        tableId: "table-02",
        footerId: "footer-02",
        formId: "form-02",
        category: APP.cache.common.employeeCategory.CONSTRUCT,
        columns: createPartnerEmployeeColumns(),
        model: {
            filters: {
                companyId: filterFactory.equals("companyId")
            }
        },
        validateBusiness: async (payload) => {
            if (!payload.companyId) {
                throw {
                    message: "会社を選択してください",
                    field: "companyId"
                };
            }
        }
    });

// export const partnerCompanyPage = () => {
//     const controller = new PageController({
//         key:"partnerCompany",

//         onDeleted: () => {
//             dispatchAction({
//                 action: "companyChanged",
//                 target: "partnerEmployee"
//             });
//         },

//         table: {
//             create: (controller, columns) => new DataTable({
//                 controller: controller,
//                 tableId: "table-01",
//                 footerId: "footer-01",
//                 columns,
//                 idKey: "companyId",
//                 checkable: true,
//                 buildParams: () => ({
//                     state: APP.cache.common.state.INITIAL,
//                     category: APP.cache.common.companyCategory.PARTNER
//                 }),
//                 buildCsvParams: () => ({
//                     state: APP.cache.common.state.INITIAL
//                 }),
//                 api: {
//                     request: api.request, // 取得方法定義
//                     // select: "partnerCompanyList",
//                     // delete: "partnerCompanyDeleteByIds",
//                     // download: "partnerCompanyCsv"
//                     select: "companyList",
//                     delete: "companyDeleteByIds",
//                     download: "companyCsv"
//                 },
//                 // repository: EmployeeRepository,
//                 // onDoubleClick: (item) => controller.openEdit(item.companyId)
//                 onDoubleClick: (item) => controller.openForm("detail", item.companyId, { bulkMode:false })
//             })
//         },
//         forms: {
//             detail: {
//                 create: (controller) => new FormController({
//                     controller: controller,
//                     formId: "form-01",
//                     key: controller.key,
//                     beforeSave: (payload, form) => {
//                         // const id = payload[controller.key];
//                         const key = form.dataset.key;
//                         const id = payload[key];
//                         if (!id || Number(id) === 0) {
//                             payload.category = APP.cache.common.companyCategory.PARTNER;
//                         }
//                     },
//                     afterSave: async (id) => {
//                     // onSaved: async (id) => {
//                         // await controller.dataTable.refresh();
//                         // controller.scrollToRow(id);
//                         await controller.refresh(id);

//                         // window.dispatchEvent(new CustomEvent("partnerCompany:changed", {
//                         //     detail: { id }
//                         // }));
//                         dispatchAction({
//                             action: "companyChanged",
//                             target: "partnerEmployee",
//                             // data: { companyId: id }
//                         });
//                     },
//                     buildParams: (id) => ({
//                         state: APP.cache.common.state.INITIAL,
//                         companyId: id
//                     }),
//                     api: {
//                         request: api.request,
//                         // find: "partnerCompanyDetail",
//                         // save: "partnerCompanySave"
//                         find: "companyDetail",
//                         save: "companySave"
//                     }
//                     // epository: companyRepository
//                 })
//             }
//         }
//     });
//     return controller;
// };

// export const partnerEmployeePage = () => {

//     const controller = new PageController({
//         key: "partnerEmployee",

//         table: {
//             create: (controller, columns) => new DataTable({
//                 controller: controller,
//                 tableId: "table-02",
//                 footerId: "footer-02",
//                 columns,
//                 idKey: "employeeId",
//                 checkable: true,
//                 buildParams: () => ({
//                     state: APP.cache.common.state.INITIAL,
//                     category: APP.cache.common.employeeCategory.CONSTRUCT,
//                 }),
//                 buildCsvParams: () => ({
//                     state: APP.cache.common.state.INITIAL
//                 }),
//                 // api: {
//                 //     request: api.request,
//                 //     select: "employeeList",
//                 //     delete: "employeeDeleteByIds",
//                 //     download: "employeeCsv"
//                 // },
//                 repository: EmployeeRepository,
//                 model: {
//                     filters: {
//                         companyId: filterFactory.equals("companyId")
//                     }
//                 },
//                 // onDoubleClick: (item) => controller.openEdit(item.employeeId),
//                 onDoubleClick: (item) => controller.openForm("detail", item.employeeId, { bulkMode:false })
//             })
//         },
//         forms: {
//             detail: {
//                 create: (controller) => new FormController({
//                     formId: "form-02",
//                     key: controller.key,
//                     controller: controller, 
//                     validateBusiness: async (payload) => {
//                     // businessValidate: (payload) => {
//                         if (!payload.companyId) {
//                             throw { message: "会社を選択してください", field: "companyId" };
//                         }
//                     },
//                     beforeSave: (payload, form) => {
//                         // const id = payload[controller.key];
//                         const key = form.dataset.key;
//                         const id = payload[key];
//                         if (!id || Number(id) === 0) {
//                             payload.category = APP.cache.common.employeeCategory.CONSTRUCT;
//                         }
//                         const code = payload.code;
//                         if (!code || Number(code) === 0) {
//                             payload.code = 0;
//                         }
//                     },
//                     afterSave: async (id) => {
//                     // onSaved: async (id) => {
//                         // await controller.dataTable.refresh();
//                         // controller.scrollToRow(id);
//                         await controller.refresh(id);
//                     },
//                     buildParams: (id) => ({
//                         state: APP.cache.common.state.INITIAL,
//                         employeeId: id                   
//                     }),
//                     // api: {
//                     //     request: api.request,
//                     //     find: "employeeDetail",
//                     //     save: "employeeSave"
//                     // }
//                     repository: EmployeeRepository
//                 })
//             }
//         }
//     });
//     return controller;
// };

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
