"use strict";

import { createCrudPage } from "../../core/page/createCrudPage.js";
import { OrderRepository } from "../../repositories/business/order/OrderRepository.js";
import { FormController } from "../../application/FormController.js";
// import { convertKey } from "../../util/keyCaseConverter.js";
// import { clearFormExceptSkipped } from "../../core/form/util/clearForm.js";
// import { filterFactory } from "../../util/filterFactory.js";
// import { createMasterPage } from "../../core/page/createMasterPage.js";
import { createOrderItemFormListController } from "./order/orderItemList.js";
import { createOrderWorkFormListController } from "./order/orderWorkList.js";
import { DialogService } from "../../core/ui/dialog/DialogService.js";

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

// const createOrderForm = (controller, options = {}) =>
//     new FormController({
//         controller,
//         formId: options.formId,
//         key: controller.key,
//         afterSave: async (id) => {
//             await controller.refresh(id);
//         },
//         buildParams: (id) => ({
//             state:APP.cache.common.state.INITIAL,
//             orderId: id
//         }),
//         repository: OrderRepository,
//     });

const createOrderForm = (controller, options = {}) => {

    const itemList = createOrderItemFormListController();
    const workList = createOrderWorkFormListController();

    const form = new FormController({
        controller,
        formId: options.formId,
        // key: controller.key,
        key: "orderId",

        repository: OrderRepository,

        changeTargetSelector: "#tab-11",
        validInputSelector: "#tab-11",

        buildParams: (id) => ({
            state: APP.cache.common.state.INITIAL,
            orderId: id
        }),

        // onOpen: async (data) => {
        //     itemList.init(data?.items ?? []);

        //     const formEl = document.getElementById(form.formId);
        //     initOrderItemInput(form, formEl, itemList);
        // },
        onOpen: async (data) => {
            let items = [];
            let works = [];

            if (data?.orderId) {
                items = await OrderRepository.findItems({
                    state: APP.cache.common.state.INITIAL,
                    orderId: data.orderId
                });

                works = await OrderRepository.findWorks({
                    state: APP.cache.common.state.INITIAL,
                    orderId: data.orderId
                });
            }

            itemList.init(items, form);
            workList.init(works, form);

            const formEl = document.getElementById(form.formId);

            initOrderItemInput(form, formEl, itemList);
            initOrderWorkInput(form, formEl, workList);
        },

        buildAdditionalPayload: () => ({
            items: itemList.getItems(),
            works: workList.getItems()
        }),

        hasAdditionalChanges: () => {
            return (
                itemList.hasChanges() ||
                workList.hasChanges()
            );
        },

        resetAdditional: () => {
            itemList.reset();
            workList.reset();
        },

        afterSave: async (id) => {
            await controller.refresh(id);
        }
    });

    form.itemList = itemList;

    return form;
};

function initOrderItemInput(formController, formEl, itemList) {
    if (!formEl) return;

    const addButton = formEl.querySelector("#add-item-btn");
    if (!addButton) return;

    // JANコード入力
    const janInput = formEl.querySelector('[name="jan-code"]');

    if (janInput && janInput.dataset.initialized !== "true") {
        janInput.dataset.initialized = "true";

        janInput.addEventListener("input", () => {
            janInput.value = janInput.value
                .replace(/[０-９]/g, s =>
                    String.fromCharCode(
                        s.charCodeAt(0) - 0xfee0
                    )
                )
                .replace(/[^0-9]/g, "");
        });
    }

    // 登録ボタン
    if (addButton.dataset.initialized === "true") {
        return;
    }

    addButton.dataset.initialized = "true";
    addButton.addEventListener("click", () => {
        const quantity = formEl.querySelector('[name="item-quantity"]')?.value?.trim();
        const item = {
            janCode: formEl.querySelector('[name="jan-code"]')?.value?.trim() ?? "",
            itemName: formEl.querySelector('[name="item-name"]')?.value?.trim() ?? "",
            itemMaker: formEl.querySelector('[name="item-maker"]')?.value?.trim() ?? "",
            itemModel: formEl.querySelector('[name="item-model"]')?.value?.trim() ?? "",
            itemQuantity: quantity ? Number(quantity) : 1
        };

        if (!item.itemModel) {
            DialogService.error("型番を入力してください");
            return;
        }
        // if (!item.itemQuantity || item.itemQuantity <= 0) {
        //     DialogService.error("数量を入力してください");
        //     return;
        // }

        itemList.add(item);
        clearOrderItemInput(formEl);

        // 保存ボタン状態を更新
        formController.setSubmitEnabled(
            formController.canSubmit()
        );
    });
}

function clearOrderItemInput(form) {
    const names = [
        "jan-code",
        "item-name",
        "item-maker",
        "item-model",
        "item-quantity"
    ];
    names.forEach(name => {
        const el = form.querySelector(`[name="${name}"]`);
        if (el) {
            el.value = "";
        }
    });
    form.querySelector('[name="jan-code"]')?.focus();
}

function initOrderWorkInput(formController, formEl, workList) {
    if (!formEl) return;

    const addButton = formEl.querySelector("#add-work-btn");
    if (!addButton) return;

    if (addButton.dataset.initialized === "true") {
        return;
    }

    addButton.dataset.initialized = "true";
    addButton.addEventListener("click", () => {
        const price = formEl.querySelector('[name="order-work-price"]')?.value?.trim();
        const quantity = formEl.querySelector('[name="order-work-quantity"]')?.value?.trim();
        const work = {
            orderWorkCode: formEl.querySelector('[name="order-work-code"]')?.value?.trim() ?? "",
            orderWorkName: formEl.querySelector('[name="order-work-name"]')?.value?.trim() ?? "",
            orderWorkPrice: price ? Number(price) : 0,
            orderWorkQuantity: quantity ? Number(quantity) : 1
        };
        // 作業名 必須
        if (!work.orderWorkName) {
            DialogService.error("作業名を入力してください");
            return;
        }
        // // 単価
        // if (work.orderWorkPrice < 0) {
        //     DialogService.error("単価を入力してください");
        //     return;
        // }
        // // 数量 必須
        // if (!work.orderWorkQuantity || work.orderWorkQuantity <= 0) {
        //     DialogService.error("数量を入力してください");
        //     return;
        // }

        workList.add(work);
        clearOrderWorkInput(formEl);

        // 保存ボタン状態を更新
        formController.setSubmitEnabled(
            formController.canSubmit()
        );
    });
}

function clearOrderWorkInput(form) {
    const names = [
        "order-work-code",
        "order-work-name",
        "order-work-price",
        "order-work-quantity"
    ];

    names.forEach(name => {
        const el = form.querySelector(`[name="${name}"]`);
        if (el) {
            el.value = "";
        }
    });
    form.querySelector('[name="order-work-code"]')?.focus();
}
