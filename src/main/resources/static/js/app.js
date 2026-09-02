"use strict"

import { loadPage } from "./core/dom/loadPage.js";
import { initHamburger } from "./core/ui/menu/hamburger.js";
import { closeHamburger } from "./core/ui/menu/hamburger.js";
import { initPushSubscription } from "./core/push/subscription.js";

window.APP = {
    security: {
        csrfToken: document.querySelector('meta[name="_csrf"]').content,
        csrfHeader: document.querySelector('meta[name="_csrf_header"]').content
    },
    cache: {},
    cacheLoaded: false
};

document.addEventListener("DOMContentLoaded", async () => {
    initApp();
});

let initialized = false;

export function initApp() {
    if (initialized) return;
    initialized = true;
    initHamburger();
    
    document.addEventListener("click", (e) => {
        const item = e.target.closest(".hamburger-item") || e.target.closest("[data-link]");
        if (!item) return;

        if (item.classList.contains("selected")) return;

        const path = item.dataset.path || item.dataset.link;
        const target = item.dataset.target || "body";

        loadPage(path, target);
        closeHamburger();
    });
}
