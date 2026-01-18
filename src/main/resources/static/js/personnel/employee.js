"use strict"

/******************************************************************************************************* 画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const table = document.getElementById(tableId);

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "employee_id",
            onDoubleClick: (item) => {
                execEdit(item.employee_id, this);
            }
        });

        createTableRow(row, item);
    });
}

/******************************************************************************************************* テーブル行作成 */

// テーブル行を作成する
function createTableRow(newRow, item) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    // ID
    newRow.insertAdjacentHTML('beforeend', '<td class="link-cell" onclick="execEdit(' + item.employee_id + ', this)">' + String(item.employee_id).padStart(4, '0') + '</td>');
    // コード
    newRow.insertAdjacentHTML('beforeend', '<td class="editable text-center" data-col="code" data-edit-type="text">' + (item.code == 0 ? "": item.code) + '</td>');
    // 名前
    newRow.insertAdjacentHTML('beforeend', '<td><span class="kana">' + item.full_name_kana + '</span><br><span>' + item.full_name + '</span></td>');
    // 携帯番号
    newRow.insertAdjacentHTML('beforeend', '<td class="editable" data-col="phone" data-edit-type="text"><span>' + (item.phone_number ?? "登録なし") + '</span></td>');
    // 会社名
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.company_name ?? "登録なし") + '</span></td>');
    // 営業所名
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.office_name ?? "登録なし") + '</span></td>');
}

/******************************************************************************************************* 入力画面 */

// 従業員登録画面を開く
async function execEdit(id, self) {
    const tab = document.querySelector('li.is-active');
    if (tab == null) return;
    const config = MODE_CONFIG[tab];

    // // 入力フォームダイアログを開く
    // openFormDialog("form-dialog-01");

    // フォーム画面を取得
    const form = document.getElementById('form-dialog-01');              

    let entity = {};
    if (id > 0) {
        // 選択されたIDのエンティティを取得
        // const data = "id=" + encodeURIComponent(parseInt(id));
        // const result = await searchFetch('/api/employee/get/id', data, token, 'application/x-www-form-urlencoded');
        // if (result == null) {
        //     openMsgDialog("msg-dialog", "データがありません", "red");
        //     return;
        // }
        const result = await searchFetch('/api/employee/get/id', JSON.stringify({id:parseInt(id)}), token);
        if (!result?.ok) return;

        entity = structuredClone(result);
    } else {
        entity = structuredClone(formEntity);
        entity.category = config.category;
    }

    setFormContent(form, entity);
        // 入力フォームダイアログを開く
    openFormDialog("form-dialog-01");
}

