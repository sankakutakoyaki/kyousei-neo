"use strict"

import { dispatchAction } from "./actionDispatcher.js";

export function initEvents(){

    document.addEventListener("click", handleEvent);
    document.addEventListener("input", handleEvent);
    document.addEventListener("change", handleEvent);
    document.addEventListener("focusin", handleFocus);
}

function handleEvent(e){
    const el = e.target.closest("[data-action]");
    if (!el) return;

    dispatchAction(e);
}