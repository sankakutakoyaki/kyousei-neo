// "use strict"

// import { getController } from "../map/controllerMap.js";
// import { actionMap } from "../map/actionMap.js";

// export function initAction(){
//     document.addEventListener("click", handleAction);
//     document.addEventListener("change", handleAction);
// }

// function handleAction(e){

//     const el = e.target.closest("[data-action]");
//     if(!el) return;

//     const action = el.dataset.action;

//     const fn = actionMap[action];
//     if(!fn) return;

//     const target = el.dataset.target;
//     const controller = target ? getController(target) : null;

//     fn(controller, el, e);
// }