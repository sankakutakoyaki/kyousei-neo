"use strict"

import { FormModel } from "../core/form/FormModel.js";
import { convertKey } from "../util/keyCaseConverter.js";
import { openFormDialog } from "../core/ui/dialog/dialogCore.js";
import { DialogService } from "../core/ui/dialog/DialogService.js";
import { SaveBehavior } from "../core/save/SaveBehavior.js";
import { FormPayloadBuilder } from "../core/form/FormPayloadBuilder.js";
import { UiValidator } from "../core/validate/UiValidator.js";
import { FormStateBehavior } from "../core/form/FormStateBehavior.js";
import { initParentChildLink } from "../util/link.js";

export class FormController {
    constructor(config){
        this.config = Object.freeze(config);
        const {
            formId,
            key,
            repository = null,
            saveHandler = null,
            beforeSave = null,
            afterSave = null,
            controller = {},
            buildParams = null,
            validateBusiness = null,
            closeOnSave = true,
            showSuccessDialog = true,
            buildAdditionalPayload = null,
            hasAdditionalChanges = null,
            resetAdditional = null,
            onOpen = null,
            changeTargetSelector = null,
            validInputSelector = null
        } = config;

        if(!formId) throw new Error("formId is required");
        if(!key) throw new Error("key is required");

        this.formId = formId;
        this.key = key;
        this.repository = repository;
        this.saveHandler = saveHandler ?? repository?.save?.bind(repository) ?? null;
        this.beforeSave = beforeSave;
        this.afterSave = afterSave;
        this.controller = controller;
        this.buildParams = buildParams;
        this.validateBusiness = validateBusiness;
        this.closeOnSave = closeOnSave;
        this.showSuccessDialog = showSuccessDialog;
        this.currentEntity = null;
        this.buildAdditionalPayload = buildAdditionalPayload;
        this.hasAdditionalChanges = hasAdditionalChanges;
        this.resetAdditional = resetAdditional;
        this.onOpen = onOpen;
        this.changeTargetSelector = changeTargetSelector;
        this.validInputSelector = validInputSelector;
        this.saveBehavior =
            new SaveBehavior({
                beforeSave: async (payload, context) => {
                    if(this.beforeSave){
                        await this.beforeSave(
                            payload,
                            context.form
                        );
                    }
                },
                validateBusiness: async (payload) => {
                    await this.runBusinessValidation(
                        payload
                    );
                },
                confirmSave: async (payload) => {
                    return await this.confirmSave(
                        payload
                    );
                },
                executeSave: async (payload) => {
                    if(!this.saveHandler) return;
                    const res = await this.saveHandler(payload);
                    if(this.showSuccessDialog){
                        DialogService.info(
                            this.isBulkMode()
                                ? "一括更新しました"
                                : "保存しました"
                        );
                    }
                    if(this.closeOnSave){
                        DialogService.close(this.formId);
                    }
                    this.controller.setBulkMode(false);
                    return {
                        response: res,
                        id: res?.data,
                        count: res?.count
                    };
                },
                afterSave: async (result) => {
                    if(this.afterSave){
                        await this.afterSave(
                            result.id ?? this.currentEntity?.[this.key]
                        );
                    }
                }
            });
    }

    async open(dataOrId = {}) {
        let data = dataOrId ?? {};

        if (typeof dataOrId !== "object") {
            if (!this.repository?.find) return;

            const params = this.buildParams
                ? this.buildParams(dataOrId)
                : { id: dataOrId };

            data = await this.repository.find(params);
        }

        const isCreate = !data?.[this.key];

        if (isCreate) {
            const filters = this.controller.state?.filters || {};

            Object.entries(filters).forEach(([key, value]) => {
                if (value == null || value === "") return;

                const kebab = convertKey(key, "camel", "kebab");

                const el = document.querySelector(
                    `#${this.formId} [name="${kebab}"],
                    #${this.formId} [data-key="${kebab}"],
                    #${this.formId} [data-key="${key}"]`
                );

                if (!el) return;

                if (data[key] == null || data[key] === "") {
                    data[key] = value;
                }
            });
        }

        this.currentEntity = structuredClone(data);

        const form = document.getElementById(this.formId);

        FormModel.clear(form);
        FormModel.fill(form, data);

        await initParentChildLink(form);

        // フォームを開いたときは先頭タブ
        const firstTab = form.querySelector(".tab-menu-item");
        const tabPanels = form.querySelectorAll(".tab-panel");

        if (firstTab) {
            form.querySelectorAll(".tab-menu-item").forEach(tab => {
                tab.classList.remove("is-active");
            });

            firstTab.classList.add("is-active");
        }

        if (tabPanels.length > 0) {
            tabPanels.forEach(panel => {
                panel.classList.remove("is-show");
            });

            const firstTabId = firstTab?.dataset.tab;

            if (firstTabId) {
                form.querySelector(`#${firstTabId}`)?.classList.add("is-show");
            }
        }

        if (form.dataset.bulk === "true") {
            form.querySelectorAll("select").forEach(select => {
                select.selectedIndex = -1;
            });
        }

        if (!this._eventsInitialized) {
            this.initEvents();
            this._eventsInitialized = true;
        }

        // フォームと関連データの初期化
        if (this.onOpen) {
            await this.onOpen(data, this);
        }

        openFormDialog({
            dialogId: this.formId,
            controller: this.controller,
            onSubmit: async (form) => {
                await this.save(form);
            },
            onReset: () => {
                this.resetForm();
            }
        });

        this.setSubmitEnabled(false);
    }

