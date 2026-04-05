"use strict"

import { initCommon } from "../../../core/init/initPage.js";
import { PageController } from "../../../controllers/PageController.js";
import { DataTable } from "../../../core/table/DataTable.js";
import { createPartnerCompanyColumns, createPartnerEmployeeColumns } from "./columns.js";
import { FormController } from "../../../controllers/FormController.js";
import { registerController } from "../../../controllers/controllers.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { select } from "../../../core/api/sqlApi.js";
import { execute } from "../../../core/api/sqlApi.js";

window.addEventListener("load", () => {

    initCommon();  

    // tab1
    const company = partnerCompanyPage();
    registerController("partnerCompany", company); 
    company.name = "partnerCompany";   
    company.init({
        columns: createPartnerCompanyColumns(company),
        data: APP.cache.companyOrigin,
        components: {
            combo: true
        },
    });

    //　tab2
    const employee = partnerEmployeePage();
    registerController("partnerEmployee", employee);
    employee.name = "partnerEmployee";
    employee.init({
        columns: createPartnerEmployeeColumns(employee),
        data: APP.cache.employeeOrigin,
        components: {
            combo: true
        }
    });
});

export const partnerCompanyPage = () => {

    const controller = new PageController({
        key:"companyId",

        table: {
            create: (controller, columns) => new DataTable({
                controller: controller,
                tableId: "table-01",
                footerId: "footer-01",
                columns,
                idKey: controller.key,
                checkable: true,
                buildParams: () => ({
                    // companyId: document.getElementById("companyId").value,
                    state: APP.cache.state.INITIAL,
                    category: APP.cache.clientCategory.PARTNER                    
                }),
                api: {
                    select: (params) => select("partnerCompanyList", params),
                    delete: "/api/company/delete",
                    download: "/api/company/download/csv"
                },
                onDoubleClick: (item) => controller.openEdit(item[controller.key])
            })
        },
        form: {
            create: (controller) => new FormController({
                controller: controller,
                formId: "form-01",
                key: controller.key,
                onSaved: async (id) => {
                    await controller.dataTable.refresh();
                    controller.scrollToRow(id);
                },
                buildParams: (id) => ({
                    state: APP.cache.state.INITIAL,
                    companyId: id
                }),
                api: {
                    // find: "/api/company/get/id",
                    find: (params) => select("partnerCompanyDetail", params),
                    save: "/api/company/partner/save"
                }
            })
        }
    });
    return controller;
};

export const partnerEmployeePage = () => {

    const controller = new PageController({
        key:"employeeId",

        table: {
            create: (controller, columns) => new DataTable({
                controller: controller,
                tableId: "table-02",
                footerId: "footer-02",
                columns,
                idKey: controller.key,
                checkable: true,
                buildParams: () => ({
                    companyState: APP.cache.state.INITIAL,
                    officeState: APP.cache.state.INITIAL,
                    employeeState: APP.cache.state.INITIAL,
                    category: APP.cache.employeeCategory.CONSTRUCT,
                }),
                api: {
                    select: (params) => select("partnerEmployeeList", params),
                    delete: "/api/employee/delete",
                    download: "/api/employee/download/csv"
                },
                model: {
                    filters: {
                        companyId: filterFactory.equals("companyId")
                    }
                },
                onDoubleClick: (item) => controller.openEdit(item[controller.key]),
            })
        },
        form: {
            create: (controller) => new FormController({
                formId: "form-02",
                key: controller.key,
                controller: controller, 
                onSaved: async (id) => {
                    await controller.dataTable.refresh();
                    controller.scrollToRow(id);
                },
                buildParams: (id) => ({
                    companyState: APP.cache.state.INITIAL,
                    officeState: APP.cache.state.INITIAL,
                    employeeState: APP.cache.state.INITIAL,
                    employeeId: id                   
                }),
                api: {
                    // find: "/api/employee/get/id",
                    find: (params) => select("partnerEmployeeDetail", params),
                    save: "/api/employee/construct/save"
                }
            })
        }
    });
    return controller;
};