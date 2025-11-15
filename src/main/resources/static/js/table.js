"use strict"

/**
 * チェックボタン押下時の処理をイベントリスナーに登録する
 */
function registCheckButtonClicked(tableId) {
    let tbl;
    if(tableId instanceof HTMLElement) {
        tbl = tableId;
    } else {
        tbl = document.getElementById(tableId);
    }
    if (tbl == null) return;
    tbl.querySelectorAll('input[name="chk-box"]').forEach(function (value) {
        value.addEventListener('change', function(e) {
            const tbl = e.currentTarget.closest('.normal-table');
            const items = tbl.querySelectorAll('[name="chk-box"]');
            const checked = Array.from(items).filter(value => { return value.checked });
            if (checked.length == items.length) {
                tbl.querySelector('[name="all-chk-btn"]').checked = true;
            } else {
                tbl.querySelector('[name="all-chk-btn"]').checked = false
            }
        }, false)
    });
}

/**
 * フッターの件数項目を更新する
 * @param {テーブル自身} tbl 
 */
function createTableFooter(footerId, list) {
    deleteElements(footerId);
    const footer = document.getElementById(footerId);
    const num = list == null ? 0: list.length;
    footer.insertAdjacentHTML('beforeend', '<span>' + num + '件 : ' + getNow() + ' 現在</span>');
}

/**
 * 選択された要素(areaId)にセレクトクラスを付与する
 * @param {選択された要素} areaId 
 */
function addSelectClassToRow(areaId) {
    let item;
    if(areaId instanceof HTMLElement) {
        item = areaId;
    } else {
        item = document.getElementById(areaId);
    }
    if (item == null) return;
    item.classList.add('selected');
}

/**
 * 選択された要素(areaId)にセレクトクラスを付与もしくは剥奪する
 * @param {選択された要素} areaId 
 */
function detachmentSelectClassToRow(areaId) {
    let item;
    if(areaId instanceof HTMLElement) {
        item = areaId;
    } else {
        item = document.getElementById(areaId);
    }
    if (item == null) return;
    if (!item.classList.contains('selected')) {
        item.classList.add('selected');
    } else {
        item.classList.remove('selected');
    }
}

/**
 * テーブルのすべての要素からセレクトクラスを付与もしくは剥奪する
 * チェックボックスがチェックされている場合は何もしない
 * @param {要素を含むテーブル} tableId 
 * @param {状態} state 
 */
function detachmentSelectClassToAllRow(tableId, state) {
    let tbl;
    if(tableId instanceof HTMLElement) {
        tbl = tableId;
    } else {
        tbl = document.getElementById(tableId);
    }
    if (tbl == null) return;
    const items = tbl.querySelectorAll('[name="data-row"]');
    items.forEach(function(value) {
        if (value.classList.contains('selected') && state == false) {
            value.classList.remove('selected');
        } else if (!value.classList.contains('selected') && state == true) {
            value.classList.add('selected');
        }
    });
}

/**
 * テーブルのすべてのチェック状態をすべて選択ボタンの状態に合わせる
 * @param {要素を含むテーブル} tableId 
 * @param {状態} state 
 */
function detachmentCheckedToAllRow(tableId, state) {
    let tbl;
    if(tableId instanceof HTMLElement) {
        tbl = tableId;
    } else {
        tbl = document.getElementById(tableId);
    }
    if (tbl == null) return;
    const items = tbl.querySelectorAll('[name="data-row"]>[name="chk-cell"]>input');
    items.forEach(function(value) {
        value.checked = state;
    });
}

/**
 * すべて選択ボタンがチェックされた時の処理
 * @param {*} tableId 
 * @param {*} self 
 */
function clickAllCheckBtn(self) {
    const tbl = self.closest('table');
    const state = self.checked;
    // すべての行の選択状態を解除する
    detachmentSelectClassToAllRow(tbl, false);
    // すべての行のチェック状態を[state]に変更する
    detachmentCheckedToAllRow(tbl, state);
}

/**
 * テーブルにスクロールバーが表示された時にクラスを付与する
 * @param {*} element 
 * @param {*} className 
 * @returns 
 */
function toggleScrollbar(element, className = 'has-scrollbar') {
    if (!element) return;

    const hasScrollbar = element.scrollHeight > element.clientHeight;

    const tbl = element.closest('table');
    if (hasScrollbar) {
        tbl.classList.add(className);
    } else {
        tbl.classList.remove(className);
    }
}
  
/**
 * テーブルをソートする関数
 * @param {*} table 
 * @param {*} col 
 * @param {*} reverse 
 */
function sortTable(table, col, reverse) {
    // テーブルのtbodyを取得
    // tbody内のすべての行を配列に変換
    const tbody = table.tBodies[1]; // 0はヘッダー
    const tr = Array.prototype.slice.call(tbody.rows);
    
    tr.sort(function(a, b) {
        const aValue = a.cells[col].textContent.trim();
        const bValue = b.cells[col].textContent.trim();
    
        // 数値に変換できるか判定
        const aNum = parseFloat(aValue);
        const bNum = parseFloat(bValue);
        const isANumber = !isNaN(aNum);
        const isBNumber = !isNaN(bNum);
    
        let result = 0;
    
        if (isANumber && isBNumber) {
            // 両方とも数値 → 数値で比較
            result = aNum - bNum;
        } else {
            // どちらか文字列 → 小文字にして文字列で比較
            const aStr = aValue.toLowerCase();
            const bStr = bValue.toLowerCase();
            if (aStr < bStr) result = -1;
            else if (aStr > bStr) result = 1;
            else result = 0;
        }
        // 昇順 or 降順
        return reverse ? result : -result;
    });

    // ソートされた行をテーブルに再配置
    tr.forEach(function(row) {
        tbody.appendChild(row);
    });
}

