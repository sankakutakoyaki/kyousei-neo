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
            validCheck: item => item.lossDate !== "9999-12-31",
            onDoubleClick: (item) => {
                execEdit(item.recycleId, this);
            }
        });

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
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.useDate == '9999-12-31' ? "-----": item.useDate) + '</span><br><span>' + (item.deliveryDate == '9999-12-31' ? "-----": item.deliveryDate) + '</span></td>');
            // 発送日　ロス処理日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.shippingDate == '9999-12-31' ? "-----": item.shippingDate) + '</span><br><span>' + (item.lossDate == '9999-12-31' ? "-----": item.lossDate) + '</span></td>');
            // 登録日　伝票番号
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.registDate == '9999-12-31' ? "-----": item.registDate) + '</span><br><span>' + (item.slipNumber ?? "-----") + '</span></td>');
            break;
        case "02":
            // 使用日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.useDate == '9999-12-31' ? "-----": item.useDate) + '</span></td>');
            break;
        case "03":
            // 引渡日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.deliveryDate == '9999-12-31' ? "-----": item.deliveryDate) + '</span></td>');
            break;
        case "04":
            // 使用日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.shippingDate == '9999-12-31' ? "-----": item.shippingDate) + '</span></td>');
            break;
        case "05":
            // 使用日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.lossDate == '9999-12-31' ? "-----": item.lossDate) + '</span></td>');
            break;
    }
    // 製造業者等名
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.makerName ?? "") + '</span></td>');
    // 品目・料金区分
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.itemName ?? "") + '</span></td>');
    // 小売業者
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.companyName ?? "") + (item.officeName ?? "") + '</span></td>');
}

/******************************************************************************************************* 入力画面 */

// 登録画面を開く
async function execEdit(id, tab, self) {
    // let entity = {};
    // const config = MODE_CONFIG[tab];
    // const form = document.getElementById(config.dialogId);

    // if (id > 0) {
    //     // 選択されたIDのエンティティを取得
    //     const result = await searchFetch('/api/recycle/get/id', JSON.stringify({id:id}), token);

    //     if (!result.ok) {
    //         openMsgDialog("msg-dialog", "データがありません", "red");
    //         return;
    //     }
    //     entity = structuredClone(result);
    // } else {
    //     entity = structuredClone(formEntity);
    //     // リスト画面を初期化
    //     deleteElements(config.tableId);
    // }

    // applyFormConfig(form, entity, RECYCLE_FORM_CONFIG);
    // applyComboConfig(form, entity, RECYCLE_COMBO_CONFIG);
    // setCompanyComboBox(form, entity, companyComboList, officeComboList);


    // openFormByMode(tab, MODE_CONFIG);

    // ID_CONFIG[tab].number.focus();
}

/******************************************************************************************************* リセット */

// 入力フォームの内容をリセットする
function resetFormInput(tab) {
    const cfg = MODE_CONFIG[tab];
    const form = document.getElementById(cfg.dialogId);

    form.querySelectorAll('[data-reset]').forEach(el => {
        if ('value' in el) {
            // input / select / textarea
            el.value = el.type === 'number' ? 0 : '';
        } else {
            // span / div など
            el.textContent = '';
        }
    });

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
        clearNumber(config.number);
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
    const area = self.closest('[data-tab]');
    if (!area) return;
    const config = MODE_CONFIG[area.dataset.tab];

    const result = await deleteTablelist(config.tableId, '/api/recycle/delete');

    if (result.ok) {
        await refleshDisplay();
        openMsgDialog("msg-dialog", result.message, "blue");
    }
}

/******************************************************************************************************* 保存 */

// // 保存処理
// async function execSave(tab) {
//     const config = MODE_CONFIG[tab];

//     // 保存処理
//     const result = await updateFetch("/api/recycle/save/" + tab, JSON.stringify({list:itemList}), token);

//     if (result.ok) {        
//         await refleshDisplay();
//         // 追加・変更行に移動
//         scrollIntoTableList(config.tableId, result.id);

//         openMsgDialog("msg-dialog", result.message, "blue");
//     }

