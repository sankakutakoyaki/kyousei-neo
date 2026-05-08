"use strict"

export const parsers = {
    recycle(v){
        return String(v ?? "").replace(/\D/g, "");
    },
    phone(v){
        return String(v ?? "").replace(/\D/g, "");
    }
};