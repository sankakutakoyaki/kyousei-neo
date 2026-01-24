"use strict"

// /**
//  * Post送信する
//  * @param {アドレス} url 
//  * @param {送信するデータ} data 
//  * @param {トークン} token 
//  * @param {コンテントタイプ} contentType 
//  * @returns Promiseデータ
//  */
// async function postFetch(url, data, token, contentType) {
//     const spinner = document.getElementById('loading');
//     if (spinner != null) {
//         spinner.classList.remove('loaded');
//     }
//     try {
//         return await fetch(url, {
//             method: "POST",   // HTTP-Methodを指定する！
//             headers: {  // リクエストヘッダを追加
//                 'X-CSRF-TOKEN': token,
//                 'Content-Type': contentType,
//             },
//             body: data,
//         });
//     } catch (e) {
//         if (e = "TypeError: Failed to fetch") {
//             alert("ネットワーク接続が異常です。");
//             reloadPage();
//         } else {
//             alert(e);  // 例外（エラー）が発生した場合に実行
//         }
//     } finally {
//         // 処理結果の成否に関わらず実行
//         if (spinner != null) {
//             spinner.classList.add('loaded');
//         }
//     }
// }

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
    // const tbl = document.getElementById(tableId);
    // if (tbl != null) {
    //     const btn = document.querySelector('.page-top');
    //     if (!btn) return;
    //     btn.addEventListener('click', function () {
    //         if (tbl.scrollTop > tbl.clientHeight) {
    //             const scroll = tbl.querySelector('tr');
    //             scroll.scrollIntoView({
    //                 block: "start",
    //                 behavior: 'smooth'
    //             });
    //         }
    //     });
    //     btn.style.opacity = "0";
    
    //     tbl.onscroll = function () {
    //         const area = tbl.closest('.table-area');
    //         if (tbl.scrollTop > tbl.clientHeight) {
    //             area.querySelector('.page-top').style.opacity = "0.7";
    //         } else if (tbl.scrollTop < tbl.clientHeight) {
    //             area.querySelector('.page-top').style.opacity = "0";
    //         }
    //     };
    // }
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
function hamburgerItemAddSelectClass(areaId, selectId) {
    const area = document.querySelector(areaId);
    const item = area.querySelector('[data-menu="' + selectId + '"]');

    // アイテムに選択クラスを追加
    item.classList.add('selected');
}

// /**
//  * メインサイドバーのクリック時の処理
//  * @param {*} self 
//  */
// function sideBarAddSelectClass(selectId) {
//     const area = document.querySelector('.normal-sidebar');
//     const item = area.querySelector('[name="' + selectId + '"]');

//     // このliに選択クラスを追加
//     item.classList.add('selected');
// }