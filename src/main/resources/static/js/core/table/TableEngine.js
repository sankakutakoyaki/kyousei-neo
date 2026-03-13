"use strict"

import {TableStore} from "./TableStore.js";
import {renderTable} from "./tableRender.js";

export class TableEngine {
    constructor(config){
        this.config = config;
        this.tableEl = document.getElementById(config.tableId);
        this.store = new TableStore();
    }

    load(list){
        this.store.setData(list);
        this.render();
    }

    render(){
        renderTable(
            this.tableEl,
            this.config,
            this.store.getData(),
            this
        );
    }

    refresh(){
        this.render();
    }
}