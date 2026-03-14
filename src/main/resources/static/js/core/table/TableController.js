"use strict"

import {getTable} from "../../components/form/table.js";

export class TableController {

    constructor(config){
        this.tableId = config.tableId;
        this.origin = config.origin;
        this.filters = config.filters || {};

        this.state = {};
    }

    set(key,value){
        this.state[key] = value;
    }

    reload(){
        let list = this.origin;

        for(const [key,value] of Object.entries(this.state)){
            if(value == null || value === "") continue;
            const filter = this.filters[key];

            if(filter){
                list = list.filter(v => filter(v,value));
            }
        }

        const table = getTable(this.tableId);
        table.load(list);
    }
}