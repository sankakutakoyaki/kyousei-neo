"use strict"

import { createComboBox } from "../../core/form/components/combo.js";

// export function initCombo(){

//     const selects = document.querySelectorAll("[data-combo]");

//     selects.forEach(select => {
//         const listName = select.dataset.combo;
//         const list = APP.cache.page[listName];
//         if (!list) return;

//         createComboBox({area:select, items:list, text:select.dataset.top});
//     });
// }
export function initCombo(controller){

    function render(){
        const selects = document.querySelectorAll(
            `[data-controller="${controller.key}"] [data-combo]`
        );
        // const selects = document.querySelectorAll('[data-combo]');

        selects.forEach(select => {

            const listName = select.dataset.combo;
            const list = APP.cache.page[listName];
            if (!list) return;

            createComboBox({
                area: select,
                items: list,
                text: select.dataset.top
            });
        });
    }

    function clear(){
        const selects = document.querySelectorAll(
            `[data-controller="${controller.key}"] [data-combo]`
        );

        selects.forEach(select => {
            select.value = "";
        });
    }

    render();

    return {
        reload: render,
        clear
    };
}