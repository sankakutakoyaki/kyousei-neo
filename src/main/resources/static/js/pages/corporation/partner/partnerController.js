"use strict"

import {TableController} from "../../../core/table/TableController.js";

export const partnerController = new TableController({
    tableId:"table-02",
    origin: APP.cache.employeeOrigin,
    filters:{
        companyId:(v,value)=> v.companyId === value,
        keyword:()=> {
            const table = getTable("table-01");
            const copy = structuredClone(APP.cache.companyOrigin);
            table.search("search-box-01", copy);
        }
    }
});