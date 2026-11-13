"use strict"

export const parsers = {
    recycle(v){
        return String(v ?? "").replace(/\D/g, "");
    },
    phone(v){
        return String(v ?? "").replace(/\D/g, "");
    },
    // 数値の桁区切りカンマを除去して送信用の値に変換
    number(v){
        return String(v ?? "").replace(/,/g, "");
    }
};