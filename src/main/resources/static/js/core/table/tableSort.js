"use strict"

/**
 * テーブルをソートする関数
 * @param {*} table 
 * @param {*} col 
 * @param {*} reverse 
 */
export function sortTable(table, col, reverse) {
    const tbody = table.tBodies[1]; // 0はヘッダー
    const tr = Array.prototype.slice.call(tbody.rows);

    tr.sort(function(a, b) {
        const aValue = a.cells[col].textContent.trim();
        const bValue = b.cells[col].textContent.trim();

        // カンマを削除して数値に変換
        const aNum = parseFloat(aValue.replace(/,/g, ''));
        const bNum = parseFloat(bValue.replace(/,/g, ''));
        const isANumber = !isNaN(aNum);
        const isBNumber = !isNaN(bNum);

        let result = 0;

        if (isANumber && isBNumber) {
            result = aNum - bNum;  // 数値で比較
        } else {
            const aStr = aValue.toLowerCase();
            const bStr = bValue.toLowerCase();
            if (aStr < bStr) result = -1;
            else if (aStr > bStr) result = 1;
            else result = 0;
        }

        return reverse ? result : -result;
    });

    tr.forEach(function(row) {
        tbody.appendChild(row);
    });
}

/**
 * テーブルのヘッダーをクリック可能にする関数
 * @param {*} tableId 
 */
export function makeSortable(tableId) {
    let targetElm;
    if(tableId instanceof HTMLElement) {
        targetElm = tableId;
    } else {
        targetElm = document.getElementById(tableId);
    }
    if (targetElm == null) return;

    const tbl = targetElm.closest('table');
    if (tbl == null) return;

    // テーブル内のすべてのヘッダーセルを取得
    const headers = tbl.querySelectorAll('th');
    
    // 各ヘッダーにクリックイベントを追加
    headers.forEach(function(header, index) {
        header.addEventListener('click', function() {
            // ヘッダーが既に昇順ソートされているか確認
            const isAsc = header.classList.contains('sorted-asc');
            // 他のヘッダーのソート状態をリセット
            resetSortIndicators(headers);
            // 昇順の場合は降順にソート、それ以外は昇順にソート
            if (isAsc) {
                sortTable(tbl, index, true); // 降順
                header.classList.add('sorted-desc'); // 降順のクラスを追加
            } else {
            sortTable(tbl, index, false); // 昇順
            header.classList.add('sorted-asc'); // 昇順のクラスを追加
        }
        });
    });
}

/**
 * すべてのヘッダーのソート状態をリセットする
 * @param {*} tableId 
 */
export function resetSortable(tableId) {
    let targetElm;
    if(tableId instanceof HTMLElement) {
        targetElm = tableId;
    } else {
        targetElm = document.getElementById(tableId);
    }
    if (targetElm == null) return;

    const tbl = targetElm.closest('table');
    if (tbl == null) return;

    // テーブル内のすべてのヘッダーセルを取得
    const headers = tbl.querySelectorAll('th');

    // 他のヘッダーのソート状態をリセット
    resetSortIndicators(headers);
}

/**
 * すべてのヘッダーのソート状態をリセットする関数
 * @param {*} headers 
 */
export function resetSortIndicators(headers) {
    // 各ヘッダーからソートクラスを削除
    headers.forEach(function(header) {
        header.classList.remove('sorted-asc', 'sorted-desc');
    });
}