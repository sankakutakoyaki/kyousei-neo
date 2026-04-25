"use strinct"

import { filterFactory } from "./filterFactory.js";

export function initParentChildLink() {

    document.querySelectorAll("[data-link-child]").forEach(parent => {
        const childId = parent.dataset.linkChild;
        const child = document.getElementById(childId);
        if (!child) return;

        const filterFn = filterFactory.parent("parent");
        parent.addEventListener("change", () => {
            applyFilter(child, filterFn, parent.value);
        });
        parent.dispatchEvent(new Event("change"));
    });
}

function applyFilter(select, filterFn, value) {

    Array.from(select.options).forEach(opt => {
        if (opt.value === "") {
            opt.hidden = false;
            return;
        }
        const visible = filterFn(opt, value);
        opt.hidden = !filterFn(opt, value);
    });

    select.value = "";
}