// コンテンツ部分作成
function setFormContent(form, entity) {
    applyFormConfig(form, entity, FORM_CONFIG);
    applyComboConfig(form, entity, COMBO_CONFIG);

    // 営業所（会社依存）
    createOfficeComboBox(form, officeList, entity.office_id);

    // form.querySelector('[name="employee-id"]').value = entity.employee_id;
    // if (entity.code == 0) {
    //     form.querySelector('[name="code"]').value = "";
    // } else {
    //     form.querySelector('[name="code"]').value = entity.code;
    // }
    // form.querySelector('[name="category"]').value = entity.category;
    // form.querySelector('[name="account"]').value = entity.account;
    // form.querySelector('[name="last-name"]').value = entity.last_name;
    // form.querySelector('[name="first-name"]').value = entity.first_name;
    // form.querySelector('[name="last-name-kana"]').value = entity.last_name_kana;
    // form.querySelector('[name="first-name-kana"]').value = entity.first_name_kana;
    // form.querySelector('[name="phone-number"]').value = entity.phone_number;
    // form.querySelector('[name="postal-code"]').value = entity.postal_code;
    // form.querySelector('[name="full-address"]').value = entity.full_address;
    // form.querySelector('[name="email"]').value = entity.email;
    // if (entity.birthday == "9999-12-31") {
    //     form.querySelector('[name="birthday"]').value = null;
    // } else {
    //     form.querySelector('[name="birthday"]').value = entity.birthday;
    // }
    // form.querySelector('[name="emergency-contact"]').value = entity.emergency_contact;
    // form.querySelector('[name="emergency-contact-number"]').value = entity.emergency_contact_number;
    // if (entity.date_of_hire == "9999-12-31") {
    //     form.querySelector('[name="date-of-hire"]').value = "";
    // } else {
    //     form.querySelector('[name="date-of-hire"]').value = entity.date_of_hire;
    // }
    // form.querySelector('[name="version"]').value = entity.version;

    // // 会社名コンボボックス
    // // const companyArea = form.querySelector('select[name="company"]');
    // // createComboBox(companyArea, companyComboList);
    // // companyArea.style.pointerEvents = 'none';
    // // setComboboxSelected(companyArea, entity.company_id);
    // // companyArea.onchange = function() { createOfficeComboBox(form, officeList); };
    // const companyArea = setComboBox(form, 'select[name="company"]', companyComboList, entity.company_id);
    // companyArea.onchange = function() { createOfficeComboBox(form, companyArea, officeList); };

    // // 営業所名コンボボックス
    // const officeArea = createOfficeComboBox(form, officeList, entity.office_id);
    // // const officeArea = form.querySelector('select[name="office"]');
    // // setComboboxSelected(officeArea, entity.office_id);

    // // 性別コンボボックス
    // // const genderArea = form.querySelector('select[name="gender"]');
    // // createComboBox(genderArea, genderComboList);
    // // setComboboxSelected(genderArea, entity.gender);
    // setComboBox(form, 'select[name="gender"]', genderComboList, entity.gender);

    // // 血液型コンボボックス
    // // const bloodTypeArea = form.querySelector('select[name="blood-type"]');
    // // createComboBox(bloodTypeArea, bloodTypeComboList);
    // // setComboboxSelected(bloodTypeArea, entity.blood_type);
    // setComboBox(form, 'select[name="blood-type"]', bloodTypeComboList, entity.blood_type);
}

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave() {
    // const form = document.getElementById('form-01');
    // const tab = document.querySelector('li.is-active');
    // if (tab == null) return;

    // // エラーチェック
    // if (formDataCheck(form) == false) {
    //     return;
    // } else {
    //     const formData = new FormData(form);
    //     const formdata = structuredClone(formEntity);
    //     formdata.employee_id = formData.get('employee-id');
    //     if (formData.get('code') == "") {
    //         formdata.code = 0;
    //     } else {
    //         formdata.code = formData.get('code');
    //     }
    //     formdata.category = Number(formData.get('category'));
    //     formdata.account = formData.get('account').trim();
    //     formdata.company_id = formData.get('company');
    //     formdata.office_id = formData.get('office');
    //     formdata.last_name = formData.get('last-name').trim();
    //     formdata.first_name = formData.get('first-name').trim();
    //     formdata.last_name_kana = formData.get('last-name-kana').trim();
    //     formdata.first_name_kana = formData.get('first-name-kana').trim();
    //     formdata.phone_number = formData.get('phone-number').trim();
    //     formdata.postal_code = formData.get('postal-code').trim();
    //     formdata.full_address = formData.get('full-address').trim();
    //     formdata.email = formData.get('email').trim();
    //     formdata.gender= formData.get('gender');
    //     formdata.blood_type = formData.get('blood-type');
    //     if (formData.get('birthday') == "") {
    //         formdata.birthday = "9999-12-31";
    //     } else {
    //         formdata.birthday = formData.get('birthday');
    //     }
    //     formdata.emergency_contact = formData.get('emergency-contact').trim();
    //     formdata.emergency_contact_number = formData.get('emergency-contact-number').trim();
    //     if (formData.get('date-of-hire') == "") {
    //         formdata.date_of_hire = "9999-12-31";
    //     } else {
    //         formdata.date_of_hire = formData.get('date-of-hire');
    //     }
    //     formdata.version = formData.get('version');

    //     // 保存処理
    //     const result = await updateFetch("/api/employee/save", JSON.stringify(formdata), token, "application/json");
    //     if (result.success) {
    //         const config = MODE_CONFIG[tab.dataset.tab];

    //         // 画面更新
    //         openMsgDialog("msg-dialog", result.message, "blue");
    //         await execUpdate();
    //         // 追加・変更行に移動
    //         scrollIntoTableList(config.tableId, result.employee_id);
    //     } else {
    //         openMsgDialog("msg-dialog", result.message, "red");
    //     }
    //     // ダイアログを閉じる
    //     closeFormDialog('form-dialog-01');
    // }
    const form = document.getElementById('form-01');
    const tab = document.querySelector('li.is-active');
    if (!tab) return;

    if (!formDataCheck(form)) return;

    const formdata = buildEntityFromForm(
        form,
        formEntity,
        SAVE_FORM_CONFIG
    );

    const result = await updateFetch(
        "/api/employee/save",
        JSON.stringify(formdata),
        token,
        "application/json"
    );

    if (result.success) {
        const config = MODE_CONFIG[tab.dataset.tab];
        openMsgDialog("msg-dialog", result.message, "blue");
        await execUpdate();
        scrollIntoTableList(config.tableId, result.employee_id);
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }

    closeFormDialog('form-dialog-01');
}

