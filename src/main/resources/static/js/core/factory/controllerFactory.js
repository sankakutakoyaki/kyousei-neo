"use strict"

import { TableController } from "../table/TableController.js";
import { closeFormDialog } from "../ui/dialog.js";
import { execSave } from "../../components/form/entity.js";

export const controllerFactory = {
    partnerCompany: () => new TableController({
        tableId:"table-01",
        formId:"form-01",
        parent:"company",
        key:"companyId",
        selectUrl: "/api/partner/get/list",
        deleteUrl: "/api/company/delete",
        downloadUrl: "/api/company/download/csv",
        onSubmit: async function(form, entity){
            closeFormDialog('form-01');
            await execSave(form, {
                parent: "company",
                key: "companyId",
                entity: entity
            });
            await this.refresh();
        }
    }),

    partnerEmployee: () => new TableController({
        tableId:"table-02",
        selectUrl: "/api/employee/get/list/partner",
        deleteUrl: "/api/employee/delete",
        downloadUrl: "/api/employee/download/csv",
        onSubmit: async function(form){
            closeFormDialog('form-02')
            await execSave(form, { parent: "employee", key: "employee_id" });
            await this.refresh();
        },
    })
}