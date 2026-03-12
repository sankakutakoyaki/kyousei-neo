"use strict"

export function init(area, config) {

    config.forEach(cfg => {
        const el = area.getElementById(cfg.comboId);
        if (!el) return;

        createComboBox({
            area: el,
            items: cfg.comboList,
            text: cfg.text ?? null,
            onChange: cfg.onChange ?? null
        });
    });
}

export function createComboBox({area, items, text = null, onChange = null}) {
    if (!area) return;
    area.replaceChildren();

    if (text !== null) {
        area.insertAdjacentHTML("beforeend",`<option value="0" data-id="0">${text}</option>`);
    }

    (items ?? []).forEach(item => {
        const v = item.number;
        if ((typeof v === "number" && v > -1) || (typeof v === "string" && v !== "")) {
            const option = document.createElement("option");
            option.value = item.number;
            option.dataset.id = item.data ?? "";
            option.textContent = item.text;
            area.appendChild(option);
        }
    });

    if (typeof onChange === "function") {
        area.addEventListener("change", onChange);
    }
}










/**
 * コンボボックスを作成する
 * @param {*} selectArea 
 * @param {*} items SimpleData
 * @returns 
 */
function createComboBoxValueString(selectArea, items) {
    deleteElements(selectArea);
    if (selectArea == null) return;
    items.forEach(function (item) {
        if (item.number != null) {
            // selectArea.insertAdjacentHTML('beforeend', '<option value="' + item.number + '">' + item.text + '</option>');
            selectArea.insertAdjacentHTML(
                "beforeend",
                `<option value="${item.number}" data-id="${item.data ?? ""}">${item.text}</option>`
            );
        }
    })
}

/**
 * 先頭に空白をつけたコンボボックスを作成する
 * @param {*} selectArea 
 * @param {*} items SimpleData
 * @param {*} text テキスト
 * @returns 
 */
function createComboBoxWithTop(selectArea, items, text) {
    if (selectArea == null) return;
    deleteElements(selectArea);
    selectArea.insertAdjacentHTML('beforeend', '<option value="0">' + text + '</option>');
    items.forEach(function (item) {
        if (item.number > -1) {
            // selectArea.insertAdjacentHTML('beforeend', '<option value="' + item.number + '">' + item.text + '</option>');
            selectArea.insertAdjacentHTML(
                "beforeend",
                `<option value="${item.number}" data-id="${item.data ?? ""}">${item.text}</option>`
            );
        }
    })
}

/**
 * コールバック付きの先頭に空白をつけたコンボボックスを作成する
 * @param {*} selectArea 
 * @param {*} items 
 * @param {*} onChange 
 * @returns 
 */
function createComboBoxWithTopAndFunc(selectArea, items, onChange, text = null) {

    if (!selectArea) return;
    deleteElements(selectArea);
    if (text) selectArea.insertAdjacentHTML('beforeend', '<option value="0">' + text + '</option>');
    items.forEach(function (item) {
        if (item.number != null) {
            selectArea.insertAdjacentHTML('beforeend', `<option value="${item.number}" data-id="${item.data ?? ""}">${item.text}</option>`);
        }
    });
    selectArea.onchange = function (e) {
        if (typeof onChange === "function") {
            onChange(e.target.value);
        }
    };
}

/**
 * コールバック付きのコンボボックスを作成する
 * @param {*} selectArea 
 * @param {*} items 
 * @param {*} onChange 
 * @returns 
 */
function createComboBoxWithFunc(selectArea, items, onChange, text = null) {

    if (!selectArea) return;
    deleteElements(selectArea);
    items.forEach(function (item) {
        if (item.number != null) {
            selectArea.insertAdjacentHTML('beforeend', `<option value="${item.number}" data-id="${item.data ?? ""}">${item.text}</option>`);
        }
    });
    selectArea.onchange = function (e) {
        if (typeof onChange === "function") {
            onChange(e.target.value);
        }
    };
}

/**
 * 指定した要素を選択する
 * @param {*} selectArea 
 * @param {*} id 
 * @returns 
 */
function setComboboxSelected(selectArea, id) {
    if (!selectArea) return false;

    const options = selectArea.querySelectorAll('option');
    let found = false;

    options.forEach(function (opt) {
        if (opt.value == id) {
            opt.selected = true;
            found = true;
        } else {
            opt.selected = false; // 他は解除（安全のため）
        }
    });

    return found;
}

/**
 * コンボボックスを選択した状態で作成する
 * @param {*} form 
 * @param {*} comboId 
 * @param {*} list 
 * @param {*} selectId 
 */
function setComboBox(form, comboId, list, selectId) {
    const area = form.querySelector(comboId);
    createComboBox(area, list);
    setComboboxSelected(area, selectId);
    return area;
}

// 選択した会社の支店をコンボボックスに登録する
function createOfficeComboBox(form, officeList, selectId) {
    const officeArea = form.querySelector('select[name="office"]');
    if (!officeArea) return;

    const list = officeList.filter(value => { return value.company_id == selectId }).map(item => ({number:item.office_id, text:item.name}));
    createComboBoxWithTop(officeArea, list, "");
    if (selectId != null && selectId > 0) {
        setComboboxSelected(officeArea, selectId);
    }
    
    return officeArea;
}