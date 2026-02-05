"use strict"

/**
 * コンボボックスを作成する
 * @param {*} selectArea 
 * @param {*} items SimpleData
 * @returns 
 */
function createComboBox(selectArea, items) {
    deleteElements(selectArea);
    if (selectArea == null) return;
    items.forEach(function (item) {
        if (item.number > -1) {
            selectArea.insertAdjacentHTML('beforeend', '<option value="' + item.number + '">' + item.text + '</option>');
        }
    })
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
            selectArea.insertAdjacentHTML('beforeend', '<option value="' + item.number + '">' + item.text + '</option>');
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
            selectArea.insertAdjacentHTML('beforeend', '<option value="' + item.number + '">' + item.text + '</option>');
        }
    })
}

/**
 * 指定した要素を選択する
 * @param {*} selectArea 
 * @param {*} id 
 * @returns 
 */
function setComboboxSelected(selectArea, id) {
    if (selectArea == null) return;
    const select = selectArea.querySelectorAll('option');
    if (select != null) {
        select.forEach(function (opt) {
            if (opt.value == id) opt.selected = true;
        })
    }
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


// タブの処理
function tabSwitch(e) {
    // クリックされた要素のデータ属性を取得
    const tabTargetData = e.currentTarget.dataset.tab;
    // クリックされた要素の親要素と、その子要素を取得
    const tabList = e.currentTarget.closest('.tab-menu');
    const tabItems = tabList.querySelectorAll('.tab-menu-item');
    // クリックされた要素の親要素の兄弟要素の子要素を取得
    const tabPanelItems = tabList.closest('.tab-area').querySelectorAll('.tab-panel');
    // クリックされたtabの同階層のmenuとpanelのクラスを削除
    tabItems.forEach((tabItem) => {
        tabItem.classList.remove('is-active');
    })
    tabPanelItems.forEach((tabPanelItem) => {
        tabPanelItem.classList.remove('is-show');
    })
    // クリックされたmenu要素にis-activeクラスを付加
    e.currentTarget.classList.add('is-active');
    // クリックしたmenuのデータ属性と等しい値を持つパネルにis-showクラスを付加
    tabPanelItems.forEach((tabPanelItem) => {
        if (tabPanelItem.dataset.panel ===  tabTargetData) {
            tabPanelItem.classList.add('is-show');
        }
    })
}

/**
 * ターゲット要素が郵便番号か確認
 * @param {*} sourceElm 
 * @returns 
 */
function checkPostalCode(sourceElm) {
    if (sourceElm.value == "") return true;
    // 文字列から[-]を除去
    const modified = sourceElm.value.replaceAll('-', '');
    // 修正した文字列が７桁の数値か確認
    if (modified.length != 7 || isNaN(modified) == true) return false;
    return true;
}

/**
 * ターゲット要素が電話番号か確認
 * @param {*} sourceElm 
 * @returns 
 */
function checkPhoneNumber(sourceElm) {
    if (sourceElm.value == "") return true;
    // 文字列から[-]を除去
    const modified = sourceElm.value.replaceAll('-', '');
    // 修正した文字列が数値か確認
    if (isNaN(modified) == true) return false;
    // 修正した文字列が11桁か10桁か確認
    if (modified.length != 11 && modified.length != 10) return false;
    return true;
}

/**
 * ターゲット要素がインボイス登録番号か確認
 * @param {*} sourceElm 
 * @returns 
 */
function checkRegistrationNumber(sourceElm) {
    if (sourceElm.value == "") return true;
    // 文字列が数値か確認
    if (isNaN(sourceElm.value) == true) return false;
    // 文字列が13桁か確認
    if (sourceElm.value.length != 13) return false;
    return true;
}

/**
 * ターゲット要素がメールアドレスか確認
 * @param {*} sourceElm 
 * @returns 
 */
function checkMailAddress(sourceElm) {
    if (sourceElm.value == "") return true;
    // 文字列がメールアドレスの形式か確認
    if (!sourceElm.value.match(/.+@.+\..+/)) return false;
    return true;
}

/**
 * ターゲット要素がWEBアドレスか確認
 * @param {*} sourceElm 
 * @returns 
 */
function checkWebAddress(sourceElm) {
    if (sourceElm.value == "") return true;
    // 文字列がWEBアドレスの形式か確認
    if (!URL.canParse(sourceElm.value)) return false;
    return true;
}

/**
 * 期間指定ボックスを変更する
 * @param {*} str 分岐用文字列
 * @returns 開始日と終了日を変更する
 */
function execSpecifyPeriod(str, startId, endId) {
    const date = new Date();

    const startdate = document.getElementById(startId);
    if (startdate == null) return;
    const start = new Date(startdate.value);

    const enddate = document.getElementById(endId);
    if (enddate == null) return;
    const end = new Date(enddate.value);

    switch (str) {
        case "last-month":
            startdate.value = new Date(date.getFullYear(), date.getMonth() - 1, 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(date.getFullYear(), date.getMonth(), 0).toLocaleDateString('sv-SE');
            break;
        case "this-month":
            startdate.value = new Date(date.getFullYear(), date.getMonth(), 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(date.getFullYear(), date.getMonth() + 1, 0).toLocaleDateString('sv-SE');
            break;
        case "prev-day":
            startdate.value = new Date(start.getFullYear(), start.getMonth(), start.getDate() - 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(end.getFullYear(), end.getMonth(), end.getDate() - 1).toLocaleDateString('sv-SE');
            break;
        case "next-day":
            startdate.value = new Date(start.getFullYear(), start.getMonth(), start.getDate() + 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(end.getFullYear(), end.getMonth(), end.getDate() + 1).toLocaleDateString('sv-SE');
            break;
        case "yesterday":
            startdate.value = new Date(date.getFullYear(), date.getMonth(), date.getDate() - 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(date.getFullYear(), date.getMonth(), date.getDate() - 1).toLocaleDateString('sv-SE');
            break;
        case "prev-week":
            startdate.value = new Date(start.getFullYear(), start.getMonth(), start.getDate() - 7).toLocaleDateString('sv-SE');
            enddate.value = new Date(start.getFullYear(), start.getMonth(), start.getDate() - 1).toLocaleDateString('sv-SE');
            break;
        case "next-week":
            startdate.value = new Date(start.getFullYear(), start.getMonth(), start.getDate() + 7).toLocaleDateString('sv-SE');
            enddate.value = new Date(start.getFullYear(), start.getMonth(), start.getDate() + 13).toLocaleDateString('sv-SE');
            break;
        case "this-week":
            startdate.value = new Date(date.getFullYear(), date.getMonth(), date.getDate()).toLocaleDateString('sv-SE');
            enddate.value = new Date(date.getFullYear(), date.getMonth(), date.getDate() + 6).toLocaleDateString('sv-SE');
            break;
        case "today":
            startdate.value = date.toLocaleDateString('sv-SE');
            enddate.value = date.toLocaleDateString('sv-SE');
            break;
        case "prev-month":
            startdate.value = new Date(start.getFullYear(), start.getMonth() - 1, 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(start.getFullYear(), start.getMonth(), 0).toLocaleDateString('sv-SE');
            break;
        case "next-month":
            startdate.value = new Date(end.getFullYear(), end.getMonth() + 1, 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(end.getFullYear(), end.getMonth() + 2, 0).toLocaleDateString('sv-SE');
            break;
        case "half-month":
            startdate.value = new Date(date.getFullYear(), date.getMonth() - 1, 16).toLocaleDateString('sv-SE');
            enddate.value = new Date(date.getFullYear(), date.getMonth(), 15).toLocaleDateString('sv-SE');
            break;
        case "end-month":
            startdate.value = new Date(date.getFullYear(), date.getMonth() - 1, 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(date.getFullYear(), date.getMonth(), 0).toLocaleDateString('sv-SE');
            break;
        case "prev-month-move":
            startdate.value = new Date(start.getFullYear(), start.getMonth() - 1, 16).toLocaleDateString('sv-SE');
            enddate.value = new Date(start.getFullYear(), start.getMonth(), 15).toLocaleDateString('sv-SE');
            break;
        case "next-month-move":
            startdate.value = new Date(start.getFullYear(), start.getMonth() + 1, 16).toLocaleDateString('sv-SE');
            enddate.value = new Date(start.getFullYear(), start.getMonth() + 2, 15).toLocaleDateString('sv-SE');
            break;
        default:
            break;
    }
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

// モードで指定したフォームを開く
async function openFormByMode(mode, modeConfig) {
    const config = modeConfig[mode];
    if (!config) return;

    openFormDialog(config.dialogId);

    const form = document.getElementById(config.dialogId);
    resetFormInput(form, mode);
    resetEnterFocus();
}

// コード付きの会社選択ボックスを登録する
function initCompanyInputs() {
    COMPANY_UI_CONFIG.forEach(cfg => {
        const codeElm = document.getElementById(cfg.codeId);
        const nameElm = document.getElementById(cfg.nameId);

        // code → combo
        bindCodeInput(codeElm, nameElm, cfg.onChange);

        // combo change
        initCompanyCombo(nameElm, cfg.onChange);
    });
}

// コードBOX変更時の処理
function bindCodeInput(codeInput, nameSelect, onBlurCallback) {

    // Enterキー処理
    codeInput.addEventListener('keydown', function (e) {
        if (e.key === "Enter") {
            setComboboxSelected(nameSelect, this.value);
            nameSelect.focus();
        }
    });

    // フォーカスアウト時の処理
    codeInput.addEventListener('blur', function () {
        if (typeof onBlurCallback === "function") {
            onBlurCallback();
        }
    });
}

// コンボボックス変更時の処理
function initCompanyCombo(selectElem, onChangeCallback) {

    // コンボボックス初期化
    createComboBoxWithTop(selectElem, companyComboList, "");

    // 変更時処理
    selectElem.onchange = async function () {
        if (typeof onChangeCallback === "function") {
            await onChangeCallback();
        }
    };
}

// すべてのコンボボックスを取得する
function getComboTargets(targetIds) {
    return targetIds
        .map(id => document.getElementById(id))
        .filter(elm => elm !== null);
}

// 共通Validate関数
function validateByConfig(area, config) {
    const messages = [];
    let focusTarget = null;

    const rules = [
        ...(config.common || []),
        ...(config.mode ? (config[config.mode] || []) : [])
    ];

    for (const rule of rules) {
        const el = area.querySelector(rule.selector);
        if (!el) continue;

        const value = el.value ?? "";

        for (const check of rule.checks) {
            if (!check.test(value, el)) {
                messages.push(check.message);
                if (!focusTarget && rule.focus) {
                    focusTarget = el;
                }
                break;
            }
        }
    }

    if (messages.length > 0) {
        openMsgDialog("msg-dialog", messages.join("\n"), "red");
        if (focusTarget) {
            setFocusElement("msg-dialog", focusTarget);
        }
        return false;
    }
    return true;
}

// select + name セットの共通関数
function setSelectValue(form, formData, formdata, {
    valueName,
    idKey,
    nameKey
}) {
    const v = formData.get(valueName);
    if (v == null) return;

    formdata[idKey] = v;
    const select = form.querySelector(`[name="${valueName}"]`);
    if (select) {
        formdata[nameKey] = select.options[select.selectedIndex].text;
    }
}

//　フォーム画面にentityの情報を登録する
function applyFormConfig(form, entity, config) {
    config.forEach(c => {
        const el = form.querySelector(`[name="${c.name}"]`);
        if (!el) return;

        let value = entity[c.key];

        if (c.emptyIf !== undefined && value == c.emptyIf) {
            value = '';
        }
        el.value = value ?? '';
    });
}

//　フォーム画面のコンボボックスにentityの情報を登録する
function applyComboConfig(form, entity, config) {
    config.forEach(c => {
        const el = setComboBox(form, c.selector, c.list, entity[c.key]);
        if (c.onChange) {
            el.onchange = () => c.onChange(form, el);
        }
    });
}

// フォーム画面の表示用entitytyを登録する
function applyViewConfig(ctx, entity, config) {
    config.forEach(c => {
        const el = document.getElementById(c.id);
        if (!el) return;
        el.textContent = c.value(entity, ctx);
    });
}

// 保存用のエンティティを作成する
function applySaveConfig(form, baseEntity) {
  const formData = new FormData(form);
  const entity = structuredClone(baseEntity);

  SAVE_CONFIG.forEach(c => {
    const raw = formData.get(c.name);
    entity[c.key] = c.parse ? c.parse(raw) : raw;
  });

  return entity;
}

// // フォームからentityへ変換する
// function buildEntityFromForm(form, baseEntity, config) {
//     const fd = new FormData(form);
//     const entity = structuredClone(baseEntity);

//     config.forEach(c => {
//         let v = fd.get(c.name);

//         if (c.trim && typeof v === 'string') {
//         v = v.trim();
//         }
//         if ((v === '' || v == null) && c.emptyTo !== undefined) {
//         v = c.emptyTo;
//         }
//         if (c.number && v !== '' && v != null) {
//         v = Number(v);
//         }
//         entity[c.key] = v;
//     });

//     return entity;
// }

function buildEntityFromForm(form, baseEntity, config) {
    const fd = new FormData(form);
    const entity = structuredClone(baseEntity);

    config.forEach(c => {
        let v = fd.get(c.name);

        // trim
        if (c.trim && typeof v === 'string') {
            v = v.trim();
        }

        // empty → null
        if ((v === '' || v == null) && c.emptyToNull) {
            v = null;
        }

        // number
        if (c.number && v !== null && v !== '') {
            v = Number(v);
        }

        // 0 → null
        if (c.zeroToNull && v === 0) {
            v = null;
        }

        // ★ null のとき代入しない
        if (v === null && c.skipIfNull) {
            return;
        }

        entity[c.key] = v;
    });

    return entity;
}

// 要素からentityへ変換する
function buildEntityFromElement(area, baseEntity, config) {
    // const fd = new FormData(form);
    const entity = structuredClone(baseEntity);

    config.forEach(c => {
        let v = area.querySelector('[name="' + c.name + '"')?.value;

        if (c.trim && typeof v === 'string') {
        v = v.trim();
        }
        if ((v === '' || v == null) && c.emptyTo !== undefined) {
        v = c.emptyTo;
        }
        if (c.number && v !== '' && v != null) {
        v = Number(v);
        }
        entity[c.key] = v;
    });

    return entity;
}

// 検索ボックスの初期設定
function registerSearchEvents(ID_CONFIG) {
    for (const cfg of Object.values(ID_CONFIG)) {
        const searchBox = document.getElementById(cfg.searchId);
        if (!searchBox) continue;

        searchBox.addEventListener(
        'search',
        async e => await execFilterDisplay(e.currentTarget),
        false
        );
    }
}

// setFormContentで、種類に応じて値を代入していく
function setFormContentValue(form, name, value) {
    const el = form.querySelector(`[name="${name}"]`);
    if (!el) return;

    const v = value ?? "";

    if ('value' in el) {
        // input / select / textarea
        el.value = v;
    } else {
        // span / div / p など
        el.textContent = v;
    }
}

// 上下キーで日付を操作する
function enableDateArrowControl(container) {
    container.addEventListener('keydown', e => {
        const el = document.activeElement;

        if (!el || el.type !== 'date') return;
        if (e.key !== 'ArrowUp' && e.key !== 'ArrowDown') return;

        e.preventDefault();

        const step = e.shiftKey ? 7 : 1;
        const diff = e.key === 'ArrowUp' ? step : -step;

        const base = el.value ? new Date(el.value) : new Date();
        base.setDate(base.getDate() + diff);

        el.value = base.toISOString().slice(0, 10);
    });
}

// end-dateがある場合のみ、2つの日付を連動させる（start > end 防止）。
function bindDateRange(startId, endId) {
    const start = document.getElementById(startId);
    const end = document.getElementById(endId);

    // end-date が存在しない画面もある前提
    if (!start || !end) return;

    // start → end
    start.addEventListener('change', () => {
        if (!start.value) return;

        end.min = start.value;

        // start > end になったら end を補正
        if (end.value && end.value < start.value) {
            end.value = start.value;
        }
    });

    // end → start
    end.addEventListener('change', () => {
        if (!end.value) return;

        start.max = end.value;

        // end < start になったら start を補正
        if (start.value && start.value > end.value) {
            start.value = end.value;
        }
    });

    start.addEventListener('blur', () => {
    if (end.value && start.value > end.value) {
        end.value = start.value;
    }
});
}