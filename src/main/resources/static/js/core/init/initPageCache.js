"use strict"

// export async function initPageCache(url) {

//     const res = await fetch(url);

//     const data = await res.json();

//     if (!APP.cache) APP.cache = {};
//     if (!APP.cache.page) APP.cache.page = {};

//     Object.assign(APP.cache.page, data);
// }

export async function initPageCache(url) {

    const res = await fetch(url);
    const data = await res.json();

    if (!APP.cache) APP.cache = {};
    if (!APP.cache.common) APP.cache.common = {};
    if (!APP.cache.page) APP.cache.page = {};

    Object.assign(APP.cache.common, data.common);
    Object.assign(APP.cache.page, data.page);
}