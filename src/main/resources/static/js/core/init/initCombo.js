"use strict"

import { createComboBox } from "../../core/form/components/combo.js";

export function initCombo(controller){

    function render(){
        const selects = document.querySelectorAll(
            `[data-controller="${controller.key}"] [data-combo]`
        );

        selects.forEach(select => {

            const listName = select.dataset.combo;
            const list = APP.cache.page[listName];
            if (!list) return;

            createComboBox({
                area: select,
                items: list,
                text: select.dataset.top
            });
            applyChildDefault(select);
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

function applyChildDefault(select){

    const type = select.dataset.childDefault;
    if (!type) return;

    const parentName = select.dataset.parentName;
    if (!parentName) return;

    if (type === "empty") {
        Array.from(select.options).forEach(opt => {

            if (opt.value === "") {
                opt.hidden = false;
            } else {
                opt.hidden = true;
            }

        });

        select.value = "";
    }
}