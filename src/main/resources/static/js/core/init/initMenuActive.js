"use strict"

export function initMenuActive(path) {

    document.querySelectorAll(".hamburger-item")
        .forEach(item => {
            item.classList.remove("selected");

            if (item.dataset.path === path) {
                item.classList.add("selected");
            }
        });
}