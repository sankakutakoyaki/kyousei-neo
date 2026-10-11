"use strict"

import { DataResolver } from "../core/behavior/DataResolver.js"
import { setEnterFocus } from "../core/form/components/enterfocus.js";
import { initEvents } from "../core/events/eventHandlers.js";
import { getToday } from "../util/time.js";

export async function initCommon(){
    initEvents();
    setEnterFocus();
    applyDefaultValues();
    DataResolver.init();
}

function applyDefaultValues(root = document) {
    root.querySelectorAll("[data-default]").forEach(el => {
        if(el.value){return;}
        switch(el.dataset.default){
            case "today":
                el.value = getToday();
                break;
            case "current-month":
                el.value = getToday().slice(0, 7);
                break;
        }
    });
}