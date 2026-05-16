"use strict"

export function updateField(el, value){
    const old = el.value;
    el.value = value;

    if(old !== value){
        el.dispatchEvent(new Event("change", { bubbles: true }));
    }
}