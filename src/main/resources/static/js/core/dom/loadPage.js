"use strict"

import { initMenuActive } from "../init/initMenuActive.js";

export async function loadPage(url) {

    if (APP.currentPage === url) {
        return; // ★ 二重防御
    }
    APP.currentPage = url;

    APP.cache.common = {};
    APP.cache.page = {};

    const res = await fetch(url);
    const html = await res.text();

    const parser = new DOMParser();
    const doc = parser.parseFromString(html, "text/html");

    const newBody = doc.querySelector(".normal-body");
    const oldBody = document.querySelector(".normal-body");

    // ★ ここが超重要
    oldBody.replaceWith(newBody);
    initMenuActive(url);

    const cssPath = newBody.dataset.css;
    const modulePath = newBody.dataset.page;

    if (cssPath) {
        await replaceCss(cssPath);
    }

    if (modulePath) {
        const module = await import(modulePath);
        await module.init?.();
    }
}

async function replaceCss(href) {

    // ① 前のページCSS削除
    document.querySelectorAll('link[data-page-css]')
        .forEach(el => el.remove());

    // ② 新しいCSS追加
    const link = document.createElement("link");
    link.rel = "stylesheet";
    link.href = href;
    link.dataset.pageCss = "true";

    document.head.appendChild(link);
}