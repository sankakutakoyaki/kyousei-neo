"use strict"

import { initMobileReadOnly } from "./core/access/mobileReadOnly.js";
import { initSessionKeepAlive } from "./core/security/sessionKeepAlive.js";
import { loadPage } from "./core/dom/loadPage.js";
import { initHamburger } from "./core/ui/menu/hamburger.js";
import { closeHamburger } from "./core/ui/menu/hamburger.js";
import { DialogService } from "./core/ui/dialog/DialogService.js";

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
    initMobileReadOnly();
    initHamburger();
    
    // 営業所アカウントの場合だけセッション維持を開始
    initSessionKeepAlive();

    document.addEventListener("click", (e) => {
        const item = e.target.closest(".hamburger-item") || e.target.closest("[data-link]");
        if (!item) return;

        const path = item.dataset.path || item.dataset.link;
        const target = item.dataset.target || "body";

        loadPage(path, target).catch(error => DialogService.error(error.message));
        closeHamburger();
    });
}
