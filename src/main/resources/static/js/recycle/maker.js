/******************************************************************************************************* 画面 */

// リスト画面の本体部分を作成する
function createTable01Content(tableId, list) {
    const table = document.getElementById(tableId);

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "recycle_maker_id"
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
    newRow.insertAdjacentHTML('beforeend', '<td class="editable" data-col="code">' + (item.code ?? "-----") + '</td>');
    // グループ
    newRow.insertAdjacentHTML('beforeend', '<td class="editable" data-col="group" data-edit-type="select" data-options-key="group" data-value="' + item.group + '">' + (item.group_name ?? "0") + '</td>');
    // 製造業者等名
    newRow.insertAdjacentHTML('beforeend', '<td class="editable" data-col="name" data-edit-type="text">' + (item.name ?? "-----") + '</td>');
    // 略称
    newRow.insertAdjacentHTML('beforeend', '<td class="editable" data-col="abbr" data-edit-type="text">' + (item.abbr_name ?? "-----") + '</td>');
}

/******************************************************************************************************* 入力画面 */

// 登録画面を開く
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
    code.focus();
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

        execSave("table-01-content", "footer-01", "search-box-01", formdata, createTable01Content);
    }
}

// 入力チェック
function form01DataCheck(area) {
    const errors = [];

    // コードチェック
    const codeInput = area.querySelector('input[name="maker-code"]');
    if (codeInput) {
        const code = Number(codeInput.value);

        if (!codeInput.value) {
            errors.push("コードを入力してください");
        } else if (Number.isNaN(code) || code < 1 || code > 999) {
            errors.push("コードは1〜999の間で入力してください");
        }
    }

    // グループチェック
    const group = area.querySelector('select[name="maker-group"]');
    if (group && group.value === "0") {
        errors.push("グループを選択してください");
    }

    // 製造業者名
    const name = area.querySelector('input[name="maker-name"]');
    if (name && !name.value.trim()) {
        errors.push("製造業者等名を入力してください");
    }

    // 略称
    const abbr = area.querySelector('input[name="maker-abbr"]');
    if (abbr && !abbr.value.trim()) {
        errors.push("略称を入力してください");
    }

    // エラー表示
    if (errors.length > 0) {
        openMsgDialog("msg-dialog", errors.join("\n"), "red");
        const code = form.querySelector('[name="maker-code"]');
        if (code != null) setFocusElement("msg-dialog", code);
        return false;
    }

    return true;
}

// TDが変更された時の処理
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

    execSave("table-01-content", "footer-01", "search-box-01", ent, createTable01Content);
}

// 保存処理
async function execSave(tableId, footerId, searchId, ent, createContent) {
    // 保存処理
    const result = await updateFetch("/api/recycle/maker/save", JSON.stringify(ent), token, "application/json");
    if (result.success) {
        // 画面更新
        await execUpdate();
        await updateTableDisplay(tableId, footerId, searchId, origin, createContent);
        // 追加・変更行に移動
        scrollIntoTableList(tableId, result.id);
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }
}

/******************************************************************************************************* 削除 */

// 削除する
async function execDelete01(self) {
    const result = await deleteTablelist('table-01-content', 'footer-01', 'search-box-01', '/recycle/maker/download/csv');

    if (!result) return;

    await updateTableDisplay('table-01-content', 'footer-01', 'search-box-01', origin, createTable01Content);
}

/******************************************************************************************************* ダウンロード */

async function execDownloadCsv01(self) {
    await downloadCsv('tabel-01-content', '/recycle/maker/download/csv', );
}

/******************************************************************************************************* 画面更新 */

// 最新のリストを取得
async function execUpdate() {
    const response = await fetch('/api/recycle/maker/get/list');

    if (!response.ok) {
        openMsgDialog("msg-dialog", "一覧の取得に失敗しました", "red");
        return null;
    }

    return await response.json();
}

async function execNumberSearch01(number) {
    const num = Number(number);
    if (Number.isNaN(num)) {
        console.log("数字じゃない")
    } else {
        if (num === 0) {
            await updateTableDisplay('table-01-content', 'footer-01', 'search-box-01', origin, createTable01Content);
        } else {
            const list = origin.filter(value => value.code >= num && value.code <= (num + 99));
            await updateTableDisplay('table-01-content', 'footer-01', 'search-box-01', list, createTable01Content);
        }
    }
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {
    // スピナー表示
    startProcessing();

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        const list = getCategoryFilterList();
        await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list, createTable01Content);
    }, false);

    // 作成フォームのグループコンボボックス
    const groupArea = document.getElementById('maker-group')
    createComboBoxWithTop(groupArea, groupComboList, "");

    // 画面更新
    await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin, createTable01Content);

    // スピナー消去
    processingEnd();
});