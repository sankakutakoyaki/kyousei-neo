"use strict"

// import { actionMap } from "../core/map/actionMap.js";
// // import { getController } from "../core/map/controllerMap.js";
// import { getController } from "../core/init/initControllers.js";

export function updateField(el, value){

    const old = el.value;
    el.value = value;

    if(old !== value){
        el.dispatchEvent(new Event("change", { bubbles: true }));
    }
}