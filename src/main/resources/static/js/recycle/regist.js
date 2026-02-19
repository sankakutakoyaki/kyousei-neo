"use strict"

/******************************************************************************************************* 画面 */

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
            idKey: "recycleId",
            validCheck: item => item.lossDate !== null,
            onDoubleClick: (item) => {
                execEdit(item.recycleId, this);
            }
        });
        row.setAttribute('data-value', item.recycleNumber);
        createTableRow(row, item, tab);
    });
}

/******************************************************************************************************* テーブル行作成 */

// テーブル行を作成する
function createTableRow(newRow, item, tab) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    // お問合せ管理票番号
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.moldingNumber ?? "") + '</span></span></td>');
    switch (tab) {
        case "01":
            // 使用日　引渡日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.useDate ?? "-----") + '</span><br><span>' + (item.deliveryDate ?? "-----") + '</span></td>');
            // 発送日　ロス処理日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.shippingDate ?? "-----") + '</span><br><span>' + (item.lossDate ?? "-----") + '</span></td>');
            // 登録日　伝票番号
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.registDate ?? "-----") + '</span><br><span>' + (item.slipNumber > 0 ? item.slipNumber : "-----") + '</span></td>');
            break;
        case "02":
            // 使用日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.useDate ?? "-----") + '</span></td>');
            break;
        case "03":
            // 引渡日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.deliveryDate ?? "-----") + '</span></td>');
            break;
        case "04":
            // 発送日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.shippingDate ?? "-----") + '</span></td>');
            break;
        case "05":
            // ロス処理日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.lossDate ?? "-----") + '</span></td>');
            break;
    }
    switch (tab) {
        case "01":
            // 製造業者等名
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.makerName ?? "") + '</span><br><span>' + (item.itemName ?? "") + '</span></td>');
            break;
        case "02":
        case "03":
        case "04":
        case "05":
            // 製造業者等名
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.makerName ?? "") + '</span></td>');
            // 品目・料金区分
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.itemName ?? "") + '</span></td>');
            break;
        default:
            break;
    }
    // 小売業者
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.companyName ?? "") + (" : " + (item.officeName ?? "-----")) + '</span></td>');
}

/******************************************************************************************************* 入力画面 */

// 登録画面を開く
async function execEdit(id, self) {
    const area = self.closest('[data-panel]');
    if (!area) return;
    const tab = area.dataset.panel;
    const cfg = MODE_CONFIG[tab];

    // フォーム画面を取得
    const form = document.getElementById('form-dialog-01');   

    tempElm = {};

    // 入力フォームダイアログを開く
    openFormDialog("form-dialog-01");
    setEnterFocus('form-01');
}

/******************************************************************************************************* リセット */

// 入力フォームの内容をリセットする
function resetFormInput(tab) {
    const cfg = MODE_CONFIG[tab];
    
    resetFormInputValue(cfg.panel);

    // cfg.panel.querySelectorAll('[data-reset]').forEach(el => {
    //     if ('value' in el) {
    //         // input / select / textarea
    //         el.value = el.type === 'number' ? 0 : '';
    //     } else {
    //         // span / div など
    //         el.textContent = '';
    //     }
    // });

    cfg.number?.focus();
}

/******************************************************************************************************* 取得・検証 */

// お問合せ管理票番号を取得して検証
async function execNumberBlur(e, tab) {
    if (e.currentTarget == null) return;

    const config = MODE_CONFIG[tab];
    if (config.number.value == null) return;

    // 入力値が正しいかチェックする
    const number = checkNumber(config.number.value);

    // Null、または不正な値はエラー
    if (number == null) {
        config.number.value = null;
        return;   
    } else if (!number) {
        openMsgDialog("msg-dialog", "不正な番号です。", 'red');
        setFocusElement("msg-dialog", config.number);
        return;
    }

    // 0000-00000000-0 に成型する
    const molNumber = moldingNumber(number);

    tempElm.recycleNumber = number;
    tempElm.moldingNumber = molNumber;

    config.number.value = molNumber;

    if (tab !== "01" && tab !== "02") {
        config.regBtn.click();
    }
}

// コードから名前を表示
function execCodeBlur(e, tab, mode) {
    e.preventDefault();
    if (!e.currentTarget) return;

    const modeCfg = MODE_CONFIG[tab][mode];
    const codeEl = modeCfg.code;
    const nameEl = modeCfg.name;

    if (codeEl.value === "" || isNaN(codeEl.value)) {
        clearFields(codeEl, nameEl);
        return;
    }

    const code = Number(codeEl.value);
    
    const codeCfg = CODE_CONFIG[mode];
    const entity = codeCfg.list.find(v => v.number === code);

    if (entity && entity.id > 0) {
        nameEl.value = entity.text;
        tempElm[codeCfg.idKey] = entity.id;
    } else {
        clearFields(codeEl, nameEl);
        openMsgDialog("msg-dialog", "コードが登録されていません", "red");
        setFocusElement("msg-dialog", codeEl);
    }
}

