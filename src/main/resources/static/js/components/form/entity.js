"use strict"

import { api } from "../../core/api/apiService.js";
import { convertKey } from "../../util/keyCaseConverter.js";
import { validate } from "./check.js";
import { openMsgDialog } from "../../core/ui/dialog.js";

function buildEntity(form, base = {}){

    const fd = new FormData(form);
    const entity = structuredClone(base);

    for(const [name, value] of fd.entries()){

        // element取得（複数対応）
        const elRaw = form.elements[name];
        const el = elRaw instanceof RadioNodeList ? elRaw[0] : elRaw;
        if(!el) continue;

        let v = value;

        const key =
            el.dataset.key ||
            convertKey(name, "kebab", "camel");

        const oldValue = base[key];

        // ------------------------
        // ① trim（デフォルトON）
        // ------------------------
        if(typeof v === "string" && !("noTrim" in el.dataset)){
            v = v.trim();
        }

        // ------------------------
        // ② 空文字 → null（削除）
        // ------------------------
        if(v === ""){
            v = null;
        }

        // ------------------------
        // ③ checkbox
        // ------------------------
        if(el.type === "checkbox"){
            v = el.checked;
        }

        // ------------------------
        // ④ number
        // ------------------------
        if("number" in el.dataset && v !== null){
            v = Number(v);
        }

        // ------------------------
        // ⑤ 0 → null
        // ------------------------
        if("zeroToNull" in el.dataset && v === 0){
            v = null;
        }

        // ------------------------
        // ⑥ skip null（未入力のみスキップ）
        // ------------------------
        if(v === null && oldValue == null && "skipIfNull" in el.dataset){
            continue;
        }

        // ------------------------
        // ⑦ 変更なしスキップ（最強安定）
        // ------------------------
        if(normalize(v) === normalize(oldValue)){
            continue;
        }

        entity[key] = v;
    }

    return entity;
}

function normalize(v){
    return v === "" || v === undefined ? null : v;
}

function diffEntity(oldObj = {}, newObj = {}){
    const diff = {};
    Object.keys(newObj).forEach(key => {
        if(normalize(oldObj[key]) !== normalize(newObj[key])){
            diff[key] = newObj[key];
        }
    });
    return diff;
}

function toApiPayload(entity){
    const result = {};
    Object.entries(entity).forEach(([key, value]) => {
        const apiKey = convertKey(key, "camel", "snake");
        result[apiKey] = value;
    });
    return result;
}

export async function execSave(form, data){

    if(!validate(form)) return;

    const edited = buildEntity(form, data.entity);
    const isUpdate = data.entity && data.entity[data.key];

    let payload;

    if(isUpdate){
        const diff = diffEntity(data.entity, edited);

        if(Object.keys(diff).length === 0){
            openMsgDialog("変更がありません", "red");
            return;
        }
        diff.version = data.entity.version;
        diff.id = data.entity[data.key];

        payload = diff;
    }else{
        // 新規
        payload = edited;
    }

    const result = await api.post(`/api/${data.parent}/save`, payload);
    if(result.ok){
        openMsgDialog(result.message, "blue");
    }
    return result;
}