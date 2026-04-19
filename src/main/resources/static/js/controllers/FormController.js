"use strict"

import { openFormDialog, closeFormDialog, openMsgDialog } from "../core/ui/dialog.js";
import { FormModel } from "../core/form/FormModel.js";
import { validate } from "../core/form/components/check.js";
// import { api } from "../core/api/apiService.js";
import { convertKey } from "../core/ui/keyCaseConverter.js";
import { normalize, normalizeValue, getOptions } from "../core/behavior/valueNormalizer.js";

export class FormController {

    constructor(config){

        this.config = Object.freeze(config);

        const {
            formId,
            key,
            api = {},
            beforeSave = null,
            onSaved = null,
            controller = {},
            buildParams = null
        } = config;

        if(!formId) throw new Error("formId is required");
        if(!key) throw new Error("key is required");

        this.formId = formId;
        this.key = key;
        this.api = api;
        this.beforeSave = beforeSave;
        this.onSaved = onSaved;
        this.controller = controller;
        this.buildParams = buildParams;

        this.currentEntity = null;
    }

    async open(dataOrId = {}) {
        let data = dataOrId;

        if (typeof dataOrId !== "object") {
            if (!this.api.find) return;

            const params = this.buildParams
                ? this.buildParams(dataOrId)
                : { id: dataOrId };

            const res = await this.api.find(params)
            data = res.data?.[0] ?? {};
        }

        // if (this.controller?.state?.companyId) {
        //     data.companyId = this.controller.state.companyId;
        // }
        Object.entries(this.controller.state || {}).forEach(([key, value]) => {
            if (value == null || value === "") return;

            const el = document.querySelector(`#${this.formId} [name="${key}"]`);
            if (!el) return; // ★ フォームに存在する項目だけ

            if (!(key in data)) {
                data[key] = value;
            }
        });

        this.currentEntity = data;

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
                this.clear();
            }
        });

        this.setSubmitEnabled(false);
    }

    // async save(form){

    //     if(!validate(form)) return;

    //     const payload = FormModel.buildPayload(form, this.currentEntity, this.key);

    //     if(payload === null){
    //         openMsgDialog({
    //             message:"変更がありません",
    //             color:"red"
    //         });
    //         return;
    //     }
    //     if(!this.api.save) return;

    //     const result = await api.post(this.api.save, payload);
    //     if(result.ok){
    //         openMsgDialog({
    //             message:result.message,
    //             color:"blue"
    //         });
    //     }

    //     closeFormDialog(this.formId);


    //     if(this.onSaved){
    //         await this.onSaved(result.data);
    //     }
        
    //     return result.data;
    // }
    async save(form){

        if(!validate(form)) return;

        const payload = FormModel.buildPayload(form, this.currentEntity, this.key);

        if(payload === null){
            openMsgDialog({
                message:"変更がありません",
                color:"red"
            });
            return;
        }

        if (this.beforeSave) {
            this.beforeSave(payload);
        }

        if(!this.api.save) return;

        const res = await this.api.request({
            queryId: this.api.save,
            params: payload
        });

        // INSERTの場合 idが返る
        const id = res.data;

        // UPDATEの場合 countが返る
        const count = res.count;

        openMsgDialog({
            message: "保存しました",
            color: "blue"
        });

        closeFormDialog(this.formId);

        if(this.onSaved){
            await this.onSaved(id ?? this.currentEntity?.[this.key]);
        }

        return id ?? count;
    }

    clear(){
        const form = document.getElementById(this.formId);
        FormModel.clear(form);
    }

    set(data){
        const form = document.getElementById(this.formId);
        FormModel.fill(form, data);
    }

    hasChanges(){
        const form = document.getElementById(this.formId);
        const fd = new FormData(form);

        for(const [name, value] of fd.entries()){
            const el = form.elements[name];
            const key = el.dataset.key || convertKey(name, "kebab", "camel");
            let v;
            if(el.type === "checkbox"){
                v = el.checked;
            } else {
                v = normalizeValue(value, getOptions(el));
            }
            const oldValue = this.currentEntity?.[key];
            if(normalize(v) !== normalize(oldValue)){
                return true;
            }
        }
        return false;
    }

    hasValidInput(){
        const form = document.getElementById(this.formId);
        const fd = new FormData(form);
        for(const [, value] of fd.entries()){
            if(value && value.trim() !== ""){
                return true;
            }
        }
        return false;
    }

    initEvents(){
        const form = document.getElementById(this.formId);
        // const update = () => {
        //     this.controller?.updateButtons();
        // };
        // const update = () => {
        //     this.dirty = true;
        //     this.updateSubmitButton();
        // };
        const update = () => {
            const enabled = this.canSubmit();
            this.setSubmitEnabled(enabled);
        };

        form.addEventListener("input", update);
        form.addEventListener("change", update);
    }
    // initEvents(){
    //     const update = () => {
    //         this.dirty = true;
    //         this.updateSubmitButton();
    //     };

    //     // ★ 毎回最新のformを取る
    //     document.addEventListener("input", (e) => {
    //         if(e.target.closest(`#${this.formId}`)){
    //             update();
    //         }
    //     });

    //     document.addEventListener("change", (e) => {
    //         if(e.target.closest(`#${this.formId}`)){
    //             update();
    //         }
    //     });
    // }

    canSubmit(){
        return this.hasChanges() && this.hasValidInput();
        // // ★ 新規（これ重要）
        // if(!this.currentEntity || !this.currentEntity[this.key]){
        //     return this.hasValidInput();
        // }

        // // 編集
        // return this.hasChanges();
    }

    setSubmitEnabled(enabled){
        const form = document.getElementById(this.formId);
        if(!form) return;

        const btn = form.querySelector('[name="submitBtn"]');
        if(!btn) return;

        btn.disabled = !enabled;
        btn.classList.toggle("disabled", !enabled);
    }

    // updateSubmitButton(){
    //     const form = document.getElementById(this.formId);
    //     if(!form) return;

    //     const btn = form.querySelector('[name="submitBtn"]');
    //     if(!btn) return;

    //     const enabled = this.canSubmit();

    //     btn.disabled = !enabled;
    //     btn.classList.toggle("disabled", !enabled);
    // }
}