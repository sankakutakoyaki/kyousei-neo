"use strict"

import { actionMap } from "../core/map/actionMap.js";
import { getController } from "../core/map/controllerMap.js";

export function runAction(el, e){

    const action = el.dataset.action;

    const fn = actionMap[action];
    if(!fn) return;

    const target = el.dataset.target;
    const controller = target ? getController(target) : null;

    fn(controller, el, e);
}

export function updateField(el, value){

    const old = el.value;
    el.value = value;

    if(old !== value){
        el.dispatchEvent(new Event("change", { bubbles: true }));
    }
}