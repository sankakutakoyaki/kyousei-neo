// "use strict"

// import { getController } from "../../core/controllerMap";

// export function init(){
//     document.addEventListener("click", (e) => {
//         const btn = e.target.closest("[data-action]");
//         if(!btn) return;

//         const container = btn.closest("[data-area='kebab']");
//         if(!container) return;

//         const action = btn.dataset.action;
//         const target = btn.dataset.target;

//         const controller = getController(target);
//         if(!controller) return;

//         switch(action){
//             case "create":
//                 controller.create(btn.dataset.form);
//                 break;
//             case "delete":
//                 controller.deleteSelected();
//                 break;
//             case "download":
//                 controller.downloadSelected();
//                 break;
//             case "reload":
//                 controller.refresh();
//                 break;
//         }
//     });
// }