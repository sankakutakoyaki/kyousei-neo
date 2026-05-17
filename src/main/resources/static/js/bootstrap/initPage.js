"use strict"

import { DataResolver } from "../core/behavior/DataResolver.js"
import { setEnterFocus } from "../core/form/components/enterfocus.js";
// import { init as initEnterFocus } from "../core/form/components/enterfocus.js";
import { initEvents } from "../core/events/eventHandlers.js";

export async function initCommon(){
    initEvents();
    setEnterFocus();
    // initEnterFocus();
    DataResolver.init();
}