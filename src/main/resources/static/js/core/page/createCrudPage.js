"use strict";

import { PageController } from "../../applcation/PageController.js";
import { FormController } from "../../applcation/FormController.js";
import { DataTable } from "../table/DataTable.js";

export function createCrudPage(config){
    const defaultForms = {
        detail: {
            create: (controller) =>  new FormController({
                controller,

                formId: config.formId,
                key: config.key,

                repository: config.repository,
                beforeSave: config.beforeSave,
                afterSave: async (id) => {
                    if(config.afterSave){await config.afterSave(controller, id);}
                },
                validateBusiness: config.validateBusiness,
                buildParams: config.buildDetailParams
            })
        }
    };
    return new PageController({
        key: config.key,
        defaultFormName: config.defaultFormName,
        components: config.components,
        
        onInit: config.onInit,
        onDeleted: config.onDeleted,

        actions: config.actions,
        conditions: config.conditions,

        table: {
            create: (controller) => new DataTable({
                controller,
                tableId: config.tableId,
                footerId: config.footerId,
                columns: config.columns,
                rowClass: config.rowClass,
                idKey: config.idKey,
                checkable: config.checkable ?? true,
                repository: config.repository,
                buildParams: config.buildParams,
                buildCsvParams: config.buildCsvParams,
                model: config.model,
                canSave: config.canSave,
                onRowClick: config.onRowClick,
                onDoubleClick: config.onDoubleClick ?? ((item) =>
                    controller.openForm("detail", item[config.idKey], { bulkMode:false })
                )
            })
        },

        forms: config.forms ?? defaultForms 
    });
}