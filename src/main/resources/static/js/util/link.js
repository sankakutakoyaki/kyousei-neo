"use strinct"

import { filterFactory } from "./filterFactory.js";

export function initParentChildLink(area = document) {

    area.querySelectorAll("[data-link-child]").forEach(parent => {
        const childId = parent.dataset.linkChild;
        const child = document.getElementById(childId);
        if (!child) return;

        const filterFn = filterFactory.parent("parent");

        if(!parent.dataset.linkInitialized){
            parent.addEventListener("change", () => {
                applyFilter(child, filterFn, parent.value);
            });
            parent.dataset.linkInitialized = "true";
        }
        parent.dispatchEvent(new Event("change"));
    });
}

function applyFilter(select, filterFn, value) {

    Array.from(select.options).forEach(opt => {
        if (opt.value === "0") {
            opt.hidden = false;
            return;
        }
        const visible = filterFn(opt, value);
        opt.hidden = !filterFn(opt, value);
    });

    select.value = "";
}