/**
 * テーブルのヘッダーをクリック可能にする関数
 * @param {*} tableId 
 */
function makeSortable(tableId) {
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
    // const headers = tbl.querySelectorAll('th:not([name="chk-cell"])');
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
function resetSortable(tableId) {
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
function resetSortIndicators(headers) {
    // 各ヘッダーからソートクラスを削除
    headers.forEach(function(header) {
        header.classList.remove('sorted-asc', 'sorted-desc');
    });
}

/**
 * 選択されているチェックボックスを全て取得する
 * @param {取得対象の親要素} parent 
 * @returns チェックされている要素のIDリストをJSON形式で返す { number:0 }
 */
function getAllSelectedIds(tableId) {
    let tbl;
    if(tableId instanceof HTMLElement) {
        tbl = tableId;
    } else {
        tbl = document.getElementById(tableId);
    }
    if (tbl == null) return;
    const ids = tbl.querySelectorAll('input[name="chk-box"]:checked');
    const checked_data = [];
    if (0 < ids.length) {
        for (let data of ids) {
            let num = parseInt(data.closest('tr').dataset.id);
            if (num > 0) checked_data.push({ 'number': num });
        }
    }
    return checked_data;
}

/**
 * 指定した要素へスクロールさせる
 * @param {*} tableId 
 * @param {*} id 
 */
function scrollIntoTableList(tableId, id) {
    const parent = document.getElementById(tableId);
    const row = parent.querySelector('[data-id="' + id + '"]');
    if (row != null) {
        row.scrollIntoView({
            block: "center"
        });
    }
}

/**
 * リスト検索
 * @param {*} boxId 
 * @returns 
 */
function filterDisplay(boxId, list) {
    // 検索ボックスを取得
    const box = document.getElementById(boxId);
    // if (box == null) return origin;
    if (box == null) return list;

    // 検索ワードを取得
    const words = box.value;
    // if (words.length == 0) return origin;
    if (words.length == 0) return list;

    // let list = structuredClone(origin);

    // 空白区切りで配列を作成
    let list_word = words.split(/\s+/);
    list_word.forEach(word => {
        const result = list.filter(val => {
            let res = false;
            Object.values(val).forEach(item => {
                // item を文字列に変換して判定
                const strItem = String(item);
                if (strItem.includes(word)) res = true;
            });
            // 要素の中に[true]が一つでもあれば要素を返す
            if (res) return val;
        });
        list = result;
    });
    return list;
}

/**
 * 選択された要素のCSVファイルを作成してダウンロードする
 * @param {*} parent 
 * @param {*} downloaddata 
 */
async function downloadCsv(tableId, url) {
    // 選択された要素を取得する
    const data = getAllSelectedIds(tableId);
    if (data.length == 0) {
        // 選択された要素がなければメッセージを表示して終了
        openMsgDialog("msg-dialog", "選択されていません", "red");
        // return {"success":false, "message":"選択されていないか、データがありません。"};
    } else {
        funcDownloadCsv(data, url);
    }
}

/**
 * 選択された要素のCSVファイルを作成してダウンロードする
 * @param {*} parent 
 * @param {*} downloaddata 
 */
async function downloadCsvByBetweenDate(tableId, url, startStr, endStr) {
    // 選択された要素を取得する
    const data = getAllSelectedIds(tableId);
    if (data.length == 0) {
        // 選択された要素がなければメッセージを表示して終了
        openMsgDialog("msg-dialog", "選択されていません", "red");
    } else {
        funcDownloadCsv({ids:data, start:startStr, end:endStr}, url);
    }
}

/**
 * CSVダウンロード処理部分
 * @param {*} data 
 * @param {*} url 
 */
async function funcDownloadCsv(data, url) {
    // スピナー表示
    startProcessing();

    // 取得処理
    // const data = JSON.stringify(ids);
    const result = await postFetch(url, JSON.stringify(data), token, "application/json");
    const text = await result.text();

    // 文字列データが返却されなければ、エラーメッセージを表示
    if (text == null || text == "") {
        openMsgDialog("msg-dialog", "取得できませんでした", "red");
    } else {
        createCsvThenDownload(text);
    }

    // スピナー消去
    processingEnd();
}

/**
 * 削除
 * @param {*} tableId 
 * @param {*} url 
 */
async function deleteTablelist(tableId, url) {
    // 選択された要素を取得する
    const data = getAllSelectedIds(tableId);
    if (data.length == 0) {
        // 選択された要素がなければメッセージを表示して終了
        // openMsgDialog("msg-dialog", "選択されていません", "red");
        return {"success":false, "message":"選択されていないか、データがありません。"};
    } else {
        const resultResponse = await postFetch(url, JSON.stringify(data), token, 'application/json');
        return await resultResponse.json();
    }
}
