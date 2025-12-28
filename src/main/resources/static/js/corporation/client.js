/******************************************************************************************************* 入力画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const tbl = document.getElementById(tableId);
    const panel = tbl.closest('.tab-panel');
    const tab = panel.dataset.panel;

    list.forEach(function (item) {
        let newRow = tbl.insertRow();
        // ID（Post送信用）
        newRow.setAttribute('name', 'data-row');
        switch (tab) {
            case "05":
                newRow.setAttribute('data-id', item.office_id);
                break;
            case "06":
                newRow.setAttribute('data-id', item.staff_id);
                break;
            default:
                newRow.setAttribute('data-id', item.company_id);
                break;
        }
        // シングルクリック時の処理
        newRow.onclick = function (e) {
            if (!e.currentTarget.classList.contains('selected')){
                // すべての行の選択状態を解除する
                detachmentSelectClassToAllRow(tbl, false);
                // 選択した行にセレクトクラスを付与する
                const result = addSelectClassToRow(e.currentTarget);
            } else {
                // 選択済みの要素をクリックした時の処理
                const clickedTd = e.target.closest("td");
                // 取得したTDの処理
            }
        }
        // ダブルクリック時の処理
        newRow.ondblclick = function (e) { 
            // チェックボックスの動作を停止させる
            e.preventDefault();
            // 選択済みの場合
            if (!e.currentTarget.classList.contains('selected')){
                // すべての行の選択状態を解除する
                detachmentSelectClassToAllRow(tableId, false);
                // 選択した行にセレクトクラスを付与する
                const result = addSelectClassToRow(e.currentTarget);
            }
            
            // フォーム入力画面を表示する
            switch (tab) {
                case "05":
                    execEdit(item.office_id, this);
                    break;
                case "06":
                    execEdit(item.staff_id, this);
                    break;
                default:
                    execEdit(item.company_id, this);
                    break;
            }
        }
        createTableRow(newRow, item, tab);
    });
}

// テーブル行を作成する
function createTableRow(newRow, item, tab) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    // ID
    switch (tab) {
        case "05":
            newRow.insertAdjacentHTML('beforeend', '<td name="id-cell" class="link-cell" data-tab="' + tab + '" onclick="execEdit(' + item.office_id + ', this)">' + String(item.office_id).padStart(4, '0') + '</td>');
            break;
        case "06":
            newRow.insertAdjacentHTML('beforeend', '<td name="id-cell" class="link-cell" data-tab="' + tab + '" onclick="execEdit(' + item.staff_id + ', this)">' + String(item.staff_id).padStart(4, '0') + '</td>');
            break;
        default:
            newRow.insertAdjacentHTML('beforeend', '<td name="id-cell" class="link-cell" data-tab="' + tab + '" onclick="execEdit(' + item.company_id + ', this)">' + String(item.company_id).padStart(4, '0') + '</td>');
            break;
    }
    // 名前
    newRow.insertAdjacentHTML('beforeend', '<td name="name-cell"><span class="kana">' + item.name_kana + '</span><br><span>' + item.name + '</span></td>');
    
    switch (tab) {
        case "06":
            // 電話番号
            newRow.insertAdjacentHTML('beforeend', '<td name="tel-cell"><span>' + (item.phone_number ?? "登録なし") + '</span></td>');
            // 支店名
            newRow.insertAdjacentHTML('beforeend', '<td name="office-cell"><span>' + (item.office_name ?? "登録なし") + '</span></td>');
            break;
        default:
            // 電話番号
            newRow.insertAdjacentHTML('beforeend', '<td name="tel-cell"><span>' + (item.tel_number ?? "登録なし") + '</span></td>');
            // メールアドレス
            newRow.insertAdjacentHTML('beforeend', '<td name="email-cell"><span>' + (item.email ?? "登録なし") + '</span></td>');
            break;
    }
}

/******************************************************************************************************* 入力画面 */

