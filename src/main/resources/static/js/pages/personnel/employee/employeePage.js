"use strict"

import { initCommon } from "../../../core/init/initPage.js";
import { PageController } from "../../../controllers/PageController.js";
import { DataTable } from "../../../core/table/DataTable.js";
import { createEmployeeColumns } from "./columns.js";
import { FormController } from "../../../controllers/FormController.js";
import { registerController } from "../../../controllers/controllers.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { api } from "../../../core/api/apiService.js";
import { initPageCache } from "../../../core/init/initPageCache.js";
import { EmployeeRepository } from "../../../repositories/prsonnel/employee/EmployeeRepository.js";
import { createCrudPage } from "../../../core/page/createCrudPage.js";
import { createEmployeePage } from "../createEmployeePage.js";
// import { dispatchAction } from "../../../util/actionDispatcher.js";
// import { dispatchAction } from "../../../core/events/actionDispatcher.js";

export async function init() {
    await initCommon();
    await initPageCache("/api/employee/init/cache");

    // tab1
    const fulltime = fulltimeEmployeePage();
    registerController("fulltime", fulltime);
    // fulltime.init({
    //     columns: createEmployeeColumns(fulltime),
    //     data: [],
    //     components: { combo: true }
    // });
    fulltime.init();
    // await fulltime.dataTable.refresh();
    await fulltime.refresh();

    // tab2
    const parttime = parttimeEmployeePage();
    registerController("parttime", parttime);
    // parttime.init({
    //     columns: createEmployeeColumns(parttime),
    //     data: [],
    //     components: { combo: true, input: true }
    // });
    parttime.init();
    // await parttime.dataTable.refresh();
    await parttime.refresh();
}

export const fulltimeEmployeePage = () =>
    createEmployeePage({
        key: "fulltime",
        components: {combo: true},
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        columns: createEmployeeColumns(),
        category:APP.cache.common.employeeCategory.FULLTIME
    });

export const parttimeEmployeePage = () =>
    createEmployeePage({
        key: "parttime",
        components: {combo: true},
        tableId: "table-02",
        footerId: "footer-02",
        formId: "form-01",
        columns: createEmployeeColumns(),
        category: APP.cache.common.employeeCategory.PARTTIME
    });

// export const fulltimeEmployeePage = () => {
//     const controller = new PageController({
//         key: "fulltime",

//         table: {
//             create: (controller, columns) => new DataTable({
//                 controller: controller,
//                 tableId: "table-01",
//                 footerId: "footer-01",
//                 columns,
//                 idKey: "employeeId",
//                 checkable: true,
//                 buildParams: () => ({
//                     state: APP.cache.common.state.INITIAL,
//                     category: APP.cache.common.employeeCategory.FULLTIME,
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
//                         officeId: filterFactory.equals("officeId")
//                     }
//                 },
//                 // onDoubleClick: (item) => controller.openEdit(item.employeeId),
//                 onDoubleClick: (item) => controller.openForm("detail", item.employeeId, { bulkMode:false }),
//             })
//         },
//         forms: {
//             detail:{
//                 create: (controller) => new FormController({
//                     formId: "form-01",
//                     key: controller.key,
//                     controller: controller,
//                     validateBusiness: async (payload) => {
//                     // businessValidate: (payload) => {
//                         if (!payload.officeId) {
//                             throw { message: "営業所を選択してください", field: "officeId" };
//                         }
//                     },
//                     beforeSave: (payload, form) => {
//                         const key = form.dataset.key;
//                         const id = payload[key];
//                         if (!id || Number(id) === 0) {
//                             payload.category = APP.cache.common.employeeCategory.FULLTIME;
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

// export const parttimeEmployeePage = () => {
//     const controller = new PageController({
//         key: "parttime",

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
//                     category: APP.cache.common.employeeCategory.PARTTIME,
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
//                         officeId: filterFactory.equals("officeId")
//                     }
//                 },
//                 onDoubleClick: (item) => controller.openEdit(item.employeeId),
//             })
//         },
//         forms: {
//             detail:{
//                 create: (controller) => new FormController({
//                     formId: "form-01",
//                     key: controller.key,
//                     controller: controller,
//                     validateBusiness: async (payload) => {
//                     // businessValidate: (payload) => {
//                         if (!payload.officeId) {
//                             throw { message: "営業所を選択してください", field: "officeId" };
//                         }
//                     },
//                     beforeSave: (payload, form) => {
//                         const key = form.dataset.key;
//                         const id = payload[key];
//                         if (!id || Number(id) === 0) {
//                             payload.category = APP.cache.common.employeeCategory.PARTTIME;
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