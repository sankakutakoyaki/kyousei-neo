"use strict"

import { execSave } from "../../../components/form/entity.js";
import { TableController } from "../../../core/table/TableController.js";

export const partnerCompanyController = new TableController({
    tableId:"table-01",
    selectUrl: "/api/partner/get/list",
    deleteUrl: "/api/company/delete",
    downloadUrl: "/api/company/download/csv",
    onSubmit: async function(form){
        console.log(form);
        await execSave(form, { originEntity: APP.cache.companyOrigin, parent: "company" });
        await this.refresh();
    },
});

export const partnerEmployeeController = new TableController({
    tableId:"table-02",
    selectUrl: "/api/employee/get/list/partner",
    deleteUrl: "/api/employee/delete",
    downloadUrl: "/api/employee/download/csv"
});