"use strict"

// import { getTable } from "../../../components/form/table.js";
import { TableController } from "../../../core/table/TableController.js";

export const partnerCompanyController = new TableController({
    tableId:"table-01"
});

export const partnerEmployeeController = new TableController({
    tableId:"table-02"
});

// export const partnerCompanyController = {
//     search(value){
//         const table = getTable("table-01");
//         table.set("keyword",value);
//         table.reload();
//     }
// };

// export const partnerEmployeeController = {
//     filterCompany(companyId){
//         const table = getTable("table-02");
//         table.set("companyId",companyId);
//         table.reload();
//     }
// };

// import {TableController} from "../../../core/table/TableController.js";

// export const partnerCompanyController = new TableController({
//     tableId:"table-01",
//     origin: () => APP.cache.companyOrigin,
//     filters:{
//         keyword:true,
//         // companyId:"eq",
//         // name:"includes"
//     }
// });

// export const partnerEmployeeController = new TableController({
//     tableId:"table-02",
//     origin: () => APP.cache.employeeOrigin,
//     filters:{
//         companyId:"eq",
//         // companyId:(v,value)=> v.companyId === value,
//     }
// });