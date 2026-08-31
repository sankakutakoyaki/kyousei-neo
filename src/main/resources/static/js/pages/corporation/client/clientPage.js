"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { createClientCompanyColumns, createOfficeColumns, createStaffColumns } from "./columns.js";
import { registerController } from "../../../application/controllerRegistry.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { initParentChildLink } from "../../../util/link.js";
import { CompanyService } from "../../../services/corporation/company/CompanyService.js";
import { OfficeRepository } from "../../../repositories/corporation/company/OfficeRepository.js";
import { StaffRepository } from "../../../repositories/corporation/company/StaffRepository.js";
import { getController } from "../../../application/controllerRegistry.js";
import { ClientCompanyService } from "../../../services/corporation/company/ClientCompanyService.js";
import { createMasterPage } from "../../../core/page/createMasterPage.js";

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
    createMasterPage({
        key: "clientCompany",
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        idKey: "companyId",
        repository: ClientCompanyService,
        category: APP.cache.common.companyCategory.PARTNER,
        columns: createClientCompanyColumns(),
        submitText: "保存",
        cancelText: "キャンセル",
        components: {combo: true},
        model: {
            filters: {category: filterFactory.equals("category")}
        },
        validateBusiness: async (payload) => {
            if(!payload.category){
                throw {
                    message: "分類を選択してください",
                    fields: ["category"]
                };
            }
        },
        afterSave: refreshClientChildren
    });

export const clientOfficePage = () =>
    createMasterPage({
        key: "clientOffice",
        tableId: "table-02",
        footerId: "footer-02",
        formId: "form-02",
        idKey: "officeId",
        repository: OfficeRepository,
        category: APP.cache.common.companyCategory.PARTNER,
        columns: createOfficeColumns(),
        submitText: "保存",
        cancelText: "キャンセル",
        components: {combo: true, input: true},
        model: {
            filters: {companyId: filterFactory.equals("companyId")}
        },
        afterSave: refreshClientChildren
    });

export const clientStaffPage = () =>
    createMasterPage({
        key: "clientStaff",
        tableId: "table-03",
        footerId: "footer-03",
        formId: "form-03",
        idKey: "staffId",
        repository: StaffRepository,
        category: APP.cache.common.companyCategory.PARTNER,
        columns: createStaffColumns(),
        submitText: "保存",
        cancelText: "キャンセル",
        components: {combo: true, input: true},
        model: {
            filters: {
                companyId: filterFactory.equals("companyId"),
                officeId: filterFactory.equals("officeId")
            }
        },
        validateBusiness: async (payload) => {
            if(
                payload.companyId !== undefined &&
                Number(payload.companyId) === 0
            ){
                throw {
                    message: "会社を選択してください",
                    fields: ["company-id"]
                };
            }
        },
    });

async function refreshClientChildren(){
    const keys = [
        "clientOffice",
        "clientStaff"
    ];
    for(const key of keys){
        const controller = getController(key);
        await controller?.refresh();
    }
}
