"use strict"

import {TableController} from "../../../core/table/TableController.js";

export const partnerCompanyController = new TableController({
    tableId:"table-01",
    origin: () => APP.cache.companyOrigin,
    filters:{
        keyword:true,
        // companyId:"eq",
        // name:"includes"
    }
});

export const partnerEmployeeController = new TableController({
    tableId:"table-02",
    origin: () => APP.cache.employeeOrigin,
    filters:{
        companyId:"eq",
        // companyId:(v,value)=> v.companyId === value,
    }
});