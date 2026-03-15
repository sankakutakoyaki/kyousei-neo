"use strict"

export class TableModel {

    constructor(config){
        this.origin = typeof config.origin === "function" ? config.origin: () => config.origin;
        this.filters = config.filters || {};
        this.state = {};
        this.view = [];
    }

    set(key,value){
        this.state[key] = value;
    }

    compute(){
        let list = this.origin();

        for(const [key,value] of Object.entries(this.state)){
            if(value == null || value === "") continue;
            const filter = this.filters[key];
            if(filter){
                list = list.filter(v => filter(v,value));
            }
        }

        if(this.sortKey){
            const key = this.sortKey;
            const dir = this.sortDir === "asc" ? 1 : -1;

            list = [...list].sort((a,b)=>{
                let av = a[key];
                let bv = b[key];
                if(av == null) return 1;
                if(bv == null) return -1;
                // 数値
                const an = Number(av);
                const bn = Number(bv);

                if(!isNaN(an) && !isNaN(bn)){
                    return (an - bn) * dir;
                }
                // 日付
                const ad = Date.parse(av);
                const bd = Date.parse(bv);

                if(!isNaN(ad) && !isNaN(bd)){
                    return (ad - bd) * dir;
                }
                // 文字
                return String(av).localeCompare(String(bv)) * dir;
            });
        }

        this.view = list;
    }

    setSort(field,dir){
        this.sortKey = field;
        this.sortDir = dir;
    }

    getData(){
        return this.view;
    }

}