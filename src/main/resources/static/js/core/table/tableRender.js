"use strict"

import { clearElement } from "../dom/clearElement.js";
import { setPageTopButton } from "../dom/pageTopButton.js";

export function renderTable(
    table,
    config,
    list,
    dataTable
){
    // テーブル初期化
    clearElement(table);

    const el = table.closest('.normal-table');
    renderHeader(el, config);

    if (!list) return;
    list.forEach(item=>{
        const row = table.insertRow();
        row.dataset.id = item[config.idKey];
        row.setAttribute("name", "data-row");

        createRow(row, item, config, dataTable);
    });

    createTableFooter(config.footerId, list)
    setPageTopButton(table);

    const header = el?.querySelector('[name="table-header"]');
    if (header) toggleScrollbar(header);
}

export function createRow(tr, item, config, dataTable){
    // check
    if(config.checkable){
        const td = document.createElement("td");
        td.className = "pc-style";
        td.setAttribute('name', 'chk-cell');

        const chk = document.createElement("input");
        chk.type = "checkbox";
        chk.setAttribute('name', 'chk-box');
        chk.className = "normal-chk";

        const id = item[config.idKey];
        // 状態反映
        chk.checked = dataTable.model.isSelected(id);
        chk.dataset.id = id;

        td.appendChild(chk);
        tr.appendChild(td);
    }

    for(const col of config.columns){
        const td = document.createElement("td");
        if(col.class){
            td.className = col.class;
        }
        if(col.field){
            td.dataset.field = col.field;
        }
        if(col.editable){
            td.classList.add("editable");
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

export function renderHeader(tableEl,config){
    const thead = tableEl.querySelector("thead");
    if (!thead) return;

    const tr = document.createElement("tr");
    tr.setAttribute("name", "table-header");

    if(config.checkable){
        const th = document.createElement("th");
        th.setAttribute('name', 'chk-cell');
        th.className = "pc-style";

        const chk = document.createElement("input");
        chk.type = "checkbox";
        chk.setAttribute('name', 'all-chk-btn');
        chk.className = "normal-chk";

        th.appendChild(chk);
        tr.appendChild(th);
    }

    config.columns.forEach(col=>{
        const th = document.createElement("th");
        th.textContent = col.label ?? col.field ?? "";

        if(col.sortable && col.field){
            th.dataset.field = col.field;
        }
        tr.appendChild(th);
    });

    thead.innerHTML="";
    thead.appendChild(tr);
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