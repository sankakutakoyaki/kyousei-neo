"use strict"

export function setActiveMenu(el) {

    // ① 全部外す
    document.querySelectorAll(".hamburger-item")
        .forEach(item => item.classList.remove("selected"));

    // ② クリックされたやつに付与
    el.classList.add("selected");
}

export function initMenuActive(path) {

    document.querySelectorAll(".hamburger-item")
        .forEach(item => {
            item.classList.remove("selected");

            if (item.dataset.path === path) {
                item.classList.add("selected");
            }
        });
}