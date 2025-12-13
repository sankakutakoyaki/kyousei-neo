"use strict"

// Numberからrecycleを取得
async function existsRecycleByNumber(number) {
    // スピナー表示
    startProcessing();
    const data = "num=" + encodeURIComponent(number);
    const url = '/recycle/exists/number';
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

function setInsertFormData(formData) {
    const formdata = structuredClone(formEntity);

    if (formData.get('recycle-id') > 0) {
        formdata.recycle_id = formData.get('recycle-id');
    } else {
        itemId -= 1;
        formdata.recycle_id = itemId;
    }
    formdata.state = 0;
    formdata.version = formData.get('version');
    formdata.recycle_number = formData.get('recycle_number');
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