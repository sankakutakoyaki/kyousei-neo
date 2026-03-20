"use strict"

import {getTable} from "../../components/form/table.js";

export class TableController {

    constructor(config){
        this.tableId = config.tableId;
        this.state = {};
    }

    set(key,value){
        this.state[key] = value;
        const table = getTable(this.tableId);
        table.set(key,value);
        table.reload();
    }
}