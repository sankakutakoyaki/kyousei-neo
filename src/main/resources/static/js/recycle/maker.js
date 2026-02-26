"use strict"

/******************************************************************************************************* 画面 */

// リスト画面の本体部分を作成する
function createTable01Content(tableId, list) {
    const table = document.getElementById(tableId);

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "recycleMakerId"
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
    newRow.insertAdjacentHTML('beforeend', '<td class="editable" data-col="group" data-edit-type="select" data-options-key="group" data-value="' + item.group + '">' + (item.groupName ?? "0") + '</td>');
    // 製造業者等名
    newRow.insertAdjacentHTML('beforeend', '<td class="editable" data-col="name" data-edit-type="text">' + (item.name ?? "-----") + '</td>');
    // 略称
    newRow.insertAdjacentHTML('beforeend', '<td class="editable" data-col="abbr-name" data-edit-type="text">' + (item.abbrName ?? "-----") + '</td>');
}

/******************************************************************************************************* 入力画面 */

// 登録画面を開く
async function execEdit01(self) {
    const config = MODE_CONFIG["01"];
    // フォーム画面を取得
    const form = document.getElementById(config.dialogId);
    const code = form.querySelector('[name="maker-code"]');
    code.value = "";
    form.querySelector('[name="maker-group"]').value = "0";
    form.querySelector('[name="maker-name"]').value = "";
    form.querySelector('[name="maker-abbr"]').value = "";

    // 入力フォームダイアログを開く
    openFormDialog(config.dialogId);
    code.focus();
}

/******************************************************************************************************* 保存 */

async function execCreate01(self) {
    
    const config = MODE_CONFIG["01"];
    const form = document.getElementById(config.formId); 

    if (!validateByConfig(form, { ...ERROR_CONFIG.recycleMaker, mode: "01" })) {
        return;
    }

    const formData = new FormData(form);
    const formdata = structuredClone(formEntity);
    formdata.code = Number(formData.get('maker-code'));
    formdata.group = Number(formData.get('maker-group'));
    formdata.name = formData.get('maker-name').trim();
    formdata.abbrName = formData.get('maker-abbr').trim();

    const result = execSave(config.tableId, config.footerId, config.searchId, formdata, createTable01Content);
    if (result) {
        closeFormDialog('form-dialog-01');

        await refleshDisplay();
        openMsgDialog("msg-dialog", "保存しました", "blue");
    }
}

// // 入力チェック
// function form01DataCheck(area) {
//     const errors = [];

//     // コードチェック
//     const codeInput = area.querySelector('input[name="maker-code"]');
//     if (codeInput) {
//         const code = Number(codeInput.value);

//         if (!codeInput.value) {
//             errors.push("コードを入力してください");
//         } else if (Number.isNaN(code) || code < 1 || code > 999) {
//             errors.push("コードは1〜999の間で入力してください");
//         }
//     }

//     // グループチェック
//     const group = area.querySelector('select[name="maker-group"]');
//     if (group && group.value === "0") {
//         errors.push("グループを選択してください");
//     }

//     // 製造業者名
//     const name = area.querySelector('input[name="maker-name"]');
//     if (name && !name.value.trim()) {
//         errors.push("製造業者等名を入力してください");
//     }

//     // 略称
//     const abbr = area.querySelector('input[name="maker-abbr"]');
//     if (abbr && !abbr.value.trim()) {
//         errors.push("略称を入力してください");
//     }

//     // エラー表示
//     if (errors.length > 0) {
//         openMsgDialog("msg-dialog", errors.join("\n"), "red");
//         const code = form.querySelector('[name="maker-code"]');
//         if (code != null) setFocusElement("msg-dialog", code);
//         return false;
//     }

//     return true;
// }

// TDが変更された時の処理
async function handleTdChange(editor) {
    const config = MODE_CONFIG["01"];
    const td = editor.closest('td.editable');
    if (!td) return;

    const type = td.dataset.col;
    const row = td.closest('tr');
    const id = row.dataset.id;

    const entity = origin.find(v => v.recycleMakerId == id);
    if (!entity) return;

    const ent = structuredClone(entity);
    switch (type) {
        case "group":
            ent.group = Number(editor.value);
            break;
        case "code":
            ent.code = Number(editor.value);
            break;
        case "name":
            ent.name = editor.value;
            break;
        case "abbr-name":
            ent.abbrName = editor.value;
            break;
    }

    const result = await execSave(config.tableId, ent);
    if (!result) reverseCode(config.tableId, ent.recycleMakerId, type);
}

