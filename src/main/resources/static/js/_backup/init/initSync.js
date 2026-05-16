// "use strict"

// export function initSync(){

//     document.addEventListener("change", (e) => {

//         const target = e.target;
//         if (!target.dataset.key) return;

//         const group = target.closest("[data-sync-group]")?.dataset.syncGroup;
//         if (!group) return;

//         const key = target.dataset.key;
//         const value = target.value;

//         const elements = document.querySelectorAll(
//             `[data-sync-group="${group}"] [data-key="${key}"]`
//         );

//         elements.forEach(el => {

//             if (el === target) return;

//             el.value = value;

//             if (el.tagName === "SELECT") {
//                 el.dispatchEvent(new Event("change"));
//             }
//         });
//     });
// }