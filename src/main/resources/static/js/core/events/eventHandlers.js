"use strict"

// import { dispatchAction } from "./actionDispatcher.js";
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
    document.addEventListener("keydown", handleEnterValidate);
    document.addEventListener("focusin", handleFocus);
}

function handleEvent(e){
    const el = e.target.closest("[data-action]");
    if (!el){
        return;
    }

    const action = el.dataset.action;
    // DOM actions
    if(domActions[action]){
        domActions[action](el);
        return;
    }

    const controller = resolveController(el);
    if(!controller){
        console.warn(
            "controller not found",
            el
        );
        return;
    }

    controller.executeAction(
        action,
        el
    );
}

function handleFocus(e){
    const el = e.target.closest("[data-action='select-on-focus']");
    if (!el) return;

    if(el.tagName !== "INPUT" && el.tagName !== "TEXTAREA"){
        return;
    }
    el.select();
}

// "use strict"

// import { dispatchAction } from "./actionDispatcher.js";
// import { handleEnterValidate } from "./actions/enterAction.js";
// import { domActions } from "./actions/domActions.js";
// import { resolveController } from "./controllerResolver.js";

// let initialized = false;

// export function initEvents(){
//     if(initialized) return;
//     initialized = true;

//     document.addEventListener("click", handleEvent);
//     document.addEventListener("input", handleEvent);
//     document.addEventListener("change", handleEvent);
//     // document.addEventListener("keydown", handleEvent);
//     document.addEventListener("keydown", handleEnterValidate);
//     document.addEventListener("focusin", handleFocus);
// }

// // function handleEvent(e){
// //     const el = e.target.closest("[data-action]");
// //     if (!el) return;

// //     console.log({
// //         action: el.dataset.action,
// //         controller: el.dataset.controller,
// //         el
// //     });

// //     // dispatchAction(e);
// //     dispatchAction({
// //         target: el.dataset.controller,
// //         action: el.dataset.action,
// //         payload: el
// //     });
// // }
// function handleEvent(e){
//     const el = e.target.closest("[data-action]");
//     if (!el){
//         return;
//     }

//     const action = el.dataset.action;
//     if(domActions[action]){
//         domActions[action](null, el);
//         return;
//     }

//     const controller = resolveController(el);
//     if(!controller){
//         console.warn("controller not found", el);
//         return;
//     }

//     controller.executeAction(action, el);
// }

// function handleFocus(e){
//     const el = e.target.closest("[data-action='select-on-focus']");
//     if (!el) return;

//     if(el.tagName !== "INPUT" && el.tagName !== "TEXTAREA") return;
//     el.select();
// }