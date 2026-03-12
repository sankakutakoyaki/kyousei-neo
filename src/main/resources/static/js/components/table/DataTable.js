"use strict"

import {renderTable} from "./tableRender.js";
import {sortRows} from "../../core/table/tableSort.js";
import {filterRows} from "../../core/table/tableFilter.js";
import {selectRow} from "../../core/table/tableSelect.js";

export class TableEngine{

    constructor(config){
        this.config = config;
        this.tableEl = document.getElementById(config.tableId);
        this.data = [];
    }

    async load(data){
        this.data = data;
        renderTable(
            this.tableEl,
            this.config,
            this.data,
            this
        );
    }

    sort(field){
        this.data = sortRows(this.data, field);
        this.refresh();
    }

    filter(keyword){
        const filtered = filterRows(this.data, keyword);
        renderTable(
            this.tableEl,
            this.config,
            filtered,
            this
        );
    }

    select(index){
        selectRow(this.tableEl,index);
    }

    refresh(){
        renderTable(
            this.tableEl,
            this.config,
            this.data,
            this
        );
    }
}