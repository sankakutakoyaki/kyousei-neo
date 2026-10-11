"use strict"

import { createComboBox } from "../core/form/components/combo.js";
import { PageCacheService } from "../services/cache/PageCacheService.js";

export function initCombo(controller){
    function render(){
        const selects = document.querySelectorAll(
            `[data-controller="${controller.key}"] [data-combo]`
        );
        selects.forEach(select => {
            const listName = select.dataset.combo;
            const list = PageCacheService.get(listName);
            if (!list) return;
            createComboBox({
                area: select,
                items: list,
                text: select.dataset.top ?? null,
                emptyOption: "emptyOption" in select.dataset
            });
            applyChildDefault(select);
            applyLoginOffice(select);
        });
    }
    function clear(){
        const selects = document.querySelectorAll(`[data-controller="${controller.key}"] [data-combo]`);
        selects.forEach(select => {select.value = "";});
    }
    render();
    return {reload: render, clear};
}

function applyChildDefault(select){
    const type = select.dataset.childDefault;
    if (!type) return;
    const parentName = select.dataset.parentName;
    if (!parentName) return;
    if (type === "empty") {
        Array.from(select.options).forEach(opt => {opt.hidden = opt.value !== "";});
        select.value = "";
    }
}

function applyLoginOffice(select){
    if(!("loginOffice" in select.dataset)){return;}
    // 既に営業所が選択されている場合は変更しない
    if(select.value && select.value !== "0"){return;}
    const loginOfficeId = PageCacheService.get("loginOfficeId");
    if(!loginOfficeId){return;}
    const value = String(loginOfficeId);
    const exists = Array.from(select.options).some(
        option => String(option.value) === value
    );
    if(!exists){return;}
    select.value = value;
}