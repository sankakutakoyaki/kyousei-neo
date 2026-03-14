"use strict"

import {TableController} from "../../../core/table/TableController.js";
import { getTable } from "../../../components/form/table.js";

export const partnerController = new TableController({
    tableId:"table-01",
    origin: APP.cache.companyOrigin,
    filters:{
        companyId:(v,value)=> v.companyId === value,
        keyword:(v,value)=> v.name.includes(value)
        // keyword:()=> {
        //     const table = getTable("table-01");
        //     const copy = structuredClone(APP.cache.companyOrigin);
        //     table.search("search-box-01", copy);
        // }
    }
});