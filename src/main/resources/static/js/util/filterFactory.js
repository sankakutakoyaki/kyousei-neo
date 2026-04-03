"use strict"

export const filterFactory = {

    keyword(){
        return (v,value)=>{
            const words = value.trim().split(/\s+/);
            const text =
                v.__search ??
                (v.__search = Object.values(v).join(" ").toLowerCase());
            return words.every(w =>
                text.includes(w.toLowerCase())
            );
        };
    },

    equals(field){
        return (v,value)=> v[field] == value;
    },

    between(field){
        return (v, value) => {
            return v[field] >= value.min && v[field] <= value.max;
        };
    },

    // 複数選択
    in(field){
        return (v, values) => {
            if(!values || values.length === 0) return true;
            return values.includes(v[field]);
        };
    },

    // 部分一致
    contains(field){
        return (v, value) => {
            if(!value) return true;
            return String(v[field] ?? "")
                .toLowerCase()
                .includes(value.toLowerCase());
        };
    },

    // 前方一致
    startsWith(field){
        return (v, value) => {
            if(!value) return true;
            return String(v[field] ?? "")
                .toLowerCase()
                .startsWith(value.toLowerCase());
        };
    },

    // 日付範囲
    dateRange(field){
        return (v, value) => {
            if(!value) return true;

            const d = new Date(v[field]);
            const from = value.from ? new Date(value.from) : null;
            const to = value.to ? new Date(value.to) : null;

            if(from && d < from) return false;
            if(to && d > to) return false;

            return true;
        };
    },

    gte(field){
        return (v, value) => v[field] >= value;
    },

    lte(field){
        return (v, value) => v[field] <= value;
    },

    custom(fn){
        return (v, value) => fn(v, value);
    }
};