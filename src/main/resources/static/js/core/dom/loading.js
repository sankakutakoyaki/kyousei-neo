"use strict"

import { clearElement } from "./clearElement.js";

// 処理開始時の処理　スピナー表示
export function startProcessing() {
    const body = document.querySelector('.normal-body');
    if (body == null) return;

    const spinner = document.querySelector('#loading');
    if (spinner == null) {
        body.insertAdjacentHTML('beforeend', '<div id="loading"><div class="spinner"></div></div>');
    } else {
        spinner.classList.remove('loaded');
    }
}

// 処理終了時の処理 スピナーを消す
export function processingEnd() {
    const spinner = document.querySelector('#loading');
    if (spinner == null) return;
    clearElement("loading", true)
}