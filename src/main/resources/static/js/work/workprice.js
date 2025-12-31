/******************************************************************************************************* 画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const tbl = document.getElementById(tableId);
    list.forEach(function (item) {
        let newRow = tbl.insertRow();
        newRow.setAttribute('name', 'data-row');
        newRow.setAttribute('data-id', item.work_price_id);
        tdChangeEdit(newRow);
        tdEnableEdit(newRow);
        createTable01Row(newRow, item);
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
function tdChangeEdit(newRow) {
    newRow.addEventListener('change', function (e) {
        const input = e.target;
        const td = input.closest('td.editable');
        const row = td.closest('tr');
        const id = row.dataset.id;

        let ent = origin.find(value => value.work_price_id == id);
        if (ent != null) {
            ent.price = Number(input.value);
            execSave(ent);
        }
    });
}

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave(ent) {
    // スピナー表示
    startProcessing();

    // 保存処理
    const resultResponse = await postFetch("/work/price/save", JSON.stringify(ent), token, "application/json");
    const result = await resultResponse.json();
    if (result.success) {
        // 画面更新
        await execUpdate();
        const list = getCategoryFilterList();
        await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list);
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

// 最新のリストを取得
async function execUpdate() {
    // スピナー表示
    startProcessing();

    const companyCombo = document.getElementById('company1');
    const data = "id=" + encodeURIComponent(parseInt(companyCombo.value));
    const resultResponse = await postFetch('/work/price/get/list/companyid', data, token, 'application/x-www-form-urlencoded');
    origin = await resultResponse.json();

    // スピナー消去
    processingEnd();
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

// テーブルリスト画面を更新する
async function updateTableDisplay(tableId, footerId, searchId) {
    // フィルター処理
    let list = origin;
    list = getCategoryFilterList(list);
    list = filterDisplay(searchId, list);
    // リスト画面を初期化
    deleteElements(tableId);
    // リスト作成
    createTableContent(tableId, list);
    // フッター作成
    createTableFooter(footerId, list);
    // チェックボタン押下時の処理を登録する
    registCheckButtonClicked(tableId);
    // すべて選択ボタンをオフにする
    turnOffAllCheckBtn(tableId);
    // テーブルのソートをリセットする
    resetSortable(tableId);
    // スクロール時のページトップボタン処理を登録する
    setPageTopButton(tableId);
    // テーブルにスクロールバーが表示されたときの処理を登録する
    document.querySelectorAll('.scroll-area').forEach(el => {
        toggleScrollbar(el);
    });
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {
    // スピナー表示
    startProcessing();

    // コンボボックス
    const company1Area = document.getElementById('company1')
    createComboBox(company1Area, companyComboList);
    company1Area.onchange = async function() {
        await execUpdate();
        await updateTableDisplay("table-01-content", "footer-01", "search-box-01");
    };
    const category1Area = document.getElementById('category1')
    createComboBoxWithTop(category1Area, categoryComboList, "");
    category1Area.onchange = async function() {
        await updateTableDisplay("table-01-content", "footer-01", "search-box-01");
    };

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function() {
        await updateTableDisplay("table-01-content", "footer-01", "search-box-01");
    }, false);

    // 画面更新
    await updateTableDisplay("table-01-content", "footer-01", "search-box-01");

    // スピナー消去
    processingEnd();
});