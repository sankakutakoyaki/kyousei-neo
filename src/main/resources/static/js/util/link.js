"use strinct"

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
            });
            parent.dataset.linkInitialized = "true";
        }
        parent.dispatchEvent(new Event("change"));
        parent.addEventListener("change", () => {
            const comboName = child.dataset.combo;
            const allItems = APP.cache.page[comboName] ?? [];
            const items = allItems.filter(
                x => String(x.parent) === String(parent.value)
            );
            createComboBox({
                area: child,
                items,
                text: "-----"
            });
            // 孫へ連鎖
            if (child.dataset.linkChild) {
                child.dispatchEvent(new Event("change"));
            }
        });
    });
}