"use strict"

/******************************************************************************************************* 画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const table = document.getElementById(tableId);

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "workItemId"
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
    newRow.insertAdjacentHTML('beforeend', '<td class="text-right">' + (item.fullCode ?? "") + '</td>');
    // 分類
    newRow.insertAdjacentHTML('beforeend', '<td>' + (item.categoryName ?? "-----") + '</td>');
    // コード
    newRow.insertAdjacentHTML('beforeend', '<td data-col="itemcode" class="editable text-right" data-edit-type="text">' + (item.code ?? "") + '</td>');
    // 作業項目
    newRow.insertAdjacentHTML('beforeend', '<td data-col="itemname" class="editable" data-edit-type="text">' + (item.name ?? "-----") + '</td>');
}

/******************************************************************************************************* 保存 */

// 新しい項目を初期値で登録する
async function execCreate(self) {
    const selectCombo = document.getElementById('category1');
    if (selectCombo.value == 0) return;

    const maxCode = createMaxCode(origin, Number(selectCombo.value));
    const uniqueName = createUniqueName({
        list: origin,
        groupFn: item => item.category_id === Number(selectCombo.value),
        nameFn: item => item.name,
        baseName: '新しい項目'
    });
    
    const ent = {'workItemId':0, 'categoryId':selectCombo.value, 'code':maxCode ,'name':uniqueName};
    execSave(ent);
}

// TDが変更された時の処理
function handleTdChange(editor) {
    const td = editor.closest('td.editable');
    const col = td.dataset.col;
    const row = td.closest('tr');
    const id = row.dataset.id;
    const cate = row.dataset.cate;

    const ent = origin.find(value => value.workItemId == id);
    switch (col) {
        case "itemcode":
            if (existsSameCode(Number(cate), Number(editor.value))) {
                editor.value = "";
                return;
            } else {
                ent.code = Number(editor.value);
                break;
            }
        case "itemname":
            if (existsSameName(Number(cate), editor.value)) {
                editor.value = "";
                return;
            } else {
                ent.name = editor.value;
                break;
            }
        default:
            return;
    }
    execSave(ent);
}

// リスト内に同じコードがないかチェック
function existsSameCode(categoryId, code) {
    return origin.some(item =>
        item.categoryId === categoryId &&
        item.code === code
    );
}

// リスト内に同じ作業名がないかチェック
function existsSameName(categoryId, name) {
    return origin.some(item =>
        item.categoryId === categoryId &&
        item.name.trim() === name.trim()
    );
}

// リスト内のコード最大値を取得
function createMaxCode(list, selectId) {
    return list.reduce((max, item) => {
        if (item.categoryId === selectId) {
            return Math.max(max, item.code);
        }
        return max;
    }, 0) + 1;
}

// 保存処理
async function execSave(ent) {
    // 保存処理
    const result = await updateFetch("/api/work/item/save", JSON.stringify(ent), token);
    if (result.ok) {
        // 画面更新
        await refleshDisplay();
        // 追加・変更行に移動
        scrollIntoTableList("table-01-content", result.id);
    }
}

/******************************************************************************************************* 削除 */

async function execDelete(self) {
    const result = await deleteTablelist('table-01-content', '/work/item/delete');

    if (result.ok) {
        await refleshDisplay();
        openMsgDialog("msg-dialog", result.message, "blue");
    }
}

/******************************************************************************************************* ダウンロード */

async function execDownloadCsv(self) {
    await downloadCsv('table-01-content', '/work/item/download/csv');
}

/******************************************************************************************************* 画面更新 */

// フィルターを通して画面を更新する
async function refleshDisplay() {
    await execUpdate();
    const list = getCategoryFilterList();
    await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list, createTableContent);
}

// 最新のリストを取得
async function execUpdate() {
    const response = await fetch('/api/work/item/get/list');
    if (!response.ok) {
        openMsgDialog("msg-dialog", "一覧の取得に失敗しました", "red");
        return;
    }
    origin = await response.json();
}

// カテゴリーフィルター選択時の処理
function getCategoryFilterList() {
    const categoryCombo = document.getElementById('category1');
    if (Number(categoryCombo.value) === 0) {
        return origin;
    } else {
        return origin.filter(value => value.categoryId === Number(categoryCombo.value));
    }
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {

    // コンボボックス
    const category1Area = document.getElementById('category1')
    createComboBoxWithTop(category1Area, categoryComboList, "");
    category1Area.onchange = function(e) {
        refleshDisplay();
    };

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        refleshDisplay();
    }, false);

    // 画面更新
    const list = getCategoryFilterList();
    await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list, createTableContent);
});