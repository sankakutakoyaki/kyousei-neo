"use strict"

import { FormModel } from "../core/form/FormModel.js";
import { convertKey } from "../core/ui/keyCaseConverter.js";
import { openFormDialog } from "../core/ui/dialog.js";
import { DialogService } from "../core/ui/DialogService.js";
import { SaveBehavior } from "../core/save/SaveBehavior.js";
import { FormPayloadBuilder } from "../core/form/FormPayloadBuilder.js";
import { UiValidator } from "../core/validate/UiValidator.js";
import { FormStateBehavior } from "../core/form/FormStateBehavior.js";

export class FormController {
    constructor(config){
        this.config = Object.freeze(config);
        const {
            formId,
            key,
            repository = null,
            beforeSave = null,
            afterSave = null,
            controller = {},
            buildParams = null,
            validateBusiness = null
        } = config;

        if(!formId) throw new Error("formId is required");
        if(!key) throw new Error("key is required");

        this.formId = formId;
        this.key = key;
        this.repository = repository;
        this.beforeSave = beforeSave;
        this.afterSave = afterSave;
        this.controller = controller;
        this.buildParams = buildParams;
        this.validateBusiness = validateBusiness;

        this.currentEntity = null;
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
                    if(!this.repository?.save) return;
                    const res = await this.repository.save(payload);
                    DialogService.info(
                        this.isBulkMode()
                            ? "一括更新しました"
                            : "保存しました"
                    );
                    DialogService.close(this.formId);
                    this.controller.setBulkMode(false);
                    return {
                        response: res,
                        id: res.data,
                        count: res.count
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
            if(!this.repository?.find) return;

            const params = this.buildParams
                ? this.buildParams(dataOrId)
                : { id: dataOrId };
            data = await this.repository.find(params);
        }

        const isCreate = !data?.[this.key];
        if(isCreate){
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
        FormModel.fill(form, data);

        if (!this._eventsInitialized) {
            this.initEvents();
            this._eventsInitialized = true;
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
        try {
            const payload = this.preparePayload(form);
            if(!payload){
                return;
            }
            return await this.saveBehavior.save(payload, { form });
        } catch(e){
            this.handleError(e);
        }
    }

    preparePayload(form){
        this.clearErrors();
        UiValidator.validate(form);

        const payload = FormPayloadBuilder.build({
                form,
                currentEntity: this.currentEntity,
                key: this.key,
                isBulkMode: this.isBulkMode(),
                getTargetIds: (payload) => this.getTargetIds(payload)
            });

        if(payload == null){
            DialogService.error("変更がありません");
            return null;
        }

        if(this.isBulkMode() && payload.ids.length === 0){
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
        // メッセージ表示
        DialogService.error(
            e.message || "エラーが発生しました"
        );

        // フィールド指定があれば強調
        if (e.field) {
            const form = document.getElementById(this.formId);

            const el =
                form?.querySelector(`[name="${e.field}"]`) ||
                form?.querySelector(`#${e.field}`);

            if (el) {
                el.classList.add("error");
                el.focus();
            }
        }
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
        this.setSubmitEnabled(false);
    }

    set(data){
        const form = document.getElementById(this.formId);
        FormModel.fill(form, data);
    }

    hasChanges(){
        const form = document.getElementById(this.formId);
        return FormStateBehavior.hasChanges({form, currentEntity: this.currentEntity});
    }

    hasValidInput(){
        const form = document.getElementById(this.formId);
        return FormStateBehavior.hasValidInput(form);
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
        // 新規
        if(!this.currentEntity || !this.currentEntity[this.key]){
            return this.hasValidInput();
        }
        // 編集
        return this.hasChanges();
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
}