"use strict"

/**
 * ページトップボタン処理を登録する
 */
export function setPageTopButton(tableId) {
    const tbl = tableId instanceof HTMLElement ? tableId: document.getElementById(tableId);
    if (tbl == null) return;

    // const area = tbl.closest('.table-area');
    const area = tbl.closest('.table-wrapper');
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