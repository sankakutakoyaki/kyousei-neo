"use strict"

import { convertKey } from "../ui/keyCaseConverter.js";
import { normalize,normalizeValue,getOptions } from "../behavior/valueNormalizer.js";

export const FormStateBehavior = {
    hasChanges({form, currentEntity}){
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

            const oldValue = currentEntity?.[key];

            if(normalize(v) !== normalize(oldValue)){
                return true;
            }
        }
        return false;
    },

    hasValidInput(form){
        const fd = new FormData(form);

        for(const [, value] of fd.entries()){
            if(value && value.trim() !== ""){
                return true;
            }
        }
        return false;
    }
};