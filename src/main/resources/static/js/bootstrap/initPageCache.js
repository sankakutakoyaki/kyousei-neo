"use strict";

export async function initPageCache(url, {isActive = () => true} = {}) {
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 15000);
    try {
        const res = await fetch(url, {signal: controller.signal});
        if (!res.ok) throw new Error(`ページの初期データを取得できませんでした（HTTP ${res.status}）`);
        const data = await res.json();
        if (!isActive()) return;
        if (!APP.cache) APP.cache = {};
        if (!APP.cache.common) APP.cache.common = {};
        if (!APP.cache.page) APP.cache.page = {};
        Object.assign(APP.cache.common, data.common);
        Object.assign(APP.cache.page, data.page);
    } catch (error) {
        if (error.name === "AbortError") throw new Error("ページの初期データの取得がタイムアウトしました");
        throw error;
    } finally {
        clearTimeout(timeout);
    }
}