function clearFields(...els) {
    els.forEach(el => el.value = "");
}

/******************************************************************************************************* 削除 */

// 削除する
async function execDelete(self) {
    const area = self.closest('[data-panel]');
    if (!area) return;
    const config = MODE_CONFIG[area.dataset.panel];

    const result = await deleteTablelist(config.tableId, '/api/recycle/delete');

    if (result.ok) {
        await refleshDisplay();
        openMsgDialog("msg-dialog", result.message, "blue");
    }
}

/******************************************************************************************************* 保存 */

// 新規作成処理
async function execRegist(tab) {
    const cfg = MODE_CONFIG[tab];
    
    // if (!validateByConfig(cfg.panel,ERROR_CONFIG.recycle)) {
    //     return;
    // }
    if (!validateByConfig(cfg.panel, ERROR_CONFIG.recycle, cfg.dateKey)) {
        return;
    }

    cfg.regBtn.disabled = true;

    const data = buildEntityFromElement(cfg.panel, tempElm, SAVE_FORM_CONFIG[tab]);
    const result = await updateFetch(cfg.url, JSON.stringify(data), token);

    if (result.ok && result.data !== null) {
        itemList.push(result.data);
        await updateTableDisplay(cfg.tableId, cfg.footerId, cfg.searchId, itemList, createTableContent);
        cfg.number.focus();
    } else {
        cfg.number.value = null;
        setFocusElement("msg-dialog", cfg.number);
    }

    resetFormInput(tab);
    cfg.regBtn.disabled = false;
}

// 更新処理
async function execBulkUpdate(self) {

    const cfg = MODE_CONFIG["06"];
    
    const form = document.getElementById(cfg.formId);
    const formdata = buildEntityFromForm(form, tempElm, SAVE_FORM_CONFIG["06"]);

    if (!formdata) return;

    const list = getAllSelectedValues(cfg.tableId);
    formdata.recycleNumbers = list.values;

    const result = await updateFetch("/api/recycle/update", JSON.stringify(formdata), token);
    if (result.ok) {
        closeFormDialog(cfg.dialogId);

        // 画面更新
        await refleshDisplay();
        openMsgDialog("msg-dialog", result.message, "blue");
    }
}

/******************************************************************************************************* ダウンロード */

// CSVファイルをダウンロードする
async function execDownloadCsv(self) {
    const config = MODE_CONFIG["06"];
    await downloadCsv(config.tableId, '/api/recycle/download/csv');
}

/******************************************************************************************************* 画面更新 */

// 画面を更新する
async function refleshDisplay() {
    const cfg = MODE_CONFIG["01"];
    const start = document.getElementById(cfg.startId);
    const end = document.getElementById(cfg.endId);

    if (!start || start.value === undefined || start.value === "") {
        execSpecifyPeriod("today", 'start-date01', 'end-date01');
        openMsgDialog("msg-dialog", "開始日が入力されていません", "red");
        setFocusElement("msg-dialog", start);
        return;        
    }
    
    if (!end || end.value === undefined || end.value === "") {
        execSpecifyPeriod("today", 'start-date01', 'end-date01');
        openMsgDialog("msg-dialog", "終了日が入力されていません", "red");
        setFocusElement("msg-dialog", end);
        return;   
    }
    
    const data = {
        start: start.value,
        end: end.value,
        type: document.getElementById('date-category').value
    };

    const result = await searchFetch(cfg.url, JSON.stringify(data), token);
    if (result.ok) {
        origin = result.data;
        await updateTableDisplay(cfg.tableId, cfg.footerId, cfg.searchId, origin, createTableContent);
    }
}

async function searchNumber() {
    const cfg = MODE_CONFIG["01"];
    const number = document.getElementById('number-box-01');
    if (!number) return;

    if (number.value == "") {
        refleshDisplay();
        return;
    }

    const result = await searchFetch("/api/recycle/get/number", JSON.stringify({value:tempElm.recycleNumber}), token);
    if (result.ok) {
        if (result.data == null) {
            openMsgDialog("msg-dialog", "お問合せ管理表番号が存在しません。", "red");
            setFocusElement("msg-dialog", number);
        }
        itemList = [];
        itemList.push(result.data);

        const dateCategoryArea = document.querySelector('select[name="date-category"]');
        setComboboxSelected(dateCategoryArea, "update");

        const start = document.getElementById(cfg.startId);
        const end = document.getElementById(cfg.endId);
        start.value = result.data.updateDate;
        end.value = result.data.updateDate;

        await updateTableDisplay(cfg.tableId, cfg.footerId, cfg.searchId, itemList, createTableContent);
    }
}

