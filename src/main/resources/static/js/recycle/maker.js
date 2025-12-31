/******************************************************************************************************* 画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const tbl = document.getElementById(tableId);
    list.forEach(function (item) {
        let newRow = tbl.insertRow();
        newRow.setAttribute('name', 'data-row');
        newRow.setAttribute('data-id', item.recycle_maker_id);
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
    newRow.insertAdjacentHTML('beforeend', '<td class="editable" data-col="code">' + (item.code ?? "-----") + '</td>');
    // グループ
    newRow.insertAdjacentHTML('beforeend', '<td class="editable" data-col="group" data-edit-type="select" data-options-key="group" data-value="' + item.group + '">' + (item.group_name ?? "0") + '</td>');
    // 製造業者等名
    newRow.insertAdjacentHTML('beforeend', '<td class="editable" data-col="name" data-edit-type="text">' + (item.name ?? "-----") + '</td>');
    // 略称
    newRow.insertAdjacentHTML('beforeend', '<td class="editable" data-col="abbr" data-edit-type="text">' + (item.abbr_name ?? "-----") + '</td>');
}

/******************************************************************************************************* 入力画面 */

// 従業員登録画面を開く
async function execEdit01(self) {
    // フォーム画面を取得
    const form = document.getElementById('form-dialog-01');
    const code = form.querySelector('[name="maker-code"]');
    code.value = "";
    form.querySelector('[name="maker-group"]').value = "0";
    form.querySelector('[name="maker-name"]').value = "";
    form.querySelector('[name="maker-abbr"]').value = "";

    // 入力フォームダイアログを開く
    openFormDialog("form-dialog-01");
    setFocusElement("msg-dialog", code);
}

/******************************************************************************************************* 保存 */

async function execCreate01(self) {
    const form = document.getElementById('form-01'); 
    // エラーチェック
    if (form01DataCheck(form) == false) {
        return;
    } else {
        const formData = new FormData(form);
        const formdata = structuredClone(formEntity);
        formdata.code = Number(formData.get('maker-code'));
        formdata.group = Number(formData.get('maker-group'));
        formdata.name = formData.get('maker-name').trim();
        formdata.abbr_name = formData.get('maker-abbr').trim();

        execSave("table-01-content", "footer-01", "search-box-01", formdata);
    }
}

// 入力チェック
function form01DataCheck(area) {
    let msg = "";
    // コードが入力されていない、または不正な値のときFalseを返す
    const code = area.querySelector('input[name="maker-code"]');
    if (code != null && code.value == "" && code.value < 0) msg += '\nコードを入力して下さい';
    if (Number(code.value) > 999 || Number(code.value) < 1) msg += '\nコードは1から999の間で入力して下さい';
    // グループが選択されていないとFalseを返す
    const group = area.querySelector('select[name="maker-group"]');
    if (group != null && group.value == 0) msg += '\nグループを選択して下さい';
    // 製造業者等名チェック
    const name = area.querySelector('input[name="maker-name"]');
    if (name != null && name.value == "") msg += '\n製造業者等名を入力してくだい';
    // 略称チェック
    const abbr = area.querySelector('input[name="maker-name"]');
    if (abbr != null && abbr.value == "") msg += '\n略称入力してくだい';
    // エラーが一つ以上あればエラーメッセージダイアログを表示する
    if (msg != "") {
        openMsgDialog("msg-dialog", msg, "red");
        return false;
    }
    return true;
}

// // 登録する
// async function execCreate(self) {
//     // 製造業者等名
//     const uniqueName1 = createUniqueName({
//         list: origin,
//         nameFn: item => item.name,
//         baseName: '新しい名前'
//     });
//     // 略称
//     const uniqueName2 = createUniqueName({
//         list: origin,
//         nameFn: item => item.abbr_name,
//         baseName: '新しい略称'
//     });
//     const ent = {'recycle_maker_id':0, 'code':999, 'group':otherCode, 'name':uniqueName1, 'abbr_name':uniqueName2, 'version':0};
//     execSave("table-01-content", "footer-01", "search-box-01", ent);
// }

// 保存する
function handleTdChange(editor) {
    const td = editor.closest('td.editable');
    if (!td) return;

    const col = td.dataset.col;
    const row = td.closest('tr');
    const id = row.dataset.id;

    const ent = origin.find(v => v.recycle_maker_id == id);
    if (!ent) return;

    switch (col) {
        case "group":
            ent.group = Number(editor.value);
            break;
        case "code":
            ent.code = Number(editor.value);
            break;
        case "name":
            ent.name = editor.value;
            break;
    }

    execSave("table-01-content", "footer-01", "search-box-01", ent);
}

// // リスト内に同じコードがないかチェック
// function existsSameCode(code) {
//     return origin.some(item =>
//         code != 999 &&
//         item.code === code
//     );
// }

// // リスト内に同じ製造業者等名がないかチェック
// function existsSameName(name) {
//     return origin.some(item =>
//         item.name.trim() === name.trim()
//     );
// }

/******************************************************************************************************* 削除 */

async function execDelete(tableId, footerId, searchId, url, self) {
    // スピナー表示
    startProcessing();
    const result = await deleteTablelist(tableId, url);

    if (result.success) {                
        await execUpdate();
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
async function execSave(tableId, footerId, searchId, ent) {
    // スピナー表示
    startProcessing();

    // 保存処理
    const resultResponse = await postFetch("/recycle/maker/save", JSON.stringify(ent), token, "application/json");
    const result = await resultResponse.json();
console.log(result.message);
console.log(result.success);
    if (result.success) {
        // 画面更新
        await execUpdate();
        await updateTableDisplay(tableId, footerId, searchId, origin);
        // 追加・変更行に移動
        scrollIntoTableList(tableId, result.id);
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

    const resultResponse = await fetch('/recycle/maker/get/list');
    origin = await resultResponse.json();

    // スピナー消去
    processingEnd();
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

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        const list = getCategoryFilterList();
        updateTableDisplay("table-01-content", "footer-01", "search-box-01", list);
    }, false);

    // 作成フォームのグループコンボボックス
    const groupArea = document.getElementById('maker-group')
    createComboBoxWithTop(groupArea, groupComboList, "");

    // 画面更新
    await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin);

    // スピナー消去
    processingEnd();
});