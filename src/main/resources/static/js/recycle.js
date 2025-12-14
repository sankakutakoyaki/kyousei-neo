"use strict"

// Numberからrecycleを取得
async function existsRecycleByNumber(number) {
    // スピナー表示
    startProcessing();
    const data = "num=" + encodeURIComponent(number);
    const url = "/recycle/exists/number";
    const contentType = 'application/x-www-form-urlencoded';
    const resultResponse = await postFetch(url, data, token, contentType);
    // スピナー消去
    processingEnd();
    if (resultResponse.ok) {
        return await resultResponse.json();
    } else {
        return null;
    }
}


// コードからrecycle_makerを取得
async function getMakerByCode(code) {
    // スピナー表示
    startProcessing();
    const data = "code=" + encodeURIComponent(parseInt(code));
    const url = '/recycle/maker/get/code';
    const contentType = 'application/x-www-form-urlencoded';
    const resultResponse = await postFetch(url, data, token, contentType);
    // スピナー消去
    processingEnd();
    if (resultResponse.ok) {
        return await resultResponse.json();
    } else {
        return null;
    }
}

// コードからrecycle_itemを取得
async function getItemByCode(code) {
    // スピナー表示
    startProcessing();
    const data = "code=" + encodeURIComponent(parseInt(code));
    const url = '/recycle/item/get/code';
    const contentType = 'application/x-www-form-urlencoded';
    const resultResponse = await postFetch(url, data, token, contentType);
    // スピナー消去
    processingEnd();
    if (resultResponse.ok) {
        return await resultResponse.json();
    } else {
        return null;
    }
}

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