async function clearTable(e) {
    itemList = [];
    tempElm = structuredClone(formEntity);

    const tab = e.currentTarget.dataset.tab;
    const cfg = MODE_CONFIG[tab];
    resetFormInput(tab);
    cfg.start.focus();

    switch (tab) {
        case "01":
            refleshDisplay();
        case "02":
        case "03":
        case "04":
        case "05":
            itemList = [];
            await updateTableDisplay(cfg.tableId, cfg.footerId, cfg.searchId, itemList, createTableContent);
    }
}

/******************************************************************************************************* チェック時の処理 */

// 品目固定チェックボックスをクリックした時の処理
function itemCheckedAfter(e) {
    if (!e.currentTarget) return;

    const itemCode = document.getElementById("item-code02");
    const itemName = document.getElementById("item-name02");

    if (e.currentTarget.checked) {
        // チェックON → 固定 → reset対象から外す
        itemCode.readOnly = true;
        itemCode.removeAttribute("tabindex");
        itemCode.removeAttribute("data-reset");
        itemName.removeAttribute("data-reset");
    } else {
        // チェックOFF → reset対象に戻す
        itemCode.readOnly = false;
        itemCode.setAttribute("tabindex", "6");
        itemCode.setAttribute("data-reset");
        itemName.setAttribute("data-reset");
    }
    resetEnterFocus();
}

// 選択した会社の支店をコンボボックスに登録する
async function updateOfficeCombo(tab) {
    const cfg = COMPANY_COMBO_CONFIG[tab];

    const companyArea = document.getElementById(cfg.nameId)
    const officeArea = document.getElementById(cfg.officeId)
    const selectId = companyArea.value;  

    const list = officeComboList.filter(value => { return value.companyId === Number(selectId) }).map(item => ({number:item.officeId, text:item.name}));
    createComboBoxWithTop(officeArea, list, "");
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {

    // 日付フィルターコンボボックス
    const dateCategoryArea = document.querySelector('select[name="date-category"]');
    createComboBoxValueString(dateCategoryArea, dateComboList);

    const date = new Date();

    for (const tab of Object.keys(MODE_CONFIG)) {
        let config = MODE_CONFIG[tab];
        if (config != null) {
            // お問合せ管理票番号入力ボックスのフォーカスが外れた時の処理を登録
            if (config.number) {
                config.number.addEventListener('blur', function (e) { execNumberBlur(e, tab); });
            }

            let panel = document.querySelector('[data-panel="' + tab + '"]');
            setEnterFocus(panel);

            if (tab === "02" || tab === "06") {
                // メーカーコード入力ボックスのフォーカスが外れた時の処理を登録
                config.makerCode.addEventListener('blur', function (e) { execCodeBlur(e, tab, 'maker'); });
                // 品目コード入力ボックスのフォーカスが外れた時の処理を登録
                config.itemCode.addEventListener('blur', function (e) { execCodeBlur(e, tab, 'item'); });
            }

            if (tab === "01") {
                execSpecifyPeriod("today", 'start-date01', 'end-date01');
            } else {
                if (config.start) {
                    config.start.value = date.toLocaleDateString('sv-SE');
                }
            }
        }
    }

    // キーボードの上下で日付を操作する
    enableDateArrowControl(document);

    // 品目固定チェックボッスクス押下時の処理を登録
    document.getElementById("fix-item-checkbox").addEventListener("change", function (e) {
        itemCheckedAfter(e);
    });

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        await refleshDisplay();
    }, false);

    initCompanyInputs();
    const companyCombo = document.getElementById('company02');
    setComboboxSelected(companyCombo, 1000);
    updateOfficeCombo("02");

    const disposalSiteCombo = document.getElementById('disposal-site03');
    createComboBoxWithTop(disposalSiteCombo, disposalSiteComboList, "");
    setComboboxSelected(disposalSiteCombo, 1066);

    const cfg = MODE_CONFIG["01"];
    cfg.start.focus();

    setCancelFunction("form-dialog-01", resetFormInput("06")); 

    // 画面更新
    await refleshDisplay();

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', e => { tabSwitch(e); clearTable(e); });
    })
});