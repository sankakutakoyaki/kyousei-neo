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
            idKey: "recycle_id",
            validCheck: item => item.loss_date !== "9999-12-31",
            onDoubleClick: (item) => {
                execEdit(item.recycle_id, 'edit', this);
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
            idKey: "recycle_id",
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
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.molding_number ?? "") + '</span></span></td>');
    // 使用日　引渡日
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.use_date == '9999-12-31' ? "-----": item.use_date) + '</span><br><span>' + (item.delivery_date == '9999-12-31' ? "-----": item.delivery_date) + '</span></td>');
    // 発送日　ロス処理日
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.shipping_date == '9999-12-31' ? "-----": item.shipping_date) + '</span><br><span>' + (item.loss_date == '9999-12-31' ? "-----": item.loss_date) + '</span></td>');
    // 登録日　最終更新日
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.regist_date == '9999-12-31' ? "-----": item.regist_date) + '</span><br><span>' + (item.update_date == '9999-12-31' ? "-----": item.update_date) + '</span></td>');
    // 小売業者
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.company_name ?? "") + '</span><br><span>' + (item.office_name ?? "") + '</span></td>');
    // 製造業者等名　品目・料金区分
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.maker_name ?? "") + '</span><br><span>' + (item.item_name ?? "") + '</span></td>');
}

// フォームテーブル行を作成する
function createFormTableRow(newRow, item, mode) {
    switch (mode) {
        case "regist":
            // お問合せ管理票番号 使用日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.molding_number ?? "") + '</span><br><span>' + (item.use_date == '9999-12-31' ? "-----": item.use_date) + '</span></td>');
            // 小売業者
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.company_name ?? "") + '</span><br><span>' + (item.office_name ?? "") + '</span></td>');
            // 製造業者等名　品目・料金区分
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.maker_name ?? "") + '</span><br><span>' + (item.item_name ?? "") + '</span></td>');
            // 削除ボタン
            newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteItem(this, ' + item.recycle_id + ')"><img src="/icons/dust.png"></div></td>');
            break;
        case "delivery":
            // 引渡日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.date == '9999-12-31' ? "-----": item.date) + '</span></td>');
            // お問合せ管理票番号
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.molding_number ?? "") + '</span></span></td>');
            // 削除ボタン
            newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteItem(this, ' + item.recycle_id + ')"><img src="/icons/dust.png"></div></td>');
            break;
        case "shipping":
            // 発送日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.date == '9999-12-31' ? "-----": item.date) + '</span></td>');
            // お問合せ管理票番号
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.molding_number ?? "") + '</span></span></td>');
            // 削除ボタン
            newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteItem(this, ' + item.recycle_id + ')"><img src="/icons/dust.png"></div></td>');
            break;
        case "loss":
            // ロス処理日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.date == '9999-12-31' ? "-----": item.date) + '</span></td>');
            // お問合せ管理票番号
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.molding_number ?? "") + '</span></span></td>');
            // 削除ボタン
            newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteItem(this, ' + item.recycle_id + ')"><img src="/icons/dust.png"></div></td>');
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
        const data = "id=" + encodeURIComponent(parseInt(id));
        const result = await searchFetch('/api/recycle/get/id', data, token, 'application/x-www-form-urlencoded');

        if (result == null) {
            openMsgDialog("msg-dialog", "データがありません", "red");
            return;
        }
        entity = structuredClone(result);
    } else {
        entity = structuredClone(formEntity);
        // リスト画面を初期化
        deleteElements(config.tableId);
    }

    // openFormByMode(mode, MODE_CONFIG);

    if (typeof mode === "string" && mode !== "edit") {
        ID_CONFIG[mode].date.value = getDate();
    }

    if (typeof mode === "string" && mode === "regist") {
        setCompanyComboBox(form, entity, companyComboList, officeComboList);
    } else if (typeof mode === "string" && mode === "edit") {
        setFormContent(form, entity);
        setCompanyComboBox(form, entity, companyComboList, officeComboList);
    }

    openFormByMode(mode, MODE_CONFIG);

    ID_CONFIG[mode].number.focus();
}

