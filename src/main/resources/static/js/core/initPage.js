"use strict"

import { getController } from "./map/controllerMap.js";
import { actionMap } from "./map/actionMap.js";
import { init as initTable } from "./ui/table.js";
import { init as initCombo } from "./initCombo.js";

export function initPage(){

    initTable();
    initCombo();

    document.addEventListener("input", handleAction);
    document.addEventListener("click", handleAction);
    document.addEventListener("search", handleAction);
    document.addEventListener("focusin", handleAction);

}

function handleAction(e){

    const el = e.target.closest("[data-action]");
    if(!el) return;

    const action = el.dataset.action;

    const fn = actionMap[action];
    if(!fn) return;

    const target = el.dataset.target;
    const controller = target ? getController(target) : null;

    fn(controller, el, e);
}