//     // ダイアログを閉じる
//     closeFormDialog(config.formId);
// }
// 新規作成処理
async function execRegist() {
    const config = MODE_CONFIG["02"];

    if (!validateByConfig(config.panel, { ...ERROR_CONFIG.recycle, tab: "02" })) {
        return;
    }

    // tempElm.useDate = config.start;
    const data = buildEntityFromForm(config.panel, tempElm, SAVE_FORM_CONFIG["02"]);

    const result = await updateFetch("/api/recycle/save/regist", JSON.stringify(data), token);
    if (result.ok) {
        // ダイアログを閉じる
        closeFormDialog(config.dialogId);
        // 画面更新
        await refleshDisplay();
        openMsgDialog("msg-dialog", result.message, "blue");
    }
}

// 更新処理
async function execSave() {
    const config = MODE_CONFIG("06");

    if (!validateByConfig(config.panel, { ...ERROR_CONFIG.recycle, tab: tab })) {
        return;
    }

    // const formdata = setInsertFormData(form);
    const result = await updateFetch("/api/recycle/save/edit", JSON.stringify(tempElm), token);
    if (result.ok) {
        // // 画面更新
        // await refleshDisplay();
        // openMsgDialog("msg-dialog", result.message, "blue");
    }

    // ダイアログを閉じる
    closeFormDialog(config.dialogId);
}

// 更新処理
async function execDateUpdate(tab) {
    const config = MODE_CONFIG[tab];

    if (!validateByConfig(config.panel, { ...ERROR_CONFIG.recycle, tab: tab })) {
        return;
    }

    const formdata = {id:0, number:config.number.value, date:config.start.velue} 
    const result = await updateFetch("/api/recycle/update/" + tab, JSON.stringify({entity:formdata}), token);
    if (result.ok) {
        // // 画面更新
        // await refleshDisplay();
        // openMsgDialog("msg-dialog", result.message, "blue");
    }
}

/******************************************************************************************************* ダウンロード */

// CSVファイルをダウンロードする
async function execDownloadCsv(self) {
    const area = self.closest('[data-tab]');
    if (!area) return;
    const config = MODE_CONFIG[area.dataset.tab];
    await downloadCsv(config.tableId, '/api/recycle/download/csv');
}

/******************************************************************************************************* 画面更新 */

// 画面を更新する
async function refleshDisplay() {
    const cfg = MODE_CONFIG["01"];
    const data = {
        start: document.getElementById(cfg.startId).value,
        end: document.getElementById(cfg.endId).value,
        type: document.getElementById('date-category').value
    };
    const result = await searchFetch(cfg.url, JSON.stringify(data), token);
    if (result.ok) {
        origin = result.data;
        await updateTableDisplay(cfg.tableId, cfg.footerId, cfg.searchId, origin, createTableContent);
    }
}

function clearTable(e) {
    const tab = e.currentTarget.dataset.tab;
    const cfg = MODE_CONFIG[tab];
    cfg.start.focus();

    tempElm = structuredClone(formEntity);
}

/******************************************************************************************************* チェック時の処理 */

// 品目固定チェックボックスをクリックした時の処理
function itemCheckedAfter() {
    const itemInput = document.getElementById("item-code");

    if (this.checked) {
        // チェックON → 固定 → reset対象から外す
        itemInput.readOnly = true;
        itemInput.removeAttribute("tabindex");
    } else {
        // チェックOFF → reset対象に戻す
        itemInput.readOnly = false;
        itemInput.setAttribute("tabindex", "2");
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

            if (tab === "02") {
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

    // // 品目固定チェックボッスクス押下時の処理を登録
    // document.getElementById("fix-item-checkbox").addEventListener("change", function () {
    //     itemCheckedAfter();
    // });

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin, createTableContent);
    }, false);

    initCompanyInputs();
    const companyCombo = document.getElementById('company02');
    setComboboxSelected(companyCombo, 1000);
    const cfg = MODE_CONFIG["01"];
    cfg.start.focus();

    // 画面更新
    await refleshDisplay();

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', e => { tabSwitch(e); clearTable(e); });
    })
});