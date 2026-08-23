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
    // null → 0
    if(v == null && opt.zeroIfNull){
        v = 0;
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
        zeroToNull: "zeroToNull" in el.dataset,
        zeroIfNull: "zeroIfNull" in el.dataset
    };
}

// export function normalize(v){
//     if(
//         v === "" ||
//         v === 0 ||
//         v === "0"
//     ){
//         return null;
//     }
//     return v;
// }
export function normalize(v){
    // null / undefined / 空文字
    if(v == null || v === ""){
        return null;
    }

    // 数値文字列を数値に統一
    if(typeof v === "string"){
        const value = v.trim();

        if(value === ""){
            return null;
        }

        // 数値文字列
        const numeric = value.replace(/,/g, "");
        if(!Number.isNaN(Number(numeric))){
            return Number(numeric);
        }
        // if(!Number.isNaN(Number(value))){
        //     return Number(value);
        // }

        return value;
    }

    return v;
}