"use strict"

import { dispatchAction } from "./actionDispatcher.js";

let initialized = false;

export function initEvents(){
    if(initialized) return;
    initialized = true;

    document.addEventListener("click", handleEvent);
    document.addEventListener("input", handleEvent);
    document.addEventListener("change", handleEvent);
    document.addEventListener("keydown", handleEvent);
    document.addEventListener("focusin", handleFocus);
}

function handleEvent(e){
    const el = e.target.closest("[data-action]");
    if (!el) return;

    dispatchAction(e);
}

function handleFocus(e){
    const el = e.target.closest("[data-action='select-on-focus']");
    if (!el) return;

    if(el.tagName !== "INPUT" && el.tagName !== "TEXTAREA") return;
    el.select();
}