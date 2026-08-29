"use strict"

import { convertKey } from "../../util/keyCaseConverter.js";
import { normalize,normalizeValue,getOptions } from "../behavior/valueNormalizer.js";

export const FormStateBehavior = {
    // hasChanges({form, currentEntity}){
    //     const fd = new FormData(form);
    //     for(const [name, value] of fd.entries()){
    //         const el = form.elements[name];
    //         const key = el.dataset.key || convertKey(name, "kebab", "camel");
    //         let v;
    //         if(el.type === "checkbox"){
    //             v = el.checked;
    //         } else {
    //             v = normalizeValue(value, getOptions(el));
    //         }
    //         const oldValue = currentEntity?.[key];
    //         if(normalize(v) !== normalize(oldValue)){
    //             return true;
    //         }
    //     }
    //     return false;
    // },
hasChanges({form, currentEntity, selector = null}){
    const target = selector
        ? form.querySelector(selector)
        : form;

    if(!target) return false;

    const elements = target.querySelectorAll("[name]");

    for(const el of elements){
        const name = el.name;

        const key =
            el.dataset.key ||
            convertKey(name, "kebab", "camel");

        let v;

        if(el.type === "checkbox"){
            v = el.checked;
        } else {
            v = normalizeValue(
                el.value,
                getOptions(el)
            );
        }

        const oldValue = currentEntity?.[key];

        // if(normalize(v) !== normalize(oldValue)){
        //     console.log("===== FORM CHANGE DETECTED =====");
        //     console.log("name =", name);
        //     console.log("key =", key);
        //     console.log("formValue =", v);
        //     console.log("oldValue =", oldValue);
        //     console.log("normalized form =", normalize(v));
        //     console.log("normalized old =", normalize(oldValue));
        //     return true;
        // }
        const current = normalize(v ?? null);
        const old = normalize(oldValue ?? null);

        if(current !== old){
            return true;
        }
    }

    return false;
},

    // hasValidInput(form){
    //     const fd = new FormData(form);

    //     for(const [, value] of fd.entries()){
    //         if(value && value.trim() !== ""){
    //             return true;
    //         }
    //     }
    //     return false;
    // }
    // hasValidInput(form, selector = null){
    //     const elements = selector
    //         ? form.querySelectorAll(
    //             `${selector} input,
    //             ${selector} select,
    //             ${selector} textarea`
    //         )
    //         : form.elements;

    //     for(const el of elements){
    //         if(el.disabled) continue;
    //         // checkbox / radio
    //         if(el.type === "checkbox" || el.type === "radio"){
    //             if(el.checked){
    //                 return true;
    //             }
    //             continue;
    //         }

    //         const value = normalizeValue(
    //             el.value,
    //             getOptions(el)
    //         );

    //         if(
    //             value != null &&
    //             String(value).trim() !== ""
    //         ){
    //             return true;
    //         }
    //     }

    //     return false;
    // }
// hasValidInput(form, selector = null){
//     const target = selector
//         ? form.querySelector(selector)
//         : form;

//     if(!target) return false;

//     const elements = target.querySelectorAll("[name]");

//     for(const el of elements){
//         const value = el.value ?? "";

//         if(
//             typeof value === "string" &&
//             value.trim() !== ""
//         ){
//             return true;
//         }
//     }

//     return false;
// },
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

        const value = normalizeValue(
            el.value,
            getOptions(el)
        );

        return (
            value != null &&
            String(value).trim() !== ""
        );
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

        const value = normalizeValue(
            el.value,
            getOptions(el)
        );

        if(
            value != null &&
            String(value).trim() !== ""
        ){
            return true;
        }
    }

    return false;
}
};