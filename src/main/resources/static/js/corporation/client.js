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
    // 名前
    newRow.insertAdjacentHTML('beforeend', '<td name="name-cell"><span class="kana">' + item.nameKana + '</span><br><span>' + item.name + '</span></td>');
    switch (tab) {
        case "06":
            // 電話番号
            newRow.insertAdjacentHTML('beforeend', '<td name="tel-cell"><span>' + (item.phoneNumber ?? "登録なし") + '</span></td>');
            // 支店名
            newRow.insertAdjacentHTML('beforeend', '<td name="office-cell"><span>' + (item.officeName ?? "登録なし") + '</span></td>');
            break;
        default:
            // 電話番号
            newRow.insertAdjacentHTML('beforeend', '<td name="tel-cell"><span>' + (item.telNumber ?? "登録なし") + '</span></td>');
            // メールアドレス
            newRow.insertAdjacentHTML('beforeend', '<td name="email-cell"><span>' + (item.email ?? "登録なし") + '</span></td>');
            break;
    }
}

/******************************************************************************************************* 入力画面 */

// 取引先登録画面を開く
async function execEdit(id, self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];

    const form = document.getElementById(config.formId);

    // let entity = {};
    originEntity = {};
    if (id > 0) {
        const result = await searchFetch("/api/" + config.categoryName + "/get/id", JSON.stringify({id:parseInt(id)}), token);
        if (!result?.ok) return;
        originEntity = structuredClone(result.data);
    } else {
        originEntity = structuredClone(config.entity);
        switch (tab) {
            case "01":
            case "02":
            case "03":
            case "04":
                originEntity.category = config.category;
                break;
            case "05":
                const code51 = document.getElementById(config.codeBox);
                if (code51.value == "") {
                    // ダイアログを閉じる
                    closeFormDialog(config.formDialogId);
                    return;
                }
                originEntity.companyId = code51.value;
                break;
            case "06":
                const name61 = document.getElementById(config.nameBox);
                const name62 = document.getElementById(config.nameBox2);
                if (name61.value < 1) {
                    // ダイアログを閉じる
                    closeFormDialog(config.formDialogId);
                    return;
                }
                originEntity.companyId = name61.value;
                originEntity.officeId = name62.value;
                const option = name61.selectedOptions[0];
                originEntity.companyName = option ? option.text : "";

                break;
            default:
                break;
        }
    }

    setFormContent(form, originEntity, tab);
    openFormDialog(config.formDialogId);
}

// コンテンツ部分作成
function setFormContent(form, entity, tab) {

    // const config = ID_CONFIG[tab];
    // if (!config) return;

    // const mode = MODE_CONFIG[tab];
    // if (!mode) return;

    // // 初期化処理（コンボ生成など）
    // if (typeof config.init === "function") {
    //     config.init(mode, entity);
    // }

    // // 共通項目
    // const commonFields = {
    //     "company-id": entity.companyId,
    //     "name": entity.name,
    //     "name-kana": entity.nameKana,
    //     "email": entity.email,
    //     "version": entity.version
    // };

    // Object.entries(commonFields).forEach(([k, v]) =>
    //     setFormContentValue(form, k, v)
    // );

    // // フィールド反映
    // config.fields.forEach(name => {
    //     // const key = name.replace(/-/g, "_"); // JS ↔ Entity 対応
    //     const key = kebabToCamel(name);
    //     // const key = camelToKebab(name);
    //     setFormContentValue(form, name, entity[key]);
    // });

    if (tab === "06") applyComboConfig(form, entity, COMBO_CONFIG);
    applyFormConfig(form, entity, FORM_CONFIG[tab]);

    // 表示制御
    document.querySelectorAll('[name="priceArea"]').forEach(el => {
        el.style.display = config.show?.includes("priceArea") ? "" : "none";
    });
}

// function setValue(form, name, value) {
//     const el = form.querySelector(`[name="${name}"]`);
//     if (!el) return;

//     const v = value ?? "";

