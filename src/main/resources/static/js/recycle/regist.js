"use strict"

// const { jsx } = require("react/jsx-runtime");

let itemId = 0;
let itemList = [];
const ID_CONFIG = {
    regist: {
        number: document.getElementById('number11'),
        recycleNumber: document.getElementById('recycle-number11'),
        moldingNumber: document.getElementById('molding-number11'),
        makerCode: document.getElementById('maker-code11'),
        itemCode: document.getElementById('item-code11'),
        makerName: document.getElementById('maker-name11'),
        itemName: document.getElementById('item-name11'),
        makerId: document.getElementById('maker-id11'),
        itemId: document.getElementById('item-id11'),
        date: document.getElementById('use-date11'),
        recycleId: document.getElementById('recycle-id11'),
        version: document.getElementById('version11'),
        regBtn: document.getElementById('regist-btn11')
    },
    delivery: {
        number: document.getElementById('number12'),
        recycleNumber: document.getElementById('recycle-number12'),
        moldingNumber: document.getElementById('molding-number12'),
        date: document.getElementById('delivery-date12'),
        recycleId: document.getElementById('recycle-id12'),
        version: document.getElementById('version12'),
        regBtn: document.getElementById('regist-btn12')
    },
    shipping: {
        number: document.getElementById('number13'),
        recycleNumber: document.getElementById('recycle-number13'),
        moldingNumber: document.getElementById('molding-number13'),
        date: document.getElementById('shipping-date13'),
        recycleId: document.getElementById('recycle-id13'),
        version: document.getElementById('version13'),
        regBtn: document.getElementById('regist-btn13')
    },
    loss: {
        number: document.getElementById('number14'),
        recycleNumber: document.getElementById('recycle-number14'),
        moldingNumber: document.getElementById('molding-number14'),
        date: document.getElementById('loss-date14'),
        recycleId: document.getElementById('recycle-id14'),
        version: document.getElementById('version14'),
        regBtn: document.getElementById('regist-btn14')
    },
    edit: {
        number: document.getElementById('number15'),
        recycleNumber: document.getElementById('recycle-number15'),
        moldingNumber: document.getElementById('molding-number15'),
        makerCode: document.getElementById('maker-code15'),
        itemCode: document.getElementById('item-code15'),
        makerName: document.getElementById('maker-name15'),
        itemName: document.getElementById('item-name15'),
        makerId: document.getElementById('maker-id15'),
        itemId: document.getElementById('item-id15'),
        useDate: document.getElementById('use-date15'),
        deliveryDate: document.getElementById('delivery-date15'),
        shippingDate: document.getElementById('shipping-date15'),
        lossDate: document.getElementById('loss-date15'),
        recycleId: document.getElementById('recycle-id15'),
        version: document.getElementById('version15'),
        regBtn: document.getElementById('regist-btn15')
    }
};
const MODE_CONFIG = {
    "01": {
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01"
    },
    regist: {
        dialogId: "form-dialog-01",
        formId: "form-01",
        // afterOpen: async (itemList) => {
        //     useDate11.value = "";
        //     await updateFormTableDisplay("table-11-content", "footer-11", itemList);
        //     setCompanyComboBox(form, entity, companyComboList, officeComboList);
        // }
        dateInput: () => useDate11,
        tableId: "table-11-content",
        footerId: "footer-11",
        regBtnId: "regist-btn11"
    },
    delivery: {
        dialogId: "form-dialog-02",
        formId: "form-02",
        dateInput: () => deliveryDate12,
        tableId: "table-12-content",
        footerId: "footer-12",
        regBtnId: "regist-btn12"
    },
    shipping: {
        dialogId: "form-dialog-03",
        formId: "form-03",
        dateInput: () => shippingDate13,
        tableId: "table-13-content",
        footerId: "footer-13",
        regBtnId: "regist-btn13"
    },
    loss: {
        dialogId: "form-dialog-04",
        formId: "form-04",
        dateInput: () => lossDate14,
        tableId: "table-14-content",
        footerId: "footer-14",
        regBtnId: "regist-btn14"
    },
    edit: {
        dialogId: "form-dialog-05",
        formId: "form-05",
        dateInput: () => editDate15,
        regBtnId: "regist-btn15"
    }
};
// const number11 = document.getElementById('number11');
// const number12 = document.getElementById('number12');
// const number13 = document.getElementById('number13');
// const number14 = document.getElementById('number14');
// const number15 = document.getElementById('number15');
// const moldingNumber11 = document.getElementById('molding-number11');
// const moldingNumber12 = document.getElementById('molding-number12');
// const moldingNumber13 = document.getElementById('molding-number13');
// const moldingNumber14 = document.getElementById('molding-number14');
// const moldingNumber15 = document.getElementById('molding-number15');
// const makerCode11 = document.getElementById('maker-code11');
// const makerCode15 = document.getElementById('maker-code15');
// const itemCode11 = document.getElementById('item-code11');
// const itemCode15 = document.getElementById('item-code15');
// const makerName11 = document.getElementById('maker-name11');
// const makerName15 = document.getElementById('maker-name15');
// const itemName11 = document.getElementById('item-name11');
// const itemName15 = document.getElementById('item-name15');
// const makerId11 = document.getElementById('maker-id11');
// const makerId15 = document.getElementById('maker-id15');
// const itemId11 = document.getElementById('item-id11');
// const itemId15 = document.getElementById('item-id15');
// const useDate11 = document.getElementById('use-date11');
// const deliveryDate12 = document.getElementById('delivery-date12');
// const shippingDate13 = document.getElementById('shipping-date13');
// const lossDate14 = document.getElementById('loss-date14');
// const useDate15 = document.getElementById('use-date15');
// const deliveryDate15 = document.getElementById('delivery-date15');
// const shippingDate15 = document.getElementById('shipping-date15');
// const lossDate15 = document.getElementById('loss-date15');
// const recycleId11 = document.getElementById('recycle-id11');
// const recycleId12 = document.getElementById('recycle-id12');
// const recycleId13 = document.getElementById('recycle-id13');
// const recycleId14 = document.getElementById('recycle-id14');
// const recycleId15 = document.getElementById('recycle-id15');

