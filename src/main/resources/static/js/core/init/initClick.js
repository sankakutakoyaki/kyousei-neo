"use strict"

// import { runAction } from "../../util/utils.js";
import { dispatchAction } from "../../util/actionDispatcher.js";

export function initClick(){
    document.addEventListener("click", dispatchAction);
}

// function handleClick(e){

//     const el = e.target.closest("[data-action]");
//     if(!el) return;

//     const action = el.dataset.action;

//     // click対象だけ許可
//     if(!["tab","create","delete","download","reload"].includes(action)) return;

//     runAction(el, e);
// }