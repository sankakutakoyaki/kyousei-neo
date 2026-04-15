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
export function initCombo(){

    function render(){
        const selects = document.querySelectorAll("[data-combo]");

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

    // 初期描画
    render();

    return {
        reload: render
    };
}