// 取引先登録画面を開く
async function execEdit(id, self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;

    let form = {};
    switch (tab) {
        case "06":
            openFormDialog("form-dialog-02");
            form = document.getElementById("form-02");
            break;
        default:
            openFormDialog("form-dialog-01");
            form = document.getElementById("form-01");
            break;
    }

    let entity = {};
    if (id > 0) {
        // 選択されたIDのエンティティを取得
        const data = "id=" + encodeURIComponent(parseInt(id));
        let url;
        switch (tab) {
            case "05":
                url = "/office/get/id";
                break;
            case "06":
                url = "/staff/get/id";
                break;
            default:
                url = "/company/get/id";
                break;
        }

        // スピナー表示
        startProcessing();

        const resultResponse = await postFetch(url, data, token, 'application/x-www-form-urlencoded');
        const result = await resultResponse.json();

        // スピナー消去
        processingEnd();

        switch (tab) {
            case "05":
                if (result.office_id == 0) {
                    openMsgDialog("msg-dialog", "データがありません", "red");
                    return;
                }
                break;
            case "06":
                if (result.staff_id == 0) {
                    openMsgDialog("msg-dialog", "データがありません", "red");
                    return;
                }
                break;
            default:
                if (result.company_id == 0) {
                    openMsgDialog("msg-dialog", "データがありません", "red");
                    return;
                }
                break;
        }

        entity = structuredClone(result);
    } else {
        switch (tab) {
            case "01":
                entity = structuredClone(companyEntity);
                entity.category = categoryPartnerCode;
                break;
            case "02":
                entity = structuredClone(companyEntity);
                entity.category = categoryShipperCode;
                break;
            case "03":
                entity = structuredClone(companyEntity);
                entity.category = categorySupplierCode;
                break;
            case "04":
                entity = structuredClone(companyEntity);
                entity.category = categoryServiceCode;
                break;
            case "07":
                entity = structuredClone(companyEntity);
                entity.category = categoryTransportCode;
                break;
            case "05":
                entity = structuredClone(officeEntity);
                if (code01.value == "") {
                    // ダイアログを閉じる
                    closeFormDialog('form-dialog-01');
                    return;
                }
                entity.company_id = code01.value;
                break;
            case "06":
                entity = structuredClone(staffEntity);
                if (name02.value < 1) {
                    // ダイアログを閉じる
                    closeFormDialog('form-dialog-02');
                    return;
                }
                entity.company_id = name02.value;
                entity.office_id = name03.value;
                break;
            default:
                break;
        }
    }

    form.querySelector('[name="company-id"]').value = entity.company_id;
    form.querySelector('[name="name"]').value = entity.name;
    form.querySelector('[name="name-kana"]').value = entity.name_kana;
    form.querySelector('[name="email"]').value = entity.email;
    form.querySelector('[name="version"]').value = entity.version;

    switch (tab) {
        case "06":
            form.querySelector('[name="staff-id"]').value = entity.staff_id;
            const combo = document.getElementById('office-id');
            createOfficeComboBox(combo);
            form.querySelector('[name="office-id"]').value = entity.office_id;
            form.querySelector('[name="phone-number"]').value = entity.phone_number;
            break;
        default:
            form.querySelector('[name="tel-number"]').value = entity.tel_number;
            form.querySelector('[name="fax-number"]').value = entity.fax_number;
            form.querySelector('[name="postal-code"]').value = entity.postal_code;
            form.querySelector('[name="full-address"]').value = entity.full_address;
            form.querySelector('[name="web-address"]').value = entity.web_address;
            switch (tab) {
                case "02":
                    form.querySelector('[name="category"]').value = entity.category;
                    form.querySelector('[name="price"]').value = entity.is_original_price;
                    form.querySelector('[name="priceArea"]').style.display = "";
                    break;
                case "05":
                    form.querySelector('[name="office-id"]').value = entity.office_id;
                    break;
                default:
                    form.querySelector('[name="category"]').value = entity.category;
                    form.querySelector('[name="priceArea"]').style.display = "none";
                    break;
            }
            break;
    }
}

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave(self) {
    const panel = document.querySelector('.is-active');
    const tab = panel.dataset.tab;

    let formId;
    switch (tab) {
        case "06":
            formId = "form-02";
            break;
        default:
            formId = "form-01";
            break;
    }

    const form = document.getElementById(formId);
    // エラーチェック
    if (formDataCheck(form) == false) {
        return;
    } else {

        const formData = new FormData(form);
        let formdata;
        let url;
        switch (tab) {
            case "06":
                formdata = structuredClone(staffEntity);
                formdata.staff_id = Number(formData.get('staff-id'));
                formdata.office_id = Number(formData.get('office-id'));
                formdata.phone_number = formData.get('phone-number').trim();
                url = "/staff/save";
                break;
            default:
                if (tab == "05") {
                    formdata = structuredClone(officeEntity);
                    formdata.office_id = Number(formData.get('office-id'));
                    url = "/office/save";
                } else {
                    formdata = structuredClone(companyEntity);
                    formdata.category = Number(formData.get('category'));
                    url = "/company/save";
                }
                formdata.tel_number = formData.get('tel-number').trim();
                formdata.fax_number = formData.get('fax-number').trim();
                formdata.postal_code = formData.get('postal-code').trim();
                formdata.full_address = formData.get('full-address').trim();
                formdata.web_address = formData.get('web-address').trim();
                break;
        }

        formdata.company_id = Number(formData.get('company-id'));
        formdata.name = formData.get('name').trim();
        formdata.name_kana = formData.get('name-kana').trim();
        formdata.email = formData.get('email').trim();                    
        formdata.version = Number(formData.get('version'));
        if (formData.get('price') != null) {
            formdata.is_original_price = formData.get('price');
        } else {
            formdata.is_original_price = 0;
        }

        formdata.user_name = user.account == null ? "kyousei@kyouseibin.com": user.account;

        // 保存処理
        const resultResponse = await postFetch(url, JSON.stringify(formdata), token, "application/json");
        const result = await resultResponse.json();
        if (result.success) {
            const tableId = "table-" + tab + "-content";
            // 画面更新
            openMsgDialog("msg-dialog", result.message, "blue");
            await execUpdate();
            // 追加・変更行に移動
            scrollIntoTableList(tableId, result.data);
        } else {
            openMsgDialog("msg-dialog", result.message, "red");
        }
        let dialogId;
        switch (tab) {
            case "06":
                dialogId = "form-dialog-02";
                break;                    
            default:
                dialogId = "form-dialog-01";
                break;
        }
        // ダイアログを閉じる
        closeFormDialog(dialogId);
    }
}

