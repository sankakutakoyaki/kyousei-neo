"use strict"

import { initFilter } from "./qualificationsFilter.js";
import { CONFIG } from "./qualificationsConfig.js";
import { initTable } from "./qualificationsTable.js";
import { UiEngine } from "/js/common/core/uiEngine.js";

window.addEventListener("load", () => {

    initFilter(CONFIG);
    initTable(CONFIG);

    UiEngine.init();
});

