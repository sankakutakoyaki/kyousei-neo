"use strict"

export function normalizeValue(v, opt = {}){

    // trim
    if(typeof v === "string" && opt.trim !== false){
        v = v.trim();
    }

    // 空 → null
    if(v === ""){
        v = null;
    }

    // number
    if(opt.number && v !== null){
        v = Number(v);
    }

    // zeroToNull
    if(opt.zeroToNull && (v === 0 || v === "0")){
        v = null;
    }

    return v;
}

export function getOptions(el){
    return {
        trim: !("noTrim" in el.dataset),
        number: "number" in el.dataset,
        zeroToNull: "zeroToNull" in el.dataset
    };
}

export function normalize(v){
    return v === "" || v === undefined ? null : v;
}