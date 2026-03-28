"use strict"

export function initFocus(){
    document.addEventListener("focusin", handleFocus);
}

function handleFocus(e){

    const el = e.target.closest("[data-action='select-on-focus']");
    if(!el) return;

    el.select();
}