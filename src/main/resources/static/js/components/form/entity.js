"use strict"

import { convertKey } from "../../util/keyCaseConverter.js";
import { validate } from "./check.js";

export function buildEntity(form, base = {}){

    const fd = new FormData(form);
    const entity = structuredClone(base);

    for(const [name, value] of fd.entries()){

        // const el = form.querySelector(`[name="${name}"]`);
        const el = form.elements[name];
        if(!el) continue;

        let v = value;

        const key =
            el.dataset.key ||
            convertKey(name, "kebab", "camel");

        // trim
        if(el.dataset.trim && typeof v === "string"){
            v = v.trim();
        }

        // empty → null
        if((v === "" || v == null) && el.dataset.emptyToNull){
            v = null;
        }

        // number
        if(el.dataset.number && v !== null && v !== ""){
            v = Number(v);
        }

        // 0 → null
        if(el.dataset.zeroToNull && v === 0){
            v = null;
        }

        // skip null
        if(v === null && el.dataset.skipIfNull){
            continue;
        }

        // checkbox
        if(el.type === "checkbox"){
            v = el.checked;
        }

        entity[key] = v;
    }

    return entity;
}

export function diffEntity(oldObj = {}, newObj = {}){
    const diff = {};

    Object.keys(newObj).forEach(key => {
        if((oldObj[key] ?? null) !== (newObj[key] ?? null)){
            diff[key] = newObj[key];
        }
    });

    return diff;
}

export function toApiPayload(entity){

    const result = {};

    Object.entries(entity).forEach(([key, value]) => {
        const apiKey = convertKey(key, "camel", "snake");
        result[apiKey] = value;
    });

    return result;
}

export async function execSave(form, data){

    if(!validate(form)) return;

    const edited = buildEntity(form, data.originEntity);
    const diff = diffEntity(data.originEntity, edited);
    
    if(data.originEntity.id && Object.keys(diff).length === 0){
        openMsgDialog("変更がありません", "red");
        return;
    }

    if(data.originEntity.id){
        diff.version = data.originEntity.version;
    }

    diff.id = data.originEntity.id;

    const payload = toApiPayload(diff);

    const result = await api.post(`/api/${data.parent}/save`, JSON.stringify(payload));

    if(result.ok){
        closeFormDialog("form");
        await refresh();
        openMsgDialog(result.message, "blue");
    }
}