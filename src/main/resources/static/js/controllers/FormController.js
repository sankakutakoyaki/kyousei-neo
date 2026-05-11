"use strict"

// import { openFormDialog, closeFormDialog, openMsgDialog, openConfirmDialog } from "../core/ui/dialog.js";
import { FormModel } from "../core/form/FormModel.js";
// import { validate } from "../core/form/components/check.js";
// import { api } from "../core/api/apiService.js";
// import { convertKey } from "../core/ui/keyCaseConverter.js";
// import { normalize, normalizeValue, getOptions } from "../core/behavior/valueNormalizer.js";
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
            api = {},
            beforeSave = null,
            // onSaved = null,
            afterSave = null,
            controller = {},
            buildParams = null,
            // businessValidate = null
            validateBusiness = null
        } = config;

        if(!formId) throw new Error("formId is required");
        if(!key) throw new Error("key is required");

        this.formId = formId;
        this.key = key;
        this.api = api;
        this.beforeSave = beforeSave;
        // this.onSaved = onSaved;
        this.afterSave = afterSave;
        this.controller = controller;
        this.buildParams = buildParams;
        // this.businessValidate = businessValidate;
        this.validateBusiness = validateBusiness;

        this.currentEntity = null;
        // this.saveBehavior =
        //     new SaveBehavior({
        //         beforeSave:
        //             async (payload, context) => {
        //                 if(this.beforeSave){
        //                     await this.beforeSave(payload, context.form);
        //                 }
        //             },
        //         validateBusiness:
        //             async (payload) => {
        //                 await this.runBusinessValidation(payload);
        //             },
        //         confirmSave:
        //             async (payload) => {
        //                 return await this.confirmSave(payload);
        //             },
        //         executeSave:
        //             async (payload) => {
        //                 return await this.executeSave(payload);
        //             }
        //     });
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
                    if(!this.api.save){
                        return null;
                    }
                    const res = await this.api.request({
                        queryId: this.api.save,
                        params: payload
                    });
                    DialogService.info(
                        this.isBulkMode()
                            ? "一括更新しました"
                            : "保存しました"
                    );
                    DialogService.close(this.formId);
                    // this.controller.state.bulkMode = false;
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
            if (!this.api.find) return;

            const params = this.buildParams
                ? this.buildParams(dataOrId)
                : { id: dataOrId };

            const res = await this.api.request({
                queryId: this.api.find,
                params: params
            });
            data = res.data?.[0] ?? {};
        }

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

        // this.currentEntity = data;
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
                // this.clear();
                this.resetForm();
            }
        });

        this.setSubmitEnabled(false);
    }

    // async save(form){
    //     try {
    //         this.clearErrors();
    //         validate(form);
    //     } catch(e) {
    //         this.handleError(e);
    //         return;
    //     }

    //     const payload = FormModel.buildPayload(form, this.currentEntity, this.key);

    //     if(payload === null){
    //         openMsgDialog({
    //             message:"変更がありません",
    //             color:"red"
    //         });
    //         return;
    //     }

    //     const controller = this.controller;
    //     const ids = controller?.dataTable?.model.getSelectedIds?.() ?? [];

    //     if(controller?.state?.bulkMode){
    //         if(ids.length === 0){
    //             openMsgDialog({
    //                 message:"選択してください",
    //                 color:"red"
    //             });
    //             return;
    //         }

    //         payload.ids = ids;
    //         openConfirmDialog({
    //             message: `${ids.length}件に適用しますか？`,
    //             onSubmit: async () => {
    //                 try {
    //                     if (this.businessValidate) {
    //                         await this.businessValidate(payload);
    //                     }
    //                     const res = await this.api.request({
    //                         queryId: this.api.save,
    //                         params: payload
    //                     });
    //                     openMsgDialog({
    //                         message: "一括更新しました",
    //                         color: "blue"
    //                     });
    //                     closeFormDialog(this.formId);
    //                     controller.state.bulkMode = false;
    //                     if(this.onSaved){
    //                         await this.onSaved();
    //                     }
    //                 } catch(e){
    //                     this.handleError(e);
    //                 }
    //             }
    //         });
    //         return;
    //     }

    //     try {
    //         if (this.beforeSave) {
    //             await this.beforeSave(payload, form);
    //         }

    //         if (this.businessValidate) {
    //             await this.businessValidate(payload);
    //         }

    //         if(!this.api.save) return;

    //         const res = await this.api.request({
    //             queryId: this.api.save,
    //             params: payload
    //         });

    //         // INSERTの場合 idが返る
    //         const id = res.data;

    //         // UPDATEの場合 countが返る
    //         const count = res.count;

    //         openMsgDialog({
    //             message: "保存しました",
    //             color: "blue"
    //         });

    //         closeFormDialog(this.formId);
    //         controller.state.bulkMode = false;
    //         if(this.onSaved){
    //             await this.onSaved(id ?? this.currentEntity?.[this.key]);
    //         }

    //         return id ?? count;
    //     } catch (e) {
    //         this.handleError(e);
    //     }
    // }
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

    // async executeSave(payload){
    //     if(!this.api.save){
    //         return;
    //     }

    //     const res = await this.api.request({
    //         queryId: this.api.save,
    //         params: payload
    //     });

    //     DialogService.info(
    //         this.isBulkMode()
    //             ? "一括更新しました"
    //             : "保存しました"
    //     );

    //     DialogService.close(this.formId);

    //     this.controller.state.bulkMode = false;

    //     const id = res.data;
    //     const count = res.count;

    //     // if(this.onSaved){
    //     //     await this.onSaved(
    //     //         id ?? this.currentEntity?.[this.key]
    //     //     );
    //     // }
    //     if(this.afterSave){
    //         await this.afterSave(
    //             id ?? this.currentEntity?.[this.key]
    //         );
    //     }
    //     return id ?? count;
    // }

    // preparePayload(form){
    //     this.clearErrors();
    //     validate(form);

    //     const payload = FormModel.buildPayload(form, this.currentEntity, this.key);
    //     if(payload === null){
    //         DialogService.error(
    //             "変更がありません"
    //         );
    //         return null;
    //     }

    //     if(this.isBulkMode()){
    //         const ids = this.getTargetIds(payload);
    //         if(ids.length === 0){
    //             DialogService.error(
    //                 "選択してください"
    //             );
    //             return null;
    //         }
    //         payload.ids = ids;
    //     }
    //     return payload;
    // }
    preparePayload(form){
        this.clearErrors();
        // validate(form);
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

    // async validateBusiness(payload){
    //     if(!this.businessValidate){
    //         return;
    //     }
    //     await this.businessValidate(payload);
    // }
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
        const ids = this.getTargetIds(payload);
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

    // resetForm(){
    //     this.clearErrors();
    //     const form = document.getElementById(this.formId);
    //     FormModel.clear(form);
    //     this.setSubmitEnabled(false);
    // }
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

    // hasChanges(){
    //     const form = document.getElementById(this.formId);
    //     const fd = new FormData(form);

    //     for(const [name, value] of fd.entries()){
    //         const el = form.elements[name];
    //         const key = el.dataset.key || convertKey(name, "kebab", "camel");
    //         let v;
    //         if(el.type === "checkbox"){
    //             v = el.checked;
    //         } else {
    //             v = normalizeValue(value, getOptions(el));
    //         }
    //         const oldValue = this.currentEntity?.[key];
    //         if(normalize(v) !== normalize(oldValue)){
    //             return true;
    //         }
    //     }
    //     return false;
    // }
    hasChanges(){
        const form = document.getElementById(this.formId);
        return FormStateBehavior.hasChanges({form, currentEntity: this.currentEntity});
    }

    hasValidInput(){
        // const form = document.getElementById(this.formId);
        // const fd = new FormData(form);
        // for(const [, value] of fd.entries()){
        //     if(value && value.trim() !== ""){
        //         return true;
        //     }
        // }
        // return false;
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
        // return !!this.controller?.state?.bulkMode;
        return this.controller?.isBulkMode?.() ?? false;
    }

    getTargetIds(payload){
        if(!this.isBulkMode()){
            return [payload[this.key]];
        }
        // return this.controller
        //     ?.dataTable
        //     ?.getSelectedIds?.() ?? [];
        return this.controller?.getSelectedIds?.() ?? [];
    }
}