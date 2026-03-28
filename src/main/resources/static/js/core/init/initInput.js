"use strict"

import { runAction } from "../../util/utils.js";

export function initInput(){
    document.addEventListener("input", handleInput);
    document.addEventListener("search", handleInput);
}

function handleInput(e){

    const el = e.target.closest("[data-action]");
    if(!el) return;

    const action = el.dataset.action;

    if(!["search","filter"].includes(action)) return;

    runAction(el, e);
}