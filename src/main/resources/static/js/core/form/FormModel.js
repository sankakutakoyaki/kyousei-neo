"use strict"

import { convertKey } from "../../util/keyCaseConverter.js";
import { normalizeValue, getOptions, normalize } from "../behavior/valueNormalizer.js";
import { resolveSubmitValue } from "../behavior/DataResolver.js";
import { formatters } from "../behavior/formatters.js";

export const FormModel = {
    toEntity(form, base = {}){
        const entity = {};
        const fields = form.querySelectorAll("input[name], select[name], textarea[name]");
        fields.forEach(el => {
            if(el.disabled) return;
            if(el.dataset.submit === "none") return;

            const name = el.name;
            if(!name) return;

            const rawKey = el.dataset.key || name;
            if(!rawKey) return;

            const key = convertKey(rawKey, "kebab", "camel");
            let value;
            if(el instanceof HTMLInputElement && el.type === "checkbox"){
                value = el.checked;
            } else {
                value = el.value;
            }
            const v = resolveSubmitValue(el, value);
            if(v === undefined) return;
            if(v == null && "skipIfNull" in el.dataset) return;
            entity[key] = v;
        });
        injectMeta(entity, form, base);
        return entity;
    },
    // 差分抽出
    diff(oldObj = {}, newObj = {}){
        const diff = {};
        Object.keys(newObj).forEach(key => {
            if(normalize(oldObj[key]) !== normalize(newObj[key])){
                diff[key] = newObj[key];
            }
        });
        return diff;
    },
    // fill
    fill(form, data = {}){
        Object.entries(data).forEach(([key, value]) => {
            const kebab = convertKey(key, "camel", "kebab");
            // data-key優先
            let el = form.querySelector(`[data-key="${kebab}"]`);
            // fallback
            if(!el){
                const elRaw = form.elements[kebab];
                el = elRaw instanceof RadioNodeList ? elRaw[0]: elRaw;
            }
            if(!el) return;

            const dataset = el.dataset ?? {};
            let v = value;
            // fillKey
            const fillKey = dataset.fillKey;
            if(fillKey){
                const camelFillKey = convertKey(fillKey, "kebab", "camel");
                v = data[camelFillKey];
            }
            // validate formatter
            const type = dataset.validate;
            if(type){
                const formatter = formatters[type];
                if(formatter){
                    v = formatter(v);
                }
            }
            // checkbox
            if(el.type === "checkbox"){
                el.checked = !!v;
            } else {
                // normalize
                if("zeroToNull" in dataset && (v === 0 || v === "0")){
                    v = null;
                }
                // el.value = v ?? "";
                el.value = v != null ? String(v): "";
            }
        });
    },

    // save
    buildPayload(form, base, key){
        const entity = this.toEntity(form, base);
        if(!base || !base[key]){
            return entity;
        }

        const diff = this.diff(base, entity);
        if(Object.keys(diff).length === 0){
            return null;
        }
        diff[key] = base[key];
        diff.version = base.version;
        return diff;
    },

    clear(form){
        [...form.elements].forEach(el => {
            if(!el.name) return;

            if(el.type === "checkbox"){
                el.checked = false;
            }else{
                el.value = "";
            }
        });
    }
};

function injectMeta(entity, form, base){
    // 主キー
    const rawKey = form.dataset.key;
    if(!rawKey) return;

    const keyName = convertKey(rawKey, "kebab", "camel");
    if(keyName && base[keyName] != null){
        entity[keyName] = base[keyName];
    }    
    // version
    if(base.version != null){
        entity.version = base.version;
    }
}