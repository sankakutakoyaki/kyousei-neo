"use strict"

export const formatters = {
    recycle: (v) => {
        const raw = String(v ?? "").replace(/\D/g, "");
        if(raw.length !== 13){
            return raw;
        }
        return (
            raw.slice(0,4) +
            "-" +
            raw.slice(4,12) +
            "-" +
            raw.slice(12)
        );
    }
};