// コンテンツ部分作成
function setFormContent(form, entity) {
    // form.querySelector('[name="recycle-id"]').value = entity.recycle_id;
    // form.querySelector('[name="version"]').value = entity.version;
    // form.querySelector('[name="number"]').value = entity.molding_number;
    // form.querySelector('[name="recycle-number"]').value = entity.recycle_number;
    // form.querySelector('[name="molding-number"]').value = entity.molding_number;
    // form.querySelector('[name="maker-code"]').value = entity.maker_code;
    // form.querySelector('[name="maker-name"]').value = entity.maker_name;
    // form.querySelector('[name="maker-id"]').value = entity.maker_id;
    // form.querySelector('[name="item-code"]').value = entity.item_code;
    // form.querySelector('[name="item-name"]').value = entity.item_name;
    // form.querySelector('[name="item-id"]').value = entity.item_id;
    // form.querySelector('[name="recycling-fee"]').value = entity.recycling_fee;

    // form.querySelector('[name="use-date"]').value = entity.use_date === "9999-12-31" ? "": entity.use_date;
    // form.querySelector('[name="delivery-date"]').value = entity.delivery_date === "9999-12-31" ? "": entity.delivery_date;
    // form.querySelector('[name="shipping-date"]').value = entity.shipping_date === "9999-12-31" ? "": entity.shipping_date;
    // form.querySelector('[name="loss-date"]').value = entity.loss_date === "9999-12-31" ? "": entity.loss_date;
    // // if (entity.use_date === "9999-12-31") {
    // //     form.querySelector('[name="use-date"]').value = "";
    // // } else {
    // //     form.querySelector('[name="use-date"]').value = entity.use_date;
    // // }

    // // if (entity.delivery_date === "9999-12-31") {
    // //     form.querySelector('[name="delivery-date"]').value = "";
    // // } else {
    // //     form.querySelector('[name="delivery-date"]').value = entity.delivery_date;
    // // }

    // // if (entity.shipping_date === "9999-12-31") {
    // //     form.querySelector('[name="shipping-date"]').value = "";
    // // } else {
    // //     form.querySelector('[name="shipping-date"]').value = entity.shipping_date;
    // // }

    // // if (entity.loss_date === "9999-12-31") {
    // //     form.querySelector('[name="loss-date"]').value = "";
    // // } else {
    // //     form.querySelector('[name="loss-date"]').value = entity.loss_date;
    // // }

    // // 製造業者等名コンボボックス
    // setCompanyComboBox(form, entity, companyComboList, officeComboList);

    // // 処分場コンボボックス
    // const disposalSiteArea = form.querySelector('select[name="disposal-site"]');
    // if (disposalSiteArea != null) {
    //     createComboBoxWithTop(disposalSiteArea, disposalSiteComboList, "");
    //     setComboboxSelected(disposalSiteArea, entity.disposal_site_id); 
    // }
    
    applyFormConfig(form, entity, RECYCLE_FORM_CONFIG);
    applyComboConfig(form, entity, RECYCLE_COMBO_CONFIG);
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
    // formdata.version = formData.get('version');
    // formdata.recycle_number = formData.get('recycle-number');
    // formdata.molding_number = formData.get('molding-number');

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
        idKey: 'company_id',
        nameKey: 'company_name'
    });

    setSelectValue(form, formData, formdata, {
        valueName: 'office',
        idKey: 'office_id',
        nameKey: 'office_name'
    });

    setSelectValue(form, formData, formdata, {
        valueName: 'disposal-site',
        idKey: 'disposal-site_id',
        nameKey: 'disposal-site_name'
    });
    // formdata.use_date = "9999-12-31";
    // formdata.delivery_date = "9999-12-31";
    // formdata.shipping_date = "9999-12-31";
    // formdata.loss_date = "9999-12-31";

    // if (formData.get('use-date') != null) {
    //     if (formData.get('use-date') != "") {
    //         formdata.use_date = formData.get('use-date');
    //     }
    // }

    // if (formData.get('delivery-date') != null) {
    //     if (formData.get('delivery-date') != "") {
    //         formdata.delivery_date = formData.get('delivery-date');
    //     }
    // }

    // if (formData.get('shipping-date') != null) {
    //     if (formData.get('shipping-date') != "") {
    //         formdata.shipping_date = formData.get('shipping-date');
    //     }
    // }

    // if (formData.get('loss-date') != null) {
    //     if (formData.get('loss-date') != "") {
    //         formdata.loss_date = formData.get('loss-date');
    //     }
    // }

    // if (formData.get('company') != null) {
    //     formdata.company_id = formData.get('company');
    //     const selectCompany = form.querySelector('[name="company"]');
    //     const companyName = selectCompany.options[selectCompany.selectedIndex].text;
    //     formdata.company_name = companyName;
    // }

    // if (formData.get('office') != null) {
    //     formdata.office_id = formData.get('office');
    //     const selectOffice = form.querySelector('[name="office"]');
    //     const officeName = selectOffice.options[selectOffice.selectedIndex].text;
    //     formdata.office_name = officeName;
    // }

    if (formData.get('maker-code') != null) {
        if (formData.get('maker-code') == "") {
            formdata.maker_id = 0;
            formdata.maker_name = "";
        } else {
            formdata.maker_id = formData.get('maker-id');
            formdata.maker_code = formData.get('maker-code');
            formdata.maker_name = formData.get('maker-name');
        }
    }

    if (formData.get('item-code') != null) {
        if (formData.get('item-code') == "") {
            formdata.item_id = 0;
            formdata.item_name = "";
        } else {
            formdata.item_id = formData.get('item-id');
            formdata.item_code = formData.get('item-code');
            formdata.item_name = formData.get('item-name');
        }
    }

    // if (formData.get('recycling-fee') != null) {
    //     formdata.recycling_fee = formData.get('recycling_fee');
    // }

    // if (formData.get('disposal-site') != null) {
    //     formdata.disposal_site_id = formData.get('disposal-site');
    //     const selectDisposalSite = form.querySelector('[name="disposal-site"]');
    //     const disposalSiteName = selectDisposalSite.options[selectDisposalSite.selectedIndex].text;
    //     formdata.disposal_site_name = disposalSiteName;
    // }

    return formdata;
}

