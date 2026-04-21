"use strict"

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
            if(!focusTarget) focusTarget = el;
            return;
        }

        // email
        if(el.dataset.email && v && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)){
            messages.push(el.dataset.email);
            if(!focusTarget) focusTarget = el;
            return;
        }

        // phone
        if(el.dataset.phone && v && !/^\d{10,11}$/.test(v.replaceAll('-', ''))){
            messages.push(el.dataset.phone);
            if(!focusTarget) focusTarget = el;
            return;
        }

        // range
        if(el.dataset.range && v){
            const [min, max, msg] = el.dataset.range.split(",");
            const n = Number(v);
            const minNum = Number(min);
            const maxNum = Number(max);

            if(isNaN(n) || n < minNum || n > maxNum){
            // if(isNaN(n) || n < min || n > max){
                messages.push(msg);
                if(!focusTarget) focusTarget = el;
                return;
            }
        }

        // date比較
        if(el.dataset.dateAfter){
            const [selector, msg] = el.dataset.dateAfter.split(",");
            const base = form.querySelector(selector)?.value;

            const d1 = Date.parse(v);
            const d2 = Date.parse(base);
            if(base && v && d1 < d2){
            // if(base && v && new Date(v) < new Date(base)){
                messages.push(msg);
                if(!focusTarget) focusTarget = el;
                return;
            }
        }

    });

    // if(messages.length){
    //     openMsgDialog({
    //         messages:messages.join("\n"),
    //         color:"red"
    //     });
    //     focusTarget?.focus();
    //     return false;
    // }

    // return true;
    if(messages.length){
        throw {
            message: messages.join("\n"),
            field: focusTarget?.name
        };
    }

    return true;
}