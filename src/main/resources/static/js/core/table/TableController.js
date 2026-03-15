"use strict"

import {getTable} from "../../components/form/table.js";
import { filterFactory } from "../filter/filterFactory.js";

export class TableController {

    constructor(config){

        this.tableId = config.tableId;
        this.origin = config.origin;
        this.state = {};

        this.filters = {};

        for(const [key,val] of Object.entries(config.filters || {})){
            if(typeof val === "string"){
                this.filters[key] = filterFactory[val](key);
            }
            else if(val === true){
                this.filters[key] = filterFactory[key]();
            }
            else if(typeof val === "function"){
                this.filters[key] = val;
            }
        }
    }

    set(key,value){
        this.state[key] = value;
    }

    reload(){
        let list = typeof this.origin === "function" ? this.origin(): this.origin;
        for(const [key,value] of Object.entries(this.state)){
            if(value == null || value === "") continue;

            const filter = this.filters[key];
            if(filter){
                list = list.filter(v => filter(v,value));
            }
        }
        getTable(this.tableId).load(list);
    }
}