// 日付データを更新したformdataを取得する
// function setDateUpdateFormData(form) {
//     const formData = new FormData(form);
//     // let formdata = {};
//     const formdata = structuredClone(formEntity);

//     formdata.recycle_id = formData.get('recycle-id');
//     formdata.recycle_number = formData.get('recycle-number');
//     formdata.molding_number = formData.get('molding-number');

//     if (formData.get('use-date') != null && formData.get('use-date') != "") {
//         formdata.date = formData.get('use-date');
//     } else if (formData.get('delivery-date') != null && formData.get('delivery-date') != "") {
//         formdata.date = formData.get('delivery-date');
//     } else if (formData.get('shipping-date') != null && formData.get('shipping-date') != "") {
//         formdata.date = formData.get('shipping-date');
//     } else if (formData.get('loss-date') != null && formData.get('loss-date') != "") {
//         formdata.date = formData.get('loss-date');
//     } else {
//         formdata.date = "9999-12-31";
//     }

//     return formdata;
// }
function setDateUpdateFormData(form) {
    const formData = new FormData(form);
    const formdata = structuredClone(formEntity);

    formdata.recycle_id = formData.get('recycle-id');
    formdata.recycle_number = formData.get('recycle-number');
    formdata.molding_number = formData.get('molding-number');

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

/******************************************************************************************************* チェック */

// // 入力チェック
// function formDataCheck(form, mode) {
//     let msg = "";
//     const num = form.querySelector('input[name="recycle-number"]');
//     if (num.value == 0) msg += '\nお問合せ管理票番号を入力して下さい';

//     switch (mode) {
//         case "regist":
//             if (form.querySelector('select[name="company"]').value === 0) msg += '\n小売業者を選択して下さい';
//             if (form.querySelector('input[name="maker-code"]').value === "") msg += '\n製造業者等名を選択して下さい';
//             if (form.querySelector('input[name="item-code"]').value === "") msg += '\n品目・料金区分を選択して下さい';
//             if (form.querySelector('input[name="use-date"]').value === "") msg += '\n使用日を入力して下さい';
//             break;
//         case "delivery":
//             if (form.querySelector('input[name="delivery-date"]').value === "") msg += '\n引渡日を入力して下さい';
//             break;
//         case "shipping":
//             if (form.querySelector('input[name="shipping-date"]').value === "") msg += '\n発送日を入力して下さい';
//             break;
//         case "loss":
//             if (form.querySelector('input[name="loss-date"]').value === "") msg += '\nロス処理日を入力して下さい';
//             break;
//         case "edit":
//             if (form.querySelector('select[name="company"]').value === 0) msg += '\n小売業者を選択して下さい';
//             if (form.querySelector('input[name="maker-code"]').value === "") msg += '\n製造業者等名を選択して下さい';
//             if (form.querySelector('input[name="item-code"]').value === "") msg += '\n品目・料金区分を選択して下さい';
//             if (form.querySelector('input[name="use-date"]').value === "") msg += '\n使用日を入力して下さい';
//             break;
//         default:
//             break;
//     }

//     // エラーが一つ以上あればエラーメッセージダイアログを表示する
//     if (msg != "") {
//         openMsgDialog("msg-dialog", msg, "red");
//         return false;
//     }
//     return true;
// }

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
    // e.preventDefault();
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
    
    // let item = {};
    let msg = "";

    if (item == null) {
        
    } else if (mode !== "edit") {
        if (item && mode === "regist") {
            msg = `「${molNumber}」は、すでに登録されています`;
        } else if (!item && (mode === "delivery" || mode === "shopping")) {
            msg = `「${molNumber}」は、使用登録されていません`;
        } else if (
            item &&
            item.delivery_date !== "9999-12-31" &&
            mode === "delivery"
        ) {
            msg = `「${molNumber}」は、引渡しされています`;
        } else if (
            item &&
            item.delivery_date === "9999-12-31" &&
            mode === "shopping"
        ) {
            msg = `「${molNumber}」は、引渡しされていません`;
        } else if (
            item &&
            item.shopping_date !== "9999-12-31" &&
            mode === "shopping"
        ) {
            msg = `「${molNumber}」は、発送されています`;
        }
    } else if (item && item.recycle_id !== Number(config.recycleId.value)) {
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
        config.recycleId.value = item ? item.recycle_id : 0;
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
    if (entity != null && entity.recycle_maker_id > 0) {
        makerName.value = entity.abbr_name;
        makerId.value = entity.recycle_maker_id;
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
    if (entity != null && entity.recycle_item_id > 0) {
        itemName.value = entity.name;
        itemId.value = entity.recycle_item_id;
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
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }
}

// フォームリストに登録したアイテムを削除する
async function deleteItem(self, id) {
    const area = self.closest('[data-mode]');
    if (!area) return;
    // const config = MODE_CONFIG[area.mode];

    if (id < 0) {
        itemList = itemList.filter(value => value.recycle_id != id);
    } else {
        const item = itemList.find(value => value.recycle_id == id);
        item.state = deleteCode;
    }

    await updateFormTableDisplay(area.mode, itemList);
}

/******************************************************************************************************* 登録 */

// フォームリストにアイテムを登録する
async function execRegistItem(self, mode) {
    const config = MODE_CONFIG[mode];
    const form = document.getElementById(config.formId);
    // if (formDataCheck(form, mode) == false) {
    //     return;
    // }
    // if (!validateForm(form, mode)) {
    //     return;
    // }
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
async function execSave(formId, tableId, mode) {
    // 保存処理
    const result = await updateFetch("/api/recycle/save/" + mode, JSON.stringify({list:itemList}), token, "application/json");

    if (result.ok) {        
        await refleshDisplay01();
        // 追加・変更行に移動
        scrollIntoTableList(tableId, result.id);

        openMsgDialog("msg-dialog", result.message, "blue");
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }
    // ダイアログを閉じる
    closeFormDialog(formId);
}

// 更新処理
async function execUpdate(mode) {
    const config = MODE_CONFIG[mode];

    const form = document.getElementById(config.formId);
    // if (formDataCheck(form, mode) == false) {
    //     return;
    // }
    // if (!validateForm(form, mode)) {
    //     return;
    // }
    if (!validateByConfig(form, { ...ERROR_CONFIG.recycle, mode: mode })) {
        return;
    }

    const formdata = setInsertFormData(form);
    const result = await updateFetch("/api/recycle/save/" + mode, JSON.stringify({entity:formdata}), token, "application/json");
    if (result.ok) {
        // 画面更新
        await refleshDisplay01();
        openMsgDialog("msg-dialog", result.message, "blue");
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
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
    // const data = "&start=" + encodeURIComponent(start) + "&end=" + encodeURIComponent(end) + "&col=" + encodeURIComponent(col);
    // const contentType = 'application/x-www-form-urlencoded';
    // List<Recycle>を取得
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
    // hamburgerItemAddSelectClass('.header-title', 'recycle');
    // hamburgerItemAddSelectClass('.normal-sidebar', 'regist');
    // スピナー表示
    startProcessing();

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

    // スピナー消去
    processingEnd();
});