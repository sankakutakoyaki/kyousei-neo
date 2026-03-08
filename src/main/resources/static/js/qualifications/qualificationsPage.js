"use strict"

// import { initTable } from "./qualificationsTable.js";
// import { initForm } from "./qualificationsForm.js";
// import { initFile } from "./qualificationsFile.js";
import { initFilter } from "./qualificationsFilter.js";
import { CONFIG } from "./qualificationsConfig.js";

window.addEventListener("load", () => {

    // initTable(CONFIG);
    // initForm(CONFIG);
    // initFile(CONFIG);
    initFilter(CONFIG);
});

