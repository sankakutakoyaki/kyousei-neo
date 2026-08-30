"use strict"

import { convertKey } from "../../util/keyCaseConverter.js";
import { normalize,normalizeValue,getOptions } from "../behavior/valueNormalizer.js";

export const FormStateBehavior = {
    hasChanges({form, currentEntity, selector = null}){
        const target = selector ? form.querySelector(selector): form;
        if(!target) return false;

        const elements = target.querySelectorAll("[name]");
        for(const el of elements){
            const name = el.name;
            const key = el.dataset.key || convertKey(name, "kebab", "camel");

            let v;
            if(el.type === "checkbox"){
                v = el.checked;
            } else {
                v = normalizeValue(el.value, getOptions(el));
            }

            const oldValue = currentEntity?.[key];
            const current = normalize(v ?? null);
            const old = normalize(oldValue ?? null);
            if(current !== old){
                return true;
            }
        }
        return false;
    },
    hasValidInput(form, selector = null){
        if(!form) return false;

        if(selector){
            const el = form.querySelector(selector);
            if(!el || el.disabled) {
                return false;
            }

            if(el.type === "checkbox" || el.type === "radio"){
                return el.checked;
            }

            const value = normalizeValue(el.value, getOptions(el));
            return (value != null && String(value).trim() !== "");
        }

        const elements = form.querySelectorAll("[name]");
        for(const el of elements){
            if(el.disabled) continue;

            if(el.type === "checkbox" || el.type === "radio"){
                if(el.checked){
                    return true;
                }
                continue;
            }

            const value = normalizeValue(el.value, getOptions(el));
            if(value != null && String(value).trim() !== ""){
                return true;
            }
        }
        return false;
    }
};