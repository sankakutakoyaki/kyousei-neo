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
    newRow.insertAdjacentHTML('beforeend', '<td name="name-cell"><span class="kana">' + (item.nameKana ?? "-----") + '</span><br><span>' + item.name + '</span></td>');
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

async function execEdit(id, self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;

    switch (MODE_CONFIG[tab].domain) {
        case "company":
            return execEditCompany(id, self);
        case "office":
            return execEditOffice(id, self);
        case "staff":
            return execEditStaff(id, self);
    }
}

//　company系（tab 01〜04）
async function execEditCompany(id, self) {
    const { tab, config, form } = getEditContext(self);

    // let entity;
    if (id > 0) {
        originEntity = await fetchEntity(config, id);
        if (!originEntity) return;
    } else {
        originEntity = structuredClone(config.entity);
        originEntity.category = config.category;
    }

    openEditForm(form, originEntity, tab, config);
}

//　office系（tab 05）
async function execEditOffice(id, self) {
    const { tab, config, form } = getEditContext(self);

    // let entity;
    if (id > 0) {
        originEntity = await fetchEntity(config, id);
        if (!originEntity) return;
    } else {
        // const code = document.getElementById(config.codeBox);
        // if (!code.value) return closeFormDialog(config.dialogId);

        const company = document.getElementById(config.nameBox);
        if (company.value < 1) return closeFormDialog(config.dialogId);

        originEntity = structuredClone(config.entity);
        // entity.companyId = code.value;
        originEntity.companyId = company.value;

        const option = company.selectedOptions[0];
        originEntity.companyName = option ? option.text : "-----";
    }

    openEditForm(form, originEntity, tab, config);
}

//　staff系（tab 06）
async function execEditStaff(id, self) {
    const { tab, config, form } = getEditContext(self);

    // let entity;
    if (id > 0) {
        originEntity = await fetchEntity(config, id);
        if (!originEntity) return;
    } else {
        const company = document.getElementById(config.nameBox);
        const office  = document.getElementById(config.nameBox2);
        if (company.value < 1) return closeFormDialog(config.dialogId);

        originEntity = structuredClone(config.entity);
        originEntity.companyId = company.value;
        originEntity.officeId  = office.value;

        const coompanyOption = company.selectedOptions[0];
        originEntity.companyName = coompanyOption ? coompanyOption.text : "";
        const officeOption = office.selectedOptions[0];
        originEntity.officeName = officeOption ? officeOption.text : "";
    }

    openEditForm(form, originEntity, tab, config);
}

function getEditContext(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];
    const form = document.getElementById(config.formId);
    return { tab, config, form };
}

async function fetchEntity(config, id) {
    const result = await searchFetch(
        `/api/${config.domain}/get/id`,
        JSON.stringify({ id: parseInt(id) }),
        token
    );
    return result?.ok ? structuredClone(result.data) : null;
}

function openEditForm(form, entity, tab, config) {
    setFormContent(form, entity, tab);
    openFormDialog(config.dialogId);
}

// コンテンツ部分作成
function setFormContent(form, entity, tab) {
    if (tab === "06") applyComboConfig(form, entity, COMBO_CONFIG);
    applyFormConfig(form, entity, FORM_CONFIG[tab]);

    // 表示制御
    document.querySelectorAll('[name="priceArea"]').forEach(el => {
        el.style.display = config.show?.includes("priceArea") ? "" : "none";
    });
}

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave() {
    const panel = document.querySelector(".tab-panel.is-show");
    const tab = panel.dataset.panel;

    const cfg = MODE_CONFIG[tab];
    const form = document.getElementById(cfg.formId);

    // 入力チェック
    if (!validateByConfig(form, ERROR_CONFIG[cfg.formId])) {
        return;
    }

    const editedEntity = buildEntityFromForm(form, originEntity, SAVE_FORM_CONFIG[tab]);
    const tempEntity = diffEntity(originEntity, editedEntity, FORM_CONFIG[tab]);

    if (originEntity[cfg.dataId] > 0) {
        if (Object.keys(tempEntity).length === 0) {
            openMsgDialog("msg-dialog", "変更がありません", "red");
            return;
        }
        tempEntity.version = originEntity.version;
    } else {
        if (tab !== "05" && tab !== "06") tempEntity.category = cfg.category;
        if (tab === "05" || tab === "06") tempEntity.companyId = originEntity.companyId;
    }
    if (tab === "06") tempEntity.officeId = originEntity.officeId;
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

/******************************************************************************************************* 削除 */

async function execDelete(self) {
    const panel = document.querySelector(".tab-panel.is-show");
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];

    const result = await deleteTablelist(config.tableId, '/api/' + config.domain + '/delete');

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

    await downloadCsv(config.tableId, '/api/' + config.domain + '/download/csv');
}

