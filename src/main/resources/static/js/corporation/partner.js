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
    // 名前
    newRow.insertAdjacentHTML('beforeend', '<td name="name-cell"><span class="kana">' + item.nameKana + '</span><br><span>' + item.name + '</span></td>');
    switch (tab) {
        case "01":
            // 電話番号
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.telNumber ?? "登録なし") + '</span></td>');
            break;
        case "02":
            // 電話番号
            newRow.insertAdjacentHTML('beforeend', '<td class="editable" data-col="phone" data-edit-type="text"><span>' + (item.phoneNumber ?? "登録なし") + '</span></td>');
            break;
    }
    // メールアドレス
    newRow.insertAdjacentHTML('beforeend', '<td name="email-cell"><span>' + (item.email ?? "登録なし") + '</span></td>');
}

/******************************************************************************************************* 入力画面 */

// 取引先登録画面を開く
async function execEdit(id, self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];

    const form = document.getElementById(config.formId);

    let entity = {};
    if (id > 0) {
        const result = await searchFetch("/api/" + config.categoryName + "/get/id", JSON.stringify({id:parseInt(id)}), token);
        if (!result?.ok) return;

        entity = structuredClone(result);
    } else {
        entity = structuredClone(config.entity);
        switch (tab) {
            case "01":
                entity.category = config.category;
                break;
            case "02":
                if (code01.value == "") {
                    // ダイアログを閉じる
                    closeFormDialog(config.formDialogId);
                    // エラーメッセージ表示
                    openMsgDialog("msg-dialog", "会社を選択してください", "red");
                    setFocusElement(config.tableId, code01);
                    return;
                }
                entity.companyId = code01.value;
                break;
            default:
                break;
        }
    }

    setFormContent(form, entity, tab);
    openFormDialog(config.formDialogId);
}

// コンテンツ部分作成
function setFormContent(form, entity, tab) {

    const config = ID_CONFIG[tab];
    if (!config) return;

    const mode = MODE_CONFIG[tab];
    if (!mode) return;

    // 初期化処理（コンボ生成など）
    if (typeof config.init === "function") {
        config.init(mode, entity);
    }

    // 共通項目
    const commonFields = {
        "company-id": entity.companyId,
        "name": entity.name,
        "name-kana": entity.nameKana,
        "email": entity.email,
        "version": entity.version
    };

    Object.entries(commonFields).forEach(([k, v]) =>
        setValue(form, k, v)
    );

    // フィールド反映
    config.fields.forEach(name => {
        const key = name.replace(/-/g, "_"); // JS ↔ Entity 対応
        setValue(form, name, entity[key]);
    });

    // 表示制御
    document.querySelectorAll('[name="priceArea"]').forEach(el => {
        el.style.display = config.show?.includes("priceArea") ? "" : "none";
    });
}

function setValue(form, name, value) {
    const el = form.querySelector(`[name="${name}"]`);
    if (el) el.value = value ?? "";
}

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave() {
    const panel = document.querySelector(".is-active");
    const tab = panel.dataset.tab;

    const config = SAVE_CONFIG[tab] ?? SAVE_CONFIG["default"];
    const form = document.getElementById(config.formId);

    // 入力チェック
    if (!formDataCheck(form)) return;

    const formData = new FormData(form);
    const entity = config.baseEntity();

    // tab 固有項目
    Object.entries(config.fields).forEach(([key, converter]) => {
        const name = key.replace(/_/g, "-");
        entity[key] = converter(formData.get(name));
    });

    // 共通項目
    Object.entries(COMMON_FIELDS).forEach(([key, converter]) => {
        const name = key.replace(/_/g, "-");
        if (formData.has(name)) {
            entity[key] = converter(formData.get(name));
        }
    });

    // ユーザー名
    entity.user_name = user?.account ?? "kyousei@kyouseibin.com";

    // 保存
    const result = await updateFetch(
        config.url,
        JSON.stringify(entity),
        token,
        "application/json"
    );

    if (result.success) {
        openMsgDialog("msg-dialog", result.message, "blue");
        await execUpdate();
        scrollIntoTableList("table-" + tab + "-content", result.data);
        closeFormDialog(config.dialogId);
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
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
        case "phone":
            if (!checkPhoneNumber(editor)) {
                editor.value = ent.phone_number;
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
    const panel = self.closest('.tab-panel');
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
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];

    await downloadCsv(config.tableId, '/api/' + config.categoryName + '/download/csv');
}

/******************************************************************************************************* 画面更新 */

async function execUpdate() {
    const panel = document.querySelector('.is-active');
    const tab = panel.dataset.tab;
    const config = MODE_CONFIG[tab];
    if (!config) return;

    // entity ごとの元データ更新
    if (config.categoryName === "company") {
        await updateCompanyOrigin();
        await updateOfficeOrigin();

        const list = companyOrigin.filter(v => v.category === config.category);

        await updateTableDisplay(
            config.tableId,
            config.footerId,
            config.searchId,
            list,
            createTableContent
        );

        makeSortable(config.tableId);
        setPageTopButton(config.tableId);
        return;
    }

    if (config.categoryName === "office") {
        await updateOfficeOrigin();
        await updateOfficeTableDisplay();
        return;
    }

    if (config.categoryName === "staff") {
        await updateStaffTableDisplay();
        return;
    }
}

// company-tab 更新処理
async function updateCompanyOrigin() {
    await updateOrigin("company");
}

async function updateOrigin(type) {
    const config = ORIGIN_CONFIG[type];
    if (!config) return;

    // 一覧取得
    const listRes = await fetch(config.listUrl);
    window[config.originKey] = await listRes.json();

    // コンボ取得
    const comboRes = await fetch(config.comboUrl);
    const comboList = await comboRes.json();
    window[config.comboKey] = comboList;

    // コンボ反映
    const targets = getComboTargets(config.comboTargetIds);
    targets.forEach(target => {
        createComboBoxWithTop(target, comboList, "");
    });
}

/******************************************************************************************************* 画面更新 */

async function updateStaffTableDisplay() {
    const panel = document.querySelector('.tab-panel.is-show');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];
    if (!config) return;

    const code = panel.querySelector('[name="code"]');
    const com = panel.querySelector('[name="company"]');

    const codeValue = com.value;
    code.value = Number(codeValue) === 0 ? "": codeValue;
    if (codeValue < 1) return;

    const resultResponse = await fetch('api/staff/get/list');
    const result = await resultResponse.json();
    if (result != null) {
        let list = result.filter(function(value) { return value.companyId == codeValue});
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

function initCompanyInputs() {
    COMPANY_UI_CONFIG.forEach(cfg => {
        const codeElm = document.getElementById(cfg.codeId);
        const nameElm = document.getElementById(cfg.nameId);

        // code → combo
        bindCodeInput(codeElm, nameElm, cfg.onChange);

        // combo change
        initCompanyCombo(nameElm, cfg.onChange);
    });
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
        await updateTableDisplay(
            cfg.tableId,
            cfg.footerId,
            cfg.searchId,
            list,
            createTableContent
        );

        makeSortable(cfg.tableId);
        setPageTopButton(cfg.tableId);
    }

    initCompanyInputs();

    // タブ
    document.querySelectorAll('.tab-menu-item')
        .forEach(tab => tab.addEventListener('click', tabSwitch));
});