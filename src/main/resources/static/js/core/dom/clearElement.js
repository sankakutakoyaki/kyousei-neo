"use strict"

export function clearElement(elOrId, isAll = false){

    const el = elOrId instanceof HTMLElement ? elOrId: document.getElementById(elOrId);
    if(!el) return;

    el.replaceChildren();
    
    if(isAll) el.remove();
}