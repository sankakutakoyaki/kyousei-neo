"use strict"

export class TableStore {

    constructor() {
        this.data = [];
        this.filtered = [];
        this.filters = [];
    }

    setData(list){
        this.data = list ?? [];
        this.apply();
    }

    getData(){
        return this.filtered;
    }

    setFilters(filters){
        this.filters = filters ?? [];
        this.apply();
    }

    apply(){
        let list = this.data;
        for(const fn of this.filters){
            list = list.filter(fn);
        }
        this.filtered = list;
    }

    reset(){
        this.filtered = this.data;
        this.filters = [];
    }
}