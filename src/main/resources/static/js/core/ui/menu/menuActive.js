"use strict"

export function menuActive(path, sw) {
    const text = sw === "header" ? ".header-title": ".normal-sidebar";
    
    document.querySelectorAll(text + ">.hamburger-item")
        .forEach(item => {
            item.classList.remove("selected");

            if (item.dataset.path === path) {
                item.classList.add("selected");
            }
        }
    );
}