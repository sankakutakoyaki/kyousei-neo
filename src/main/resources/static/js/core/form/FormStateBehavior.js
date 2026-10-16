"use strict"

import { convertKey } from "../../util/keyCaseConverter.js";
import { normalize,normalizeValue,getOptions } from "../behavior/valueNormalizer.js";

export const FormStateBehavior = {
    hasChanges({form, currentEntity, selector = null}){
        const target = selector ? form.querySelector(selector): form;
        if(!target) return false;

        const elements = target.querySelectorAll("input[name], select[name], textarea[name]");
        for(const el of elements){
            if(el.dataset.submit === "none") continue;

            const name = el.getAttribute("name");
            if(!name) continue;

            // const key = el.dataset.key || convertKey(name, "kebab", "camel");
            const key = convertKey(el.dataset.key || name, "kebab", "camel");
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

        const target = selector ? form.querySelector(selector) : form;
        if(!target) return false;

        const elements = target.matches("input, select, textarea")
            ? [target]
            : target.querySelectorAll("input[name], select[name], textarea[name]");

        for(const el of elements){
            if(el.dataset.submit === "none" || el.disabled) continue;

            if(el.type === "checkbox" || el.type === "radio"){
                if(el.checked) return true;
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