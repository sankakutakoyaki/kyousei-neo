"use strict"

/******************************************************************************************************* 入力画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const table = document.getElementById(tableId);
    const panel = table.closest('.tab-panel');
    const tab = panel.dataset.panel;

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "workingConditionsId",
            onDoubleClick: (item) => {
                execEdit(item.employeeId, item.fullName, tab);
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
    newRow.insertAdjacentHTML('beforeend', '<td name="id-cell" class="link-cell" onclick="execEdit(' + item.employeeId + ', \'' + item.fullName + '\', this)">' + String(item.employeeId).padStart(4, '0') + '</td>');
    // 名前
    newRow.insertAdjacentHTML('beforeend', '<td name="name-cell"><span class="kana">' + item.fullNameKana + '</span><br><span>' + item.fullName + '</span></td>');
    // 営業所名
    newRow.insertAdjacentHTML('beforeend', '<td name="office-cell"><span>' + (item.officeName ?? "登録なし") + '</span></td>');
}

/******************************************************************************************************* 入力画面 */

// 勤務条件登録画面を開く
async function execEdit(id, name, tab) {

    const result = await searchFetch('/api/working_conditions/get/employeeid', JSON.stringify({id:parseInt(id)}), token);
    if (!result?.ok) return;

    let entity = {};
    if (result.data.workingConditionsId > 0) {
        entity = structuredClone(result.data);
    } else {
        entity = structuredClone(formEntity);
        entity.employeeId = id;
        const config = MODE_CONFIG[tab];
        entity.category = config.category;
    }

    const employeeName = document.getElementById('employee-name');
    employeeName.textContent = String(id).padStart(4, '0') + " : " + name;

    const ctx = {
        employeeId: id,
        employeeName: name
    };

    // フォーム画面を取得
    const form = document.getElementById('form-dialog-01');
    applyViewConfig(ctx, entity, VIEW_CONFIG);
    applyFormConfig(form, entity, FORM_CONFIG);
    applyComboConfig(form, entity, COMBO_CONFIG);

    // 入力フォームダイアログを開く
    openFormDialog("form-dialog-01");
}

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave() {
    const form = document.getElementById('form-01');
    // エラーチェック
    if (formDataCheck(form) == false) {
        return;
    } else {
        const formdata = applySaveConfig(form, formEntity);
        formdata.user_name = user.account == null ? 'kyousei@kyouseibin.com': user.account;

        // 保存処理
        const result = await updateFetch("/api/working_conditions/save", JSON.stringify(formdata), token);
        if (result?.ok) {                        
            // ダイアログを閉じる
            closeFormDialog('form-dialog-01');
            // 画面更新
            await execUpdate();
            // 追加・変更行に移動
            const tableId = getTableIdByCategory(formdata.category);
            scrollIntoTableList(tableId, result.data.id);
            
            openMsgDialog("msg-dialog", result.message, "blue");
        }
    }
}

function getTableIdByCategory(category) {
    return Object.values(MODE_CONFIG)
        .find(c => c.category === category)
        ?.tableId ?? null;
}

// 入力チェック
function formDataCheck(area) {
    let msg = "";
    // 基本給が入力されていないとFalseを返す
    const salary = area.querySelector('input[name="base-salary"]');
    if (salary != null && salary.value == "") msg += '\n基本給または時給が入力されていません';
    // 交通費が入力されていないとFalseを返す
    const trans = area.querySelector('input[name="trans-cost"]');
    if (trans != null && trans.value == "") msg += '\n交通費が入力されていません';
    // 始業時間が入力されていないとFalseを返す
    const start = area.querySelector('input[name="phone-number"]');
    if (start != null && start.value == "") msg += '\n始業時間が入力されていません';
    // 終業時間が入力されていないとFalseを返す
    const end = area.querySelector('input[name="postal-code"]');
    if (end != null && end.value == "") msg += '\n終業時間が入力されていません';
    // エラーが一つ以上あればエラーメッセージダイアログを表示する
    if (msg != "") {
        openMsgDialog("msg-dialog", msg, "red");
        return false;
    }
    return true;
}

/******************************************************************************************************* 削除 */

async function execDelete(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;

    const config = MODE_CONFIG[tab];
    const result = await deleteTablelist(config.tableId, '/api/working_conditions/delete');

    if (result?.ok) {                
        await execUpdate();
        openMsgDialog("msg-dialog", result.message, "blue");
    }
}

/******************************************************************************************************* ダウンロード */

async function execDownloadCsv(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];
    await downloadCsv(config.tableId, '/api/working_conditions/download/csv');
}

/******************************************************************************************************* 画面更新 */

async function execUpdate() {
    const resultResponse = await fetch('/api/working_conditions/get/list');
    origin = await resultResponse.json();

    for (const mode of Object.values(MODE_CONFIG)) {
        const list = origin.filter(v => v.category === mode.category);
        await updateTableDisplay(mode.tableId, mode.footerId, mode.searchId, list, createTableContent);
    }
}

async function execFilterDisplay(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];
    const list = origin.filter(function(value) { return value.category == config.category });
    await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);
}

// データを category ごとに一度だけ分ける
function groupByCategory(list) {
  return list.reduce((map, v) => {
    (map[v.category] ??= []).push(v);
    return map;
  }, {});
}

// テーブル初期表示
async function renderAllTables(origin) {
    const grouped = groupByCategory(origin);

    for (const mode of Object.values(MODE_CONFIG)) {
        const list = grouped[mode.category] ?? [];
        
        makeSortable(mode.tableId);
        await updateTableDisplay(mode.tableId, mode.footerId, mode.searchId, list, createTableContent);
    }
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async  () => {

    // エンターフォーカス処理をイベントリスナーに登録する
    setEnterFocus("form-01");

    registerSearchEvents(MODE_CONFIG);
    await renderAllTables(origin);

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })
});