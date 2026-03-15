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

// export const filterFactory = {
//     keyword(){
//         return (v,value)=>{
//             if(!value) return true;

//             const words = value.trim().split(/\s+/);
//             const text = v.__search ?? (v.__search = Object.values(v).join(" "));
//             return words.every(w => text.includes(w));
//         };
//     },

//     eq(field){
//         return (v,value)=> v[field] === value;
//     },

//     includes(field){
//         return (v,value)=>{
//             if(!value) return true;
//             return String(v[field]).includes(value);
//         };
//     },

//     in(field){
//         return (v,value)=>{
//             if(!value || value.length === 0) return true;
//             return value.includes(v[field]);
//         };
//     },

//     range(field){
//         return (v,value)=>{
//             if(!value) return true;

//             const min = value.min ?? -Infinity;
//             const max = value.max ?? Infinity;

//             return v[field] >= min && v[field] <= max;
//         };
//     }

// };