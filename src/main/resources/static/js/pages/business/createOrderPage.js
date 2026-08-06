"use strict";

import { createCrudPage } from "../../core/page/createCrudPage.js";
import { OrderRepository } from "../../repositories/business/order/OrderRepository.js";
import { FormController } from "../../applcation/FormController.js";
import { convertKey } from "../../util/keyCaseConverter.js";
import { clearFormExceptSkipped } from "../../core/form/util/clearForm.js";
import { filterFactory } from "../../util/filterFactory.js";
import { createMasterPage } from "../../core/page/createMasterPage.js";

export function createOrderPage(config){
    const defaultForms = {
        detail: {
            create: (controller) => createOrderForm(controller, { formId: config.formId, afterSave: config.afterSave })
        }
    };
    return createCrudPage({
        key: config.key,
        defaultFormName: config.defaultFormName,
        components: config.components,
        onInit: config.onInit,
        actions: config.actions,
        conditions: config.conditions,
        tableId: config.tableId,
        footerId: config.footerId,
        formId: config.formId,
        idKey: "orderId",
        repository: OrderRepository,
        columns: config.columns,
        rowClass: config.rowClass,
        buildParams: config.buildParams,
        buildCsvParams: config.buildCsvParams,
        model: config.model,
        canSave: config.canSave,
        checkable: config.checkable,
        forms: config.forms ?? {
            [config.defaultFormName ?? "detail"]:
                defaultForms[config.defaultFormName ?? "detail"]
        },
        onDeleted: config.onDeleted
    });
}

const createOrderForm = (controller, options = {}) =>
    new FormController({
        controller,
        formId: options.formId,
        key: controller.key,
        afterSave: async (id) => {
            await controller.refresh(id);
        },
        buildParams: (id) => ({
            state:APP.cache.common.state.INITIAL,
            orderId: id
        }),
        repository: OrderRepository,
    });