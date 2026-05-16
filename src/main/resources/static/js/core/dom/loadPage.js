"use strict"

import { initApp } from "../../app.js";
import { menuActive } from "./menuActive.js";

export async function loadPage(url, target = "body") {
    if (APP.currentPage === url && target === "body") {
        return;
    }

    APP.currentPage = url;

    APP.cache.common = {};
    APP.cache.page = {};

    const res = await fetch(url);
    const html = await res.text();

    const parser = new DOMParser();
    const doc = parser.parseFromString(html, "text/html");

    let base;

    if (target === "body") {
        const newBody = doc.querySelector(".normal-body");
        const oldBody = document.querySelector(".normal-body");

        if (!newBody || !oldBody) {
            console.error("bodyが見つかりません");
            return;
        }
        oldBody.replaceWith(newBody);
        base = newBody;
        menuActive(url, "sidebar");
    } else if (target === "layout") {
        let newLayout = doc.querySelector(".normal-main");
        if (!newLayout) {
            newLayout = doc.querySelector(".no-side-body");
        }
        let oldLayout = document.querySelector(".normal-main");
        if (!oldLayout) {
            oldLayout = document.querySelector(".no-side-body");
        }

        if (!newLayout || !oldLayout) {
            console.error("layoutが見つかりません");
            return;
        }

        oldLayout.replaceWith(newLayout);
        base = newLayout;

        menuActive(url, "header");
    }

    // 共通処理
    const cssPath = base.dataset.css;
    const modulePath = base.dataset.page;

    if (cssPath) {
        await replaceCss(cssPath);
    }

    if (modulePath) {
        const module = await import(modulePath);
        await module.init?.();
    }

    initApp();
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