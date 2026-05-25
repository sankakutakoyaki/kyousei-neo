"use strict";

// import { createCrudPage } from "../../core/page/createCrudPage.js";
import { filterFactory } from "../../util/filterFactory.js";
import { EmployeeRepository } from "../../repositories/prsonnel/employee/EmployeeRepository.js";
import { createMasterPage } from "../../core/page/createMasterPage.js";

export function createEmployeePage(config){

    return createMasterPage({

        ...config,

        idKey: "employeeId",

        repository:
            EmployeeRepository,

        buildDetailParams:
            (id) => ({
                state:
                    APP.cache.common
                        .state
                        .INITIAL,

                employeeId:
                    id
            }),

        model:
            config.model ?? {

                filters: {

                    officeId:
                        filterFactory
                            .equals("officeId")
                }
            },

        validateBusiness:
            config.validateBusiness ??
            (async (payload) => {

                if(!payload.officeId){

                    throw {

                        message:
                            "営業所を選択してください",

                        fields:
                            ["officeId"]
                    };
                }
            }),

        beforeSave:
            (payload) => {

                if(
                    !payload.employeeId ||
                    Number(payload.employeeId) === 0
                ){
                    payload.category =
                        config.category;
                }

                if(
                    !payload.code ||
                    Number(payload.code) === 0
                ){
                    payload.code = 0;
                }

                if(config.beforeSave){
                    config.beforeSave(payload);
                }
            }
    });
}


// export function createEmployeePage(config){
//     return createCrudPage({
//         key: config.key,
//         components: config.components,
//         tableId: config.tableId,
//         footerId: config.footerId,
//         formId: config.formId,
//         idKey: "employeeId",
//         // repository: EmployeeRepository,
//         saveHandler: EmployeeRepository.save,
//         columns: config.columns,
//         buildParams: () => ({
//             state: APP.cache.common.state.INITIAL,
//             category: config.category,
//         }),
//         buildCsvParams: () => ({
//             state: APP.cache.common.state.INITIAL
//         }),
//         model: config.model ?? {
//             filters: {officeId:filterFactory.equals("officeId")}
//         },
//         validateBusiness: config.validateBusiness ?? (async (payload) => {
//             if (!payload.officeId) {
//                 throw {
//                     message: "営業所を選択してください",
//                     // field: "officeId"
//                     fields: ["officeId"]
//                 };
//             }
//         }),
//         beforeSave: (payload, form) => {
//             const key = form.dataset.key;
//             const id = payload[key];
//             if (!id || Number(id) === 0) {
//                 payload.category = config.category;
//             }

//             const code = payload.code;
//             if (!code || Number(code) === 0) {
//                 payload.code = 0;
//             }
//         },
//         afterSave: async (controller, id) => {
//             await controller.refresh(id);
//         },
//         buildDetailParams: (id) => ({
//             state: APP.cache.common.state.INITIAL,
//             employeeId: id
//         })
//     });
// }