"use strict"

import { clearElement } from "../dom/clearElement.js";
import { setPageTopButton, hidePageTopButton } from "../ui/pageTopButton.js";
import { formatDate } from "../../util/time.js";
import { normalizeValue } from "../behavior/valueNormalizer.js";
import { convertKey } from "../../util/keyCaseConverter.js";

export function renderTable(table, config, list){
    table.innerHTML = "";
    const wrapper = table.closest(".normal-table");
    // ヘッダー
    renderHeader(wrapper, config, list);
    // ボディ
    list.forEach(item => {
        const tr = document.createElement("tr");

        tr.dataset.id = item[config.idKey];
        tr.setAttribute("name", "data-row");

        createRow(tr, item, config);

        table.appendChild(tr);
    });

    // フッター
    if(config.footerId){
        createTableFooter(
            config.footerId,
            list,
            config.totalCount
        );
    }

    // ページトップボタン
    const area = table.closest(".table-wrapper");
    const pageTop = area?.querySelector(".page-top");

    if(config.pageTopButton){
        setPageTopButton(table);
    } else {
        hidePageTopButton(table);
    }

    // スクロール
    const header = wrapper?.querySelector('[name="table-header"]');
    if(header){
        toggleScrollbar(table);
    }
}

export function createRow(tr, item, config){
    // 行class
    if(config.rowClass){
        const cls = config.rowClass(item);
        if(cls){
            tr.classList.add(cls);
        }
    }

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
        chk.checked = item._selected ?? false;
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
            const key = convertKey(col.field, "kebab", "camel");
            
            let value = normalizeValue(item[key], col);
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

export function renderHeader(tableEl,config,list){
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

        const total = list.length;
        const selected = list.filter(v => v._selected).length;
        chk.checked = total > 0 && selected === total;

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

 // フッターの件数項目を更新する
export function createTableFooter(footerId, list, totalCount) {
    clearElement(footerId);
    const footer = document.getElementById(footerId);
    if (!footer) return;
    const viewCount = list?.length ?? 0;
    footer.insertAdjacentHTML('beforeend', `<span>
        ${viewCount}/${totalCount}件 :
        ${formatDate(new Date(), "yyyy-MM-dd HH:mm")}
        現在
    </span>`);
}

// テーブルにスクロールバーが表示された時にクラスを付与する
export function toggleScrollbar(element, className = 'has-scrollbar') {
    if (!element) return;

    const hasScrollbar = element.scrollHeight > element.clientHeight;

    const tbl = element.closest('.normal-table');
    if (!tbl) return;

    if (hasScrollbar) {
        tbl.classList.add(className);
    } else {
        tbl.classList.remove(className);
    }
}