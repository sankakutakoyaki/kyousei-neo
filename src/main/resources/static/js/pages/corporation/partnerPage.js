"use strict"

import {initPage} from "/js/core/initPage.js";
import {partnerConfig} from "/js/config/corporation/partnerConfig.js";
import {TableEngine} from "/js/core/table/TableEngine.js";
import {createPartnerRow} from "./partnerTable.js";

let table;

window.addEventListener("load", async () => {

    initPage(partnerConfig);

    table = new TableEngine({
        tableId:"table-01",
        createRow:createPartnerRow
    });
    const res = await fetch("/api/client/get/list");
    const data = await res.json();

    table.load(data.data);
});