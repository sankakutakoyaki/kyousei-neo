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
 * フォームデータを保存する
 * @param {*} formData 
 * @param {*} url 
 * @returns 
 */
async function saveFormData(formData, url) {
    // スピナー表示
    startProcessing();

    // 保存処理
    const data = JSON.stringify(formData);
    const resultResponse = await postFetch(url, data, token, 'application/json');
    const result = await resultResponse.json();

    // スピナー消去
    processingEnd();          

    if (result.number == 0) {
        openMsgDialog("msg-dialog", result.text, "red");
        return 0;
    } else {
        openMsgDialog("msg-dialog", result.text, "blue");
        return result.number;
    }
}