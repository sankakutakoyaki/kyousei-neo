"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { createPartnerCompanyColumns, createPartnerEmployeeColumns } from "./columns.js";
import { registerController } from "../../../application/controllerRegistry.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { CompanyService } from "../../../services/corporation/company/CompanyService.js";
import { createEmployeePage } from "../../personnel/createEmployeePage.js";
import { getController } from "../../../application/controllerRegistry.js";
import { createMasterPage } from "../../../core/page/createMasterPage.js";

export async function init() {
    await initCommon();
    await initPageCache("/api/partner/init/cache");

    // tab1
    const company = partnerCompanyPage();
    registerController("partnerCompany", company);
    company.init();
    await company.refresh();

    // tab2
    const employee = partnerEmployeePage();
    registerController("partnerEmployee", employee);
    employee.init();
    await employee.refresh();
}

export const partnerCompanyPage = () =>
    createMasterPage({
        key: "partnerCompany",
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        idKey: "companyId",
        repository: CompanyService,
        category: APP.cache.common.companyCategory.PARTNER,
        insertCategory: true,
        columns: createPartnerCompanyColumns(),
        submitText: "保存",
        cancelText: "キャンセル",
        components: {combo: true},
        model: {
            filters: {category: filterFactory.equals("category")}
        },
        // afterSave: refreshClientChildren
        afterSave: async (controller, id) => {
            await controller.refresh(id);
            await refreshClientChildren();
        }
    });

export const partnerEmployeePage = () =>
    createEmployeePage({
        key: "partnerEmployee",
        tableId: "table-02",
        footerId: "footer-02",
        formId: "form-02",
        category: APP.cache.common.employeeCategory.CONSTRUCT,
        columns: createPartnerEmployeeColumns(),
        submitText: "保存",
        cancelText: "キャンセル",
        components: {combo: true, input: true},
        model: {
            filters: {companyId: filterFactory.equals("companyId")}
        },
        validateBusiness: async (payload) => {
            if(!payload.companyId){
                throw {
                    message: "会社を選択してください",
                    fields: ["companyId"]
                };
            }
        }
    });

async function refreshClientChildren(){
    const keys = [
        "partnerEmployee",
    ];
    for(const key of keys){
        const controller = getController(key);
        await controller?.executeAction("companyChanged");
    }
}