function setDateUpdateFormData(form) {
    const formData = new FormData(form);
    let formdata = {};

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

// コードから[maker]を取得して、名前を表示
async function searchForNameByMakerCode(form, makerCode, makerName, makerId) {
    // if (e.currentTarget == null) return;
    // const form = e.currentTarget.closest('.dialog-content');
    let code = 0;
    const makerBtn = form.querySelector('input[name="maker-code"]');
    if (makerCode.value == "" || isNaN(makerCode.value)) {
        makerCode.value = "";
        makerName.value = "";
        makerId.value = "";
        return;
    }
    code = makerCode.value;

    const entity = await getMakerByCode(Number(code));

    if (entity != null && entity.recycle_maker_id > 0) {
        makerName.value = entity.name;
        makerId.value = entity.recycle_maker_id;
    } else {
        makerCode.value = "";
        makerName.value = "";
        makerId.value = "";
        openMsgDialog("msg-dialog", "コードが登録されていません", 'red');
        setFocusElement("msg-dialog", makerBtn);
    }
}

// コードから[item]を取得して、名前を表示
async function searchForNameByMakerCode(form, itemCode, itemName, itemId) {
    // if (e.currentTarget == null) return;
    // const form = e.currentTarget.closest('.dialog-content');
    let code = 0;
    const codeBtn = form.querySelector('input[name="item-code"]')
    if (itemCode.value == "" || isNaN(itemCode.value)) {
        itemCode.value = "";
        itemName.value = "";
        itemId.value = "";
        return;
    }
    code = itemCode.value;

    const entity = await getItemByCode(Number(code));

    if (entity != null && entity.recycle_item_id > 0) {
        itemName.value = entity.name;
        itemId.value = entity.recycle_item_id;
    } else {
        itemCode.value = "";
        itemName.value = "";
        itemId.value = "";
        itemCode.focus();
        openMsgDialog("msg-dialog", "コードが登録されていません", 'red');
        setFocusElement("msg-dialog", codeBtn);
    }
}

// // コードから[item]を取得して、名前を表示
// async function searchForNameByItemCode(form, itemCode, itemName, itemId) {
//     // if (e.currentTarget == null) return;
//     // const form = e.currentTarget.closest('.dialog-content');
//     let code = 0;
//     const codeBtn = form.querySelector('input[name="item-code"]');

//     if (itemCode.value == "" || isNaN(itemCode11.value)) {
//         itemCode.value = "";
//         itemName.value = "";
//         itemId.value = "";
//         code = itemCode.value;

//         if (itemCode.value == "" || isNaN(itemCode.value)) {
//             itemCode.value = "";
//             itemName.value = "";
//             itemId.value = "";
//             return;
//         }
//         code = itemCode.value;
//     }
// }


// お問合せ管理票番号を取得して検証
async function searchForExistByNumber(form, list, numberBox, recycleId, moldingId, versionId, str) {
    const numberBtn = form.querySelector('input[name="recycle-number"]');
    const number = checkNumber(numberBox); 

    if (number == "") {
        clearNumber(numberBox);
        return;   
    }

    const item = list.find(value => value.recyle_number == number);
    if (item != null) {
        clearNumber(numberBox);
        openMsgDialog("msg-dialog", "その番号は、リストに存在します", 'red');
        setFocusElement("msg-dialog", numberBtn);
        return;
    }

    const entity = await existsRecycleByNumber(number);

    if (entity != null && entity.loss_date != "9999-12-31") {
        clearNumber(numberBox);
        openMsgDialog("msg-dialog", "その番号は、ロス処理済みです", 'red');
        setFocusElement("msg-dialog", numberBtn);
    } else {
        processNumberAfterCheck(entity, number, str, numberBox, recycleId, moldingId, versionId, numberBtn);
    }
}

function processNumberAfterCheck(entity, number, str, numberBox, recycleId, moldingId, versionId, numberBtn) {
    switch (str) {
        case "regist":
            if (entity != null && entity.recycle_id > 0) {
                clearNumber(numberBox);
                openMsgDialog("msg-dialog", "その番号は、すでに登録されています", 'red');
                setFocusElement("msg-dialog", numberBtn);
                return;
            } else {
                recycleId.value = "";
                versionId.value = "";
                moldingId.value = moldingNumber(numberBox);
                if (moldingId.value != "") numberBox.value = number;
            }
            break;
        case "delivery":
            if (entity == null) {
                clearNumber(numberBox);
                openMsgDialog("msg-dialog", "その番号は、使用登録されていません", 'red');
                setFocusElement("msg-dialog", numberBtn);
                return;
            } else if (entity.delivery_date != "9999-12-31") {
                clearNumber(numberBox);
                openMsgDialog("msg-dialog", "その番号は、引渡しされています", 'red');
                setFocusElement("msg-dialog", numberBtn);
                return;
            } else {
                recycleId.value = entity.recycle_id;
                versionId.value = entity.version;
                moldingId.value = moldingNumber(numberBox);
                if (moldingId.value != "") numberBox.value = number;
            }
            break;
        case "shipping":
            if (entity == null) {
                clearNumber(numberBox);
                openMsgDialog("msg-dialog", "その番号は、使用登録されていません", 'red');
                setFocusElement("msg-dialog", numberBtn);
                return;
            } else if (entity.delivery_date == "9999-12-31") {
                clearNumber(numberBox);
                openMsgDialog("msg-dialog", "その番号は、引渡しされていません", 'red');
                setFocusElement("msg-dialog", numberBtn);
                return
            } else if (entity.shipping_date != "9999-12-31") {
                clearNumber(numberBox);
                openMsgDialog("msg-dialog", "その番号は、発送されています", 'red');
                setFocusElement("msg-dialog", numberBtn);
                return;
            } else {
                recycleId.value = entity.recycle_id;
                versionId.value = entity.version;
                moldingId.value = moldingNumber(numberBox);
                if (moldingId.value != "") numberBox.value = number;
            }
            break;
        case "loss":
            if (entity != null) {
                recycleId.value = entity.recycle_id;
                versionId.value = entity.version;
            } else {
                recycleId.value = 0;
                versionId.value = 0;
            }
            moldingId.value = moldingNumber(numberBox);
            if (moldingId.value != "") numberBox.value = number;
            break;
        default:
            break;
    }
}

function clearNumber(numberBox) {
    if (numberBox != null) {
        numberBox.value = "";
    }
}

function checkNumber(numberBox) {
    if (numberBox != null) {
        const num = numberBox.value;
        const number = removeEdgeA(num);
        // 文字列で13桁かチェック
        if (typeof number !== "string" || number.length !== 13 || !/^\d+$/.test(number)) {
            return "";
        }
        return number;
    } else {
        return "";
    }
}

function moldingNumber(numberBox) {
    const number = checkNumber(numberBox);
    // 正しい場合のみ成形
    if (number == "") {
        return "";
    }
    return number.replace(/(\d{4})(\d{8})(\d{1})/, "$1-$2-$3");
}

function removeEdgeA(number) {
    number = number.toLowerCase();

    if (number.startsWith('a') && number.endsWith('a')) {
        return number.slice(1, -1);
    }
    return number;
}