"use strict"

/**
 * ハンバーガーメニュー初期化
 */
export function initHamburger() {
    const menuOpen = document.querySelector("#menu-open");
    const menuClose = document.querySelector("#menu-close");
    const menuPanel = document.getElementById("hamburger-area");
    if (!menuPanel) return;

    menuOpen?.addEventListener("click", () => {
        menuPanel.classList.add("hamburger-open");
        menuPanel.classList.remove("hamburger-close");
    });

    menuClose?.addEventListener("click", () => {
        closeHamburger();
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