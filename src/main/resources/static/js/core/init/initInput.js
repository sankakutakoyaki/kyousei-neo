"use strict"

// import { runAction } from "../../util/utils.js";
import { dispatchAction } from "../../util/actionDispatcher.js";

export function initInput(){
    document.addEventListener("input", dispatchAction);
    document.addEventListener("search", dispatchAction);
}

// function handleInput(e){

//     const el = e.target.closest("[data-action]");
//     if(!el) return;

//     const action = el.dataset.action;

//     if(!["search","filter"].includes(action)) return;
//     if(!["filter"].includes(action)) return;

//     runAction(el, e);
// }
// function handleInput(e){

//     const el = e.target.closest("[data-action]");
//     if(!el) return;

//     runAction(el, e);
// }-