// 入力チェック
function formDataCheck(area) {
    let msg = "";
    // アカウントが入力されていないとFalseを返す
    const account = area.querySelector('input[name="account"]');
    if (account != null && account.value == "") msg += '\nアカウントが入力されていません';
    // 姓が入力されていないとFalseを返す
    const name = area.querySelector('input[name="last-name"]');
    if (name != null && name.value == "") msg += '\n姓が入力されていません';
    // 電話番号チェック
    const phone = area.querySelector('input[name="phone-number"]');
    if (!checkPhoneNumber(phone)) msg += '\n電話番号に誤りがあります';
    // 電話番号チェック
    const postal = area.querySelector('input[name="postal-code"]');
    if (!checkPostalCode(postal)) msg += '\n郵便番号に誤りがあります';
    // メールアドレスチェック
    const email = area.querySelector('input[name="email"]');
    if (!checkMailAddress(email)) msg += '\nメールアドレスに誤りがあります';
    // エラーが一つ以上あればエラーメッセージダイアログを表示する
    if (msg != "") {
        openMsgDialog("msg-dialog", msg, "red");
        return false;
    }
    return true;
}

// TDが変更された時の処理
async function handleTdChange(editor) {
    const td = editor.closest('td.editable');
    const col = td.dataset.col;
    const row = td.closest('tr');
    const id = row.dataset.id;

    const ent = origin.find(value => value.employee_id == id);
    // const ent = getEmployee(id);
    switch (col) {
        case "code":
            if (existsSameCode(Number(editor.value))) {
                editor.value = ent.code;
                return;
            }
            break;
        case "phone":
            if (!checkPhoneNumber(editor)) {
                editor.value = ent.phone_number;
                return;
            }
            break;
        default:
            return;
    }

    // const data = "id=" + encodeURIComponent(parseInt(id)) + "&data=" + (editor.value);
    // await searchFetch('/api/employee/update/' + col, data, token, 'application/x-www-form-urlencoded');
    await updateFetch('/api/employee/update/' + col, JSON.stringify({number:id,text:editor.value}), token);
    await execUpdate();
}

// リスト内に同じコードがないかチェック
function existsSameCode(code) {
    return origin.some(item =>
        item.code === code
    );
}

/******************************************************************************************************* 削除 */

async function execDelete(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];
    
    const result = await deleteTablelist(config.tableId, '/api/employee/delete');

    if (result.success) {                
        await execUpdate();
        openMsgDialog("msg-dialog", result.message, "blue");
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }
}


/******************************************************************************************************* ダウンロード */

async function execDownloadCsv(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];

    await downloadCsv(config.tableId, '/api/employee/download/csv');
}

/******************************************************************************************************* 画面更新 */

async function execUpdate() {
    const tab = document.querySelector('li.is-active');
    if (tab == null) return;
    const config = MODE_CONFIG[tab.dataset.tab];

    // リスト取得
    const resultResponse = await fetch('/api/employee/get/list');
    origin = await resultResponse.json();

    // 画面更新
    const list = origin.filter(function(value) { return value.category == config.category });
    await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);
}

/******************************************************************************************************* 画面更新 */

async function execFilterDisplay(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;

    const config = MODE_CONFIG[tab];
    const list = origin.filter(value => { return value.category === config.category });
    await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {
    // スピナー表示
    startProcessing();

    for (const mode of Object.keys(MODE_CONFIG)) {
        let config = MODE_CONFIG[mode];
        if (config != null) {
            setEnterFocus(config.formId);
            // 検索ボックス入力時の処理
            document.getElementById(config.searchId).addEventListener('search', async function(e) {
                await execFilterDisplay(e.currentTarget);
            }, false);
        }
        let list = origin.filter(value => { return value.category === config.category });
        await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);
    }

    // 郵便番号入力ボックスでエンターキーが押された時の処理を登録
    document.getElementById('employee-postal-code').addEventListener('keydown', async function (e) {
        await getAddress(e, 'employee-postal-code', 'employee-full-address');                                                                                                                                                                                                                                                                                                                                                       ;
    });

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })

    // スピナー消去
    processingEnd();
});