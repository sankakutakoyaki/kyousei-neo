// "use strict"

// import { getController } from "../map/controllerMap.js";
// import { actionMap } from "../map/actionMap.js";

// export function initSearch(){
//     // document.addEventListener("input", handleSearch);
//     document.addEventListener("search", handleSearch);
// }

// function handleSearch(e){

//     const el = e.target.closest("[data-action]");
//     if(!el) return;

//     const action = el.dataset.action;

//     // if(action !== "filter" && action !== "search") return;
//     if(action !== "search") return;

//     const fn = actionMap[action];
//     if(!fn) return;

//     const target = el.dataset.target;
//     const controller = target ? getController(target) : null;

//     fn(controller, el, e);
// }