"use strict"

export function switchTab(el){

    const group = el.dataset.group;
    const target = el.dataset.target;

    document
        .querySelectorAll(`[data-group="${group}"]`)
        .forEach(t => t.classList.remove("is-active"));

    el.classList.add("is-active");

    document
        .querySelectorAll(`[data-tab-content="${group}"]`)
        .forEach(c => c.classList.remove("is-show"));

    document
        .getElementById(target)
        .classList.add("is-show");

    requestAnimationFrame(resetEnterFocus);
}