"use strict"

import { loadPage } from "./core/dom/loadPage.js";

window.APP = {
    security: {
        csrfToken: document.querySelector('meta[name="_csrf"]').content,
        csrfHeader: document.querySelector('meta[name="_csrf_header"]').content
    },
    cache: {},
    cacheLoaded: false
};

export function initApp() {

    document.querySelectorAll(".hamburger-item")
        .forEach(item => {

            item.addEventListener("click", () => {

                if (item.classList.contains("selected")) return;

                const path = item.dataset.path;

                loadPage(path);
                hamburgerClose(); // 必要なら
            });
        });
}