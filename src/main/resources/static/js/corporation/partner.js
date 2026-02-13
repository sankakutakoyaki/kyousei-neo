"use strict"

/******************************************************************************************************* 入力画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const table = document.getElementById(tableId);
    const panel = table.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const idKey = MODE_CONFIG[tab].dataId;

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: idKey,
            onDoubleClick: (item) => {
                execEdit(item[idKey], row);
            }
        });

        createTableRow(row, item, tab);
    });
}

/******************************************************************************************************* テーブル行作成 */

// テーブル行を作成する
function createTableRow(newRow, item, tab) {
    const idKey = MODE_CONFIG[tab].dataId;

    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    // ID
    newRow.insertAdjacentHTML('beforeend', '<td name="id-cell" class="link-cell" data-tab="' + tab + '" onclick="execEdit(' + item[idKey] + ', this)">' + String(item[idKey]).padStart(4, '0') + '</td>');
    if (tab === "02") {
        // コード
        newRow.insertAdjacentHTML('beforeend', '<td class="editable text-center" data-col="code" data-edit-type="text">' + (item.code == 0 ? "": item.code) + '</td>');
    }
    switch (tab) {
        case "01":
            // 名前
            newRow.insertAdjacentHTML('beforeend', '<td><span class="kana">' + (item.nameKana ?? "-----") + '</span><br><span>' + item.name + '</span></td>');
            // 電話番号
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.telNumber ?? "登録なし") + '</span></td>');
            // メールアドレス
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.email ?? "登録なし") + '</span></td>');
            break;
        case "02":
            // 名前
            newRow.insertAdjacentHTML('beforeend', '<td><span class="kana">' + (item.fullNameKana ?? "-----") + '</span><br><span>' + item.fullName + '</span></td>');
            // 電話番号
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.phoneNumber ?? "登録なし") + '</span></td>');
            // 会社名
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.companyName ?? "-----") + '</span></td>');
            break;
    }
}

/******************************************************************************************************* 入力画面 */

// 登録画面を開く
async function execEdit(id, self) {
    const panel = document.querySelector(".tab-panel.is-show");
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];

    const form = document.getElementById(config.formId);

    originEntity = {};
    if (id > 0) {
        const result = await searchFetch("/api/" + config.categoryName + "/get/id", JSON.stringify({id:parseInt(id)}), token);
        if (!result?.ok) return;

        originEntity = structuredClone(result.data);
    } else {
        originEntity = structuredClone(config.entity);
        switch (tab) {
            case "01":
                originEntity.category = config.category;
                break;
            case "02":
                if (code01.value == "") {
                    // ダイアログを閉じる
                    closeFormDialog(config.dialogId);
                    // エラーメッセージ表示
                    openMsgDialog("msg-dialog", "会社を選択してください", "red");
                    setFocusElement(config.tableId, code01);
                    return;
                }
                originEntity.companyId = code01.value;
                break;
            default:
                break;
        }
    }

    setFormContent(form, originEntity, tab);
    openFormDialog(config.dialogId);
}

// コンテンツ部分作成
function setFormContent(form, entity, tab) {

    // if (tab === "01") {
        // 表示制御
        document.querySelectorAll('[name="priceArea"]').forEach(el => {
            el.style.display = config.show?.includes("priceArea") ? "" : "none";
        });        
    // }
    
    if (tab === "02") {
        applyComboConfig(form, entity, COMBO_CONFIG);
    }
    applyFormConfig(form, entity, FORM_CONFIG[tab]);
}

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave() {
    const panel = document.querySelector(".tab-panel.is-show");
    const tab = panel.dataset.panel;

    const cfg = MODE_CONFIG[tab];
    const form = document.getElementById(cfg.formId);

    // 入力チェック
    if (!formDataCheck(form)) return;

    let tempEntity = {};

    const editedEntity = buildEntityFromForm(form, originEntity, SAVE_FORM_CONFIG[tab]);
    tempEntity = diffEntity(originEntity, editedEntity, UPDATE_FORM_CONFIG[tab]);

    if (originEntity[cfg.dataId] > 0) {
        if (Object.keys(tempEntity).length === 0) {
            openMsgDialog("msg-dialog", "変更がありません", "red");
            return;
        }
        tempEntity.version = originEntity.version;
    } else {
        tempEntity.category = cfg.category;
        if (tab === "02") tempEntity.companyId = originEntity.companyId;
    }

    tempEntity[cfg.dataId] = originEntity[cfg.dataId]; 

    // 保存
    const result = await updateFetch(cfg.url, JSON.stringify(tempEntity), token);

    if (result.ok) {
        closeFormDialog(cfg.dialogId);
        await execUpdate();
        scrollIntoTableList(cfg.tableId, result.data);
        
        openMsgDialog("msg-dialog", result.message, "blue");
    }
}

