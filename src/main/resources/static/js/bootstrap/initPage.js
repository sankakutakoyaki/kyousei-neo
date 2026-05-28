"use strict"

import { DataResolver } from "../core/behavior/DataResolver.js"
import { setEnterFocus } from "../core/form/components/enterfocus.js";
import { initEvents } from "../core/events/eventHandlers.js";
import { bindFormatters } from "../core/behavior/bindFormatter.js";

export async function initCommon(){
    initEvents();
    setEnterFocus();
    DataResolver.init();
    bindFormatters();
}