"use strict"

// export function renderTable(table,config,data,engine){

//     table.innerHTML = "";

//     const thead = document.createElement("thead");
//     const tr = document.createElement("tr");

//     config.columns.forEach(col=>{

//         const th = document.createElement("th");

//         th.textContent = col.label;

//         if(config.sortable){

//             th.addEventListener("click",()=>{
//                 engine.sort(col.field);
//             });

//         }

//         tr.appendChild(th);

//     });

//     thead.appendChild(tr);
//     table.appendChild(thead);

//     const tbody = document.createElement("tbody");

//     data.forEach((row,i)=>{

//         const tr = document.createElement("tr");

//         tr.addEventListener("click",()=>{
//             engine.select(i);
//         });

//         config.columns.forEach(col=>{

//             const td = document.createElement("td");

//             td.textContent = row[col.field];

//             tr.appendChild(td);

//         });

//         tbody.appendChild(tr);

//     });

//     table.appendChild(tbody);

// }


/** ここから以前のもの */
export async function updateTableDisplay(
    tableId,
    footerId,
    searchId,
    list,
    createContent   // ★ 画面ごとの処理
) {

    // フィルター処理
    const result = filterDisplay(searchId, list);

    // テーブル初期化
    deleteElements(tableId);

    // ★ 画面ごとのテーブル生成
    if (typeof createContent === "function") {
        createContent(tableId, result);
    }

    // フッター
    createTableFooter(footerId, list);

    // 共通後処理
    registCheckButtonClicked(tableId);
    turnOffAllCheckBtn(tableId);
    resetSortable(tableId);
    setPageTopButton(tableId);

    document.querySelectorAll('.scroll-area').forEach(el => {
        toggleScrollbar(el);
    });
}

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
    deleteElements(footerId);
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