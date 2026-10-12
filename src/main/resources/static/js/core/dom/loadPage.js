"use strict";

import { clearControllers } from "../../application/controllerRegistry.js";
import { initApp } from "../../app.js";
import { menuActive } from "../ui/menu/menuActive.js";

let generation = 0;
let pendingNavigation = null;
let commitQueue = Promise.resolve();
let committing = false;

export function loadPage(url, target = "body") {
    if (!url || !["body", "layout"].includes(target)) {
        return Promise.reject(new Error("ページの移動先が不正です"));
    }
    if (pendingNavigation?.url === url && pendingNavigation.target === target) {
        return pendingNavigation.promise;
    }
    const navigation = {url, target, generation: ++generation, abort: new AbortController()};
    pendingNavigation?.abort.abort();
    if (!committing && APP.currentPage === url && (APP.currentTarget ?? "body") === target) {
        pendingNavigation = null;
        return Promise.resolve(true);
    }
    pendingNavigation = navigation;
    navigation.promise = navigate(navigation).finally(() => {
        if (pendingNavigation === navigation) pendingNavigation = null;
    });
    return navigation.promise;
}

function pageRoot(doc, target) {
    return target === "body" ? doc.querySelector(".normal-body")
        : doc.querySelector(".normal-main") ?? doc.querySelector(".no-side-body");
}
function pageResources(root) {
    return root.dataset.page || root.dataset.css ? root
        : root.querySelector("[data-page], [data-css]") ?? root;
}
function waitFor(promise, signal) {
    return new Promise((resolve, reject) => {
        const aborted = () => reject(new DOMException("ページ移動を中止しました", "AbortError"));
        if (signal.aborted) { aborted(); return; }
        signal.addEventListener("abort", aborted, {once: true});
        Promise.resolve(promise).then(resolve, reject).finally(() => signal.removeEventListener("abort", aborted));
    });
}

async function navigate(navigation) {
    const {url, target, abort} = navigation;
    let stylesheet = null;
    let timedOut = false;
    const timeout = window.setTimeout(() => { timedOut = true; abort.abort(); }, 15000);
    try {
        const res = await waitFor(fetch(url, {signal: abort.signal}), abort.signal);
        if (!res.ok) throw new Error(`ページを取得できませんでした（HTTP ${res.status}）`);
        const html = await waitFor(res.text(), abort.signal);
        const doc = new DOMParser().parseFromString(html, "text/html");
        const base = pageRoot(doc, target);
        if (!base) throw new Error(res.redirected
            ? "移動先の画面を取得できませんでした。ログイン状態を確認してください。"
            : "移動先の画面が見つかりません");
        const resources = pageResources(base);
        const module = resources.dataset.page
            ? await waitFor(import(resources.dataset.page), abort.signal) : null;
        stylesheet = await stageCss(resources.dataset.css, abort.signal);
        window.clearTimeout(timeout);

        // A module's init may await cache/API calls without a cancellation contract.
        // Keep the next DOM/cache exchange behind its completion.
        const commit = commitQueue.then(async () => {
            if (navigation.generation !== generation || abort.signal.aborted) return false;
            committing = true;
            try { return await commitPage(base, module, stylesheet, navigation); }
            finally { committing = false; }
        });
        commitQueue = commit.catch(() => {});
        const committed = await commit;
        if (!committed) stylesheet?.remove();
        return committed;
    } catch (error) {
        stylesheet?.remove();
        if (navigation.generation !== generation) return false;
        if (timedOut) throw new Error("ページの読込がタイムアウトしました。再度お試しください。");
        throw error;
    } finally {
        window.clearTimeout(timeout);
    }
}

async function stageCss(href, signal) {
    if (!href) return null;
    const link = document.createElement("link");
    link.rel = "stylesheet";
    link.href = href;
    link.media = "not all";
    link.dataset.pageCss = "true";
    try {
        await waitFor(new Promise((resolve, reject) => {
            link.onload = resolve;
            link.onerror = () => reject(new Error("ページのスタイルを読み込めませんでした"));
            document.head.appendChild(link);
        }), signal);
        return link;
    } catch (error) { link.remove(); throw error; }
    finally { link.onload = null; link.onerror = null; }
}

function recoveryTemplate(root) {
    const copy = root.cloneNode(true);
    for (const el of [copy, ...copy.querySelectorAll("*")]) {
        // These markers refer to listeners which cloneNode does not copy.
        delete el.dataset.initialized;
        delete el.dataset.linkInitialized;
        delete el.dataset.enterFocusInitialized;
    }
    return copy;
}

async function commitPage(base, module, stylesheet, {url, target}) {
    const oldRoot = pageRoot(document, target);
    if (!oldRoot) throw new Error("現在の画面が見つかりません");
    const previous = {
        root: recoveryTemplate(oldRoot), cache: APP.cache,
        url: APP.currentPage, target: APP.currentTarget,
        resources: {...pageResources(oldRoot).dataset},
        styles: [...document.querySelectorAll("link[data-page-css]")]
            .filter(link => link !== stylesheet && link.media !== "not all")
            .map(link => ({link, media: link.media}))
    };
    clearControllers();
    oldRoot.replaceWith(base);
    APP.cache = {...previous.cache, common: {}, page: {}};
    previous.styles.forEach(({link}) => { link.media = "not all"; });
    if (stylesheet) stylesheet.media = "";
    try {
        await module?.init?.();
        APP.currentPage = url;
        APP.currentTarget = target;
        menuActive(url, target === "body" ? "sidebar" : "header");
        initApp();
        previous.styles.forEach(({link}) => link.remove());
        return true;
    } catch (error) {
        clearControllers();
        base.replaceWith(previous.root);
        APP.cache = previous.cache;
        APP.currentPage = previous.url;
        APP.currentTarget = previous.target;
        stylesheet?.remove();
        previous.styles.forEach(({link, media}) => { link.media = media; });
        try {
            if (previous.resources.page) {
                const oldModule = await import(previous.resources.page);
                await oldModule.init?.();
            }
        } catch (recoveryError) {
            throw new Error(`ページの初期化と元の画面の復元に失敗しました。${recoveryError.message || ""}`, {cause: error});
        }
        throw error;
    }
}
