"use strict"

let initialized = false;

/**
 * ハンバーガーメニュー初期化
 */
export function initHamburger() {
    if (initialized) return;
    initialized = true;
    document.addEventListener("click", event => {
        if (event.target.closest("#menu-open")) {
            const menuPanel = document.getElementById("hamburger-area");
            menuPanel?.classList.add("hamburger-open");
            menuPanel?.classList.remove("hamburger-close");
        }
        if (event.target.closest("#menu-close")) closeHamburger();
    });
}

/**
 * ハンバーガーメニューを閉じる
 */
export function closeHamburger() {
    const menuPanel = document.getElementById("hamburger-area");
    if (!menuPanel) return;

    menuPanel.classList.add("hamburger-close");
    menuPanel.classList.remove("hamburger-open");
}

/**
 * ハンバーガーアイテム選択
 */
export function selectHamburgerItem(areaId, self) {
    const area = document.querySelector(areaId);
    if (!area) return;

    area.querySelectorAll(".hamburger-item")
        .forEach(el => {
            el.classList.remove("selected");
        });
    self.classList.add("selected");
}
