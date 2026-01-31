"use strict"

let itemId = 0;
let itemList = [];

/******************************************************************************************************* 画面 */

// リスト画面の本体部分を作成する
function createTable01Content(tableId, list) {
    const table = document.getElementById(tableId);

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "recycleId",
            validCheck: item => item.lossDate !== "9999-12-31",
            onDoubleClick: (item) => {
                execEdit(item.recycleId, 'edit', this);
            }
        });

        createTable01Row(row, item);
    });
}

// フォームリスト画面の本体部分を作成する
function createFormTableContent(mode, list) {
    const config = MODE_CONFIG[mode];
    const table = document.getElementById(config.tableId);

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "recycleId",
            validCheck: item => item.state !== deleteCode,
        });

        createFormTableRow(row, item, mode);
    });
}

/******************************************************************************************************* テーブル行作成 */

// テーブル行を作成する
function createTable01Row(newRow, item) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    // お問合せ管理票番号
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.moldingNumber ?? "") + '</span></span></td>');
    // 使用日　引渡日
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.useDate == '9999-12-31' ? "-----": item.useDate) + '</span><br><span>' + (item.deliveryDate == '9999-12-31' ? "-----": item.deliveryDate) + '</span></td>');
    // 発送日　ロス処理日
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.shippingDate == '9999-12-31' ? "-----": item.shippingDate) + '</span><br><span>' + (item.lossDate == '9999-12-31' ? "-----": item.lossDate) + '</span></td>');
    // 登録日　最終更新日
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.registDate == '9999-12-31' ? "-----": item.registDate) + '</span><br><span>' + (item.updateDate == '9999-12-31' ? "-----": item.updateDate) + '</span></td>');
    // 小売業者
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.companyName ?? "") + '</span><br><span>' + (item.officeName ?? "") + '</span></td>');
    // 製造業者等名　品目・料金区分
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.makerName ?? "") + '</span><br><span>' + (item.itemName ?? "") + '</span></td>');
}

// フォームテーブル行を作成する
function createFormTableRow(newRow, item, mode) {
    switch (mode) {
        case "regist":
            // お問合せ管理票番号 使用日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.moldingNumber ?? "") + '</span><br><span>' + (item.useDate == '9999-12-31' ? "-----": item.useDate) + '</span></td>');
            // 小売業者
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.companyName ?? "") + '</span><br><span>' + (item.officeName ?? "") + '</span></td>');
            // 製造業者等名　品目・料金区分
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.makerName ?? "") + '</span><br><span>' + (item.itemName ?? "") + '</span></td>');
            // 削除ボタン
            newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteItem(this, ' + item.recycleId + ')"><img src="/icons/dust.png"></div></td>');
            break;
        case "delivery":
            // 引渡日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.date == '9999-12-31' ? "-----": item.date) + '</span></td>');
            // お問合せ管理票番号
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.moldingNumber ?? "") + '</span></span></td>');
            // 削除ボタン
            newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteItem(this, ' + item.recycleId + ')"><img src="/icons/dust.png"></div></td>');
            break;
        case "shipping":
            // 発送日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.date == '9999-12-31' ? "-----": item.date) + '</span></td>');
            // お問合せ管理票番号
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.moldingNumber ?? "") + '</span></span></td>');
            // 削除ボタン
            newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteItem(this, ' + item.recycleId + ')"><img src="/icons/dust.png"></div></td>');
            break;
        case "loss":
            // ロス処理日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.date == '9999-12-31' ? "-----": item.date) + '</span></td>');
            // お問合せ管理票番号
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.moldingNumber ?? "") + '</span></span></td>');
            // 削除ボタン
            newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteItem(this, ' + item.recycleId + ')"><img src="/icons/dust.png"></div></td>');
            break;
    }
}

/******************************************************************************************************* 入力画面 */

// 商品登録画面を開く
async function execEdit(id, mode, self) {
    itemList = [];
    let entity = {};
    const config = MODE_CONFIG[mode];
    const form = document.getElementById(config.dialogId);

    if (id > 0) {
        // 選択されたIDのエンティティを取得
        const result = await searchFetch('/api/recycle/get/id', JSON.stringify({id:id}), token);

        if (!result.ok) {
            openMsgDialog("msg-dialog", "データがありません", "red");
            return;
        }
        entity = structuredClone(result);
    } else {
        entity = structuredClone(formEntity);
        // リスト画面を初期化
        deleteElements(config.tableId);
    }

    if (typeof mode === "string" && mode !== "edit") {
        ID_CONFIG[mode].date.value = getDate();
    }

    if (typeof mode === "string" && mode === "regist") {
        setCompanyComboBox(form, entity, companyComboList, officeComboList);
    } else if (typeof mode === "string" && mode === "edit") {
        applyFormConfig(form, entity, RECYCLE_FORM_CONFIG);
        applyComboConfig(form, entity, RECYCLE_COMBO_CONFIG);
        setCompanyComboBox(form, entity, companyComboList, officeComboList);
    }

    openFormByMode(mode, MODE_CONFIG);

    ID_CONFIG[mode].number.focus();
}

