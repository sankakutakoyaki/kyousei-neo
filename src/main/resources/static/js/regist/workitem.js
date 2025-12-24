/******************************************************************************************************* 画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const tbl = document.getElementById(tableId);
    list.forEach(function (item) {
        let newRow = tbl.insertRow();
        // ID（Post送信用）
        newRow.setAttribute('name', 'data-row');
        newRow.setAttribute('data-id', item.work_item_id);
        newRow.setAttribute('data-cate', item.category_id);
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
    newRow.insertAdjacentHTML('beforeend', '<td class="text-right">' + (String(item.category_code).padStart(2, '0') + String(item.code).padStart(2, '0') ?? "") + '</td>');
    // 分類
    newRow.insertAdjacentHTML('beforeend', '<td>' + (item.category_name ?? "-----") + '</td>');
    // コード
    newRow.insertAdjacentHTML('beforeend', '<td data-col="itemcode" class="editable text-right">' + (item.code ?? "") + '</td>');
    // 作業項目
    newRow.insertAdjacentHTML('beforeend', '<td data-col="itemname" class="editable">' + (item.name ?? "-----") + '</td>');
}

/******************************************************************************************************* 作成 */

// 登録する
async function execCreate(self) {
    const selectCombo = document.getElementById('category1');
    if (selectCombo.value == 0) return;

    const maxCode = createMaxCode(origin, Number(selectCombo.value));
    const uniqueName = createUniqueName(origin, Number(selectCombo.value));
    
    const workItem = {'work_item_id':0, 'category_id':selectCombo.value,'code':maxCode ,'name':uniqueName};
    execSave(workItem);
}

// TDが変更された時の処理
function tdChangeEdit(newRow) {
    newRow.addEventListener('change', function (e) {
        const input = e.target;
        const td = input.closest('td.editable');
        const col = td.dataset.col;
        const row = td.closest('tr');
        const id = row.dataset.id;
        const cate = row.dataset.cate;

        const ent = origin.find(value => value.work_item_id == id);
        switch (col) {
            case "itemcode":
                if (existsSameCode(Number(cate), Number(input.value))) {
                    input.value = "";
                    return;
                } else {
                    ent.code = Number(input.value);
                    break;
                }
            case "itemname":
                if (existsSameName(Number(cate), input.value)) {
                    input.value = "";
                    return;
                } else {
                    ent.name = input.value;
                    break;
                }
            default:
                return;
        }
        execSave(ent);
    });
}

// リスト内に同じコードがないかチェック
function existsSameCode(categoryId, code) {
    return origin.some(item =>
        item.category_id === categoryId &&
        item.code === code
    );
}

// リスト内に同じ作業名がないかチェック
function existsSameName(categoryId, name) {
    return origin.some(item =>
        item.category_id === categoryId &&
        item.name.trim() === name.trim()
    );
}

// リスト内のコード最大値を取得
function createMaxCode(list, selectId) {
    return list.reduce((max, item) => {
        if (item.category_id === selectId) {
            return Math.max(max, item.code);
        }
        return max;
    }, 0) + 1;
}

// 数字が被らなように名前を作成する
function createUniqueName(itemList, categoryId, baseName = '新しい項目') {
    const usedNumbers = itemList
        .filter(item => item.category_id === categoryId)
        .map(item => {
            const m = item.name?.match(/^新しい項目\((\d+)\)$/);
            return m ? Number(m[1]) : null;
        })
        .filter(n => n !== null);

    let num = 1;
    while (usedNumbers.includes(num)) {
        num++;
    }
    return `${baseName}(${num})`;
}

/******************************************************************************************************* 削除 */

async function execDelete(tableId, footerId, searchId, url, self) {
    // スピナー表示
    startProcessing();
    const result = await deleteTablelist(tableId, url);

    if (result.success) {                
        await execUpdate();
        const list = getCategoryFilterList();
        await updateTableDisplay(tableId, footerId, searchId, list);
        openMsgDialog("msg-dialog", result.message, "blue");
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }

    // スピナー消去
    processingEnd();
}

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave(ent) {
    // スピナー表示
    startProcessing();

    // 保存処理
    const resultResponse = await postFetch("/work/item/save", JSON.stringify(ent), token, "application/json");
    const result = await resultResponse.json();
    if (result.success) {
        // 画面更新
        await execUpdate();
        const list = getCategoryFilterList();
        await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list);
        // 追加・変更行に移動
        scrollIntoTableList("table-01-content", result.id);
        // itemList = [];
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

    const resultResponse = await fetch('/work/item/get/list');
    origin = await resultResponse.json();

    // スピナー消去
    processingEnd();
}

// カテゴリーフィルター選択時の処理
function getCategoryFilterList() {
    const categoryCombo = document.getElementById('category1');
    if (Number(categoryCombo.value) === 0) {
        return origin;
    } else {
        return origin.filter(value => value.category_id === Number(categoryCombo.value));
    }
}

// テーブルリスト画面を更新する
async function updateTableDisplay(tableId, footerId, searchId, list) {
    // フィルター処理
    const result = filterDisplay(searchId, list);
    // リスト画面を初期化
    deleteElements(tableId);
    // リスト作成
    createTableContent(tableId, result);
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
    // const company1Area = document.getElementById('company1')
    // createComboBox(company1Area, companyComboList);
    const category1Area = document.getElementById('category1')
    createComboBoxWithTop(category1Area, categoryComboList, "");
    category1Area.onchange = function(e) {
        const list = getCategoryFilterList();
        updateTableDisplay("table-01-content", "footer-01", "search-box-01", list);
    };

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        const list = getCategoryFilterList();
        updateTableDisplay("table-01-content", "footer-01", "search-box-01", list);
    }, false);

    // 画面更新
    await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin);

    // スピナー消去
    processingEnd();
});