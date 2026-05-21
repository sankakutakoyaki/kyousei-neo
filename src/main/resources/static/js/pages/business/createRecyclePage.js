"use strict";

import { createCrudPage } from "../../core/page/createCrudPage.js";
import { RecycleRepository } from "../../repositories/business/recycle/RecycleRepository.js";
import { FormController } from "../../applcation/FormController.js";
import { convertKey } from "../../util/keyCaseConverter.js";

export function createRecyclePage(config){
    // const defaultForms = {
    //     detail: {
    //         create: (controller) => createRecycleForm(controller, {formId: config.formId})
    //     },
    //     ...(config.bulkFormId && {
    //         bulk: {
    //             create: (controller) => createRecycleForm(controller, {formId: config.bulkFormId})}
    //         }
    //     )
    // };
    const defaultForms = {
        detail: {
            create: (controller) => createRecycleForm(controller, { formId: config.formId })
        },
        inlineUse: {
            create: (controller) => createRecycleUseForm(controller, { formId: config.formId })
        },
        inlineDelivery: {
            create: (controller) => createRecycleDeliveryForm(controller, { formId: config.formId })
        },
        inlineShipping: {
            create: (controller) => createRecycleShippingForm(controller, { formId: config.formId })
        },
        inlineLoss: {
            create: (controller) => createRecycleLossForm(controller, { formId: config.formId })
        },
        ...(config.bulkFormId && {
            bulk: {
                create: (controller) => createRecycleForm(controller, { formId: config.bulkFormId })
            }
        })
    };
    return createCrudPage({
        key: config.key,
        components: config.components,
        onInit: config.onInit,
        actions: config.actions,
        conditions: config.conditions,
        tableId: config.tableId,
        footerId: config.footerId,
        formId: config.formId,
        idKey: "recycleId",
        repository: RecycleRepository,
        columns: config.columns,
        buildParams: config.buildParams,
        buildCsvParams: config.buildCsvParams,
        model: config.model,
        canSave: config.canSave,
        // forms: config.forms ?? defaultForms,
        forms: config.forms ?? {
            [config.defaultFormName ?? "detail"]:
                defaultForms[config.defaultFormName ?? "detail"],
            ...(defaultForms.bulk && { bulk: defaultForms.bulk })
        },
        onDeleted: config.onDeleted
    });
}

// tab1フォーム共通処理
const createRecycleForm = (controller, options = {}) =>
    new FormController({
        controller,
        formId: options.formId,
        key: controller.key,
        afterSave: async (id) => {
            await controller.refresh(id);
        },
        buildParams: (id) => ({
            state:APP.cache.common.state.INITIAL,
            recycleId: id
        }),
        validateBusiness: async (payload) => {
            const table =  controller.dataTable;
            const ids = controller.isBulkMode() ? table.getSelectedIds(): [payload.recycleId];

            for(const id of ids){
                const origin = table.findOriginById(id);
                validatePersisted(payload, origin, "useDate", "使用日");
                validatePersisted(payload, origin, "deliveryDate", "引渡日");
                validatePersisted(payload, origin, "shippingDate", "発送日");
            }
        },
        repository: RecycleRepository,
    });

const createRecycleUseForm = (controller) =>
    new FormController({
        controller,
        formId: "header-02",
        key: controller.key,
        repository: RecycleRepository,
        afterSave: async () => {
            await controller.refresh();
        },
        validateBusiness: async (payload) => {
            const date = document.getElementById("use-date02");
            if (!date || date.value == null) {
                throw {
                    message: '使用日を指定してください',
                };
            }
        }
    });

function validatePersisted(
    payload,
    origin,
    field,
    label
){
    // 未変更
    if(!Object.hasOwn(payload, field)){
        return;
    }
    // 空更新は許可
    if(payload[field] == null || payload[field] === ""){
        return;
    }
    // 元データなし
    if(!origin?.[field]){
        throw {
            message:
                `${label}が未登録のため変更できません`,
            field:
                convertKey(field, "camel", "kebab")
        };
    }
}