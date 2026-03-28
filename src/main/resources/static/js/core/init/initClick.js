"use strict"

import { runAction } from "../../util/utils.js";

export function initClick(){
    document.addEventListener("click", handleClick);
}

function handleClick(e){

    const el = e.target.closest("[data-action]");
    if(!el) return;

    const action = el.dataset.action;

    // click対象だけ許可
    if(!["tab","create","delete","download","reload"].includes(action)) return;

    runAction(el, e);
}