"use strict"

/**
 * リンク先へジャンプさせる
 * @param {リンク先} link 
 */
function moveLinkPage(link) {
    location.href = link;
};

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

/**
 * ページトップボタン処理を登録する
 */
function setPageTopButton(tableId) {
    const tbl = document.getElementById(tableId);
    if (!tbl) return;

    const area = tbl.closest('.table-area');
    if (!area) return;

    const btn = area.querySelector('.page-top');
    if (!btn) return; // ★ page-top が無ければ何もしない

    // クリック時
    btn.addEventListener('click', function () {
        if (tbl.scrollTop > tbl.clientHeight) {
            const firstRow = tbl.querySelector('tr');
            if (firstRow) {
                firstRow.scrollIntoView({
                    block: "start",
                    behavior: "smooth"
                });
            }
        }
    });

    // 初期状態
    btn.style.opacity = "0";

    // スクロール時
    tbl.addEventListener('scroll', function () {
        if (tbl.scrollTop > tbl.clientHeight) {
            btn.style.opacity = "0.7";
        } else {
            btn.style.opacity = "0";
        }
    });
}

/**
 * 指定したエレメント以下の要素を削除
 * @param {削除対象のエレメントもしくはエレメントのID} areaId
 */
function deleteElements(areaId) {
    let item;
    if(areaId instanceof HTMLElement) {
        item = areaId;
    } else {
        item = document.getElementById(areaId);
    }
    if (item == null) return;
    while (item.firstChild) {
        item.removeChild(item.firstChild);
    };
}

/**
* 指定したエレメントを全て削除（自分自身を含む）
* @param {削除対象のエレメントもしくはエレメントのID} areaId
*/
function deleteElementsAll(areaId) {
    let item;
    if(areaId instanceof HTMLElement) {
        item = areaId;
    } else {
        item = document.getElementById(areaId);
    }
    // const item = document.getElementById(areaId);
    if (item == null) return;
    while (item.firstChild) {
        item.removeChild(item.firstChild);
    };
    item.remove();
}

/**
 * ハンバーガーメニュー作成
 */
const menuOpen = document.querySelector('#menu-open');
const menuClose = document.querySelector('#menu-close');
const menuPanel = document.getElementById('hamburger-area');
document.addEventListener('DOMContentLoaded', function () {
    if (menuOpen != null) {
        menuOpen.addEventListener('click', () => {
            menuPanel.classList.add('hamburger-open');
            menuPanel.classList.remove('hamburger-close');
        });
    };
    if (menuClose != null) {
        menuClose.addEventListener('click', () => {
            menuPanel.classList.add('hamburger-close');
            menuPanel.classList.remove('hamburger-open');
        });
        hamburgerClose();
    }
});

/**
 * ハンバーガーメニューを手動で閉じる
 */
function hamburgerClose() {
    const menuClose = document.querySelector('#menu-close');
    if (menuClose != null) menuClose.click();
}

/**
 * ハンバーガーアイテムクリック時の処理
 * @param {*} self 
 */
function hamburgerItemAddSelectClass(areaId, self) {
    const area = document.querySelector(areaId);
    area.querySelectorAll('.hamburger-item').forEach(el => el.classList.remove('selected'));
    self.classList.add('selected');
}