/******************************************************************************************************* 画面 */

function createTable01Content(tableId, list) {
    const table = document.getElementById(tableId);

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "recycle_id",
            validCheck: item => item.loss_date !== "9999-12-31",
            onDoubleClick: (item, row) => {
                execEdit(item.recycle_id, 'edit', this);
            }
        });

        createTable01Row(row, item);
    });
}

function createFormTableContent(mode, list) {
    const config = MODE_CONFIG[mode];
    const table = document.getElementById(config.tableId);

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "recycle_id",
            validCheck: item => item.state !== deleteCode,
            // onDoubleClick: (item, row) => {
            //     // execEdit(item.recycle_id, row);
            // }
        });

        createFormTableRow(row, item, mode);
        // switch (tableId) {
        //     case "table-11-content":
        //         createTable11Row(newRow, item);
        //         break;
        //     case "table-12-content":
        //         createTable12Row(newRow, item);
        //         break;
        //     case "table-13-content":
        //         createTable13Row(newRow, item);
        //         break;
        //     case "table-14-content":
        //         createTable14Row(newRow, item);
        //         break;
        //     default:
        //         break;
        // }  
    });
}
// // リスト画面の本体部分を作成する
// function createFormTableContent(tableId, list) {
//     if (list == null) return;
//     const tbl = document.getElementById(tableId);
//     list.forEach(function (item) {
//         if (item.state != deleteCode) {
//             let newRow = tbl.insertRow();
//             // ID（Post送信用）
//             newRow.setAttribute('name', 'data-row');
//             newRow.setAttribute('data-id', item.recycle_id);

//             switch (tableId) {
//                 case "table-11-content":
//                     createTable11Row(newRow, item);
//                     break;
//                 case "table-12-content":
//                     createTable12Row(newRow, item);
//                     break;
//                 case "table-13-content":
//                     createTable13Row(newRow, item);
//                     break;
//                 case "table-14-content":
//                     createTable14Row(newRow, item);
//                     break;
//                 default:
//                     break;
//             }                        
//         }
//     });
// }
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
        // const result = await resultResponse.json();

        // const form = document.getElementById("form-dialog-05");

        if (result == null) {
            openMsgDialog("msg-dialog", "データがありません", "red");
            return;
        }
        entity = structuredClone(result);

        // openFormDialog("form-dialog-05"); 
        // resetFormInput(form, str);
        // setFormContent(form, entity);
        // setEnterFocus("form-05");
        // setCompanyComboBox(form, entity, companyComboList, officeComboList);
    } else {
        // openFormByMode(str, itemList, MODE_CONFIG);
        // if (str == "regist") {
        entity = structuredClone(formEntity);
        // リスト画面を初期化
        deleteElements(config.tableId);
            // const form = document.getElementById("form-dialog-01");
            // setCompanyComboBox(form, entity, companyComboList, officeComboList);
        // }
    }

    openFormByMode(mode, MODE_CONFIG);

    if (typeof mode === "string" && mode !== "edit") {
        ID_CONFIG[mode].date.value = getDate();
    }

    if (typeof mode === "string" && mode === "regist") {
        setCompanyComboBox(form, entity, companyComboList, officeComboList);
    } else if (typeof mode === "string" && mode === "edit") {
        setFormContent(form, entity);
        setCompanyComboBox(form, entity, companyComboList, officeComboList);
    }

    ID_CONFIG[mode].number.focus();
}

// コンテンツ部分作成
function setFormContent(form, entity) {
    form.querySelector('[name="recycle-id"]').value = entity.recycle_id;
    form.querySelector('[name="version"]').value = entity.version;
    form.querySelector('[name="number"]').value = entity.molding_number;
    form.querySelector('[name="recycle-number"]').value = entity.recycle_number;
    form.querySelector('[name="molding-number"]').value = entity.molding_number;
    form.querySelector('[name="maker-code"]').value = entity.maker_code;
    form.querySelector('[name="maker-name"]').value = entity.maker_name;
    form.querySelector('[name="maker-id"]').value = entity.maker_id;
    form.querySelector('[name="item-code"]').value = entity.item_code;
    form.querySelector('[name="item-name"]').value = entity.item_name;
    form.querySelector('[name="item-id"]').value = entity.item_id;
    form.querySelector('[name="recycling-fee"]').value = entity.recycling_fee;

    if (entity.use_date === "9999-12-31") {
        form.querySelector('[name="use-date"]').value = "";
    } else {
        form.querySelector('[name="use-date"]').value = entity.use_date;
    }

    if (entity.delivery_date === "9999-12-31") {
        form.querySelector('[name="delivery-date"]').value = "";
    } else {
        form.querySelector('[name="delivery-date"]').value = entity.delivery_date;
    }

    if (entity.shipping_date === "9999-12-31") {
        form.querySelector('[name="shipping-date"]').value = "";
    } else {
        form.querySelector('[name="shipping-date"]').value = entity.shipping_date;
    }

    if (entity.loss_date === "9999-12-31") {
        form.querySelector('[name="loss-date"]').value = "";
    } else {
        form.querySelector('[name="loss-date"]').value = entity.loss_date;
    }

    // 製造業者等名コンボボックス
    setCompanyComboBox(form, entity, companyComboList, officeComboList);

    // 処分場コンボボックス
    const disposalSiteArea = form.querySelector('select[name="disposal-site"]');
    if (disposalSiteArea != null) {
        createComboBoxWithTop(disposalSiteArea, disposalSiteComboList, "");
        setComboboxSelected(disposalSiteArea, entity.disposal_site_id); 
    }

    // form.querySelector('[autofocus]').focus();
}


