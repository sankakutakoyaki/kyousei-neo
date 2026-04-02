"use strict"

import { initCommon } from "../../../core/init/initPage.js";
import { PageController } from "../../../controllers/PageController.js";
import { DataTable } from "../../../core/table/DataTable.js";
import { createPartnerCompanyColumns } from "./columns.js";
import { FormController } from "../../../controllers/FormController.js";

window.addEventListener("load", () => {

    initCommon();
    const page = partnerPage();
    page.init({
        columns: createPartnerCompanyColumns(page)
    });
    page.refresh();

});

export const partnerPage = () => {

    const controller = new PageController({
        // tableId:"table-01",
        // formId:"form-01",
        key:"companyId",

        // findUrl: "/api/company/get/id",
        // selectUrl: "/api/partner/get/list",
        // deleteUrl: "/api/company/delete",
        // downloadUrl: "/api/company/download/csv",
        // saveUrl: "/api/company/save",

        table: {
            create: (controller, columns) => new DataTable({
                tableId: "table-01",
                columns,
                idKey: controller.key,
                checkable: true,
                api: {
                    select: "/api/partner/get/list",
                    delete: "/api/company/delete",
                    download: "/api/company/download/csv"
                },
                model: {
                    // requiredFilters: ["companyId"],
                    pageSize: 100
                },
                onDoubleClick: (item) => controller.openEdit(item[controller.key])
            })
        },
        form: {
            create: (controller) => new FormController({
                formId: "form-01",
                key: controller.key,
                api: {
                    find: "/api/company/get/id",
                    save: "/api/company/save"
                }
                // findUrl: controller.findUrl,
                // saveUrl: controller.saveUrl
            })
        }
    });
    return controller;
};