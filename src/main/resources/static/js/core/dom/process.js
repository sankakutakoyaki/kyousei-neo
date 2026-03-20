"use strict"

/**
 * 処理開始時の処理　スピナー表示
 */
function startProcessing() {
    const body = document.querySelector('.normal-body');
    if (body == null) return;
    body.inert = true;

    const spinner = document.querySelector('#loading');
    if (spinner == null) {
        body.insertAdjacentHTML('beforeend', '<div id="loading"><div class="spinner"></div></div>');
    } else {
        spinner.classList.remove('loaded');
    }
}

/**
* 処理終了時の処理 スピナーを消す
*/
function processingEnd() {
    const spinner = document.querySelector('#loading');
    if (spinner == null) return;
    deleteElementsAll("loading");

    const body = document.querySelector('.normal-body');
    if (body != null) body.inert = false;
}