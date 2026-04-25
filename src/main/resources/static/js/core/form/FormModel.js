"use strict"

import { convertKey } from "../ui/keyCaseConverter.js"
import { normalizeValue, getOptions, normalize } from "../behavior/valueNormalizer.js";

export const FormModel = {

    // ------------------------
    // フォーム → entity
    // ------------------------
    // toEntity(form, base = {}){

    //     const fd = new FormData(form);
    //     const entity = structuredClone(base);

    //     for(const [name, value] of fd.entries()){

    //         const elRaw = form.elements[name];
    //         const el = elRaw instanceof RadioNodeList ? elRaw[0] : elRaw;
    //         if(!el) continue;

    //         const key =
    //             el.dataset.key ||
    //             convertKey(name, "kebab", "camel");

    //         const oldValue = base[key];

    //         let v;

    //         // checkboxだけ別
    //         if(el.type === "checkbox"){
    //             v = el.checked;
    //         } else {
    //             v = normalizeValue(value, getOptions(el));
    //         }

    //         // skip null（新規未入力）
    //         if(v === null && oldValue == null && "skipIfNull" in el.dataset){
    //             continue;
    //         }

    //         // 変更なしスキップ
    //         if(normalize(v) === normalize(oldValue)){
    //             continue;
    //         }

    //         entity[key] = v;
    //     }

    //     return entity;
    // },
    toEntity(form, base = {}){

        const fd = new FormData(form);
        const entity = {};

        for(const [name, value] of fd.entries()){

            const elRaw = form.elements[name];
            const el = elRaw instanceof RadioNodeList ? elRaw[0] : elRaw;
            if(!el) continue;

            // const key =
            //     el.dataset.key ||
            //     convertKey(name, "kebab", "camel");
            const key = el.dataset.key
                ? convertKey(el.dataset.key, "kebab", "camel")
                : convertKey(name, "kebab", "camel");

            let v;

            if(el.type === "checkbox"){
                v = el.checked;
            } else {
                v = normalizeValue(value, getOptions(el));
            }

            entity[key] = v;
        }

        // ★ 汎用：hiddenやdata属性で持たせる
        injectMeta(entity, form, base);

        return entity;
    },

    // ------------------------
    // 差分抽出
    // ------------------------
    diff(oldObj = {}, newObj = {}){

        const diff = {};

        Object.keys(newObj).forEach(key => {
            if(normalize(oldObj[key]) !== normalize(newObj[key])){
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

                let v = value;

                // ★ 表示用の最小変換だけ
                if("zeroToNull" in el.dataset && (v === 0 || v === "0")){
                    v = null;
                }

                el.value = v ?? "";
            }
        });
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
    },
};

function injectMeta(entity, form, base){

    // 主キー
    // const keyName = form.dataset.key;
    const rawKey = form.dataset.key;
    const keyName = convertKey(rawKey, "kebab", "camel");

    if(keyName && base[keyName] != null){
        entity[keyName] = base[keyName];
    }
    
    // version
    if(base.version != null){
        entity.version = base.version;
    }
}