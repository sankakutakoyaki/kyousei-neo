"use strict"

import {registCheckButtonClicked} from "./tableSelect.js";
// import {makeSortable} from "./tableSort.js";
import {clearElement} from "../dom/clearElement.js";

export function renderTable(
    table,
    config,
    list,
    dataTable
){
    // テーブル初期化
    clearElement(table);

    if (!list) return;
    list.forEach(item=>{
        const row = createSelectableRow({
            table: table,
            item: item,
            idKey: config.idKey
        });
        createRow(row, item, config);
    });

    registCheckButtonClicked(table);
    // makeSortable(table);

    const el = table.closest('.normal-table').querySelector('table-header');
    if (el) toggleScrollbar(el);
}

export function createRow(tr, item, config){
    // check
    if(config.checkable){
        const td = document.createElement("td");
        td.className = "pc-style";
        td.setAttribute('name', 'chk-cell');
        const chk = document.createElement("input");
        chk.type = "checkbox";
        chk.setAttribute('name', 'chk-cell');
        chk.className = "normal-chk";
        td.appendChild(chk);
        tr.appendChild(td);
    }

    for(const col of config.columns){
        const td = document.createElement("td");
        if(col.class){
            td.className = col.class;
        }
        // render関数
        if(col.render){
            td.innerHTML = col.render(item);
        }
        // field指定
        else if(col.field){
            let value = item[col.field];
            if(value == null){
                value = col.default ?? "";
            }
            if(col.format){
                value = col.format(value);
            }
            td.textContent = value;
        }
        tr.appendChild(td);
    }
}
// export async function updateTableDisplay(
//     tableId,
//     footerId,
//     searchId,
//     list,
//     createContent   // ★ 画面ごとの処理
// ) {

//     // フィルター処理
//     const result = filterDisplay(searchId, list);

//     // テーブル初期化
//     deleteElements(tableId);

//     // ★ 画面ごとのテーブル生成
//     if (typeof createContent === "function") {
//         createContent(tableId, result);
//     }

//     // フッター
//     createTableFooter(footerId, list);

//     // 共通後処理
//     registCheckButtonClicked(tableId);
//     turnOffAllCheckBtn(tableId);
//     resetSortable(tableId);
//     setPageTopButton(tableId);

//     document.querySelectorAll('.scroll-area').forEach(el => {
//         toggleScrollbar(el);
//     });
// }

export function createSelectableRow({
    table,
    item,
    idKey,
    validCheck,
    onDoubleClick,
    onSingleClick
}) {
    const row = table.insertRow();

    row.setAttribute("name", "data-row");
    row.dataset.id = item[idKey];
    row.dataset.valid = validCheck ? validCheck(item) : true;
    tdEnableEdit(row);

    // シングルクリック
    row.onclick = function (e) {
        if (!row.classList.contains("selected")) {
            detachmentSelectClassToAllRow(table, false);
            addSelectClassToRow(row);
            if (onSingleClick) onSingleClick(item, row, e);
        }
    };

    // ダブルクリック
    row.ondblclick = function (e) {
        e.preventDefault();

        if (!row.classList.contains("selected")) {
            detachmentSelectClassToAllRow(table, false);
            addSelectClassToRow(row);
        }

        if (onDoubleClick) onDoubleClick(item, row, e);
    };

    return row;
}

/**
 * フッターの件数項目を更新する
 * @param {テーブル自身} tbl 
 */
export function createTableFooter(footerId, list) {
    clearElement(footerId);
    const footer = document.getElementById(footerId);
    if (!footer) return;
    const num = list == null ? 0: list.length;
    footer.insertAdjacentHTML('beforeend', '<span>' + num + '件 : ' + getNow() + ' 現在</span>');
}

/**
 * テーブルにスクロールバーが表示された時にクラスを付与する
 * @param {*} element 
 * @param {*} className 
 * @returns 
 */
export function toggleScrollbar(element, className = 'has-scrollbar') {
    if (!element) return;

    const hasScrollbar = element.scrollHeight > element.clientHeight;

    const tbl = element.closest('table');
    if (!tbl) return;
    
    if (hasScrollbar) {
        tbl.classList.add(className);
    } else {
        tbl.classList.remove(className);
    }
}

/**
 * 指定した要素へスクロールさせる
 * @param {*} tableId 
 * @param {*} id 
 */
export function scrollIntoTableList(tableId, id) {
    const parent = document.getElementById(tableId);
    const row = parent.querySelector('[data-id="' + id + '"]');
    if (row != null) {
        row.scrollIntoView({
            block: "center"
        });
    }
}