// 保存処理
async function execSave(tableId, ent) {
    // 保存処理
    const result = await updateFetch("/api/recycle/maker/save", JSON.stringify(ent), token);

    if (result.ok) {
        // 画面更新
        refleshDisplay();
        // origin = await execUpdate();
        // await updateTableDisplay(tableId, footerId, searchId, origin, createContent);
        // 追加・変更行に移動
        scrollIntoTableList(tableId, result.data.recycleMakerId);
    // } else {
    //     reverseCode(tableId, ent.recycle_maker_id);
    //     // openMsgDialog("msg-dialog", result.message, "red");
        return true;
    } else {
        return false;
    }
}

// 変更した部分を元に戻す
function reverseCode(tableId, id, type) {
    const entity = origin.find(value => value.recycleMakerId === id);
    const tbl = document.getElementById(tableId);
    const row = tbl.querySelector('tr[data-id="' + entity.recycleMakerId + '"]');
    if (!row) return;

    const box = row.querySelector('td[data-col="' + type + '"]');
    if (!box) return;
    box.textContent = entity[type];
}

/******************************************************************************************************* 削除 */

// 削除する
async function execDelete01(self) {
    const config = MODE_CONFIG["01"];
    const result = await deleteTablelist(config.tableId, '/api/recycle/maker/delete');

    if (result.ok) {
        closeFormDialog('form-dialog-01');

        await refleshDisplay();
        openMsgDialog("msg-dialog", result.message, "blue");
    }
}

/******************************************************************************************************* ダウンロード */

async function execDownloadCsv01(self) {
    const config = MODE_CONFIG["01"];
    await downloadCsv(config.tableId, '/api/recycle/maker/download/csv', );
}

/******************************************************************************************************* 画面更新 */
// 画面を更新する
async function refleshDisplay() {
    // 最新のリストを取得
    const resultResponse = await fetch('/api/recycle/maker/get/list');
    origin = await resultResponse.json();

    // const cfg = MODE_CONFIG["01"];
    const btn = document.querySelector('[name="filter-area"] .normal-btn.selected');
    const num = btn.dataset.code;

    execNumberSearch01(num, btn);
}

function addSelectClassToEntity(areaId) {
    let item;
    if(areaId instanceof HTMLElement) {
        item = areaId;
    } else {
        item = document.getElementById(areaId);
    }
    if (item == null) return;
    item.classList.add('selected');
}

// // 最新のリストを取得
// async function execUpdate() {
//     const resultResponse = await fetch('/api/recycle/maker/get/list');

//     if (!resultResponse) {
//         openMsgDialog("msg-dialog", "一覧の取得に失敗しました", "red");
//         return origin;
//     }

//     return await resultResponse.json();
// }

async function execNumberSearch01(number, self) {
    const items = document.querySelectorAll('[name="filter-area"] .normal-btn');
    if (!items) return;

    items.forEach(value => {
        if (value.classList.contains('selected')) value.classList.remove('selected');
    })
    self.classList.add('selected');

    const num = Number(number);
    if (Number.isNaN(num)) {
        console.log("数字じゃない")
    } else {
        const config = MODE_CONFIG["01"];
        if (num === 0) {
            await updateTableDisplay(config.tableId, config.footerId, config.searchId, origin, createTable01Content);
        } else {
            const list = origin.filter(value => value.code >= num && value.code <= (num + 199));
            await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTable01Content);
        }
    }
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {
    // hamburgerItemAddSelectClass('.header-title', 'recycle');
    // hamburgerItemAddSelectClass('.normal-sidebar', 'maker');
    // // スピナー表示
    // startProcessing();

    const config = MODE_CONFIG["01"];
    // 検索ボックス入力時の処理
    document.getElementById(config.searchId).addEventListener('search', async function(e) {
        const list = getCategoryFilterList();
        await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTable01Content);
    }, false);

    // 作成フォームのグループコンボボックス
    const groupArea = document.getElementById('maker-group')
    createComboBoxWithTop(groupArea, groupComboList, "");

    setEnterFocus(config.formId);
    makeSortable(config.tableId);

    // 画面更新
    await updateTableDisplay(config.tableId, config.footerId, config.searchId, origin, createTable01Content);

    // // スピナー消去
    // processingEnd();
});