function createFormdata(form, mode) {
    // document.getElementById(regBtnId).focus();
    const config = ID_CONFIG[mode];
    config.regBtn.focus();
    
    let formdata = [];
    // const formdata = structuredClone(formEntity);

    switch (mode) {
        case "regist":
            formdata = setInsertFormData(form);
            break;
        // case "delivery":
        //     formdata = setDateUpdateFormData(form);
        //     break;
        // case "shipping":
        //     formdata = setDateUpdateFormData(form);
        //     break;
        // case "loss":
        //     formdata = setDateUpdateFormData(form);
        //     break;
        default:
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
    formdata.version = formData.get('version');
    formdata.recycle_number = formData.get('recycle-number');
    formdata.molding_number = formData.get('molding-number');
    
    formdata.use_date = "9999-12-31";
    formdata.delivery_date = "9999-12-31";
    formdata.shipping_date = "9999-12-31";
    formdata.loss_date = "9999-12-31";

    if (formData.get('use-date') != null) {
        if (formData.get('use-date') != "") {
            formdata.use_date = formData.get('use-date');
        }
    }

    if (formData.get('delivery-date') != null) {
        if (formData.get('delivery-date') != "") {
            formdata.delivery_date = formData.get('delivery-date');
        }
    }

    if (formData.get('shipping-date') != null) {
        if (formData.get('shipping-date') != "") {
            formdata.shipping_date = formData.get('shipping-date');
        }
    }

    if (formData.get('loss-date') != null) {
        if (formData.get('loss-date') != "") {
            formdata.loss_date = formData.get('loss-date');
        }
    }

    if (formData.get('company') != null) {
        formdata.company_id = formData.get('company');
        const selectCompany = form.querySelector('[name="company"]');
        const companyName = selectCompany.options[selectCompany.selectedIndex].text;
        formdata.company_name = companyName;
    }

    if (formData.get('office') != null) {
        formdata.office_id = formData.get('office');
        const selectOffice = form.querySelector('[name="office"]');
        const officeName = selectOffice.options[selectOffice.selectedIndex].text;
        formdata.office_name = officeName;
    }

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

    if (formData.get('recycling-fee') != null) {
        formdata.recycling_fee = formData.get('recycling_fee');
    }

    if (formData.get('disposal-site') != null) {
        formdata.disposal_site_id = formData.get('disposal-site');
        const selectDisposalSite = form.querySelector('[name="disposal-site"]');
        const disposalSiteName = selectDisposalSite.options[selectDisposalSite.selectedIndex].text;
        formdata.disposal_site_name = disposalSiteName;
    }

    return formdata;
}

// 日付データを更新したformdataを取得する
function setDateUpdateFormData(form) {
    const formData = new FormData(form);
    // let formdata = {};
    const formdata = structuredClone(formEntity);

    formdata.recycle_id = formData.get('recycle-id');
    formdata.recycle_number = formData.get('recycle-number');
    formdata.molding_number = formData.get('molding-number');

    if (formData.get('use-date') != null && formData.get('use-date') != "") {
        formdata.date = formData.get('use-date');
    } else if (formData.get('delivery-date') != null && formData.get('delivery-date') != "") {
        formdata.date = formData.get('delivery-date');
    } else if (formData.get('shipping-date') != null && formData.get('shipping-date') != "") {
        formdata.date = formData.get('shipping-date');
    } else if (formData.get('loss-date') != null && formData.get('loss-date') != "") {
        formdata.date = formData.get('loss-date');
    } else {
        formdata.date = "9999-12-31";
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

// 入力チェック
function formDataCheck(form, mode) {
    let msg = "";
    const num = form.querySelector('input[name="recycle-number"]');
    if (num.value == 0) msg += '\nお問合せ管理票番号を入力して下さい';

    switch (mode) {
        case "regist":
            if (form.querySelector('select[name="company"]').value === 0) msg += '\n小売業者を選択して下さい';
            if (form.querySelector('input[name="maker-code"]').value === "") msg += '\n製造業者等名を選択して下さい';
            if (form.querySelector('input[name="item-code"]').value === "") msg += '\n品目・料金区分を選択して下さい';
            if (form.querySelector('input[name="use-date"]').value === "") msg += '\n使用日を入力して下さい';
            break;
        case "delivery":
            if (form.querySelector('input[name="delivery-date"]').value === "") msg += '\n引渡日を入力して下さい';
            break;
        case "shipping":
            if (form.querySelector('input[name="shipping-date"]').value === "") msg += '\n発送日を入力して下さい';
            break;
        case "loss":
            if (form.querySelector('input[name="loss-date"]').value === "") msg += '\nロス処理日を入力して下さい';
            break;
        case "edit":
            if (form.querySelector('select[name="company"]').value === 0) msg += '\n小売業者を選択して下さい';
            if (form.querySelector('input[name="maker-code"]').value === "") msg += '\n製造業者等名を選択して下さい';
            if (form.querySelector('input[name="item-code"]').value === "") msg += '\n品目・料金区分を選択して下さい';
            if (form.querySelector('input[name="use-date"]').value === "") msg += '\n使用日を入力して下さい';
            break;
        default:
            break;
    }

    // エラーが一つ以上あればエラーメッセージダイアログを表示する
    if (msg != "") {
        openMsgDialog("msg-dialog", msg, "red");
        return false;
    }
    return true;
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
// function resetFormInput(form, str) {
//     form.querySelector('[name="recycle-number"]').value = "";
//     form.querySelector('[name="molding-number"]').value = "";

//     switch (str) {
//         case "regist":
//             form.querySelector('[name="maker-code"]').value = "";
//             form.querySelector('[name="maker-name"]').value = "";
//             form.querySelector('[name="maker-id"]').value = "";
//             form.querySelector('[name="item-code"]').value = "";
//             form.querySelector('[name="item-name"]').value = "";
//             form.querySelector('[name="item-id"]').value = "";
//             form.querySelector('[name="recycling-fee"]').value = "";
//             break;
//         case "edit":
//             form.querySelector('[name="maker-code"]').value = "";
//             form.querySelector('[name="maker-name"]').value = "";
//             form.querySelector('[name="maker-id"]').value = "";
//             form.querySelector('[name="item-code"]').value = "";
//             form.querySelector('[name="item-name"]').value = "";
//             form.querySelector('[name="item-id"]').value = "";
//             form.querySelector('[name="use-date"]').value = "";
//             form.querySelector('[name="delivery-date"]').value = "";
//             form.querySelector('[name="shipping-date"]').value = "";
//             form.querySelector('[name="loss-date"]').value = "";
//             form.querySelector('[name="company"]').value = 0;
//             form.querySelector('[name="disposal-site"]').value = 0;
//             form.querySelector('[name="recycling-fee"]').value = 0;
//             form.querySelector('[name="version"]').value = 0;
//             form.querySelector('[name="state"]').value = 0;
//             break;
//         default:
//             break;
//     }
//     form.querySelector('input[name="recycle-number"]').focus();
// }

/******************************************************************************************************* 取得・検証 */

// お問合せ管理票番号を取得して検証
// async function searchForExistByNumber(form, list, numberBox, recycleId, moldingId, versionId, mode) {
// async function searchForExistByNumber(list, mode) {
async function execNumberBlur(e, mode) {
    e.preventDefault();
    if (e.currentTarget == null) return;

    const config = ID_CONFIG[mode];
    if (config.number.value == null) return;

    // const number = removeEdgeA(config.number.value);

    // 入力値が正しいかチェックする
    const number = checkNumber(config.number.value);
    // const number = await existsRecycleByNumber(entity);

    // const numberBtn = form.querySelector('input[name="recycle-number"]');
    // const regBtn = form.querySelector('button[name="regist-btn"]');
    // const number = checkNumber(numberBox); 

    // Null、または不正な値はエラー
    if (number == null) {
        // config.number.focus();
        clearNumber(config.number);
        return;   
    } else if (!number) {
        openMsgDialog("msg-dialog", "不正な番号です。", 'red');
        setFocusElement("msg-dialog", config.number);
        return;
    }

    // テーブルリストに追加されている場合はエラー
    if (mode !== "edit") {
        const entity = itemList.find(value => value.recyle_number === number);
        if (entity != null) {
            // clearNumber(config.number);
            openMsgDialog("msg-dialog", "「" + molNumber + "」は、リストに存在します", 'red');
            setFocusElement("msg-dialog", config.number);
            return;
        }
    }

    // DBを確認する
    const item = await existsRecycleByNumber(number);

    // let item = {};
    let msg = "";
    // 0000-00000000-0 に成型する
    const molNumber = moldingNumber(number);

    if (mode !== "edit") {
        // const entity = itemList.find(value => value.recyle_number === number);
        // if (entity != null) {
        //     // clearNumber(config.number);
        //     openMsgDialog("msg-dialog", "「" + molNumber + "」は、リストに存在します", 'red');
        //     setFocusElement("msg-dialog", config.number);
        //     return;
        // }

        // item = origin.find(value => value.recycle_number === number);
        if (item != null && mode === "regist") {
            msg = "「" + molNumber + "」は、すでに登録されています";
        } else if (item == null && mode === "delivery") {
            msg = "「" + molNumber + "」は、使用登録されていません";
        } else if (item == null && mode === "shopping") {
            msg = "「" + molNumber + "」は、使用登録されていません";
        } else if (item != null && item.delivery_date !== "9999-12-31" && mode === "delivery") {
            msg = "「" + molNumber + "」は、引渡しされています";
        } else if (item != null && item.delivery_date === "9999-12-31" && mode === "shopping") {
            msg = "「" + molNumber + "」は、引渡しされていません";
        } else if (item != null && item.shopping_date !== "9999-12-31" && mode === "shopping") {
            msg = "「" + molNumber + "」は、発送されています";
        }
    } else {
        if (config.recycleId == null) return;
        // const recycleId = Number(config.recycleId.value);
        // item = origin.find(value => value.recycle_id === recycleId && value.recycle_number === number);
        if (item != null && item.recycle_id !== Number(config.recycleId.value)) {
            // const result = origin.find(value => value.recycle_id !== recycleId && value.recycle_number === number);
            // EDITでエラーの場合は画面を閉じる
            // if (result != null) {
                openMsgDialog("msg-dialog", "「" + molNumber + "」は、すでに登録されています", 'red');
                setFocusElement("msg-dialog", config.number);
                config.number.value = config.moldingNumber.value;
                // // ダイアログを閉じる
                // closeFormDialog(MODE_CONFIG[mode].dialogId);
                return;
            // }
        }
    // } else {
    //     // const entity = await existsRecycleByNumber(number);
        
    //     // if (entity != null && entity.loss_date != "9999-12-31") {
    //     //     clearNumber(config.number);
    //     //     openMsgDialog("msg-dialog", "その番号は、ロス処理済みです", 'red');
    //     //     setFocusElement("msg-dialog", config.number);
    //     // } else {
    //     //     processNumberAfterCheck(entity, number, mode, numberBox, recycleId, moldingId, versionId, numberBtn, regBtn);
    //     // }
    
    //     if (item != null && mode === "regist") {
    //         msg = "その番号は、すでに登録されています";
    //     } else if (item == null && mode === "delivery") {
    //         msg = "その番号は、使用登録されていません";
    //     } else if (item == null && mode === "shopping") {
    //         msg = "その番号は、使用登録されていません";
    //     } else if (item != null && item.delivery_date !== "9999-12-31" && mode === "delivery") {
    //         msg = "その番号は、引渡しされています";
    //     } else if (item != null && item.delivery_date === "9999-12-31" && mode === "shopping") {
    //         msg = "その番号は、引渡しされていません";
    //     } else if (item != null && item.shopping_date !== "9999-12-31" && mode === "shopping") {
    //         msg = "その番号は、発送されています";
    //     }
    }

    if (msg !== "") {
        openMsgDialog("msg-dialog", msg, 'red');
        setFocusElement("msg-dialog", config.number);
        clearNumber(config.number);
        // config.number.value = config.moldingNumber.value;
        // config.number.focus();
    } else {
        config.recycleId.value = item == null ? 0: item.recycle_id;
        config.version.value = item == null ? 0: item.version;
        // const molNumber = moldingNumber(config.number);
        config.recycleNumber.value = number;
        config.moldingNumber.value = molNumber;

        config.number.value = molNumber !== "" ? molNumber: "";
        if (mode !== "regist" && mode !== "edit") {
            config.regBtn.click();
        }
    }
}

// // お問合せ管理票番号検証後の処理
// function processNumberAfterCheck(entity, number, mode, numberBox, recycleId, moldingId, versionId, numberBtn, regBtn) {
//     switch (mode) {
//         case "regist":
//             if (entity != null && entity.recycle_id > 0) {
//                 clearNumber(numberBox);
//                 openMsgDialog("msg-dialog", "その番号は、すでに登録されています", 'red');
//                 setFocusElement("msg-dialog", numberBtn);
//                 return;
//             } else {
//                 recycleId.value = "";
//                 versionId.value = "";
//                 moldingId.value = moldingNumber(numberBox);
//                 if (moldingId.value != "") numberBox.value = number;
//             }
//             break;
//         case "delivery":
//             if (entity == null) {
//                 clearNumber(numberBox);
//                 openMsgDialog("msg-dialog", "その番号は、使用登録されていません", 'red');
//                 setFocusElement("msg-dialog", numberBtn);
//                 return;
//             } else if (entity.delivery_date != "9999-12-31") {
//                 clearNumber(numberBox);
//                 openMsgDialog("msg-dialog", "その番号は、引渡しされています", 'red');
//                 setFocusElement("msg-dialog", numberBtn);
//                 return;
//             } else {
//                 recycleId.value = entity.recycle_id;
//                 versionId.value = entity.version;
//                 moldingId.value = moldingNumber(numberBox);
//                 if (moldingId.value != "") numberBox.value = number;
//                 regBtn.click();
//             }
//             break;
//         case "shipping":
//             if (entity == null) {
//                 clearNumber(numberBox);
//                 openMsgDialog("msg-dialog", "その番号は、使用登録されていません", 'red');
//                 setFocusElement("msg-dialog", numberBtn);
//                 return;
//             } else if (entity.delivery_date == "9999-12-31") {
//                 clearNumber(numberBox);
//                 openMsgDialog("msg-dialog", "その番号は、引渡しされていません", 'red');
//                 setFocusElement("msg-dialog", numberBtn);
//                 return
//             } else if (entity.shipping_date != "9999-12-31") {
//                 clearNumber(numberBox);
//                 openMsgDialog("msg-dialog", "その番号は、発送されています", 'red');
//                 setFocusElement("msg-dialog", numberBtn);
//                 return;
//             } else {
//                 recycleId.value = entity.recycle_id;
//                 versionId.value = entity.version;
//                 moldingId.value = moldingNumber(numberBox);
//                 if (moldingId.value != "") numberBox.value = number;
//                 regBtn.click();
//             }
//             break;
//         case "loss":
//             if (entity != null) {
//                 recycleId.value = entity.recycle_id;
//                 versionId.value = entity.version;
//             } else {
//                 recycleId.value = 0;
//                 versionId.value = 0;
//             }
//             moldingId.value = moldingNumber(numberBox);
//             if (moldingId.value != "") numberBox.value = number;
//             regBtn.click();
//             break;
//         default:
//             break;
//     }
// }

// コードから[maker]を取得して、名前を表示
// async function searchForNameByMakerCode(form, makerCode, makerName, makerId) {
// async function searchForNameByMakerCode(mode) {
async function execMakerCodeBlur(e, mode) {
    e.preventDefault();
    if (e.currentTarget == null) return;
    // let code = 0;
    // const makerBtn = form.querySelector('input[name="maker-code"]');
    // if (makerCode.value == "" || isNaN(makerCode.value)) {
    //     makerCode.value = "";
    //     makerName.value = "";
    //     makerId.value = "";
    //     return;
    // }
    // code = makerCode.value;

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
        makerName.value = entity.name;
        makerId.value = entity.recycle_maker_id;
    } else {
        makerCode.value = "";
        makerName.value = "";
        makerId.value = "";
        // makerCode.focus();
        openMsgDialog("msg-dialog", "コードが登録されていません", 'red');
        setFocusElement("msg-dialog", makerCode);

        const dialog = document.getElementById("msg-dialog");
        const focusBtn = dialog.querySelector('[name="focus-btn"]');
        if (focusBtn != null) focusBtn.focus();
    }
}

// コードから[item]を取得して、名前を表示
// async function searchForNameByItemCode(form, itemCode, itemName, itemId) {
// async function searchForNameByItemCode(mode) {
async function execItemCodeBlur(e, mode) {
    e.preventDefault();
    if (e.currentTarget == null) return;
    // let code = 0;
    // const codeBtn = form.querySelector('input[name="item-code"]')

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
    // code = itemCode.value;

    const entity = await getItemByCode(Number(itemCode.value));
    if (entity != null && entity.recycle_item_id > 0) {
        itemName.value = entity.name;
        itemId.value = entity.recycle_item_id;
    } else {
        itemCode.value = "";
        itemName.value = "";
        itemId.value = "";
        // itemCode.focus();
        openMsgDialog("msg-dialog", "コードが登録されていません", 'red');
        setFocusElement("msg-dialog", itemCode);
    }
}

/******************************************************************************************************* 削除 */

async function execDelete(self) {
    const area = self.closest('[data-tab]');
    if (!area) return;
    const config = MODE_CONFIG[area.dataset.tab];

    // // スピナー表示
    // startProcessing();

    const result = await deleteTablelist(config.tableId, '/api/recycle/delete');

    if (result.success) {                
        // await execDate01Search(tableId);
        await refleshDisplay01();
        // origin = await getRecyclesBetween("start-date01", "end-date01", "/api/recycle/get/between");
        // await updateTableDisplay(config.tableId, config.footerId, config.searchId, origin, createTable01Content);
        openMsgDialog("msg-dialog", result.message, "blue");
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }

    // // スピナー消去
    // processingEnd();
}

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

    // const config = MODE_CONFIG[str];
    await updateFormTableDisplay(area.mode, itemList);

    // switch (str) {
    //     case "regist":
    //         await updateFormTableDisplay("table-11-content", "footer-11", itemList);
    //         break;
    //     case "delivery":
    //         await updateFormTableDisplay("table-12-content", "footer-12", itemList);
    //         break;
    //     case "shipping":
    //         await updateFormTableDisplay("table-13-content", "footer-13", itemList);
    //         break;
    //     case "loss":
    //         await updateFormTableDisplay("table-14-content", "footer-14", itemList);
    //         break;
    //     default:
    //         break;
    // }
}



// /******************************************************************************************************* 入力画面 */

// // リスト画面の本体部分を作成する
// function createTableContent(tableId, list) {
//     const tbl = document.getElementById(tableId);
//     list.forEach(function (item) {
//         let newRow = tbl.insertRow();
//         // ID（Post送信用）
//         newRow.setAttribute('name', 'data-row');
//         newRow.setAttribute('data-id', item.recycle_id);
//         if (item.loss_date != "9999-12-31") {
//             newRow.setAttribute('data-valid', true);
//         } else {
//             newRow.setAttribute('data-valid', false);
//         }
//         // シングルクリック時の処理
//         newRow.onclick = function (e) {
//             if (!e.currentTarget.classList.contains('selected')){
//                 // すべての行の選択状態を解除する
//                 detachmentSelectClassToAllRow(tbl, false);
//                 // 選択した行にセレクトクラスを付与する
//                 const result = addSelectClassToRow(e.currentTarget);
//             } else {
//                 // 選択済みの要素をクリックした時の処理
//                 const clickedTd = e.target.closest("td");
//                 // 取得したTDの処理
//             }
//         }
//         // ダブルクリック時の処理
//         newRow.ondblclick = function (e) { 
//             // チェックボックスの動作を停止させる
//             e.preventDefault();
//             // 選択済みの場合
//             if (!e.currentTarget.classList.contains('selected')){
//                 // すべての行の選択状態を解除する
//                 detachmentSelectClassToAllRow(tableId, false);
//                 // 選択した行にセレクトクラスを付与する
//                 const result = addSelectClassToRow(e.currentTarget);
//             }
            
//             // フォーム入力画面を表示する
//             execEdit(item.recycle_id, this);
//         }

//         createTable01Row(newRow, item);
//     });
// }

// // リスト画面の本体部分を作成する
// function createFormTableContent(tableId, list) {
//     if (list == null) return;
//     const tbl = document.getElementById(tableId);
//     list.forEach(function (item) {
//         if (item.state != deleteCode) {
//             let newRow = tbl.insertRow();
//             // ID（Post送信用）
//             newRow.setAttribute('name', 'data-row');
//             newRow.setAttribute('data-id', item.recycle_id);

//             switch (tableId) {
//                 case "table-11-content":
//                     createTable11Row(newRow, item);
//                     break;
//                 case "table-12-content":
//                     createTable12Row(newRow, item);
//                     break;
//                 case "table-13-content":
//                     createTable13Row(newRow, item);
//                     break;
//                 case "table-14-content":
//                     createTable14Row(newRow, item);
//                     break;
//                 default:
//                     break;
//             }                        
//         }
//     });
// }



// /******************************************************************************************************* 入力画面 */

// // 商品登録画面を開く
// async function execEdit(id, self, str) {
//     // スピナー表示
//     startProcessing();

//     itemList = [];
//     let entity = {};
//     if (id > 0) {
//         // 選択されたIDのエンティティを取得
//         const data = "id=" + encodeURIComponent(parseInt(id));
//         const resultResponse = await postFetch('/api/recycle/get/id', data, token, 'application/x-www-form-urlencoded');
//         const result = await resultResponse.json();

//         let form = document.getElementById("form-dialog-05");

//         if (result.recycle_id == 0) {
//             openMsgDialog("msg-dialog", "データがありません", "red");
            
//             processingEnd();
//             return;
//         }
//         entity = structuredClone(result);
//         openFormDialog("form-dialog-05"); 
//         resetFormInput(form, str);
//         setFormContent(form, entity);
//         setEnterFocus("form-05");
//     } else {
//         entity = structuredClone(formEntity);

//         openFormByMode("regist", itemList, ORDER_MODE_CONFIG);
//         // let form = "";
//         // let formId = "";
//         // let formDialogId = "";

//         // switch (str) {
//         //     case "regist":
//         //         formDialogId = "form-dialog-01";
//         //         formId = "form-01";
//         //         useDate11.value = "";
//         //         await updateFormTableDisplay("table-11-content", "footer-11", itemList);
//         //         break;
//         //     case "delivery":
//         //         formDialogId = "form-dialog-02";
//         //         formId = "form-02";
//         //         deliveryDate12.value = "";
//         //         await updateFormTableDisplay("table-12-content", "footer-12", itemList);
//         //         break;
//         //     case "shipping":
//         //         formDialogId = "form-dialog-03";
//         //         formId = "form-03";
//         //         shippingDate13.value = "";
//         //         await updateFormTableDisplay("table-13-content", "footer-13", itemList);
//         //         break;
//         //     case "loss":
//         //         formDialogId = "form-dialog-04";
//         //         formId = "form-04";
//         //         lossDate14.value = "";
//         //         await updateFormTableDisplay("table-14-content", "footer-14", itemList);
//         //         break;
//         //     default:
//         //         break;
//         // }

//         // openFormDialog(formDialogId); 
//         // form = document.getElementById(formDialogId);
//         // resetFormInput(form, str);
//         // setEnterFocus(formId);

//         // if (str == "regist") {
//         //     setCompanyComboBox(form, entity, companyComboList, officeComboList);
//         // }
//     }

//     // スピナー消去
//     processingEnd();
// }





/******************************************************************************************************* 登録 */

async function execRegistItem(self, mode) {
    // let regBtnId = "";
    // let formId = "";
    // let tableId = "";
    // let footerId = "";

    // switch (str) {
    //     case "regist":
    //         regBtnId = 'regist-btn11';
    //         formId = 'form-01';
    //         tableId = "table-11-content";
    //         footerId = "footer-11";
    //         break;
    //     case "delivery":
    //         regBtnId = 'regist-btn12';
    //         formId = 'form-02';
    //         tableId = "table-12-content";
    //         footerId = "footer-12";
    //         break;
    //     case "shipping":
    //         regBtnId = 'regist-btn13';
    //         formId = 'form-03';
    //         tableId = "table-13-content";
    //         footerId = "footer-13";
    //         break;
    //     case "loss":
    //         regBtnId = 'regist-btn14';
    //         formId = 'form-04';
    //         tableId = "table-14-content";
    //         footerId = "footer-14";
    //         break;
    //     default:
    //         break;
    // }

    // const form = document.getElementById(formId);
    // if (formDataCheck(form, str) == false) {
    //     return;
    // }

    // const formdata = createFormdata(form, regBtnId, str);

    // itemList.push(formdata);

    // await updateFormTableDisplay(tableId, footerId, itemList);
    // scrollIntoTableList(tableId, itemId);
    // resetFormInput(form, str);

    const config = MODE_CONFIG[mode];
    const form = document.getElementById(config.formId);
    if (formDataCheck(form, mode) == false) {
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
    // // スピナー表示
    // startProcessing();

    // 保存処理
    const result = await updateFetch("/api/recycle/save/" + mode, JSON.stringify({list:itemList}), token, "application/json");

    // const result = await resultResponse.json();
    if (result.success) {        
        await refleshDisplay01();
        // origin = await getRecyclesBetween("start-date01", "end-date01", "/api/recycle/get/between");
        // await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin, createTable01Content);
        // await execDate01Search(tableId);
        // 追加・変更行に移動
        scrollIntoTableList(tableId, result.id);

        openMsgDialog("msg-dialog", result.message, "blue");
        // itemList = [];
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }
    // ダイアログを閉じる
    closeFormDialog(formId);

    // // スピナー消去
    // processingEnd();
}

// 更新処理
async function execUpdate(mode) {
    const config = MODE_CONFIG[mode];

    const form = document.getElementById(config.formId);
    if (formDataCheck(form, mode) == false) {
        return;
    }

    // // スピナー表示
    // startProcessing();

    const formdata = setInsertFormData(form);
    const result = await updateFetch("/api/recycle/save/" + mode, JSON.stringify({entity:formdata}), token, "application/json");
    // const result = await resultResponse.json();
    if (result.success) {
        // 画面更新
        await refleshDisplay01();
        // origin = await getRecyclesBetween("start-date01", "end-date01", "/api/recycle/get/between");
        // await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin, createTable01Content);
        // await execDate01Search(tableId);
        openMsgDialog("msg-dialog", result.message, "blue");
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }

    // ダイアログを閉じる
    closeFormDialog(config.dialogId);

    // // スピナー消去
    // processingEnd();
}

/******************************************************************************************************* ダウンロード */

async function execDownloadCsv(self) {
    const area = self.closest('[data-tab]');
    if (!area) return;
    const config = MODE_CONFIG[area.dataset.tab];
    await downloadCsv(config.tableId, '/api/recycle/download/csv');
}

/******************************************************************************************************* 画面更新 */

// // テーブルリスト画面を更新する
// async function updateTableDisplay(tableId, footerId, searchId, list) {
//     // フィルター処理
//     const result = filterDisplay(searchId, list);
//     // リスト画面を初期化
//     deleteElements(tableId);
//     // リスト作成
//     createTableContent(tableId, result);
//     // フッター作成
//     createTableFooter(footerId, list);
//     // チェックボタン押下時の処理を登録する
//     registCheckButtonClicked(tableId);
//     // すべて選択ボタンをオフにする
//     turnOffAllCheckBtn(tableId);
//     // テーブルのソートをリセットする
//     resetSortable(tableId);
//     // スクロール時のページトップボタン処理を登録する
//     setPageTopButton(tableId);
//     // テーブルにスクロールバーが表示されたときの処理を登録する
//     document.querySelectorAll('.scroll-area').forEach(el => {
//         toggleScrollbar(el);
//     });
// }

// テーブルリスト画面を更新する
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

// async function execFilterDisplay(tableId) {
//     await updateTableDisplay(tableId, "footer-01", "search-box-01", origin);
// }

async function refleshDisplay01() {
    origin = await getRecyclesBetween("start-date01", "end-date01", "/api/recycle/get/between");
    await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin, createTable01Content);
}

// 一覧表示用のリスト取得
async function getRecyclesBetween(startId, endId, url) {
    // // スピナー表示
    // startProcessing();

    const start = document.getElementById(startId).value;
    const end = document.getElementById(endId).value;
    const col = document.getElementById('date-category01').value;
    const data = "&start=" + encodeURIComponent(start) + "&end=" + encodeURIComponent(end) + "&col=" + encodeURIComponent(col);
    const contentType = 'application/x-www-form-urlencoded';
    // List<Recycle>を取得
    return await searchFetch(url, data, token, contentType);

    // // スピナー消去
    // processingEnd();

    // return await resultResponse.json();
}

/******************************************************************************************************* コード入力時の処理 */

// // コード入力ボックスからフォーカスが外れた時の処理
// function execMakerCodeBlur(e, mode) {
//     e.preventDefault();
//     if (e.currentTarget == null) return;
//     const form = e.currentTarget.closest('.dialog-content');
//     searchForNameByMakerCode(mode);

//     // const config = ID_CONFIG[str];
//     // const makerCode = config.makerCode;
//     // const makerName = config.makerName;
//     // const makerId = config.makerId
//     // // let makerCode = "";
//     // // let makerName = "";
//     // // let makerId = "";
//     // // switch (str) {
//     // //     case "regist":
//     // //         makerCode = makerCode11;
//     // //         makerName = makerName11;
//     // //         makerId = makerId11;
//     // //         break;
//     // //     case "edit":
//     // //         makerCode = makerCode15;
//     // //         makerName = makerName15;
//     // //         makerId = makerId15
//     // //         break;
//     // //     default:
//     // //         break;
//     // // }
//     // searchForNameByMakerCode(form, makerCode, makerName, makerId)
// }

// // コード入力ボックスからフォーカスが外れた時の処理
// function execItemCodeBlur(e, mode) {
//     e.preventDefault();
//     if (e.currentTarget == null) return;
//     const form = e.currentTarget.closest('.dialog-content');
//     searchForNameByItemCode(mode);

//     // const config = ID_CONFIG[str];
//     // const itemCode = config.itemCode;
//     // const itemName = config.itemName;
//     // const itemId = config.itemId

//     // // let itemCode = "";
//     // // let itemName = "";
//     // // let itemId = "";
//     // // switch (str) {
//     // //     case "regist":
//     // //         itemCode = itemCode11;
//     // //         itemName = itemName11;
//     // //         itemId = itemId11
//     // //         break;
//     // //     case "edit":
//     // //         itemCode = itemCode15;
//     // //         itemName = itemName15;
//     // //         itemId = itemId15
//     // //         break;
//     // //     default:
//     // //         break;
//     // // }
//     // searchForNameByItemCode(form, itemCode, itemName, itemId)
// }

// async function execNumberBlur(e, mode) {
//     e.preventDefault();
//     if (e.currentTarget == null) return;
//     const form = e.currentTarget.closest('.dialog-content');
//     // let numberBox = "";
//     // let recycleId = "";
//     // let moldingId = "";
//     // let versionId = "";
//     // switch (str) {
//     //     case "regist":
//     //         numberBox = number11;
//     //         recycleId = recycleId11;
//     //         moldingId = moldingNumber11;
//     //         versionId = version11;
//     //         break;
//     //     case "delivery":
//     //         numberBox = number12;
//     //         recycleId = recycleId12;
//     //         moldingId = moldingNumber12;
//     //         versionId = version12;
//     //         break;
//     //     case "shipping":
//     //         numberBox = number13;
//     //         recycleId = recycleId13;
//     //         moldingId = moldingNumber13;
//     //         versionId = version13;
//     //         break;
//     //     case "loss":
//     //         numberBox = number14;
//     //         recycleId = recycleId14;
//     //         moldingId = moldingNumber14;
//     //         versionId = version14;
//     //         break;
//     //     case "edit":
//     //         numberBox = number15;
//     //         recycleId = recycleId15;
//     //         moldingId = moldingNumber15;
//     //         versionId = version15;
//     //         break;
//     //     default:
//     //         break;
//     // }
//     // await searchForExistByNumber(form, itemList, numberBox, recycleId, moldingId, versionId, str);
//     await searchForExistByNumber(itemList, mode);
// }

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
    // tabFocusElements = createTabFocusElements();
    resetEnterFocus();
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {
    // // スピナー表示
    // startProcessing();

    // 日付フィルターコンボボックス
    const dateCategoryArea = document.querySelector('select[name="date-category"]')
    createComboBoxValueString(dateCategoryArea, dateComboList);

    // const config = MODE_CONFIG['regist'];
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
    // // お問合せ管理票番号入力ボックスのフォーカスが外れた時の処理を登録
    // number11.addEventListener('blur', function (e) { execNumberBlur(e, 'regist'); });
    // // お問合せ管理票番号入力ボックスのフォーカスが外れた時の処理を登録
    // number12.addEventListener('blur', function (e) { execNumberBlur(e, 'delivery'); });
    // // お問合せ管理票番号入力ボックスのフォーカスが外れた時の処理を登録
    // number13.addEventListener('blur', function (e) { execNumberBlur(e, 'shipping'); });
    // // お問合せ管理票番号入力ボックスのフォーカスが外れた時の処理を登録
    // number14.addEventListener('blur', function (e) { execNumberBlur(e, 'loss'); });
    // // お問合せ管理票番号入力ボックスのフォーカスが外れた時の処理を登録
    // number15.addEventListener('blur', function (e) { execNumberBlur(e, 'edit'); });
    // // メーカーコード入力ボックスのフォーカスが外れた時の処理を登録
    // makerCode11.addEventListener('blur', function (e) { execMakerCodeBlur(e, 'regist'); });
    // // メーカーコード入力ボックスのフォーカスが外れた時の処理を登録
    // makerCode15.addEventListener('blur', function (e) { execMakerCodeBlur(e, 'edit'); });
    // // 品目コード入力ボックスのフォーカスが外れた時の処理を登録
    // itemCode11.addEventListener('blur', function (e) { execItemCodeBlur(e, 'regist'); });
    // // 品目コード入力ボックスのフォーカスが外れた時の処理を登録
    // itemCode15.addEventListener('blur', function (e) { execItemCodeBlur(e, 'edit'); });

    // 品目固定チェックボッスクス押下時の処理を登録
    document.getElementById("fix-item-checkbox").addEventListener("change", function () {
        itemCheckedAfter();
    });

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        // await execFilterDisplay("table-01-content");
        await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin, createTable01Content);
    }, false);

    execSpecifyPeriod("today", 'start-date01', 'end-date01');

    // 画面更新
    await refleshDisplay01();
    // origin01 = origin.filter(value => value.classification == goodsCode);
    // await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin, createTable01Content);

    // // スピナー消去
    // processingEnd();
});