/******************************************************************************************************* 画面更新 */

async function execUpdate() {
    const panel = document.querySelector(".tab-panel.is-show");
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];
    if (!config) return;

    switch (MODE_CONFIG[tab].domain) {
        case "company":
            return execUpdateCompanyDisplay(tab);
        case "office":
            return execUpdateOfficeDisplay();
        case "staff":
            return execUpdateStaffDisplay();
    }
}

// tab1〜4画面更新
async function execUpdateCompanyDisplay(tab) {
    const config = MODE_CONFIG[tab];
    const resultResponse = await fetch('/api/client/get/list');
    const result = await resultResponse.json();
    companyOrigin = result.data;

    await execFilterDisplay(tab);
}

// 画面をフィルターにとおす
async function execFilterDisplay(tab) {
    const config = MODE_CONFIG[tab];

    const list = companyOrigin.filter(function(value) { return value.category == config.category });
    await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);
}

// 共通更新関数
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
    if (!result.ok) return;

    let list = result.data.filter(v => v.companyId === codeValue);

    if (extraFilter) {
        list = extraFilter(list, panel);
    }

    await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);
}

// tab5画面更新
async function execUpdateOfficeDisplay() {
    const resultResponse = await fetch('/api/office/get/list');
    const result = await resultResponse.json();
    officeOrigin = result.data;
    await updateTableByCompany({apiUrl: 'api/office/get/client/list'});
}

// tab6画面更新
async function execUpdateStaffDisplay() {
    const config = MODE_CONFIG["06"];
    const companyId = Number(document.getElementById(config.nameBox)?.value);
    const officeId  = Number(document.getElementById(config.nameBox2)?.value);

    if (!companyId) {
        clearStaffTable(); // 未選択時はクリア
        return;
    }

    await updateTableByCompany({
        apiUrl: 'api/staff/get/list',
        companyId,
        extraFilter: (list) => {
            if (officeId > 0) {
                return list.filter(v => v.officeId === officeId);
            }
            return list;
        }
    });
}

// tab6画面クリア
async function clearStaffTable() {
    const cfg = MODE_CONFIG["06"];
    await updateTableDisplay(cfg.tableId, cfg.footerId, cfg.searchId, {}, createTableContent);
}

//　company変更時にofficeを更新する
async function createOfficeComboBoxFromClient() {
    const panel = document.querySelector('.tab-panel.is-show');
    const companySelect = panel.querySelector('select[name="company"]');
    const officeSelect  = panel.querySelector('select[name="office"]');

    // 初期 office 作成
    await updateOfficeCombo(companySelect, officeSelect);

    // company 変更
    companySelect.onchange = () => {
        createOfficeComboBoxFromClient();
        execUpdate();
    };

    // office 変更
    officeSelect.onchange = () => {
        execUpdate();
    };
}

//　officeを更新処理
async function updateOfficeCombo(companySelect, officeSelect) {
    if (!companySelect || !officeSelect) return;

    const companyId = Number(companySelect.value);
    if (!companyId) {
        // company 未選択 → office を初期化
        createComboBoxWithTop(officeSelect, [], "");
        return;
    }

    const res = await fetch('api/office/get/list');
    const list = await res.json();
    if (!Array.isArray(list)) return;

    const offices = list
        .filter(o => o.companyId === companyId)
        .map(o => ({
            number: o.officeId,
            text: o.name
        }));

    createComboBoxWithTop(officeSelect, offices, "");
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
        await updateTableDisplay(cfg.tableId, cfg.footerId, cfg.searchId, list, createTableContent);
        makeSortable(cfg.tableId);
        setPageTopButton(cfg.tableId);
    }

    companyComboList = companyOrigin.map(item => ({number:item.companyId, text:item.name}));
    officeComboList = officeOrigin.map(item => ({number:item.officeId, text:item.name}));
    initCompanyInputs();

    // タブ
    document.querySelectorAll('.tab-menu-item')
        .forEach(tab => tab.addEventListener('click', e => { tabSwitch(e); execUpdate; }));

    processingEnd();
});