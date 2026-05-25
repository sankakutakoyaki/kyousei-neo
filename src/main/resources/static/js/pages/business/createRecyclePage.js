"use strict";

import { createCrudPage } from "../../core/page/createCrudPage.js";
import { RecycleRepository } from "../../repositories/business/recycle/RecycleRepository.js";
import { FormController } from "../../applcation/FormController.js";
import { convertKey } from "../../util/keyCaseConverter.js";
import { clearFormExceptSkipped } from "../../core/form/util/clearForm.js";

export function createRecyclePage(config){
    const defaultForms = {
        detail: {
            create: (controller) => createRecycleForm(controller, { formId: config.formId, afterSave: config.afterSave })
        },
        inlineUse: {
            create: (controller) => createRecycleUseForm(controller, { formId: config.formId, afterSave: config.afterSave })
        },
        inlineDelivery: {
            create: (controller) => createRecycleDeliveryForm(controller, { formId: config.formId, afterSave: config.afterSave })
        },
        inlineShipping: {
            create: (controller) => createRecycleShippingForm(controller, { formId: config.formId, afterSave: config.afterSave })
        },
        inlineLoss: {
            create: (controller) => createRecycleLossForm(controller, { formId: config.formId, afterSave: config.afterSave })
        },
        ...(config.bulkFormId && {
            bulk: {
                create: (controller) => createRecycleForm(controller, { formId: config.bulkFormId })
            }
        })
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
        idKey: "recycleId",
        repository: RecycleRepository,
        columns: config.columns,
        rowClass: config.rowClass,
        buildParams: config.buildParams,
        buildCsvParams: config.buildCsvParams,
        model: config.model,
        canSave: config.canSave,
        checkable: config.checkable,
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

// tab(2,3,4,5)フォーム共通処理
const createInlineRecycleForm = ({
    controller,
    formId,
    saveHandler,
    recycleInputId,
    requiredFields,
    afterSave
}) => new FormController({
        controller,
        formId,
        key: controller.key,
        saveHandler,
        closeOnSave: false,
        showSuccessDialog: false,
        afterSave: async (...args) => {
            await controller.refresh();
            const form = document.getElementById(formId);
            clearFormExceptSkipped(form);
            requestAnimationFrame(() => {
                const el = document.getElementById(recycleInputId);
                if(el){
                    el.value = null;
                    el.focus();
                }
            });
            if(afterSave){
                await afterSave(...args);
            }
        },
        validateBusiness: async () => {
            const errors = [];
            for(const field of requiredFields){
                const el = document.getElementById(field.id);
                const invalid =
                    !el ||
                    el.value == null ||
                    el.value.trim() === "";
                if(invalid){
                    errors.push(field);
                }
            }
            if(errors.length > 0){
                throw {
                    message: errors.map(x => x.label).join("、") + "を入力してください",
                    fields:errors.map(x => x.id)
                };
            }
        }
    });

const createRecycleUseForm =
    (controller, options = {}) =>
        createInlineRecycleForm({
            controller,
            formId: "header-02",
            saveHandler: RecycleRepository.save,
            recycleInputId: "recycle-number02",
            requiredFields: [
                {
                    id: "recycle-number02",
                    label: "お問合せ管理票番号"
                },
                {
                    id: "use-date02",
                    label: "使用日"
                },
                {
                    id: "item-code02",
                    label: "品目"
                },
                {
                    id: "maker-code02",
                    label: "製造業者等名"
                }
            ],
            afterSave: options.afterSave
        });

const createRecycleDeliveryForm =
    (controller, options = {}) =>
        createInlineRecycleForm({
            controller, 
            formId: "header-03",
            saveHandler: RecycleRepository.saveDelivery,
            recycleInputId: "recycle-number03",
            afterSave: options.afterSave,
            requiredFields: [
                {
                    id: "recycle-number03",
                    label: "お問合せ管理票番号"
                },
                {
                    id: "delivery-date02",
                    label: "引渡日"
                }
            ]
        });

const createRecycleShippingForm =
    (controller, options = {}) =>
        createInlineRecycleForm({
            controller,
            formId: "header-04",
            saveHandler: RecycleRepository.saveShipping,
            recycleInputId: "recycle-number04",
            afterSave: options.afterSave,
            requiredFields: [
                {
                    id: "recycle-number04",
                    label: "お問合せ管理票番号"
                },
                {
                    id: "shipping-date02",
                    label: "発送日"
                }
            ]
        });

const createRecycleLossForm =
    (controller, options = {}) =>
        createInlineRecycleForm({
            controller,
            formId: "header-05",
            saveHandler: RecycleRepository.saveLoss,
            recycleInputId: "recycle-number05",
            afterSave: options.afterSave,
            requiredFields: [
                {
                    id: "recycle-number05",
                    label: "お問合せ管理票番号"
                },
                {
                    id: "loss-date02",
                    label: "ロス処理日"
                }
            ]
        });


function validatePersisted(payload, origin, field, label){
    // 未変更
    if(!Object.hasOwn(payload, field)) return;
    // 空更新は許可
    if(payload[field] == null || payload[field] === "") return;
    // 元データなし
    if(!origin?.[field]){
        throw {
            message: `${label}が未登録のため変更できません`,
            fields: [convertKey(field, "camel", "kebab")]
        };
    }
}

// const createRecycleUseForm = (controller, options = {}) =>
//     new FormController({
//         controller,
//         formId: "header-02",
//         key: controller.key,
//         saveHandler: RecycleRepository.save,
//         closeOnSave: false,
//         showSuccessDialog: false,
//         afterSave: async (...args) => {
//             await controller.refresh();
//             const form = document.getElementById("header-02");
//             clearFormExceptSkipped(form);
//             requestAnimationFrame(() => {
//                 const el = document.getElementById("recycle-number02");
//                 if (el) {el.value = null; el.focus();}
//             });
//             if(options.afterSave){
//                 await options.afterSave(...args);
//             }
//         },
//         validateBusiness: async () => {
//             // const requiredFields = [
//             //     { id: "recycle-number02", label: "お問合せ管理票番号" },
//             //     { id: "use-date02", label: "使用日" },
//             //     { id: "item-code02", label: "品目" },
//             //     { id: "maker-code02", label: "製造業者等名" }
//             // ];
//             const errors = [];
//             for (const field of requiredFields) {
//                 const el = document.getElementById(field.id);
//                 const invalid = !el || el.value == null || el.value.trim() === "";
//                 if (invalid)  errors.push(field);
//             }
//             // if (errors.length > 0) {
//             //     throw {
//             //         message: errors.map(x => x.label).join("、") + "を入力してください",
//             //         fields:  errors.map(x => x.id)
//             //     };
//             // }
//         }
//     });
// const createRecycleDeliveryForm = (controller, options = {}) =>
//     new FormController({
//         controller,
//         formId: "header-03",
//         key: controller.key,
//         saveHandler: RecycleRepository.saveDelivery,
//         closeOnSave: false,
//         showSuccessDialog: false,
//         afterSave: async (...args) => {
//             await controller.refresh();
//             const form = document.getElementById("header-03");
//             clearFormExceptSkipped(form);
//             requestAnimationFrame(() => {
//                 const el = document.getElementById("recycle-number03");
//                 if (el) {el.value = null; el.focus();}
//             });
//             if(options.afterSave){
//                 await options.afterSave(...args);
//             }
//         },
//         validateBusiness: async () => {
//             const requiredFields = [
//                 { id: "recycle-number03", label: "お問合せ管理票番号" },
//                 { id: "delivery-date02", label: "引渡日" }
//             ];
//             const errors = [];
//             for (const field of requiredFields) {
//                 const el = document.getElementById(field.id);
//                 const invalid = !el || el.value == null || el.value.trim() === "";
//                 if (invalid)  errors.push(field);
//             }
//             if (errors.length > 0) {
//                 throw {
//                     message: errors.map(x => x.label).join("、") + "を入力してください",
//                     fields:  errors.map(x => x.id)
//                 };
//             }
//         }
//     });
// const createRecycleShippingForm = (controller, options = {}) =>
//     new FormController({
//         controller,
//         formId: "header-04",
//         key: controller.key,
//         saveHandler: RecycleRepository.saveShipping,
//         closeOnSave: false,
//         showSuccessDialog: false,
//         afterSave: async (...args) => {
//             await controller.refresh();
//             const form = document.getElementById("header-04");
//             clearFormExceptSkipped(form);
//             requestAnimationFrame(() => {
//                 const el = document.getElementById("recycle-number04");
//                 if (el) {el.value = null; el.focus();}
//             });
//             if(options.afterSave){
//                 await options.afterSave(...args);
//             }
//         },
//         validateBusiness: async () => {
//             const requiredFields = [
//                 { id: "recycle-number04", label: "お問合せ管理票番号" },
//                 { id: "shipping-date02", label: "発送日" }
//             ];
//             const errors = [];
//             for (const field of requiredFields) {
//                 const el = document.getElementById(field.id);
//                 const invalid = !el || el.value == null || el.value.trim() === "";
//                 if (invalid)  errors.push(field);
//             }
//             if (errors.length > 0) {
//                 throw {
//                     message: errors.map(x => x.label).join("、") + "を入力してください",
//                     fields:  errors.map(x => x.id)
//                 };
//             }
//         }
//     });

// const createRecycleLossForm = (controller, options = {}) =>
//     new FormController({
//         controller,
//         formId: "header-05",
//         key: controller.key,
//         saveHandler: RecycleRepository.saveLoss,
//         closeOnSave: false,
//         showSuccessDialog: false,
//         afterSave: async (...args) => {
//             await controller.refresh();
//             const form = document.getElementById("header-05");
//             clearFormExceptSkipped(form);
//             requestAnimationFrame(() => {
//                 const el = document.getElementById("recycle-number05");
//                 if (el) {el.value = null; el.focus();}
//             });
//             if(options.afterSave){
//                 await options.afterSave(...args);
//             }
//         },
//         validateBusiness: async () => {
//             const requiredFields = [
//                 { id: "recycle-number05", label: "お問合せ管理票番号" },
//                 { id: "loss-date02", label: "ロス処理日" }
//             ];
//             const errors = [];
//             for (const field of requiredFields) {
//                 const el = document.getElementById(field.id);
//                 const invalid = !el || el.value == null || el.value.trim() === "";
//                 if (invalid)  errors.push(field);
//             }
//             if (errors.length > 0) {
//                 throw {
//                     message: errors.map(x => x.label).join("、") + "を入力してください",
//                     fields:  errors.map(x => x.id)
//                 };
//             }
//         }
//     });