// 入力チェック
function formDataCheck(area) {
    let msg = "";
    // 名前が入力されていないとFalseを返す
    const name = area.querySelector('input[name="name"]');
    if (name != null && name.value == "") msg += '\n名称が入力されていません';
    // 電話番号チェック
    const tel = area.querySelector('input[name="tel-number"]');
    if (tel != null) {
        if (!checkPhoneNumber(tel)) msg += '\n電話番号に誤りがあります';
    }                
    // 携帯番号チェック
    const phone = area.querySelector('input[name="phone-number"]');
    if (phone != null) {
        if (!checkPhoneNumber(phone)) msg += '\n携帯番号に誤りがあります';
    }                
    // ファックス番号チェック
    const fax = area.querySelector('input[name="fax-number"]');
    if (fax != null) {
        if (!checkPhoneNumber(fax)) msg += '\nFAX番号に誤りがあります';
    }                
    // 郵便番号チェック
    const postal = area.querySelector('input[name="postal-code"]');
    if (postal != null) {
        if (!checkPostalCode(postal)) msg += '\n郵便番号に誤りがあります';
    }                
    // メールアドレスチェック
    const email = area.querySelector('input[name="email"]');
    if (email != null) {
        if (!checkMailAddress(email)) msg += '\nメールアドレスに誤りがあります';
    }                
    // webアドレスチェック
    const web = area.querySelector('input[name="web-address"]');
    if (web != null) {
        if (!checkWebAddress(web)) msg += '\nWEBアドレスに誤りがあります';
    }                
    // エラーが一つ以上あればエラーメッセージダイアログを表示する
    if (msg != "") {
        openMsgDialog("msg-dialog", msg, "red");
        return false;
    }
    return true;
}

/******************************************************************************************************* 削除 */

async function execDelete(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    let result;
    switch (tab) {
        case "05":
            result = await deleteTablelist('table-05-content', '/office/delete');
            break;
        case "06":
            result = await deleteTablelist('table-06-content', '/staff/delete');
            break;
        default:
            result = await deleteTablelist('table-' + tab + '-content', '/company/delete');
            break;
    }

    if (result.success) {                
        execUpdate();
        openMsgDialog("msg-dialog", result.message, "blue");
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }
}

/******************************************************************************************************* ダウンロード */

async function execDownloadCsv(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    let result;
    switch (tab) {
        case "05":
            result = await downloadCsv('table-05-content', '/office/download/csv');
            break;
        case "06":
            result = await downloadCsv('table-06-content', '/staff/download/csv');
            break;
        default:
            result = await downloadCsv('table-' + tab + '-content', '/company/download/csv');
            break;
    }
}

/******************************************************************************************************* 画面更新 */

