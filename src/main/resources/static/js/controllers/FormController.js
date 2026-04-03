"use strict"

import { openFormDialog, closeFormDialog, openMsgDialog } from "../core/ui/dialog.js";
import { FormModel } from "../core/form/FormModel.js";
import { validate } from "../core/form/components/check.js";
import { api } from "../core/api/apiService.js";

export class FormController {

    constructor(config){

        this.config = Object.freeze(config);

        const {
            formId,
            key,
            api = {},
            onSaved = null,
            controller = {},
        } = config;

        if(!formId) throw new Error("formId is required");
        if(!key) throw new Error("key is required");

        this.formId = formId;
        this.key = key;
        this.api = api;
        this.onSaved = onSaved;
        this.controller = controller;

        this.currentEntity = null;
    }

    async open(dataOrId = {}){

        let data = dataOrId;

        if(typeof dataOrId !== "object"){
            if(!this.api.find) return;

            const res = await api.post(this.api.find, { id: dataOrId });
            data = res.data;
        }

        if(this.controller?.state?.companyId){
            data.companyId = this.controller.state.companyId;
        }

        this.currentEntity = data;

        const form = document.getElementById(this.formId);
        FormModel.fill(form, data);

        openFormDialog(this.formId, {
            onSubmit: async (form) => {
                await this.save(form);
            },
            onReset: () => {
                this.clear(); 
            }
        });
    }

    async save(form){

        if(!validate(form)) return;

        const payload = FormModel.buildPayload(form, this.currentEntity, this.key);

        if(payload === null){
            openMsgDialog("変更がありません", "red");
            return;
        }
        if(!this.api.save) return;

        const result = await api.post(this.api.save, payload);
        if(result.ok){
            openMsgDialog(result.message, "blue");
        }

        closeFormDialog(this.formId);


        if(this.onSaved){
            await this.onSaved(result.data);
        }
        
        return result.data;
    }

    clear(){
        const form = document.getElementById(this.formId);
        FormModel.clear(form);
    }

    set(data){
        const form = document.getElementById(this.formId);
        FormModel.fill(form, data);
    }
}