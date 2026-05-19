"use strinct"

// import { filterFactory } from "./filterFactory.js";
import { createComboBox } from "../core/form/components/combo.js";

export function initParentChildLink(area = document){
    area.querySelectorAll("[data-link-child]").forEach(parent => {
        const childId = parent.dataset.linkChild;
        const child = document.getElementById(childId);
        if(!child) return;

        if(!parent.dataset.linkInitialized){
            parent.addEventListener("change", () => {
                const comboName = child.dataset.combo;
                const allItems = APP.cache.page[comboName] ?? [];
                const items = allItems.filter(x => String(x.parent) === String(parent.value));
                createComboBox({area: child, items, text: "-----"});
                // child.value = "0";
            });
            parent.dataset.linkInitialized = "true";
        }
        parent.dispatchEvent(new Event("change"));
    });
}

// export function initParentChildLink(area = document) {
//     area.querySelectorAll("[data-link-child]").forEach(parent => {
//         const childId = parent.dataset.linkChild;
//         const child = document.getElementById(childId);
//         if (!child) return;

//         const filterFn = filterFactory.parent("parent");

//         if(!parent.dataset.linkInitialized){
//             parent.addEventListener("change", () => {
//                 applyFilter(child, filterFn, parent.value);
//             });
//             parent.dataset.linkInitialized = "true";
//         }
//         parent.dispatchEvent(new Event("change"));
//     });
// }

// function applyFilter(select, filterFn, value){
//     Array.from(select.options).forEach(opt => {
//         if(opt.value === "0"){
//             opt.hidden = false;
//             return;
//         }
//         opt.hidden = !filterFn(opt, value);
//     });
//     const exists = [...select.options].some(opt => !opt.hidden && opt.value === select.value);
//     if(!exists){select.value = "0";}
// }
// // function applyFilter(select, filterFn, value) {

// //     Array.from(select.options).forEach(opt => {
// //         if (opt.value === "0") {
// //             opt.hidden = false;
// //             return;
// //         }
// //         const visible = filterFn(opt, value);
// //         opt.hidden = !filterFn(opt, value);
// //     });

// //     select.value = "";
// // }