async function execUpdate() {
    startProcessing();

    const panel = document.querySelector('.is-active');
    const tab = panel.dataset.tab;

    switch (tab) {
        case "01":
            await updateCompanyOrigin();
            await updateOfficeOrigin();
            const list01 = companyOrigin.filter(function(value) { return value.category == categoryPartnerCode });
            await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list01);
            break;
        case "02":
            await updateCompanyOrigin();
            await updateOfficeOrigin();
            const list02 = companyOrigin.filter(function(value) { return value.category == categoryShipperCode });
            await updateTableDisplay("table-02-content", "footer-02", "search-box-02", list02);
            break
        case "03":
            await updateCompanyOrigin();
            await updateOfficeOrigin();
            const list03 = companyOrigin.filter(function(value) { return value.category == categorySupplierCode });
            await updateTableDisplay("table-03-content", "footer-03", "search-box-03", list03);
            break;
        case "04":
            await updateCompanyOrigin();
            await updateOfficeOrigin();
            const list04 = companyOrigin.filter(function(value) { return value.category == categoryServiceCode });
            await updateTableDisplay("table-04-content", "footer-04", "search-box-04", list04);
            break;
        case "07":
            await updateCompanyOrigin();
            await updateOfficeOrigin();
            const list07 = companyOrigin.filter(function(value) { return value.category == categoryTransportCode });
            await updateTableDisplay("table-07-content", "footer-07", "search-box-07", list07);
            break;
        case "05":
            await updateOfficeOrigin();
            await updateOfficeTableDisplay();
            break;
        case "06":
            await updateStaffTableDisplay();
            break;
        default:
            break;
    }

    processingEnd();
}

// company-tab 更新処理
async function updateCompanyOrigin() {
    let resultResponse = await fetch('/client/get/list');
    companyOrigin = await resultResponse.json();

    resultResponse = await fetch('/client/get/combo');
    companyComboList = await resultResponse.json();

    createComboBoxWithTop(name01, companyComboList, '');
    createComboBoxWithTop(name02, companyComboList, '');
}

// office-tab 更新処理
async function updateOfficeOrigin() {
    let resultResponse = await fetch('/office/get/list');
    officeOrigin = await resultResponse.json();

    resultResponse = await fetch('/office/get/combo');
    officeComboList = await resultResponse.json();

    createComboBoxWithTop(name03, officeComboList, '');
}

/******************************************************************************************************* 画面更新 */

// テーブルリスト画面を更新する
async function updateTableDisplay(tableId, footerId, searchId, list) {
    // フィルター処理
    result = filterDisplay(searchId, list);
    createDisplay(tableId, footerId, result);
}

// テーブルリスト画面をクリアする
function initialTableDisplay(tableId, footerId) {
    // フィルター処理
    const result = [];
    createDisplay(tableId, footerId, result);
}

// テーブル画面作成の共通処理部分
function createDisplay(tableId, footerId, list) {
    // リスト画面を初期化
    deleteElements(tableId);
    // リスト作成
    createTableContent(tableId, list);
    // フッター作成
    createTableFooter(footerId, list);
    // チェックボタン押下時の処理を登録する
    registCheckButtonClicked(tableId);
    // すべて選択ボタンをオフにする
    turnOffAllCheckBtn(tableId);
    // テーブルのソートをリセットする
    resetSortable(tableId);
    // テーブルにスクロールバーが表示されたときの処理を登録する
    document.querySelectorAll('.scroll-area').forEach(el => {
        toggleScrollbar(el);
    });
}

async function updateOfficeTableDisplay() {
    code01.value = name01.value == 0 ? "": name01.value;
    const resultResponse = await fetch('office/get/list');
    officeOrigin = await resultResponse.json();
    const list = officeOrigin.filter(function(value) { return value.company_id == code01.value });
    createDisplay("table-05-content", "footer-05", list);
    createOfficeComboBox(name03);
}

async function updateStaffTableDisplay() {
    code02.value = name02.value == 0 ? "": name02.value;
    if (name02.value < 1) return;
    const resultResponse = await fetch('staff/get/list');
    staffOrigin = await resultResponse.json();
    let list = staffOrigin.filter(function(value) { return value.company_id == name02.value });
    if (name03.value > 0) {
        list = list.filter(function(value) { return value.office_id == name03.value });
    }
    createDisplay("table-06-content", "footer-06", list);
}

// 選択した会社の支店をコンボボックスに登録する
async function createOfficeComboBox(self) {
    const selectId = name02.value;
    const comboList = officeComboList.filter(value => { return value.company_id == selectId }).map(item => ({number:item.office_id, text:item.name}));
    createComboBoxWithTop(self, comboList, "");
    self.onchange = async function() {
        await updateStaffTableDisplay();
    };
}

