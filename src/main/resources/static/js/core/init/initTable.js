// "use strict"

// import { getTableConfig } from "../map/tableConfigMap.js";
// import { DataTable } from "../table/DataTable.js";

// const tables = new Map();

// export function initTable(){
//     const list = document.querySelectorAll("[data-table]");

//     list.forEach(async el => {
//         const name = el.dataset.table;
//         const config = getTableConfig(name);

//         const table = new DataTable(config);

//         tables.set(table.config.tableId, table);
//         await table.initData();
//     });

// }

// export function getTable(id){
//     const table = tables.get(id);

//     if(!table){
//         console.error("table not found:", id);
//     }
//     return table;
// }