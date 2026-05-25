// "use strict";

// import { createCrudPage } from "../../core/page/createCrudPage.js";
// import { OfficeRepository } from "../../repositories/corporation/company/OfficeRepository.js";

// export function createOfficePage(config){
//     return createCrudPage({
//         key: config.key,
//         components: config.components,
//         tableId: config.tableId,
//         footerId: config.footerId,
//         formId: config.formId,
//         idKey: "officeId",
//         repository: OfficeRepository,
//         columns: config.columns,
//         buildParams: () => ({
//             state: APP.cache.common.state.INITIAL,
//             category: config.category
//         }),
//         buildCsvParams: () => ({
//             state: APP.cache.common.state.INITIAL
//         }),
//         model: config.model,
//         validateBusiness: config.validateBusiness,
//         beforeSave: config.beforeSave,
//         afterSave: async (controller, id) => {
//             await controller.refresh(id);
//             if(config.afterSave){
//                 await config.afterSave(controller, id);
//             }
//         },
//         buildDetailParams: (id) => ({
//             state: APP.cache.common.state.INITIAL,
//             officeId: id
//         })
//     });
// }