// 保存用のformdataを作成する
function createFormdata(form, mode) {
    const config = ID_CONFIG[mode];
    config.regBtn.focus();
    
    let formdata = [];

    switch (mode) {
        case "regist":
            formdata = setInsertFormData(form);
            break;
        default:
            // 日付の更新のみ
            formdata = setDateUpdateFormData(form);
            break;
    }
    return formdata;
}

// データを保存するためのformdataを作成する
function setInsertFormData(form) {
    const formData = new FormData(form);
    const formdata = structuredClone(formEntity);

    if (formData.get('recycle-id') > 0) {
        formdata.recycle_id = formData.get('recycle-id');
    } else {
        itemId -= 1;
        formdata.recycle_id = itemId;
    }
    formdata.state = 0;

    Object.entries(FIELD_MAP).forEach(([key, name]) => {
        const v = formData.get(name);
        if (v != null) formdata[key] = v;
    });
    
    DATE_FIELDS.forEach(f => {
        const v = formData.get(f.name);
        formdata[f.key] = v ? v : "9999-12-31";
    });

    setSelectValue(form, formData, formdata, {
        valueName: 'company',
        idKey: 'company-id',
        nameKey: 'company-name'
    });

    setSelectValue(form, formData, formdata, {
        valueName: 'office',
        idKey: 'office-id',
        nameKey: 'office-name'
    });

    setSelectValue(form, formData, formdata, {
        valueName: 'disposal-site',
        idKey: 'disposal-site-id',
        nameKey: 'disposal-site-name'
    });

    if (formData.get('maker-code') != null) {
        if (formData.get('maker-code') == "") {
            formdata.makerId = 0;
            formdata.makerName = "";
        } else {
            formdata.makerId = formData.get('maker-id');
            formdata.makerCode = formData.get('maker-code');
            formdata.makerName = formData.get('maker-name');
        }
    }

    if (formData.get('item-code') != null) {
        if (formData.get('item-code') == "") {
            formdata.itemId = 0;
            formdata.itemName = "";
        } else {
            formdata.itemId = formData.get('item-id');
            formdata.itemCode = formData.get('item-code');
            formdata.itemName = formData.get('item-name');
        }
    }

    return formdata;
}


function setDateUpdateFormData(form) {
    const formData = new FormData(form);
    const formdata = structuredClone(formEntity);

    formdata.recycleId = formData.get('recycle-id');
    formdata.recycleNumber = formData.get('recycle-number');
    formdata.moldingNumber = formData.get('molding-number');

    // 初期化
    formdata.date = DEFAULT_DATE;

    for (const field of DATE_FIELDS) {
        const v = formData.get(field.name);
        if (v) {
            formdata.date = v;
            break;
        }
    }

    return formdata;
}

// 入力ボックスを初期化
function clearNumber(numberBox) {
    if (numberBox != null) {
        numberBox.value = "";
    }
}

/******************************************************************************************************* リセット */

// 入力フォームの内容をリセットする
function resetFormInput(form, mode) {
    form.querySelectorAll('[data-reset]').forEach(el => {
        if (el.dataset.reset.includes(mode)) {
            el.value = el.type === "number" ? 0 : "";
        }
    });
    form.querySelector('input[name="recycle-number"]').focus();
}

/******************************************************************************************************* 取得・検証 */

