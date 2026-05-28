"use strict"

import { getController } from "../../../applcation/controllerRegistry.js";

export function handleTab(event, el){

    const group = el.dataset.group;
    const targetId = el.dataset.tab;

    if(!group || !targetId){
        console.warn("tab missing data:", el);
        return;
    }

    document.querySelectorAll(`[data-group="${group}"]`).forEach(tab => tab.classList.remove("is-active"));
    el.classList.add("is-active");
    document.querySelectorAll(`[data-tab-content="${group}"]`).forEach(panel => panel.classList.remove("is-show"));

    const target = document.getElementById(targetId);
    if(target){
        target.classList.add("is-show");
        const area = target.querySelector("[data-controller]");
        const name = area?.dataset.controller;
        const controller = getController(name);
        controller?.updateButtons();
    }
}