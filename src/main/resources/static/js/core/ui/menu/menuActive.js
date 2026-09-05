"use strict"

export function menuActive(path, sw) {
    const selector = sw === "header"
        ? ".header-title > .hamburger-item"
        : ".normal-sidebar .hamburger-item";
    
    document.querySelectorAll(selector)
        .forEach(item => {
            item.classList.remove("selected");

            if (item.dataset.path === path) {
                item.classList.add("selected");
            }
        }
    );
}