// お問合せ管理票番号を取得して検証
async function execNumberBlur(e, mode) {
    if (e.currentTarget == null) return;

    const config = ID_CONFIG[mode];
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

    // テーブルリストに追加されている場合はエラー
    if (mode !== "edit") {
        const entity = itemList.find(value => value.recycle_number === number);
        if (entity != null) {
            openMsgDialog("msg-dialog", `「${molNumber}」は、リストに存在します`, 'red');
            setFocusElement("msg-dialog", config.number);
            return;
        }
    }

    // DBを確認する
    const item = await existsRecycleByNumber(number);
    
    let msg = "";

    if (item == null) {
        
    } else if (mode !== "edit") {
        if (item && mode === "regist") {
            msg = `「${molNumber}」は、すでに登録されています`;
        } else if (!item && (mode === "delivery" || mode === "shopping")) {
            msg = `「${molNumber}」は、使用登録されていません`;
        } else if (
            item &&
            item.deliveryDate !== "9999-12-31" &&
            mode === "delivery"
        ) {
            msg = `「${molNumber}」は、引渡しされています`;
        } else if (
            item &&
            item.deliveryDate === "9999-12-31" &&
            mode === "shopping"
        ) {
            msg = `「${molNumber}」は、引渡しされていません`;
        } else if (
            item &&
            item.shoppingDate !== "9999-12-31" &&
            mode === "shopping"
        ) {
            msg = `「${molNumber}」は、発送されています`;
        }
    } else if (item && item.recycleId !== Number(config.recycleId.value)) {
        openMsgDialog("msg-dialog", `「${molNumber}」は、すでに登録されています`, 'red');
        setFocusElement("msg-dialog", config.number);
        config.number.value = config.moldingNumber.value;
        return;
    }

    if (msg) {
        openMsgDialog("msg-dialog", msg, 'red');
        setFocusElement("msg-dialog", config.number);
        clearNumber(config.number);
    } else {
        config.recycleId.value = item ? item.recycleId : 0;
        config.version.value = item ? item.version : 0;
        config.recycleNumber.value = number;
        config.moldingNumber.value = molNumber;
        config.number.value = molNumber;

        if (mode !== "regist" && mode !== "edit") {
            config.regBtn.click();
        }
    }
}

// コードから[maker]を取得して、名前を表示
async function execMakerCodeBlur(e, mode) {
    e.preventDefault();
    if (e.currentTarget == null) return;

    const config = ID_CONFIG[mode];
    const makerCode = config.makerCode;
    const makerName = config.makerName;
    const makerId = config.makerId;

    if (makerCode.value === "" || isNaN(makerCode.value)) {
        makerCode.value = "";
        makerName.value = "";
        makerId.value = "";
        return;
    }

    const entity = await getMakerByCode(Number(makerCode.value));
    if (entity != null && entity.recycleMakerId > 0) {
        makerName.value = entity.abbrName;
        makerId.value = entity.recycleMakerId;
    } else {
        makerCode.value = "";
        makerName.value = "";
        makerId.value = "";
        openMsgDialog("msg-dialog", "コードが登録されていません", 'red');
        setFocusElement("msg-dialog", makerCode);

        const dialog = document.getElementById("msg-dialog");
        const focusBtn = dialog.querySelector('[name="focus-btn"]');
        if (focusBtn != null) focusBtn.focus();
    }
}

// コードから[item]を取得して、名前を表示
async function execItemCodeBlur(e, mode) {
    e.preventDefault();
    if (e.currentTarget == null) return;

    const config = ID_CONFIG[mode];
    const itemCode = config.itemCode;
    const itemName = config.itemName;
    const itemId = config.itemId;

    if (itemCode.value === "" || isNaN(itemCode.value)) {
        itemCode.value = "";
        itemName.value = "";
        itemId.value = "";
        return;
    }

    const entity = await getItemByCode(Number(itemCode.value));
    if (entity != null && entity.recycleItemId > 0) {
        itemName.value = entity.name;
        itemId.value = entity.recycleItemId;
    } else {
        itemCode.value = "";
        itemName.value = "";
        itemId.value = "";
        openMsgDialog("msg-dialog", "コードが登録されていません", 'red');
        setFocusElement("msg-dialog", itemCode);
    }
}

/******************************************************************************************************* 削除 */

// 削除する
async function execDelete(self) {
    const area = self.closest('[data-tab]');
    if (!area) return;
    const config = MODE_CONFIG[area.dataset.tab];

    const result = await deleteTablelist(config.tableId, '/api/recycle/delete');

    if (result.ok) {
        await refleshDisplay01();
        openMsgDialog("msg-dialog", result.message, "blue");
    }
}

// フォームリストに登録したアイテムを削除する
async function deleteItem(self, id) {
    const area = self.closest('[data-mode]');
    if (!area) return;

    if (id < 0) {
        itemList = itemList.filter(value => value.recycleId != id);
    } else {
        const item = itemList.find(value => value.recycleId == id);
        item.state = deleteCode;
    }

    await updateFormTableDisplay(area.mode, itemList);
}

/******************************************************************************************************* 登録 */

