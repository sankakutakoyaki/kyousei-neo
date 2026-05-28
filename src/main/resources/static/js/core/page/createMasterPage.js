"use strict"

import { createCrudPage } from "./createCrudPage.js";

export function createMasterPage(config){
    return createCrudPage({
        key: config.key,
        components: config.components,
        tableId: config.tableId,
        footerId: config.footerId,
        formId: config.formId,
        idKey: config.idKey,
        repository: config.repository,
        saveHandler: config.saveHandler,
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
            if(config.category && config.insertCategory){
                const key = form.dataset.key;
                const id = payload[key];
                if(!id || Number(id) === 0){
                    payload.category = config.category;
                }
            }
        },
        afterSave: async (controller, id) => {
            await controller.refresh(id);
            if(config.afterSave){
                await config.afterSave(controller, id);
            }
        },
        onDeleted: config.onDeleted,
        buildDetailParams: (id) => ({
            state: APP.cache.common.state.INITIAL,
            [config.idKey]: id
        })
    });
}