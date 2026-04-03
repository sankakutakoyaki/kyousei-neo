"use strict"

// import { runAction } from "../../util/utils.js";
import { dispatchAction } from "../../util/actionDispatcher.js";

export function initChange(){
    document.addEventListener("change", dispatchAction);
}

// function handleChange(e){

//     const el = e.target.closest("[data-action]");
//     if(!el) return;

//     const action = el.dataset.action;

//     if(!["filter"].includes(action)) return;

//     runAction(el, e);
// }