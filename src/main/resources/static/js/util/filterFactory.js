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
    }
};