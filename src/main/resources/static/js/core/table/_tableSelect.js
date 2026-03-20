// "use strict"

// export function createSelectableRow({
//     table,
//     item,
//     idKey,
//     validCheck,
//     onDoubleClick,
//     onSingleClick
// }) {
//     const row = table.insertRow();

//     row.setAttribute("name", "data-row");
//     row.dataset.id = item[idKey];
//     row.dataset.valid = validCheck ? validCheck(item) : true;
//     tdEnableEdit(row);

//     // シングルクリック
//     row.onclick = function (e) {
//         if (!row.classList.contains("selected")) {
//             detachmentSelectClassToAllRow(table, false);
//             addSelectClassToRow(row);
//             if (onSingleClick) onSingleClick(item, row, e);
//         }
//     };

//     // ダブルクリック
//     row.ondblclick = function (e) {
//         e.preventDefault();

//         if (!row.classList.contains("selected")) {
//             detachmentSelectClassToAllRow(table, false);
//             addSelectClassToRow(row);
//         }

//         if (onDoubleClick) onDoubleClick(item, row, e);
//     };

//     return row;
// }

// /**
//  * チェックボタン押下時の処理をイベントリスナーに登録する
//  */
// export function registCheckButtonClicked(tableId) {
//     // let tbl;
//     // if(tableId instanceof HTMLElement) {
//     //     tbl = tableId;
//     // } else {
//     //     tbl = document.getElementById(tableId);
//     // }
//     // if (!tbl) return;
//     const tbl = tableId instanceof HTMLElement ? tableId: document.getElementById(tableId);
//     if (tbl == null) return;

//     // チェックボタン押下時の処理
//     tbl.querySelectorAll('input[name="chk-box"]').forEach(chk => {
//         chk.addEventListener('change', e => {
//             const tbl = e.currentTarget.closest('.normal-table');console.log(tbl)
//             if (!tbl) return;

//             const items = tbl.querySelectorAll('[name="chk-box"]');
//             const checked = Array.from(items).filter(v => v.checked);

//             const allChk = tbl.querySelector('[name="all-chk-btn"]');
//             if (allChk) {
//                 allChk.checked = items.length > 0 && checked.length === items.length;
//             }
//         });
//     });

//     // テーブル外をクリックしたら選択を外す
//     document.addEventListener("click", (e) => {
//         if (!tbl) return;

//         // table の外をクリックした場合だけ
//         if (!tbl.contains(e.target)) {
//             detachmentSelectClassToAllRow(tableId, false);
//         }
//     });

//     turnOffAllCheckBtn(tbl);
// }

// /**
//  * 選択されているチェックボックスを全て取得する
//  * @param {取得対象の親要素} parent 
//  * @returns チェックされている要素のIDリストをJSON形式で返す。
//  */
// export function getAllSelectedIds(tableId) {
//     const tbl = tableId instanceof HTMLElement ? tableId: document.getElementById(tableId);
//     if (tbl == null) return;

//     const ids = tbl.querySelectorAll('input[name="chk-box"]:checked');
//     const checked_data = [];
//     if (0 < ids.length) {
//         for (let data of ids) {
//             let num = parseInt(data.closest('tr').dataset.id);
//             if (num > 0) checked_data.push(num);
//         }
//         return JSON.stringify({ ids:checked_data });
//     } else {
//         // 選択された要素がなければメッセージを表示して終了
//         openMsgDialog("msg-dialog", "選択されていません", "red");
//         return null;
//     }
// }

// /**
//  * 
//  * @param {*} tableId 
//  * @returns 
//  */
// export function getAllSelectedValues(tableId) {
//     const tbl = tableId instanceof HTMLElement ? tableId: document.getElementById(tableId);
//     if (!tbl) return null;

//     const values = tbl.querySelectorAll('input[name="chk-box"]:checked');
//     const checked_data = [];

//     for (let data of values) {
//         const v = data.closest('tr')?.dataset.value;
//         if (v) checked_data.push(v);
//     }

//     if (checked_data.length === 0) {
//         openMsgDialog("msg-dialog", "選択されていません", "red");
//         return null;
//     }
    
//     // JSON化は呼び出し側でする
//     return checked_data;
// }

// /**
//  * すべて選択ボタンをオフにする
//  * @param {*} tableId 
//  * @returns 
//  */
// export function turnOffAllCheckBtn(tableId) {
//     const tbl = tableId instanceof HTMLElement ? tableId: document.getElementById(tableId);
//     if (!tbl) return null;

//     const tableRoot = tbl.closest('.normal-table');
//     if (!tableRoot) return;

//     const allChk = tableRoot.querySelector('[name="all-chk-btn"]');
//     if (!allChk) return; 

//     allChk.checked = false;
// }

// /**
//  * すべて選択ボタンがチェックされた時の処理
//  * @param {*} tableId 
//  * @param {*} self 
//  */
// export function clickAllCheckBtn(self) {
//     const tbl = self.closest('table');
//     const state = self.checked;
//     // すべての行の選択状態を解除する
//     detachmentSelectClassToAllRow(tbl, false);
//     // すべての行のチェック状態を[state]に変更する
//     detachmentCheckedToAllRow(tbl, state);
// }

// /**
//  * 選択された要素(areaId)にセレクトクラスを付与する
//  * @param {選択された要素} areaId 
//  */
// export function addSelectClassToRow(areaId) {
//     const item = areaId instanceof HTMLElement ? areaId: document.getElementById(areaId);
//     if (!item) return null;

//     item.classList.add('selected');
// }

// /**
//  * 選択された要素(areaId)にセレクトクラスを付与もしくは剥奪する
//  * @param {選択された要素} areaId 
//  */
// export function detachmentSelectClassToRow(areaId) {
//     const item = areaId instanceof HTMLElement ? areaId: document.getElementById(areaId);
//     if (!item) return null;

//     if (!item.classList.contains('selected')) {
//         item.classList.add('selected');
//     } else {
//         item.classList.remove('selected');
//     }
// }

// /**
//  * テーブルのすべての要素からセレクトクラスを付与もしくは剥奪する
//  * チェックボックスがチェックされている場合は何もしない
//  * @param {要素を含むテーブル} tableId 
//  * @param {状態} state 
//  */
// export function detachmentSelectClassToAllRow(tableId, state) {
//     const tbl = tableId instanceof HTMLElement ? tableId: document.getElementById(tableId);
//     if (!tbl) return null;

//     const items = tbl.querySelectorAll('[name="data-row"]');
//     items.forEach(function(value) {
//         if (value.classList.contains('selected') && state == false) {
//             value.classList.remove('selected');
//         } else if (!value.classList.contains('selected') && state == true) {
//             value.classList.add('selected');
//         }
//     });
// }

// /**
//  * テーブルのすべてのチェック状態をすべて選択ボタンの状態に合わせる
//  * @param {要素を含むテーブル} tableId 
//  * @param {状態} state 
//  */
// export function detachmentCheckedToAllRow(tableId, state) {
//     const tbl = tableId instanceof HTMLElement ? tableId: document.getElementById(tableId);
//     if (!tbl) return null;

//     const items = tbl.querySelectorAll('[name="data-row"]>[name="chk-cell"]>input');
//     items.forEach(function(value) {
//         value.checked = state;
//     });
// }