// フォームリストにアイテムを登録する
async function execRegistItem(mode, self) {
    const config = MODE_CONFIG[mode];
    const form = document.getElementById(config.formId);
    if (!validateByConfig(form, { ...ERROR_CONFIG.recycle, mode: mode })) {
        return;
    }
    const formdata = createFormdata(form, mode);

    itemList.push(formdata);

    await updateFormTableDisplay(mode, itemList);
    scrollIntoTableList(config.tableId, itemId);
    resetFormInput(form, mode);

    ID_CONFIG[mode].number.focus();
}

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave(mode) {
    const config = MODE_CONFIG[mode];

    // 保存処理
    const result = await updateFetch("/api/recycle/save/" + mode, JSON.stringify({list:itemList}), token);

    if (result.ok) {        
        await refleshDisplay01();
        // 追加・変更行に移動
        scrollIntoTableList(config.tableId, result.id);

        openMsgDialog("msg-dialog", result.message, "blue");
    }

    // ダイアログを閉じる
    closeFormDialog(config.formId);
}

// 更新処理
async function execUpdate(mode) {
    const config = MODE_CONFIG[mode];

    const form = document.getElementById(config.formId);
    if (!validateByConfig(form, { ...ERROR_CONFIG.recycle, mode: mode })) {
        return;
    }

    const formdata = setInsertFormData(form);
    const result = await updateFetch("/api/recycle/save/" + mode, JSON.stringify({entity:formdata}), token);
    if (result.ok) {
        // 画面更新
        await refleshDisplay01();
        openMsgDialog("msg-dialog", result.message, "blue");
    }

    // ダイアログを閉じる
    closeFormDialog(config.dialogId);
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

// フォームのテーブルリスト画面を更新する
async function updateFormTableDisplay(mode, list) {
    const config = MODE_CONFIG[mode];
    // リスト画面を初期化
    deleteElements(config.tableId);
    // リスト作成
    createFormTableContent(mode, list);
    // フッター作成
    createTableFooter(config.footerId, list);
    // スクロール時のページトップボタン処理を登録する
    setPageTopButton(config.tableId);
    // テーブルにスクロールバーが表示されたときの処理を登録する
    document.querySelectorAll('.scroll-area').forEach(el => {
        toggleScrollbar(el);
    });
}

// 画面を更新する
async function refleshDisplay01() {
    const result = await getRecyclesBetween("start-date01", "end-date01", "/api/recycle/get/between");
    if (result.ok) {
        origin = result;
        await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin, createTable01Content);
    }
}

// 一覧表示用のリスト取得
async function getRecyclesBetween(startId, endId, url) {
    const start = document.getElementById(startId).value;
    const end = document.getElementById(endId).value;
    const col = document.getElementById('date-category01').value;
    const data = {
        start: document.getElementById(startId).value,
        end: document.getElementById(endId).value,
        type: document.getElementById('date-category01').value
    };
    return await searchFetch(url, JSON.stringify(data), token);
}

/******************************************************************************************************* チェック時の処理 */

// 品目固定チェックボックスをクリックした時の処理
function itemCheckedAfter() {
    const itemInput = document.getElementById("item-code11");
    const nameInput = document.getElementById("item-name11");

    if (this.checked) {
        // チェックON → 固定 → reset対象から外す
        itemInput.removeAttribute("data-reset");
        itemInput.readOnly = true;
        itemInput.removeAttribute("tabindex");
        nameInput.removeAttribute("data-reset");
    } else {
        // チェックOFF → reset対象に戻す
        itemInput.setAttribute("data-reset", "regist");
        itemInput.readOnly = false;
        itemInput.setAttribute("tabindex", "15");
        nameInput.setAttribute("data-reset", "regist");
    }
    resetEnterFocus();
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {

    // 日付フィルターコンボボックス
    const dateCategoryArea = document.querySelector('select[name="date-category"]')
    createComboBoxValueString(dateCategoryArea, dateComboList);

    for (const mode of Object.keys(MODE_CONFIG)) {
        let config = ID_CONFIG[mode];
        if (config != null) {
            // お問合せ管理票番号入力ボックスのフォーカスが外れた時の処理を登録
            config.number.addEventListener('blur', function (e) { execNumberBlur(e, mode); });
            setEnterFocus(MODE_CONFIG[mode].formId);
            if (mode === "regist" || mode === "edit") {
                // メーカーコード入力ボックスのフォーカスが外れた時の処理を登録
                config.makerCode.addEventListener('blur', function (e) { execMakerCodeBlur(e, mode); });
                // 品目コード入力ボックスのフォーカスが外れた時の処理を登録
                config.itemCode.addEventListener('blur', function (e) { execItemCodeBlur(e, mode); });
            }
        }
    }

    // 品目固定チェックボッスクス押下時の処理を登録
    document.getElementById("fix-item-checkbox").addEventListener("change", function () {
        itemCheckedAfter();
    });

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin, createTable01Content);
    }, false);

    execSpecifyPeriod("today", 'start-date01', 'end-date01');

    // 画面更新
    await refleshDisplay01();
});