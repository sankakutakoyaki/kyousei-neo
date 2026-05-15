"use strict";

import { createCrudPage } from "../../core/page/createCrudPage.js";
import { StaffRepository } from "../../repositories/corporation/company/StaffRepository.js";

export function createStaffPage(config){
    return createCrudPage({
        key: config.key,
        components: config.components,
        tableId: config.tableId,
        footerId: config.footerId,
        formId: config.formId,
        idKey: "staffId",
        repository: StaffRepository,
        columns: config.columns,
        buildParams: () => ({
            state: APP.cache.common.state.INITIAL,
            category: config.category
        }),
        buildCsvParams: () => ({
            state: APP.cache.common.state.INITIAL
        }),
        model: config.model,
        validateBusiness: config.validateBusiness,
        beforeSave: config.beforeSave,
        afterSave: async (controller, id) => {
            await controller.refresh(id);
            if(config.afterSave){
                await config.afterSave(controller, id);
            }
        },
        buildDetailParams: (id) => ({
            state: APP.cache.common.state.INITIAL,
            staffId: id
        })
    });
}