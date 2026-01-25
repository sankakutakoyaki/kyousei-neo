"use strict"

/******************************************************************************************************* 画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const table = document.getElementById(tableId);

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "work_price_id"
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
    newRow.insertAdjacentHTML('beforeend', '<td class="text-right">' + (item.full_code ?? "-----") + '</td>');
    // 分類
    newRow.insertAdjacentHTML('beforeend', '<td>' + (item.category_name ?? "-----") + '</td>');
    // 作業項目
    newRow.insertAdjacentHTML('beforeend', '<td>' + (item.work_item_name ?? "-----") + '</td>');
    // 料金
    newRow.insertAdjacentHTML('beforeend', '<td class="editable text-right" data-edit-type="number">' + (item.price ?? 0).toLocaleString('ja-JP') + '</td>');
    // 荷主
    newRow.insertAdjacentHTML('beforeend', '<td>' + (item.company_name ?? "-----") + '</td>');
}

/******************************************************************************************************* 保存 */

// TDが変更された時の処理
function handleTdChange(editor) {
    const td = editor.closest('td.editable');
    const row = td.closest('tr');
    const id = row.dataset.id;

    let entity = origin.find(value => value.work_price_id == id);
    if (entity == null) return;
    
    const ent = structuredClone(entity);
    ent.price = Number(editor.value);
    execSave(ent);
}

// 保存処理
async function execSave(ent) {
    // スピナー表示
    startProcessing();

    // 保存処理
    const result = await updateFetch("/api/work/price/save", JSON.stringify(ent), token, "application/json");
    if (result.success) {
        // 画面更新
        refleshDisplay();
        // 追加・変更行に移動
        scrollIntoTableList("table-01-content", result.id);
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }

    // スピナー消去
    processingEnd();
}

/******************************************************************************************************* ダウンロード */

async function execDownloadCsv(tableId, url, self) {
    await downloadCsv(tableId, url);
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
    const companyCombo = document.getElementById('company1');
    const data = "id=" + encodeURIComponent(parseInt(companyCombo.value));
    origin = await searchFetch('/api/work/price/get/list/companyid', data, token, 'application/x-www-form-urlencoded');
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
    // hamburgerItemAddSelectClass('.header-title', 'regist');
    // hamburgerItemAddSelectClass('.normal-sidebar', 'work-price');
    // スピナー表示
    startProcessing();

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

    // スピナー消去
    processingEnd();
});