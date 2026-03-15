"use strict"

export class TableModel {

    constructor(config){

        this.origin = null;

        this.state = {};
        this.filters = config.filters || {};
        this.requiredFilters = config.requiredFilters || [];

        this.sortKey = null;
        this.sortDir = "asc";

        this.page = 1;
        this.pageSize = config.pageSize || 50;

        this.result = [];
    }

    set(key,value){
        this.state[key] = value;
    }

    setSort(field,dir){
        this.sortKey = field;
        this.sortDir = dir;
    }

    compute(){

        for(const key of this.requiredFilters){
            if(!this.state[key]) {
                this.result = [];
                return;
            }
        }

        let list =
            typeof this.origin === "function"
            ? this.origin()
            : this.origin || [];

        list = this.applyFilter(list);
        list = this.applySort(list);
        list = this.applyPage(list);

        this.result = list;

    }

    applyFilter(list){

        for(const [key,value] of Object.entries(this.state)){

            if(value == null || value === "") continue;

            const filter = this.filters[key];
            if(!filter) continue;

            list = list.filter(v => filter(v,value));
        }

        return list;
    }

    applySort(list){

        if(!this.sortKey) return list;

        const key = this.sortKey;
        const dir = this.sortDir === "asc" ? 1 : -1;

        return [...list].sort((a,b)=>{

            let av = a[key];
            let bv = b[key];

            if(av == null) return 1;
            if(bv == null) return -1;

            const an = Number(av);
            const bn = Number(bv);

            if(!isNaN(an) && !isNaN(bn)){
                return (an - bn) * dir;
            }

            return String(av).localeCompare(String(bv)) * dir;

        });

    }

    applyPage(list){

        const start = (this.page - 1) * this.pageSize;
        return list.slice(start, start + this.pageSize);

    }

    getData(){
        return this.result;
    }

}

// export class TableModel {

//     constructor(config){
//         this.origin = typeof config.origin === "function" ? config.origin: () => config.origin;
//         this.filters = config.filters || {};
//         this.state = {};
//         this.view = [];
//     }

//     set(key,value){
//         this.state[key] = value;
//     }

//     compute(){
//         let list = this.origin();

//         for(const [key,value] of Object.entries(this.state)){
//             if(value == null || value === "") continue;
//             const filter = this.filters[key];
//             if(filter){
//                 list = list.filter(v => filter(v,value));
//             }
//         }

//         if(this.sortKey){
//             const key = this.sortKey;
//             const dir = this.sortDir === "asc" ? 1 : -1;

//             list = [...list].sort((a,b)=>{
//                 let av = a[key];
//                 let bv = b[key];
//                 if(av == null) return 1;
//                 if(bv == null) return -1;
//                 // 数値
//                 const an = Number(av);
//                 const bn = Number(bv);

//                 if(!isNaN(an) && !isNaN(bn)){
//                     return (an - bn) * dir;
//                 }
//                 // 日付
//                 const ad = Date.parse(av);
//                 const bd = Date.parse(bv);

//                 if(!isNaN(ad) && !isNaN(bd)){
//                     return (ad - bd) * dir;
//                 }
//                 // 文字
//                 return String(av).localeCompare(String(bv)) * dir;
//             });
//         }

//         this.view = list;
//     }

//     setSort(field,dir){
//         this.sortKey = field;
//         this.sortDir = dir;
//     }

//     getData(){
//         return this.view;
//     }

// }