// 入力チェック
function formDataCheck(area) {
    let msg = "";
    // 名前が入力されていないとFalseを返す
    const name = area.querySelector('input[name="name"]');
    if (name != null && name.value == "") msg += '\n名称が入力されていません';
    // 電話番号チェック
    const tel = area.querySelector('input[name="tel-number"]');
    if (tel != null) {
        if (!checkPhoneNumber(tel)) msg += '\n電話番号に誤りがあります';
    }                
    // 携帯番号チェック
    const phone = area.querySelector('input[name="phone-number"]');
    if (phone != null) {
        if (!checkPhoneNumber(phone)) msg += '\n携帯番号に誤りがあります';
    }                
    // ファックス番号チェック
    const fax = area.querySelector('input[name="fax-number"]');
    if (fax != null) {
        if (!checkPhoneNumber(fax)) msg += '\nFAX番号に誤りがあります';
    }                
    // 郵便番号チェック
    const postal = area.querySelector('input[name="postal-code"]');
    if (postal != null) {
        if (!checkPostalCode(postal)) msg += '\n郵便番号に誤りがあります';
    }                
    // メールアドレスチェック
    const email = area.querySelector('input[name="email"]');
    if (email != null) {
        if (!checkMailAddress(email)) msg += '\nメールアドレスに誤りがあります';
    }                
    // webアドレスチェック
    const web = area.querySelector('input[name="web-address"]');
    if (web != null) {
        if (!checkWebAddress(web)) msg += '\nWEBアドレスに誤りがあります';
    }                
    // エラーが一つ以上あればエラーメッセージダイアログを表示する
    if (msg != "") {
        openMsgDialog("msg-dialog", msg, "red");
        return false;
    }
    return true;
}

// TDが変更された時の処理
async function handleTdChange(editor) {
    const td = editor.closest('td.editable');
    const type = td.dataset.col;
    const row = td.closest('tr');
    const id = row.dataset.id;

    const ent = origin.find(value => value.employeeId == id);
    switch (type) {
        case "code":
            if (existsSameCode(Number(editor.value))) {
                editor.value = ent.code;
                return;
            }
            break;
        default:
            return;
    }

    await searchFetch('/api/employee/update/' + type, JSON.stringify({primaryId:parseInt(id), secondaryId:parseInt(editor.value)}), token);
    await execUpdate();
}

// リスト内に同じコードがないかチェック
function existsSameCode(code) {
    return staffOrigin.some(item =>
        item.code === code
    );
}

/******************************************************************************************************* 削除 */

async function execDelete(self) {
    const panel = document.querySelector(".tab-panel.is-show");
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];

    const result = await deleteTablelist(config.tableId, '/api/' + config.categoryName + '/delete');

    if (result.ok) {                
        await execUpdate();
        openMsgDialog("msg-dialog", result.message, "blue");
    }
}

/******************************************************************************************************* ダウンロード */

async function execDownloadCsv(self) {
    const panel = document.querySelector(".tab-panel.is-show");
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];

    await downloadCsv(config.tableId, '/api/' + config.categoryName + '/download/csv');
}

/******************************************************************************************************* 画面更新 */

async function execUpdate() {
    const panel = document.querySelector(".tab-panel.is-show");
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];
    if (!config) return;

    // entity ごとの元データ更新
    if (config.categoryName === "company") {
        companyOrigin = await updateOrigin("company");
        await updateTableDisplay(config.tableId, config.footerId, config.searchId, companyOrigin, createTableContent);
        return;
    }

    if (config.categoryName === "employee") {
        await updateStaffTableDisplay();
        return;
    }
}

async function updateStaffTableDisplay() {
    const panel = document.querySelector('.tab-panel.is-show');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];
    if (!config) return;

    const code = panel.querySelector('[name="code"]');
    const com = panel.querySelector('[name="company"]');

    const codeValue = com.value;
    code.value = Number(codeValue) === 0 ? "": codeValue;

    const resultResponse = await fetch('api/employee/get/list/partner');
    const result = await resultResponse.json();

    let list = [];
    if (result.ok) {
        if (codeValue > 0) {
            list = result.data.filter(function(value) { return value.companyId == codeValue});
        } else {
            list = result.data;
        }
        
        await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);
    }
}

async function execFilterDisplay(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];

    const list = companyOrigin.filter(function(value) { return value.category == config.category });
    await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);
}

// コンボを再構成
async function updateOrigin(type) {
    const config = ORIGIN_CONFIG[type];
    if (!config) return;

    // 一覧取得
    const listRes = await fetch(config.listUrl);
    const originList = await listRes.json();
    window[config.originKey] = originList.data;

    // コンボ取得
    const comboRes = await fetch(config.comboUrl);
    const comboList = await comboRes.json();
    window[config.comboKey] = comboList.data;

    // コンボ反映
    const targets = getComboTargets(config.comboTargetIds);
    targets.forEach(target => {
        createComboBoxWithTop(target, comboList.data, "");
    });

    return window[config.originKey];
}

function refleshCode() {
    code01.value = code01.value !== name01.value ? name01.value: code01.value;
}

/******************************************************************************************************* 初期化時 */

window.addEventListener("load", async () => {

    Object.values(MODE_CONFIG).forEach(cfg => {
        setEnterFocus(cfg.formId);
        const el = document.getElementById(cfg.searchId);
        if (!el) return;

        el.addEventListener('search', async e => {
            await execFilterDisplay(e.currentTarget);
        });
    });

    // 郵便番号
    document.getElementById('postal-code')
        .addEventListener('keydown', async e => await getAddress(e,'postal-code','full-address'));

    for (const [tab, cfg] of Object.entries(MODE_CONFIG)) {
        if (!cfg.category) continue;
        
        let list = cfg.list.filter(value => { return value.category === cfg.category });
        await updateTableDisplay(cfg.tableId, cfg.footerId, cfg.searchId, list, createTableContent );
    }

    initCompanyInputs();

    // タブ
    document.querySelectorAll('.tab-menu-item')
        .forEach(tab => tab.addEventListener('click', tabSwitch));
});