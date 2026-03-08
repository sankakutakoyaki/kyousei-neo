"use strict"

import { updateFilter03State } from "/js/qualifications/qualificationsFilter.js";

/******************************************************************************************************* 入力画面 */

// リスト画面の本体部分を作成する
export function createTableContent(tableId, list) {
    const table = document.getElementById(tableId);

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "qualificationsId",
            onDoubleClick: (item) => {
                execEdit(item.qualificationsId);
            }
        });

        createTableRow(row, item);
    });
}

// テーブル行を作成する
export function createTableRow(newRow, item) {
    // 名前
    newRow.insertAdjacentHTML('beforeend', '<td><span class="kana">' + item.ownerNameKana + '</span><br><span>' + item.ownerName + '</span></td>');
    // 資格名
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.qualificationName ?? "登録なし") + '</span></td>');
    // 番号
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.number ?? "-----") + '</span></td>');
    // 有効期限
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.expiryDate ?? "") + '</span></td>');
    // ステータス
    newRow.insertAdjacentHTML('beforeend', '<td name="enabled-cell"><span>' + (item.status == "取得済み" && item.is_enabled == 0 ? "期限切れ": "") + '</span></td>');
}

/******************************************************************************************************* 画面更新 */

export async function execUpdate(tab) {
    const cfg = CONFIG.MODE[tab];

    const resultResponse = await fetch(cfg.getUrl);
    const result = await resultResponse.json();

    if (resultResponse.ok){
        origin = result.data;
        // 画面更新
        await execFilterDisplay(tab);        
    }
}

// コードでフィルターする
export function codeFilter(codeValue, keyName, list) {
    if (!codeValue) return list;
    
    const num = Number(codeValue);
    if (Number.isNaN(num)) return list;

    return list.filter(v => v[keyName] === num);
}

// 資格情報フィルター
export function qualificationsFilter(filterValue, list) {
    if (filterValue === '0') return list;

    const num = Number(filterValue);
    return list.filter(v => v.qualificationMasterId === num);
}

// 一括フィルター
export async function execFilterDisplay(tab) {
    const cfg = CONFIG.MODE[tab];

    const codeValue = document.getElementById(cfg.codeId)?.value;
    if (!codeValue) return;
    const filterValue = document.getElementById(cfg.filterId)?.value;
    if (!filterValue) return;

    const list = qualificationsFilter(
        filterValue,
        codeFilter(codeValue, cfg.ownerKeyName, origin)
    );
    await updateTableDisplay(cfg.tableId, cfg.footerId, cfg.searchId, list, createTableContent);
}

// companyCombo変更時の処理
export function refleshCode(codeId, nameId) {
    const code = document.getElementById(codeId);
    const name = document.getElementById(nameId);
    code.value = code.value === name.value ? code.value: Number(name.value) === 0 ? "": name.value ;
    updateFilter03State();
}