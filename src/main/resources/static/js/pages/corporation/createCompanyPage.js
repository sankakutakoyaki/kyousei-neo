"use strict";

import { createCrudPage } from "../../core/page/createCrudPage.js";

export function createCompanyPage(config){
    return createCrudPage({
        key: config.key,
        components: config.components,
        tableId: config.tableId,
        footerId: config.footerId,
        formId: config.formId,
        idKey: "companyId",
        repository: config.service,
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
        beforeSave: (payload, form) => {
            if(config.beforeSave){
                config.beforeSave(payload, form);
            }
            const key = form.dataset.key;
            const id = payload[key];
            if(!id || Number(id) === 0){
                payload.category = config.category;
            }
        },
        afterSave: async (controller, id) => {
            await controller.refresh(id);
            if(config.afterSave){ 
                await config.afterSave(controller, id);
            }
        },
        buildDetailParams: (id) => ({
            state: APP.cache.common.state.INITIAL,
            companyId: id
        })
    });
}