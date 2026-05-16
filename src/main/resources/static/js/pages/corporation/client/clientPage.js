"use strict"

import { initCommon } from "../../../core/init/initPage.js";
import { createClientCompanyColumns, createOfficeColumns, createStaffColumns } from "./columns.js";
import { registerController } from "../../../controllers/controllers.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { initPageCache } from "../../../core/init/initPageCache.js";
import { initParentChildLink } from "../../../util/link.js";
import { CompanyService } from "../../../services/corporation/company/CompanyService.js";
import { createCompanyPage } from "../createCompanyPage.js";
import { createOfficePage } from "../createOfficePage.js";
import { createStaffPage } from "../createStaffPage.js";
import { getController } from "../../../controllers/controllers.js";
import { ClientCompanyService } from "../../../services/corporation/company/ClientCompanyService.js";

export async function init() {
    await initCommon();
    await initPageCache("/api/client/init/cache");

    // tab1
    const company = clientCompanyPage();
    registerController("clientCompany", company);
    company.init();
    await company.refresh();

    // tab2
    const office = clientOfficePage();
    registerController("clientOffice", office);
    office.init();
    await office.refresh();

    // tab3
    const staff = clientStaffPage();
    registerController("clientStaff", staff);
    staff.init();
    await staff.refresh();

    initParentChildLink();
}

export const clientCompanyPage = () =>
    createCompanyPage({
        key: "clientCompany",
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        service: ClientCompanyService,
        category: APP.cache.common.companyCategory.PARTNER,
        columns: createClientCompanyColumns(),
        components: {combo: true},
        model: {
            filters: {
                category: filterFactory.equals("category")
            }
        },
        validateBusiness:
            async (payload) => {
                if (!payload.category) {
                    throw {
                        message: "分類を選択してください",
                        field: "category"
                    };
                }
            },
        afterSave: async () => {
            const office = getController("clientOffice");
            const staff =  getController("clientStaff");
            await office?.executeAction("companyChanged");
            await staff?.executeAction("companyChanged");
        }
    });

export const clientOfficePage = () =>
    createOfficePage({
        key: "clientOffice",
        tableId: "table-02",
        footerId: "footer-02",
        formId: "form-02",
        category: APP.cache.common.companyCategory.PARTNER,
        columns: createOfficeColumns(),
        components: {combo: true, input: true},
        model: {
            filters: {
                companyId: filterFactory.equals("companyId")
            }
        },
        afterSave: async () => {
            const office = getController("clientOffice");
            const staff = getController("clientStaff");
            await office?.executeAction("officeChanged");
            await staff?.executeAction("officeChanged");
        },
        onDeleted: async () => {
            const office = getController("clientOffice");
            const staff = getController("clientStaff");
            await office?.executeAction("officeChanged");
            await staff?.executeAction("officeChanged");
        }
    });

export const clientStaffPage = () =>
    createStaffPage({
        key: "clientStaff",
        tableId: "table-03",
        footerId: "footer-03",
        formId: "form-03",
        category: APP.cache.common.companyCategory.PARTNER,
        columns: createStaffColumns(),
        components: {combo: true, input: true},
        model: {
            filters: {
                companyId: filterFactory.equals("companyId"),
                officeId: filterFactory.equals("officeId")
            }
        }
    });