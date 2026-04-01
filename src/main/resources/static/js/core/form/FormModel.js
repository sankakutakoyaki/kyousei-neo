"use strict"

import { convertKey } from "../ui/keyCaseConverter.js"

export const FormModel = {

    // ------------------------
    // フォーム → entity
    // ------------------------
    toEntity(form, base = {}){

        const fd = new FormData(form);
        const entity = structuredClone(base);

        for(const [name, value] of fd.entries()){

            const elRaw = form.elements[name];
            const el = elRaw instanceof RadioNodeList ? elRaw[0] : elRaw;
            if(!el) continue;

            let v = value;

            const key =
                el.dataset.key ||
                convertKey(name, "kebab", "camel");

            const oldValue = base[key];

            // trim
            if(typeof v === "string" && !("noTrim" in el.dataset)){
                v = v.trim();
            }

            // 空 → null
            if(v === ""){
                v = null;
            }

            // checkbox
            if(el.type === "checkbox"){
                v = el.checked;
            }

            // number
            if("number" in el.dataset && v !== null){
                v = Number(v);
            }

            // 0 → null
            if("zeroToNull" in el.dataset && v === 0){
                v = null;
            }

            // skip null（新規未入力）
            if(v === null && oldValue == null && "skipIfNull" in el.dataset){
                continue;
            }

            // 変更なしスキップ
            if(this.normalize(v) === this.normalize(oldValue)){
                continue;
            }

            entity[key] = v;
        }

        return entity;
    },

    // ------------------------
    // 差分抽出
    // ------------------------
    diff(oldObj = {}, newObj = {}){

        const diff = {};

        Object.keys(newObj).forEach(key => {
            if(this.normalize(oldObj[key]) !== this.normalize(newObj[key])){
                diff[key] = newObj[key];
            }
        });

        return diff;
    },

    // ------------------------
    // fill
    // ------------------------
    fill(form, data){

        Object.entries(data).forEach(([key, value]) => {

            const name = convertKey(key, "camel", "kebab");
            const el = form.elements[name];
            if(!el) return;

            if(el.type === "checkbox"){
                el.checked = !!value;
            }else{
                el.value = value ?? "";
            }
        });
    },

    // ------------------------
    // normalize
    // ------------------------
    normalize(v){
        return v === "" || v === undefined ? null : v;
    },

    // ------------------------
    // save
    // ------------------------
    buildPayload(form, base, key){

        const entity = this.toEntity(form, base);

        if(!base || !base[key]){
            return entity;
        }

        const diff = this.diff(base, entity);

        if(Object.keys(diff).length === 0){
            return null;
        }

        diff.id = base[key];
        diff.version = base.version;

        return diff;
    }
};