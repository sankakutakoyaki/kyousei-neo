"use strict"

/******************************************************************************************************* 画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const table = document.getElementById(tableId);

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "employeeId",
            onDoubleClick: (item) => {
                execEdit(item.employeeId, this);
            }
        });

        createTableRow(row, item);
    });
}

/******************************************************************************************************* テーブル行作成 */

// テーブル行を作成する
function createTableRow(newRow, item) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    // ID
    newRow.insertAdjacentHTML('beforeend', '<td class="link-cell" onclick="execEdit(' + item.employeeId + ', this)">' + String(item.employeeId).padStart(4, '0') + '</td>');
    // コード
    newRow.insertAdjacentHTML('beforeend', '<td class="editable text-center" data-col="code" data-edit-type="text">' + (item.code == 0 ? "": item.code) + '</td>');
    // 名前
    newRow.insertAdjacentHTML('beforeend', '<td><span class="kana">' + (item.fullNameKana ?? "-----") + '</span><br><span>' + item.fullName + '</span></td>');
    // 携帯番号
    newRow.insertAdjacentHTML('beforeend', '<td class="editable" data-col="phone" data-edit-type="text"><span>' + (item.phoneNumber ?? "登録なし") + '</span></td>');
    // 営業所名
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.officeName ?? "登録なし") + '</span></td>');
}

/******************************************************************************************************* 入力画面 */

// 従業員登録画面を開く
async function execEdit(id, self) {
    const panel = document.querySelector('.tab-panel.is-show');
    if (!panel) return;
    const tab = panel.dataset.panel

    const cfg = MODE_CONFIG[tab];

    // フォーム画面を取得
    const form = document.getElementById('form-dialog-01');

    // let entity = {};
    if (id > 0) {
        // 選択されたIDのエンティティを取得
        const result = await searchFetch('/api/employee/get/id', JSON.stringify({id:parseInt(id)}), token);
        if (!result?.ok) return;

        originEntity = structuredClone(result.data);
    } else {
        originEntity = structuredClone(formEntity);
    }

    setFormContent(form, originEntity);
    // 入力フォームダイアログを開く
    openFormDialog("form-dialog-01");
}

// コンテンツ部分作成
function setFormContent(form, entity) {
    applyComboConfig(form, entity, COMBO_CONFIG);
    applyFormConfig(form, entity, FORM_CONFIG);
}

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave() {
    const form = document.getElementById('form-01');
    const panel = document.querySelector('.tab-panel.is-show');
    if (!panel) return;
    const tab = panel.dataset.panel

    const cfg = MODE_CONFIG[tab];

    // 入力チェック
    if (!validateByConfig(form, ERROR_CONFIG[cfg.formId])) {
        return;
    }
  
    let tempEntity = {};

    const editedEntity = buildEntityFromForm(form, originEntity, SAVE_FORM_CONFIG);
    tempEntity = diffEntity(originEntity, editedEntity, UPDATE_FORM_CONFIG);

    if (originEntity.employeeId > 0) {
        if (Object.keys(tempEntity).length === 0) {
            openMsgDialog("msg-dialog", "変更がありません", "red");
            return;
        }
        tempEntity.version = originEntity.version;
    } else {
        tempEntity.companyId = ownCompanyId;
        tempEntity.category = cfg.category;        
    }

    tempEntity.employeeId = originEntity.employeeId;    

    const result = await updateFetch("/api/employee/save", JSON.stringify(tempEntity), token);

    if (result.ok) {
        closeFormDialog('form-dialog-01');

        await execUpdate();
        scrollIntoTableList(cfg.tableId, result.data);
        
        openMsgDialog("msg-dialog", result.message, "blue");
    }
}

// TDが変更された時の処理
async function handleTdChange(editor) {
    const td = editor.closest('td.editable');
    const col = td.dataset.col;
    const row = td.closest('tr');
    const id = row.dataset.id;

    const ent = origin.find(value => value.employeeId == id);
    switch (col) {
        case "code":
            if (existsSameCode(Number(editor.value))) {
                editor.value = ent.code;
                return;
            }
            break;
        case "phone":
            if (!checkPhoneNumber(editor)) {
                editor.value = ent.phoneNumber;
                return;
            }
            break;
        default:
            return;
    }

    await updateFetch('/api/employee/update/' + col, JSON.stringify({number:id,text:editor.value}), token);
    await execUpdate();
}

// リスト内に同じコードがないかチェック
function existsSameCode(code) {
    return origin.some(item =>
        item.code === code
    );
}

/******************************************************************************************************* 削除 */

async function execDelete(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];
    
    const result = await deleteTablelist(config.tableId, '/api/employee/delete');

    if (result.ok) {
        await execUpdate();
        openMsgDialog("msg-dialog", result.message, "blue");
    }
}

/******************************************************************************************************* ダウンロード */

async function execDownloadCsv(self) {
    const panel = self.closest('.tab-panel.is-show');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];

    await downloadCsv(config.tableId, '/api/employee/download/csv');
}

/******************************************************************************************************* 画面更新 */

async function execUpdate() {
    const tab = document.querySelector('li.is-active');
    if (tab == null) return;
    const config = MODE_CONFIG[tab.dataset.tab];

    // リスト取得
    const resultResponse = await fetch('/api/employee/get/list');
    const result = await resultResponse.json();
    origin = result.data;

    if (resultResponse.ok) {
        // 画面更新
        const list = origin.filter(value => { return value.category === config.category });
        await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);
    }
}

/******************************************************************************************************* 画面更新 */

async function execFilterDisplay(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;

    const config = MODE_CONFIG[tab];
    const list = origin.filter(value => { return value.category === config.category });
    await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {

    // 各タブの検索ボックス処理の登録とリスト作成をする
    for (const mode of Object.keys(MODE_CONFIG)) {
        let config = MODE_CONFIG[mode];
        if (config != null) {
            // 検索ボックス入力時の処理
            document.getElementById(config.searchId).addEventListener('search', async function(e) {
                await execFilterDisplay(e.currentTarget);
            }, false);
        }
        let list = origin.filter(value => { return value.category === config.category });
        await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);
    }

    // 郵便番号入力ボックスでエンターキーが押された時の処理を登録
    document.getElementById('employee-postal-code').addEventListener('keydown', async function (e) {
        await getAddress(e, 'employee-postal-code', 'employee-full-address');                                                                                                                                                                                                                                                                                                                                                       ;
    });

    // エンターフォーカス処理をイベントリスナーに登録する
    setEnterFocus("form-01");

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })
});