"use strict"

import { TableController } from "../table/TableController.js";
import { closeFormDialog } from "../ui/dialog.js";
// import { execSave } from "../../components/form/entity.js";

export const controllerFactory = {
    partnerCompany: () => new TableController({
        tableId:"table-01",
        formId:"form-01",
        key:"companyId",
        findUrl: "/api/company/get/id",
        selectUrl: "/api/partner/get/list",
        deleteUrl: "/api/company/delete",
        downloadUrl: "/api/company/download/csv",
        saveUrl: "/api/company/save",
        onSubmit: async function(form){
            await this.save(form);
        }
    }),

    partnerEmployee: () => new TableController({
        tableId:"table-02",
        formId:"form-02",
        key:"employeeId",
        findUrl: "/api/employee/get/id",
        selectUrl: "/api/employee/get/list/partner",
        deleteUrl: "/api/employee/delete",
        downloadUrl: "/api/employee/download/csv",
        saveUrl: "/api/employee/save",
        onSubmit: async function(form){
            await this.save(form);
        },
    })
}