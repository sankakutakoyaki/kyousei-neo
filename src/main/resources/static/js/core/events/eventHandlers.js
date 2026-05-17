"use strict"

import { handleEnterValidate } from "./actions/enterAction.js";
import { domActions } from "./actions/domActions.js";
import { resolveController } from "./controllerResolver.js";

let initialized = false;

export function initEvents(){
    if(initialized) return;
    initialized = true;

    document.addEventListener("click", handleEvent);
    document.addEventListener("input", handleEvent);
    document.addEventListener("change", handleEvent);
    document.addEventListener("focusin", handleFocus);
    document.addEventListener("keydown", handleEvent);
    document.addEventListener("keydown", handleEnterValidate);
}

// function handleEvent(e){
//     const el = e.target.closest("[data-action]");
//     if (!el){
//         return;
//     }

//     const action = el.dataset.action;
//     // DOM actions
//     if(domActions[action]){
//         domActions[action](el);
//         return;
//     }

//     const controller = resolveController(el);
//     if(!controller){
//         console.warn(
//             "controller not found",
//             el
//         );
//         return;
//     }

//     controller.executeAction(
//         action,
//         el
//     );
// }

function handleEvent(e){
    const el = e.target.closest("[data-action]");
    if(!el) return;

    const controller = resolveController(el);

    const actions = el.dataset.action?.split(/\s+/).filter(Boolean) ?? [];
    actions.forEach(action => {
        // DOM actions
        if(domActions[action]){
            domActions[action](e, el);
            return;
        }

        if(!controller){
            console.warn("controller not found", el);
            return;
        }

        controller.executeAction(action, el);
    });
}

function handleFocus(e){
    const el = e.target.closest("[data-action='select-on-focus']");
    if (!el) return;

    if(el.tagName !== "INPUT" && el.tagName !== "TEXTAREA"){
        return;
    }
    el.select();
}