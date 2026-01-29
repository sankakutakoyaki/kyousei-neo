"use strict"

/******************************************************************************************************* 画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const table = document.getElementById(tableId);

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "workPriceId"
        });

        createTable01Row(row, item);
    });
}

/******************************************************************************************************* テーブル行作成 */

// テーブル行を作成する
function createTable01Row(newRow, item) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    // コード
    newRow.insertAdjacentHTML('beforeend', '<td class="text-right">' + (item.fullCode ?? "-----") + '</td>');
    // 分類
    newRow.insertAdjacentHTML('beforeend', '<td>' + (item.categoryName ?? "-----") + '</td>');
    // 作業項目
    newRow.insertAdjacentHTML('beforeend', '<td>' + (item.workItemName ?? "-----") + '</td>');
    // 料金
    newRow.insertAdjacentHTML('beforeend', '<td class="editable text-right" data-edit-type="number">' + (item.price ?? 0).toLocaleString('ja-JP') + '</td>');
    // 荷主
    newRow.insertAdjacentHTML('beforeend', '<td>' + (item.companyName ?? "-----") + '</td>');
}

/******************************************************************************************************* 保存 */

// TDが変更された時の処理
function handleTdChange(editor) {
    const td = editor.closest('td.editable');
    const row = td.closest('tr');
    const id = row.dataset.id;

    let entity = origin.find(value => value.workPriceId == id);
    if (entity == null) return;
    
    const ent = structuredClone(entity);
    ent.price = Number(editor.value);
    execSave(ent);
}

// 保存処理
async function execSave(ent) {
    // 保存処理
    const result = await updateFetch("/api/work/price/save", JSON.stringify(ent), token);
    if (result.ok) {
        // 画面更新
        await refleshDisplay();
        // 追加・変更行に移動
        scrollIntoTableList("table-01-content", result.id);
    }
}

/******************************************************************************************************* ダウンロード */

async function execDownloadCsv(self) {
    await downloadCsv('table-01-content', '/work/price/download/csv');
}

/******************************************************************************************************* 画面更新 */

// フィルターを通して画面を更新する
async function refleshDisplay() {
    await execUpdate();
    const list = getCategoryFilterList(origin);
    await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list, createTableContent);
}

// 最新のリストを取得
async function execUpdate() {
    origin = await searchFetch('/api/work/price/get/list/companyid', JSON.stringify({id:parseInt(companyCombo.value)}), token);
}

// 荷主フィルター選択時の処理
function getCompanyFilterList(list) {
    const companyCombo = document.getElementById('company1');
    if (Number(companyCombo.value) === 0) {
        return list;
    } else {
        return list.filter(value => value.company_id === Number(companyCombo.value));
    }
}

// カテゴリーフィルター選択時の処理
function getCategoryFilterList(list) {
    const categoryCombo = document.getElementById('category1');
    if (Number(categoryCombo.value) === 0) {
        return list;
    } else {
        return list.filter(value => value.category_id === Number(categoryCombo.value));
    }
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {

    // コンボボックス
    const company1Area = document.getElementById('company1')
    createComboBox(company1Area, companyComboList);
    company1Area.onchange = async function() {
        refleshDisplay();
    };
    const category1Area = document.getElementById('category1')
    createComboBoxWithTop(category1Area, categoryComboList, "");
    category1Area.onchange = async function() {
        refleshDisplay();
    };

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function() {
        refleshDisplay();
    }, false);

    // 画面更新
    const list = getCategoryFilterList(origin);
    await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list, createTableContent);
});