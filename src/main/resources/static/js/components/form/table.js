"use strict"

import {DataTable} from "../../core/table/DataTable.js";
import {api} from "../../core/api/apiService.js";

export const tables = new Map();

export async function init(configs) {
    for(const config of configs){
        const table = new DataTable(config);
        tables.set(config.tableId, table);

        const ds = config.dataSource;
        if(!ds) continue;

        if(ds.type === "api"){
            const res = await api.get(ds.url);
            table.load(res.data);
        }

        if(ds.type === "origin"){
            table.load(ds.data);
        }
    }   
}

export function getTable(id){
    return tables.get(id);
}