"use strict"

import {DataTable} from "../../core/table/DataTable.js";

export const tables = new Map();

export async function init(configs){
    for(const config of configs){
        if(tables.has(config.tableId)){
            console.warn("table already exists:", config.tableId);
        }

        const table = new DataTable(config);
        tables.set(config.tableId, table);
        await table.initData();
    }

}

export function getTable(id){
    const table = tables.get(id);

    if(!table){
        console.error("table not found:", id);
    }
    return table;
}


// export async function init(configs) {
//     for(const config of configs){
//         const table = new DataTable(config);
//         tables.set(config.tableId, table);

//         const ds = config.dataSource;
//         if(!ds) continue;

//         if(ds.type === "api"){
//             const res = await api.get(ds.url);
//             table.load(res.data);
//         }

//         if(ds.type === "origin"){
//             table.load(ds.data);
//         }
//     }   
// }