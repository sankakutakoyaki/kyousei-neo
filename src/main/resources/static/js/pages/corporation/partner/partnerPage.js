"use strict"

import { initCommon } from "../../../core/init/initPage.js";
import { createPartnerCompanyColumns, createPartnerEmployeeColumns } from "./columns.js";
import { registerController } from "../../../controllers/controllers.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { initPageCache } from "../../../core/init/initPageCache.js";
import { CompanyService } from "../../../services/corporation/company/CompanyService.js";
import { createCompanyPage } from "../createCompanyPage.js";
import { createEmployeePage } from "../../personnel/createEmployeePage.js";
import { getController } from "../../../controllers/controllers.js";

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
    createCompanyPage({
        key: "partnerCompany",
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        service: CompanyService,
        category: APP.cache.common.companyCategory.PARTNER,
        columns: createPartnerCompanyColumns(),
        afterSave: async () => {
            const employee = getController("partnerEmployee");
            await employee?.executeAction("companyChanged");
        }
    });

export const partnerEmployeePage = () =>
    createEmployeePage({
        key: "partnerEmployee",
        components: {combo: true, input: true},
        tableId: "table-02",
        footerId: "footer-02",
        formId: "form-02",
        category: APP.cache.common.employeeCategory.CONSTRUCT,
        columns: createPartnerEmployeeColumns(),
        model: {
            filters: {
                companyId: filterFactory.equals("companyId")
            }
        },
        validateBusiness: async (payload) => {
            if (!payload.companyId) {
                throw {
                    message: "会社を選択してください",
                    field: "companyId"
                };
            }
        }
    });