    async save(form){
        if(this._saving) return;
        this._saving = true;

        try {
            const payload = this.preparePayload(form);

            if(!payload) return;
            return await this.saveBehavior.save(payload, { form });
        } catch(e){
            console.error(e.stack); 
            this.handleError(e);
        } finally {
            this._saving = false;
        }
    }

    preparePayload(form){
        this.clearErrors();
        UiValidator.validate(form);

        let payload = FormPayloadBuilder.build({
            form,
            currentEntity: this.currentEntity,
            key: this.key,
            isBulkMode: this.isBulkMode(),
            getTargetIds: (payload) => this.getTargetIds(payload)
        });

        const additionalChanged =
            this.hasAdditionalChanges?.() ?? false;

        // フォーム変更なし＋追加データ変更なし
        if(payload == null && !additionalChanged){
            DialogService.error("変更がありません");
            return null;
        }

        // フォーム変更なしでも明細変更があれば保存可能
        if(payload == null){
            payload = {};
        }

        // 編集時はIDを必ず保持
        if(this.currentEntity?.[this.key]){
            payload[this.key] = this.currentEntity[this.key];
        }

        // 楽観ロック用
        if(this.currentEntity?.version != null){
            payload.
            version = this.currentEntity.version;
        }
        // 追加データ
        if(this.buildAdditionalPayload){
            Object.assign(
                payload,
                this.buildAdditionalPayload()
            );
        }

        if(this.isBulkMode() && payload.ids?.length === 0){
            DialogService.error("選択してください");
            return null;
        }

        return payload;
    }

    async runBusinessValidation(payload){
        if(!this.validateBusiness){
            return;
        }
        await this.validateBusiness(payload);
    }

    async confirmSave(payload){
        if(!this.isBulkMode()){
            return true;
        }
        const ids = payload.ids ?? [];
        return await DialogService.confirm(
            `${ids.length}件に適用しますか？`
        );
    }

    handleError(e) {
        DialogService.error(e.message || "エラーが発生しました");

        const form = document.getElementById(this.formId);
        if(!form) return;
        // 単数・複数両対応
        const fields = [
            ...(e.field ? [e.field] : []),
            ...(e.fields ?? [])
        ];

        fields.forEach((field, index) => {
            const el =
                form.querySelector(`[name="${field}"]`) ||
                form.querySelector(`#${field}`);
            if (!el) return;
            el.classList.add("error");
            // 最初だけfocus
            if(index === 0){
                requestAnimationFrame(() => {
                    el.focus();
                });
            }
        });
    }

    clearErrors() {
        const form = document.getElementById(this.formId);
        form?.querySelectorAll(".error").forEach(el => {
            el.classList.remove("error");
        });
    }

    clear(){
        const form = document.getElementById(this.formId);
        FormModel.clear(form);
    }

    resetForm(){
        this.clearErrors();

        const form = document.getElementById(this.formId);

        FormModel.fill(
            form,
            this.currentEntity ?? {}
        );

        this.resetAdditional?.();

        this.setSubmitEnabled(false);
    }

    set(data){
        const form = document.getElementById(this.formId);
        FormModel.fill(form, data);
    }

    hasChanges(){
        const form = document.getElementById(this.formId);

        return FormStateBehavior.hasChanges({
            form,
            currentEntity: this.currentEntity,
            selector: this.changeTargetSelector
        });
    }

    hasValidInput(){
        const form = document.getElementById(this.formId);
        if(!form) return false;

        return FormStateBehavior.hasValidInput(form, this.validInputSelector);
    }

    initEvents(){
        const form = document.getElementById(this.formId);
        const update = () => {
            const enabled = this.canSubmit();
            this.setSubmitEnabled(enabled);
        };

        form.addEventListener("input", update);
        form.addEventListener("change", update);
    }

    canSubmit(){
        const formChanged = this.hasChanges();
        const additionalChanged =
            this.hasAdditionalChanges?.() ?? false;

        if(!this.currentEntity || !this.currentEntity[this.key]){
            return this.hasValidInput() || additionalChanged;
        }

        return formChanged || additionalChanged;
    }

    setSubmitEnabled(enabled){
        const form = document.getElementById(this.formId);
        if(!form) return;

        const btn = form.querySelector('[name="submitBtn"]');
        if(!btn) return;

        btn.disabled = !enabled;
        btn.classList.toggle("disabled", !enabled);
    }

    isHidden(){
        const form = document.getElementById(this.formId);
        return form?.classList.contains("none");
    }

    isBulkMode(){
        return this.controller?.isBulkMode?.() ?? false;
    }

    getTargetIds(payload){
        if(!this.isBulkMode()){
            return [payload[this.key]];
        }
        return this.controller?.getSelectedIds?.() ?? [];
    }

    setEditMode(editable){
        this.formEl.querySelectorAll("[data-editable]").forEach(el => {
            el.readOnly = !editable;
            el.classList.toggle("readonly-view", !editable);
            el.classList.toggle("edit-mode", editable);
        });
    }
}