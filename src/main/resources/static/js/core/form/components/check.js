"use strict"

import { validators } from "../../behavior/validators.js";

export function validate(form){
    const messages = [];
    let focusTarget = null;

    const elements = form.querySelectorAll("[name]");
    elements.forEach(el => {
        let v = el.value ?? "";
        // trim
        if(el.dataset.trim && typeof v === "string"){
            v = v.trim();
        }
        // required
        if(el.dataset.required && v === ""){
            messages.push(el.dataset.required);
            if(!focusTarget){
                focusTarget = el;
            }
            return;
        }
        // validate
        const type = el.dataset.validate;
        if(type && v){
            const fn = validators[type];
            if(fn && !fn(v)){
                messages.push(el.dataset.message);
                if(!focusTarget){
                    focusTarget = el;
                }
                return;
            }
        }
        // range
        if(el.dataset.range && v){
            const [min, max, msg] = el.dataset.range.split(",");
            const n = Number(v);
            const minNum = Number(min);
            const maxNum = Number(max);

            if(isNaN(n) || n < minNum || n > maxNum){
                messages.push(msg);
                if(!focusTarget){
                    focusTarget = el;
                }
                return;
            }
        }
        // date比較
        if(el.dataset.dateAfter){
            const [selector, msg] = el.dataset.dateAfter.split(",");
            const base = form.querySelector(selector) ?.value;
            const d1 = Date.parse(v);
            const d2 = Date.parse(base);

            if(base && v && d1 < d2){
                messages.push(msg);
                if(!focusTarget){
                    focusTarget = el;
                }
                return;
            }
        }
    });

    if(messages.length){
        throw {message: messages.join("\n"), field: focusTarget?.name};
    }
    return true;
}