"use strict";

import { isMobileReadOnly } from "../../core/access/mobileReadOnly.js";

import { createCrudPage } from "../../core/page/createCrudPage.js";
import { OrderRepository } from "../../repositories/business/order/OrderRepository.js";
import { FormController } from "../../application/FormController.js";
import { createOrderItemFormListController } from "./order/orderItemList.js";
import { createOrderWorkFormListController } from "./order/orderWorkList.js";
import { DialogService } from "../../core/ui/dialog/DialogService.js";
import { AttachmentManager } from "../../components/attachment/AttachmentManager.js";

export function createOrderPage(config){
    const defaultForms = {
        detail: {
            create: (controller) => createOrderForm(controller, {
                formId: config.formId,
                afterSave: config.afterSave,
                submitText: config.submitText,
                cancelText: config.cancelText
            })
        }
    };
    return createCrudPage({
        key: config.key,
        defaultFormName: config.defaultFormName,
        components: config.components,
        onInit: config.onInit,
        conditions: config.conditions,
        tableId: config.tableId,
        footerId: config.footerId,
        formId: config.formId,
        idKey: "orderId",
        repository: OrderRepository,
        columns: config.columns,
        submitText: config.submitText,
        cancelText: config.cancelText,
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
        onDeleted: config.onDeleted,
        actions: {
            ...config.actions,
            "add-order-item": async (controller) => {
                const form = controller.getActiveForm();
                if(!form) return;
                const formEl = document.getElementById(form.formId);
                if(!formEl || !form.itemList) return;
                addOrderItem(form, formEl, form.itemList);
            },
            "add-order-work": async (controller) => {
                const form = controller.getActiveForm();
                if(!form) return;
                const formEl = document.getElementById(form.formId);
                if(!formEl || !form.workList) return;
                addOrderWork(form, formEl, form.workList);
            }
        },
    });
}

const createOrderForm = (controller, options = {}) => {
    const itemList = createOrderItemFormListController();
    const workList = createOrderWorkFormListController();
    let attachmentManager;

    const form = new FormController({
        controller,
        formId: options.formId,
        key: "orderId",
        idKey: "orderId",
        trackInitialState: true,
        submitText: options.submitText,
        cancelText: options.cancelText,
        repository: OrderRepository,
        inheritFilters: data => !data?.ocrLogId,
        changeTargetSelector: "#tab-11",
        validInputSelector: "#tab-11",
        buildParams: (id) => ({
            state: APP.cache.common.state.INITIAL,
            orderId: id
        }),
        onOpen: async (data) => {
            let items = data?.items ?? [];
            let works = data?.works ?? [];
            if (data?.orderId) {
                items = await OrderRepository.findItems({state: APP.cache.common.state.INITIAL, orderId: data.orderId});
                works = await OrderRepository.findWorks({state: APP.cache.common.state.INITIAL, orderId: data.orderId});
            }
            itemList.init(items, form);
            workList.init(works, form);
            const formEl = document.getElementById(form.formId);

            const title = formEl.querySelector(".dialog-header > span");
            if (title) title.textContent = isMobileReadOnly(controller.key) ? "受注詳細（閲覧）" : "受注編集";
            const source = formEl.querySelector("[data-ocr-source]");
            if (source) {
                source.hidden = !data?.ocrLogId;
                source.querySelector("a").href = data?.orderImportId ? `/api/order/import/${encodeURIComponent(data.orderImportId)}/file` : "#";
                source.querySelector("span").textContent = data?.ocrDateWarning || "原本と照合して保存してください。";
            }
            const postal = formEl.querySelector('[name="postal-code"]');
            if (postal) postal.dataset.lastId = postal.value.trim();

            attachmentManager ??= new AttachmentManager(formEl?.querySelector("[data-attachment-manager]"));
            await attachmentManager.open(data?.orderId);
        },
        buildAdditionalPayload: () => ({
            ...(form.currentEntity?.ocrLogId ? {ocrLogId: form.currentEntity.ocrLogId} : {}),
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
        onClose: () => form.finishOcrReview?.(),
        afterSave: async (id) => {
            form.finishOcrReview?.(id);
            await controller.refresh(id);
        }
    });
    form.itemList = itemList;
    form.workList = workList;
    return form;
};

function getValue(form, name) {
    return form.querySelector(`[name="${name}"]`)?.value?.trim() ?? "";
}

function clearInputs(form, names, focusName) {
    names.forEach(name => {
        const el = form.querySelector(`[name="${name}"]`);
        if(el){el.value = "";}
    });
    form.querySelector(`[name="${focusName}"]`)?.focus();
}

function clearOrderItemInput(form) {
    clearInputs(form, ["jan-code", "item-name", "item-maker", "item-model", "item-quantity"], "jan-code");
}

function clearOrderWorkInput(form) {
    clearInputs(form, ["order-work-code", "order-work-name", "order-work-price", "order-work-quantity"], "order-work-code");
}

function addOrderItem(formController, formEl, itemList) {
    const quantity = getValue(formEl, "item-quantity");
    const item = {
        janCode: getValue(formEl, "jan-code"),
        itemMaker: getValue(formEl, "item-maker"),
        itemName: getValue(formEl, "item-name"),
        itemModel: getValue(formEl, "item-model"),
        itemQuantity: quantity ? Number(quantity) : 1
    };
    if(!item.itemName){
        DialogService.error("商品名を入力してください");
        return;
    }
    itemList.add(item);
    clearOrderItemInput(formEl);
    formController.updateSubmitState();
}

function addOrderWork(formController, formEl, workList) {
    const price = getValue(formEl, "order-work-price");
    const quantity = getValue(formEl, "order-work-quantity");
    const work = {
        orderWorkCode: getValue(formEl, "order-work-code"),
        orderWorkName: getValue(formEl, "order-work-name"),
        orderWorkPrice: price ? Number(price) : 0,
        orderWorkQuantity: quantity ? Number(quantity) : 1
    };
    if(!work.orderWorkName){
        DialogService.error("作業名を入力してください");
        return;
    }
    workList.add(work);
    clearOrderWorkInput(formEl);
    formController.updateSubmitState();
}