//     if ('value' in el) {
//         // input / select / textarea
//         el.value = v;
//     } else {
//         // span / div / p など
//         el.textContent = v;
//     }
// }

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave() {
    const panel = document.querySelector(".tab-panel.is-show");
    const tab = panel.dataset.panel;

    // const cfg = SAVE_CONFIG[tab] ?? SAVE_CONFIG["default"];
    const cfg = MODE_CONFIG[tab];
    const form = document.getElementById(cfg.formId);

    // 入力チェック
    if (!formDataCheck(form)) return;

    // const formData = new FormData(form);
    // const entity = config.baseEntity();

    // // tab 固有項目
    // Object.entries(config.fields).forEach(([key, field]) => {
    //     const name = field.name ?? camelToKebab(key);
    //     const raw  = formData.get(name);
    //     entity[key] = field.convert(raw);
    // });

    // // 共通項目
    // Object.entries(COMMON_FIELDS).forEach(([key, converter]) => {
    //     // const name = key.replace(/_/g, "-");
    //     const name = camelToKebab(key);
    //     if (formData.has(name)) {
    //         entity[key] = converter(formData.get(name));
    //     }
    // });

    // // ユーザー名
    // entity.userName = user?.account ?? "guest@kyouseibin.com";

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
        openMsgDialog("msg-dialog", result.message, "blue");

        await execUpdate();
        scrollIntoTableList(cfg.tableId, result.data);
        closeFormDialog(cfg.dialogId);
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

/******************************************************************************************************* 削除 */

async function execDelete(self) {
    const panel = document.querySelector(".tab-panel.is-show");
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];

    const result = await deleteTablelist(config.tableId, '/api/' + config.categoryName + '/delete');

    if (result.ok) {
        openMsgDialog("msg-dialog", result.message, "blue");
        await execUpdate();
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
        officeOrigin = await updateOrigin("office");

        const list = companyOrigin.filter(v => v.category === config.category);

        await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);

        // makeSortable(config.tableId);
        // setPageTopButton(config.tableId);
        return;
    }

    if (config.categoryName === "office") {
        officeOrigin = await updateOrigin("office");
        await updateOfficeTableDisplay();
        return;
    }

    if (config.categoryName === "staff") {
        await updateStaffTableDisplay();
        return;
    }
}

// コンボ更新処理
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

    return window[config.originKey];
}

//　共通更新関数
async function updateTableByCompany({apiUrl, extraFilter, requireCompany = false}) {
    const panel = document.querySelector('.tab-panel.is-show');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];
    if (!config) return;

    const code = panel.querySelector('[name="code"]');
    const com = panel.querySelector('[name="company"]');

    const codeValue = Number(com.value);
    code.value = codeValue === 0 ? "" : codeValue;

    if (requireCompany && codeValue < 1) return;

    const res = await fetch(apiUrl);
    const result = await res.json();
    if (!result) return;

    let list = result.filter(v => v.companyId === codeValue);

    if (extraFilter) {
        list = extraFilter(list, panel);
    }

    await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);
}

//　支店画面更新
async function updateOfficeTableDisplay() {
    await updateTableByCompany({apiUrl: 'api/office/get/client/list'});
}

// スタッフ画面更新
async function updateStaffTableDisplay() {
    await updateTableByCompany({
        apiUrl: 'api/staff/get/list',
        requireCompany: true,
        extraFilter: (list, panel) => {
            const ofc = panel.querySelector('[name="office"]');
            const officeId = Number(ofc.value);
            return officeId > 0
                ? list.filter(v => v.officeId === officeId)
                : list;
        }
    });
}

// 画面をフィルターにとおす
async function execFilterDisplay(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];

    const list = companyOrigin.filter(function(value) { return value.category == config.category });
    await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);

}

// // 初期化処理登録
// function initCompanyInputs() {
//     COMPANY_UI_CONFIG.forEach(cfg => {
//         const codeElm = document.getElementById(cfg.codeId);
//         const nameElm = document.getElementById(cfg.nameId);

//         // code → combo
//         bindCodeInput(codeElm, nameElm, cfg.onChange);

//         // combo change
//         initCompanyCombo(nameElm, cfg.onChange);
//     });
// }

// 選択した会社の支店をコンボボックスに登録する
async function createOfficeComboBoxFromClient() {
    const panel = document.querySelector('.tab-panel.is-show');
    const resultResponse = await fetch('api/office/get/list');
    const result = await resultResponse.json();
    if (result != null) {
        const companyArea = panel.querySelector('select[name="company"]');
        const officeArea = panel.querySelector('select[name="office"]');
        const selectId = companyArea.value;  
        const list = result.filter(value => { return value.companyId === Number(selectId) }).map(item => ({number:item.officeId, text:item.name}));
        createComboBoxWithTop(officeArea, list, "");
        officeArea.onchange = () => updateStaffTableDisplay();
    }
}

// 選択した会社の支店をコンボボックスに登録する
async function createFormOfficeComboBox(formId, id) {
    const resultResponse = await fetch('api/office/get/list');
    const result = await resultResponse.json();
    if (result != null) {
        const form = document.getElementById(formId);
        const companyArea = form.querySelector('[name="company-id"]');
        const officeArea = form.querySelector('select[name="office"]');
        const list = result.filter(value => { return value.companyId === Number(companyArea.value) }).map(item => ({number:item.officeId, text:item.name}));
        createComboBoxWithTop(officeArea, list, "");
        setComboboxSelected(officeArea, id);
    }
}

function getComboTargets(targetIds) {
    return targetIds
        .map(id => document.getElementById(id))
        .filter(elm => elm !== null);
}

/******************************************************************************************************* 初期化時 */

window.addEventListener("load", async () => {

    startProcessing();

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

        const list = companyOrigin.filter(v => v.category === cfg.category);

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

    processingEnd();
});