async function execFilterDisplay(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    let list = [];
    switch (tab) {
        case "01":
            list = companyOrigin.filter(function(value) { return value.category == categoryPartnerCode });
            await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list);
            break;
        case "02":
            list = companyOrigin.filter(function(value) { return value.category == categoryShipperCode });
            await updateTableDisplay("table-02-content", "footer-02", "search-box-02", list);
            break;
        case "03":
            list = companyOrigin.filter(function(value) { return value.category == categorySupplierCode });
            await updateTableDisplay("table-03-content", "footer-03", "search-box-03", list);
            break;
        case "04":
            list = companyOrigin.filter(function(value) { return value.category == categoryServiceCode });
            await updateTableDisplay("table-04-content", "footer-04", "search-box-04", list);
            break;
        case "07":
            list = companyOrigin.filter(function(value) { return value.category == categoryTransportCode });
            await updateTableDisplay("table-07-content", "footer-07", "search-box-07", list);
            break;
        default:
            break;
    }
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {
    // スピナー表示
    startProcessing();

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        await execFilterDisplay(e.currentTarget);
    }, false);
    document.getElementById('search-box-02').addEventListener('search', async function(e) {
        await execFilterDisplay(e.currentTarget);
    }, false);
    document.getElementById('search-box-03').addEventListener('search', async function(e) {
        await execFilterDisplay(e.currentTarget);
    }, false);
    document.getElementById('search-box-04').addEventListener('search', async function(e) {
        await execFilterDisplay(e.currentTarget);
    }, false);
    document.getElementById('search-box-07').addEventListener('search', async function(e) {
        await execFilterDisplay(e.currentTarget);
    }, false);
    // 郵便番号入力ボックスでエンターキーが押された時の処理を登録
    document.getElementById('postal-code').addEventListener('keydown', async function (e) {
        await getAddress(e, 'postal-code', 'full-address');                                                                                                                                                                                                                                                                                                                                                       ;
    });

    // エンターフォーカス処理をイベントリスナーに登録する
    setEnterFocus("form-01");
    setEnterFocus("form-02");

    // 画面更新
    const list01 = companyOrigin.filter(function(value) { return value.category == categoryPartnerCode });
    await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list01);
    // テーブルをソート可能にする
    makeSortable("table-01-content");
    // スクロール時のページトップボタン処理を登録する
    setPageTopButton("table-01-content");

    const list02 = companyOrigin.filter(function(value) { return value.category == categoryShipperCode });
    await updateTableDisplay("table-02-content", "footer-02", "search-box-02", list02);
    // テーブルをソート可能にする
    makeSortable("table-02-content");
    // スクロール時のページトップボタン処理を登録する
    setPageTopButton("table-02-content");

    const list03 = companyOrigin.filter(function(value) { return value.category == categorySupplierCode });
    await updateTableDisplay("table-03-content", "footer-03", "search-box-03", list03);
    // テーブルをソート可能にする
    makeSortable("table-03-content");
    // スクロール時のページトップボタン処理を登録する
    setPageTopButton("table-03-content");

    const list04 = companyOrigin.filter(function(value) { return value.category == categoryServiceCode });
    await updateTableDisplay("table-04-content", "footer-04", "search-box-04", list04);
    // テーブルをソート可能にする
    makeSortable("table-04-content");
    // スクロール時のページトップボタン処理を登録する
    setPageTopButton("table-04-content");

    const list07 = companyOrigin.filter(function(value) { return value.category == categoryTransportCode });
    await updateTableDisplay("table-07-content", "footer-07", "search-box-07", list07);
    // テーブルをソート可能にする
    makeSortable("table-07-content");
    // スクロール時のページトップボタン処理を登録する
    setPageTopButton("table-07-content");

    initialTableDisplay("table-05-content", "footer-05");

    // 会社IDボックス
    code01.addEventListener('keydown', function (e) {
        if ((e && e.key === "Enter") || e === "Enter") {
            setComboboxSelected(name01, this.value);
            name01.focus();
        }
    });
    code01.addEventListener('blur', function (e) {
        updateOfficeTableDisplay();
    });
    code02.addEventListener('keydown', function (e) {
        if ((e && e.key === "Enter") || e === "Enter") {
            setComboboxSelected(name02, this.value);
            name02.focus();
        }
    });
    code02.addEventListener('blur', function (e) {
        updateStaffTableDisplay();
        createOfficeComboBox(name03);
    });

    // 会社名コンボボックス
    createComboBoxWithTop(name01, companyComboList, "");
    name01.onchange = async function() {
        updateOfficeTableDisplay();
    };
    createComboBoxWithTop(name02,companyComboList, "");
    name02.onchange = async function() {
        updateStaffTableDisplay();
        createOfficeComboBox(name03);
    };

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })

    // スピナー消去
    processingEnd();
});