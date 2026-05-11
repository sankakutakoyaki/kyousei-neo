"use strict"

import { validators } from "../../behavior/validators.js";
import { formatters } from "../../behavior/formatters.js";

export function handleEnterValidate(e){
    if(e.key !== "Enter") return;

    const el = e.target;
    if(!(el instanceof HTMLInputElement)) return;

    // readonly / disabled は対象外
    if(el.readOnly || el.disabled) return;
    // 対象外
    if(el.dataset.enterValidate === undefined) return;

    const value = el.value?.trim();
    // 一旦消す
    el.classList.remove("error");
    // 空なら通常動作
    if(!value){
        return;
    }

    const type = el.dataset.validate;
    if(!type) return;

    const fn = validators[type];
    if(!fn) return;

    // NG
    if(!fn(value)){
        e.preventDefault();
        el.classList.add("error");
        requestAnimationFrame(() => {
            el.select();
        });
        return;
    }

    // OK
    const formatter = formatters[type];
    if(formatter){
        el.value = formatter(value);
    }
}