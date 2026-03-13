"use strict"

export class TableStore {

    constructor() {
        this.data = [];
        this.filtered = [];
    }

    setData(list){
        this.data = list;
        this.filtered = list;
    }

    getData(){
        return this.filtered;
    }

    filter(fn){
        this.filtered = this.data.filter(fn);
    }

    reset(){
        this.filtered = this.data;
    }

}