/******************************************************************************************************* 入力画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const tbl = document.getElementById(tableId);
    list.forEach(function (item) {
        let newRow = tbl.insertRow();
        // ID（Post送信用）
        newRow.setAttribute('name', 'data-row');
        newRow.setAttribute('data-id', item.order_id);
        // シングルクリック時の処理
        newRow.onclick = function (e) {
            if (!e.currentTarget.classList.contains('selected')){
                // すべての行の選択状態を解除する
                detachmentSelectClassToAllRow(tbl, false);
                // 選択した行にセレクトクラスを付与する
                const result = addSelectClassToRow(e.currentTarget);
                const clickedTr = e.target.closest("tr");
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
            switch (tableId) {
                case "table-01-content":
                    execEdit(item.order_id, this);
                    break;
                default:
                    break;
            }
        }

        switch (tableId) {
            case "table-01-content":
                createTableRow(newRow, item, "01");
                break;
            default:
                break;
        }

    });
}

// テーブル行を作成する
function createTableRow(newRow, item, tab) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    // ID 依頼番号
    newRow.insertAdjacentHTML('beforeend', '<td name="id-cell"><span class="link-cell" onclick="execEdit(' + item.order_id + ', this)">' + String(item.order_id).padStart(5, '0') + '</span><br><span>' + (item.request_number ?? "登録なし") + '</span></td>');
    // 着工日 完工日
    newRow.insertAdjacentHTML('beforeend', '<td name="date-cell"><span>' + (item.start_date == '9999-12-31' ? "": item.start_date) + '</span><br><span>' + (item.end_date == '9999-12-31' ? "": item.end_date) + '</span></td>');
    // 元請 支店
    newRow.insertAdjacentHTML('beforeend', '<td name="prime-constractor-cell"><span>' + (item.prime_constractor_name ?? "") + '</span><br><span>' + (item.prime_constractor_office_name ?? "") + '</span></td>');
    // 件名 住所
    newRow.insertAdjacentHTML('beforeend', '<td name="detail-cell"><span>' + (item.title ?? "") + '</span><br><span>' + (item.order_full_address ?? "") + '</span></td>');
}

/******************************************************************************************************* 入力画面 */

// 受注伝票登録画面を開く
async function execEdit(id, self) {
    const form = document.getElementById('form-dialog-01');
    // const form01 = document.getElementById('form-01');
    const selectTab = form.querySelector('.tab-menu-item.is-active');
    if (selectTab != null) selectTab.classList.remove('is-active');
    const selectPanel = form.querySelector('.tab-panel.is-show');                
    if (selectPanel != null) selectPanel.classList.remove('is-show');
    const targetTab = form.querySelector('[data-tab="11"]');
    targetTab.classList.add('is-active');
    const targetPanel = form.querySelector('[data-panel="11"]');
    targetPanel.classList.add('is-show');

    resetForm12Input();
    resetForm13Input();
    resetForm14Input();

    // スピナー表示
    startProcessing();

    // // 入力フォームダイアログを開く
    // openFormDialog("form-dialog-01"); 

    let entity = {};
    if (id > 0) {
        // 選択されたIDのエンティティを取得
        const data = "id=" + encodeURIComponent(parseInt(id));
        // const resultResponse = await postFetch('/api/order/get/id', data, token, 'application/x-www-form-urlencoded');
        // const result = await resultResponse.json();
        // if (result.order_id == 0) {
        //     openMsgDialog("msg-dialog", "データがありません", "red");
        //     return;
        // }
        const result = await searchFetch('/api/order/get/id', JSON.stringify({id:parseInt(id)}), token);
        if (!result?.ok) return;

        entity = structuredClone(result);
    } else {
        entity = structuredClone(formEntity);
    }

    form.querySelector('[name="order-id"]').value = entity.order_id;
    form.querySelector('[name="request-number"]').value = entity.request_number;
    form.querySelector('[name="title"]').value = entity.title;
    form.querySelector('[name="order-postal-code"]').value = entity.order_postal_code;
    form.querySelector('[name="order-full-address"]').value = entity.order_full_address;
    form.querySelector('[name="contact-information"]').value = entity.contact_information;
    form.querySelector('[name="contact-information2"]').value = entity.contact_information2;
    if (entity.start_date == "9999-12-31") {
        form.querySelector('[name="start-date"]').value = "";
    } else {
        form.querySelector('[name="start-date"]').value = entity.start_date;
    }
    if (entity.end_date == "9999-12-31") {
        form.querySelector('[name="end-date"]').value = "";
    } else {
        form.querySelector('[name="end-date"]').value = entity.end_date;
    }
    form.querySelector('[name="remarks"]').value = entity.remarks;
    form.querySelector('[name="version"]').value = entity.version;

    itemList = entity.item_list;
    workList = entity.work_list;
    staffList = entity.staff_list;

    // 商品リスト作成
    await updateTable12Display("table-12-content");
    // 配送員リスト作成
    await updateTable14Display("table-14-content");

    // 元請コンボボックス
    const primeConstractorArea = form.querySelector('select[name="prime-constractor"]');
    createComboBoxWithTop(primeConstractorArea, primeConstractorComboList, "");
    setComboboxSelected(primeConstractorArea, entity.prime_constractor_id);
    primeConstractorArea.onchange = function() { createPrimeConstractorComboBox('office') };
    // 元請支店コンボボックス
    const primeConstractorOfficeArea = form.querySelector('select[name="prime-constractor-office"]');
    createPrimeConstractorComboBox('office');                
    setComboboxSelected(primeConstractorOfficeArea, entity.prime_constractor_office_id);
    primeConstractorOfficeArea.onchange = function() { createPrimeConstractorComboBox('staff') };
    // 営業担当コンボボックス
    const primeConstractorStaffArea = form.querySelector('select[name="prime-constractor-staff"]');
    createPrimeConstractorComboBox('staff');
    setComboboxSelected(primeConstractorStaffArea, entity.prime_constractor_staff_id);

    // 入力フォームダイアログを開く
    openFormDialog("form-dialog-01"); 

    // スピナー消去
    processingEnd();
}

// 選択した元請の支店をコンボボックスに登録する
function createPrimeConstractorComboBox(str) {
    const form = document.getElementById('form-dialog-01');
    const primeConstractorArea = form.querySelector('select[name="prime-constractor"]');
    const primeConstractorOfficeArea = form.querySelector('select[name="prime-constractor-office"]');
    const primeConstractorStaffArea = form.querySelector('select[name="prime-constractor-staff"]');

    const selectId = primeConstractorArea.value;  
    switch (str) {
        case "office":
            const officeComboList = officeList.filter(value => { return value.company_id == selectId }).map(item => ({number:item.office_id, text:item.name}));
            createComboBoxWithTop(primeConstractorOfficeArea, officeComboList, "");
            createPrimeConstractorComboBox('staff');
            break;
        case "staff":
            const selectOfficeId = primeConstractorOfficeArea.value;
            const staffComboList = salesStaffList.filter(value => { return value.company_id == selectId && value.office_id == selectOfficeId }).map(item => ({number:item.staff_id, text:item.name}));
            createComboBoxWithTop(primeConstractorStaffArea, staffComboList, "");
        default:
            break;
    }
}

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave() {
    const form = document.getElementById('form-01');
    // エラーチェック
    if (formDataCheck(form) == false) {
        return;
    } else {
        const formData = new FormData(form);
        const formdata = structuredClone(formEntity);
        formdata.order_id = formData.get('order-id');
        formdata.request_number = formData.get('request-number');
        if (formData.get('start-date') == "") {
            formdata.start_date = "9999-12-31";
        } else {
            formdata.start_date = formData.get('start-date');
        }
        if (formData.get('end-date') == "") {
            formdata.end_date = "9999-12-31";
        } else {
            formdata.end_date = formData.get('end-date');
        }
        formdata.prime_constractor_id = formData.get('prime-constractor');
        formdata.prime_constractor_office_id = formData.get('prime-constractor-office');
        formdata.title = formData.get('title').trim();
        formdata.order_postal_code = formData.get('order-postal-code').trim();
        formdata.order_full_address = formData.get('order-full-address').trim();
        formdata.contact_information = formData.get('contact-information').trim();
        formdata.contact_information2 = formData.get('contact-information2').trim();
        formdata.remarks = formData.get('remarks').trim();
        formdata.version = formData.get('version');

        itemList.forEach(item => { 
            if (item.order_item_id === -1) {
                item.order_item_id = 0;
            }
        });
        staffList.forEach(staff => { 
            if (staff.delivery_staff_id === -1) {
                staff.delivery_staff_id = 0;
            }
        });
        workList.forEach(work => { 
            if (work.work_content_id === -1) {
                work.work_content_id = 0;
            }
        });

        // 保存処理
        const resultResponse = await postFetch("/api/order/save", JSON.stringify({orderEntity:formdata, itemEntityList:itemList, staffEntityList:staffList, workEntityList:workList}), token, "application/json");
        const result = await resultResponse.json();
        if (result.success) {
            // 画面更新
            openMsgDialog("msg-dialog", result.message, "blue");
            await execDate01Search();
            // 追加・変更行に移動
            scrollIntoTableList("table-01-content", result.id);
        } else {
            openMsgDialog("msg-dialog", result.message, "red");
        }
        // ダイアログを閉じる
        closeFormDialog('form-dialog-01');
    }
}

// 入力チェック
function formDataCheck(area) {
    let msg = "";
    // 件名が入力されていないとFalseを返す
    const title = area.querySelector('input[name="title"]');
    if (title != null && title.value == "") msg += '\n件名が入力されていません';
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
        case "01":
            result = await deleteTablelist('table-01-content', '/api/order/delete');
            break;
        default:
            break;
    }
    
    if (result.success) {                
        await execDate01Search();
        openMsgDialog("msg-dialog", result.message, "blue");
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }
}

/******************************************************************************************************* 商品登録 */

async function execRegistItem(self) {
    const panel = self.closest('#form-dialog-01');
    const form = panel.querySelector('#form-01');
    if (form12DataCheck(form) == false) {
        return;
    } else {
        const formData = new FormData(form);
        const formdata = structuredClone(itemEntity);
        itemId -= 1;
        formdata.order_id = formData.get('order-id');
        formdata.order_item_id = itemId;
        if (formData.get('item-maker') == "") {
            formdata.item_maker = "-----";
        } else {
            formdata.item_maker = formData.get('item-maker');
        }
        if (formData.get('item-name') == "") {
            formdata.item_name = "-----";
        } else {
            formdata.item_name = formData.get('item-name');
        }
        formdata.item_model = formData.get('item-model');
        if (formData.get('item-quantity') == "") {
            formdata.item_quantity = 1;
        } else {
            formdata.item_quantity = formData.get('item-quantity');
        }
        formdata.arrival_date = "9999-12-31";
        formdata.state = 0;

        itemList.push(formdata);

        await updateTable12Display("table-12-content");

        resetForm12Input();
        maker12.focus();
    }
}

// 入力フォームの内容をリセットする
function resetForm12Input() {
    if (maker12 != null) maker12.value = "";
    if (name12 != null) name12.value = "";
    if (model12 != null) model12.value = "";
    if (quantity12 != null) quantity12.value = "";
}

// 入力チェック
function form12DataCheck(area) {
    let msg = "";
    // 型番が入力されていないとFalseを返す
    const model = area.querySelector('input[name="item-model"]');
    if (model != null && model.value == "") msg += '\n型番が入力されていません';
    // エラーが一つ以上あればエラーメッセージダイアログを表示する
    if (msg != "") {
        openMsgDialog("msg-dialog", msg, "red");
        setFocusElement("msg-dialog", maker12);
        return false;
    }
    return true;
}

// リスト画面の本体部分を作成する
function createTable12Content(tableId) {
    if (itemList == null) return;
    const tbl = document.getElementById(tableId);
    itemList.forEach(function (item) {
        if (item.state != deleteCode) {
            let newRow = tbl.insertRow();
            // ID（Post送信用）
            newRow.setAttribute('name', 'data-row');
            newRow.setAttribute('data-id', item.order_item_id);

            createTable12Row(newRow, item, "12");
        }
    });
}

// テーブル行を作成する
function createTable12Row(newRow, item, tab) {
    // メーカー
    newRow.insertAdjacentHTML('beforeend', '<td name="maker"><span>' + (item.item_maker ?? '') + '</span></td>');
    // 品目
    newRow.insertAdjacentHTML('beforeend', '<td name="item"><span>' + (item.item_name ?? '') + '</span></td>');
    // 型番
    newRow.insertAdjacentHTML('beforeend', '<td name="model"><span>' + (item.item_model ?? '') + '</span></td>');
    // 数量
    newRow.insertAdjacentHTML('beforeend', '<td name="quantity" class="text-center"><span>' + (item.item_quantity ?? '0') + '</span></td>');
    // 削除ボタン
    newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteItem(' + item.order_item_id + ')"><img src="/icons/dust.png"></div></td>');
}

// テーブルリスト画面を更新する
async function updateTable12Display(tableId) {
    // リスト画面を初期化
    deleteElements(tableId);
    // リスト作成
    createTable12Content(tableId);
    // スクロール時のページトップボタン処理を登録する
    setPageTopButton(tableId);
    // テーブルにスクロールバーが表示されたときの処理を登録する
    document.querySelectorAll('.scroll-area').forEach(el => {
        toggleScrollbar(el);
    });
}

async function deleteItem(id) {
    if (id < 0) {
        itemList = itemList.filter(value => value.order_item_id != id);
    } else {
        const item = itemList.find(value => value.order_item_id == id);
        item.state = deleteCode;
    }
    await updateTable12Display("table-12-content");
}

/******************************************************************************************************* 作業登録 */

async function execRegistWork(self) {
    const panel = self.closest('#form-dialog-01');
    const form = panel.querySelector('#form-01');
    if (form13DataCheck(form) == false) {
        return;
    } else {
        const formData = new FormData(form);
        const formdata = structuredClone(workEntity);
        workId -= 1;
        formdata.order_id = formData.get('order-id');
        formdata.work_content_id = workId;
        formdata.work_content = formData.get('work-content');
        if (formData.get('work-quantity') == "") {
            formdata.work_quantity = 1;
        } else {
            formdata.work_quantity = formData.get('work-quantity');
        }
        if (formData.get('work-payment') == "") {
            formdata.work_payment = 0;
        } else {
            formdata.work_payment = formData.get('work-payment');
        }
        
        formdata.state = 0;

        workList.push(formdata);

        await updateTable13Display("table-13-content");

        resetForm13Input();
        code13.focus();
    }
}

// 入力フォームの内容をリセットする
function resetForm13Input() {
    if (code13 != null) code13.value = "";
    if (cont13 != null) cont13.value = "";
    if (quan13 != null) quan13.value = "";
    if (pay13 != null) pay13.value = "";
}

// 入力チェック
function form13DataCheck(area) {
    let msg = "";
    // 型番が入力されていないとFalseを返す
    const cont = area.querySelector('input[name="work-content"]');
    if (cont13 != null && cont13.value == "") msg += '\n作業内容が入力されていません';
    // エラーが一つ以上あればエラーメッセージダイアログを表示する
    if (msg != "") {
        openMsgDialog("msg-dialsog", msg, "red");
        setFocusElement("msg-dialog", code13);
        return false;
    }
    return true;
}

// リスト画面の本体部分を作成する
function createTable13Content(tableId) {
    if (itemList == null) return;
    const tbl = document.getElementById(tableId);
    workList.forEach(function (item) {
        if (item.state != deleteCode) {
            let newRow = tbl.insertRow();
            // ID（Post送信用）
            newRow.setAttribute('name', 'data-row');
            newRow.setAttribute('data-id', item.work_content_id);

            createTable13Row(newRow, item, "12");
        }
    });
}

// テーブル行を作成する
function createTable13Row(newRow, item, tab) {
    // 品目
    newRow.insertAdjacentHTML('beforeend', '<td name="work"><span>' + (item.work_content ?? '') + '</span></td>');
    // 型番
    newRow.insertAdjacentHTML('beforeend', '<td name="pay" class="text-right"><span>' + (item.work_payment ?? '0') + '</span></td>');
    // 数量
    newRow.insertAdjacentHTML('beforeend', '<td name="quantity" class="text-right"><span>' + (quan = item.work_quantity ?? '0') + '</span></td>');
    // 小計
    newRow.insertAdjacentHTML('beforeend', '<td name="subtotal" class="text-right"><span>' + item.work_quantity * item.work_payment + '</span></td>');
    // 削除ボタン
    newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteItem(' + item.work_content_id + ')"><img src="/icons/dust.png"></div></td>');
}

// テーブルリスト画面を更新する
async function updateTable13Display(tableId) {
    // リスト画面を初期化
    deleteElements(tableId);
    // リスト作成
    createTable13Content(tableId);
    // スクロール時のページトップボタン処理を登録する
    setPageTopButton(tableId);
    // テーブルにスクロールバーが表示されたときの処理を登録する
    document.querySelectorAll('.scroll-area').forEach(el => {
        toggleScrollbar(el);
    });
}

async function deleteItem(id) {
    if (id < 0) {
        workList = workList.filter(value => value.work_content_id != id);
    } else {
        const item = workList.find(value => value.work_content_id == id);
        item.state = deleteCode;
    }
    await updateTable13Display("table-12-content");
}

/******************************************************************************************************* 配送員登録 */

async function execRegistStaff() {
    const panel = document.getElementById('form-dialog-01');
    const form = panel.querySelector('#form-01');
    if (form14DataCheck(form) == false) {
        resetForm14Input();
        return;
    } else {
        const formData = new FormData(form);
        const formdata = structuredClone(staffEntity);
        staffId -= 1;
        formdata.order_id = formData.get('order-id');
        formdata.delivery_staff_id = staffId;
        formdata.employee_id = formData.get('code');
        formdata.full_name = formData.get('employee-name');
        formdata.company_name = formData.get('company-name');
        formdata.state = 0;

        staffList.push(formdata);

        await updateTable14Display("table-14-content", staffList);
    
        resetForm14Input();
        code14.focus();
    }
}

// 入力フォームの内容をリセットする
function resetForm14Input() {
    if (code14 != null) code14.value = "";
    if (emp14 != null) emp14.value = "";
    if (com14 != null) com14.value = "";                
}

// 入力チェック
function form14DataCheck(area) {
    let msg = "";
    if (emp14 != null && emp14.value == "") msg += '\n担当者が入力されていません';
    // 同一担当者でないか
    if (staffList != null) {
        const staff = staffList.find(value => value.employee_id == Number(code14.value));
        if (staff != null) msg += '\n既に登録済みです';
    }
    // エラーが一つ以上あればエラーメッセージダイアログを表示する
    if (msg != "") {
        openMsgDialog("msg-dialog", msg, "red");
        setFocusElement("msg-dialog", code14);
        return false;
    }
    return true;
}

// リスト画面の本体部分を作成する
function createTable14Content(tableId) {
    if (staffList == null) return;
    const tbl = document.getElementById(tableId);
    staffList.forEach(function (item) {
        if (item.state != deleteCode) {
            let newRow = tbl.insertRow();
            // ID（Post送信用）
            newRow.setAttribute('name', 'data-row');
            newRow.setAttribute('data-id', item.delivery_staff_id);

            createTable14Row(newRow, item, "14");
        }
    });
}

// テーブル行を作成する
function createTable14Row(newRow, item, tab) {
    // ID
    newRow.insertAdjacentHTML('beforeend', '<td name="id-cell">' + String(item.employee_id).padStart(4, '0') + '</td>');
    // 担当者名
    newRow.insertAdjacentHTML('beforeend', '<td name="emp"><span>' + (item.full_name ?? '') + '</span></td>');
    // 会社名
    newRow.insertAdjacentHTML('beforeend', '<td name="com"><span>' + (item.company_name ?? '') + '</span></td>');
    // 削除ボタン
    newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteStaff(' + item.delivery_staff_id + ')"><img src="/icons/dust.png"></div></td>');
}

// テーブルリスト画面を更新する
async function updateTable14Display(tableId) {
    // リスト画面を初期化
    deleteElements(tableId);
    // リスト作成
    createTable14Content(tableId);
    // スクロール時のページトップボタン処理を登録する
    setPageTopButton(tableId);
    // テーブルにスクロールバーが表示されたときの処理を登録する
    document.querySelectorAll('.scroll-area').forEach(el => {
        toggleScrollbar(el);
    });
}

async function deleteStaff(id) {
    if (id < 0) {
        staffList = staffList.filter(value => value.delivery_staff_id != id);
    } else {
        const staff = staffList.find(value => value.delivery_staff_id == id);
        staff.state = deleteCode;
    }
    await updateTable14Display("table-14-content", staffList);
}

// コード入力ボックスからフォーカスが外れた時の処理
function execCode14Blur(e) {
    if (e.target.value == "") {
        emp14.value = "";
        com14.value = "";
        return;
    }
    searchForNameByCode(e);
}

// コード入力ボックスでエンターを押した時の処理
function execCode14Changed(e) {
    if (e == null) return;
    // ボックスが空白なら処理しない
    if (e.target.value == "") {
        emp14.value = "";
        com14.value = "";
        return;
    }
    if(e.key === 'Enter'){
        e.preventDefault();
        execCodeBlur(e);
    }
}

// コードから[employee]を取得して、名前と会社名を表示
async function searchForNameByCode(e) {
    e.preventDefault();
    if (code14 == null || emp14 == null || com14 == null) return;
    if (code14.value == "" || isNaN(code14.value)) {
        emp14.value = "";
        com14.value = "";
        return;
    }

    const entity = await getEmployeeByCode(Number(code14.value));

    if (entity != null && entity.employee_id > 0) {
        emp14.value = entity.full_name;
        com14.value = entity.company_name;
        // regBtn14.focus();
    } else {
        code14.value = "";
        emp14.value = "";
        com14.value = "";
        openMsgDialog("msg-dialog", "コードが登録されていません", 'red');
        setFocusElement("msg-dialog", code14);
    }                
}

// コード入力ボックスからフォーカスが外れた時の処理
function execCode13Blur(e) {
    if (e.target.value == "") {
        cont13.value = "";
        quan13.value = "";
        pay13.value = "";
        return;
    }
    searchForContentByCode(e);
}

// コードから[work_content]を取得して、名前と料金を表示
async function searchForContentByCode(e) {
    e.preventDefault();
    if (code13 == null || cont13 == null || quan13 == null || pay13 == null) return;
    if (code13.value == "" || isNaN(code13.value)) {
        cont13.value = "";
        quan13.value = "";
        pay13.value = "";
        return;
    }
    const entity = await getWorkContentByCode(Number(code13.value));

    if (entity != null && entity.work_item_id > 0) {
        cont13.value = entity.name;
        pay13.value = entity.payment;
        // regBtn14.focus();
    } else {
        code13.value = "";
        cont13.value = "";
        quan13.value = "";
        pay13.value = "";
        openMsgDialog("msg-dialog", "コードが登録されていません", 'red');
        setFocusElement("msg-dialog", code13);
    }                
}

/******************************************************************************************************* ダウンロード */

async function execDownloadCsv(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    let result;
    switch (tab) {
        case "01":
            result = await downloadCsv('table-01-content', '/api/order/download/csv');
            break;
        default:
            break;
    }
}

/******************************************************************************************************* 画面更新 */

// テーブルリスト画面を更新する
async function updateTableDisplay(tableId, footerId, searchId, list) {
    // フィルター処理
    const result = filterDisplay(searchId, list);
    // リスト画面を初期化
    deleteElements(tableId);
    // リスト作成
    createTableContent(tableId, result);
    // フッター作成
    createTableFooter(footerId, list);
    // チェックボタン押下時の処理を登録する
    registCheckButtonClicked(tableId);
    // すべて選択ボタンをオフにする
    turnOffAllCheckBtn(tableId);
    // テーブルのソートをリセットする
    resetSortable(tableId);
    // スクロール時のページトップボタン処理を登録する
    setPageTopButton(tableId);
    // テーブルにスクロールバーが表示されたときの処理を登録する
    document.querySelectorAll('.scroll-area').forEach(el => {
        toggleScrollbar(el);
    });
}

async function execDate01Search() {
    // リスト取得
    origin = await getOrdersBetween("start-date01", "end-date01", "/api/order/get/between");

    // 画面更新
    await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin);
}

// 一覧表示用のリスト取得
async function getOrdersBetween(startId, endId, url) {
    const start = document.getElementById(startId).value;
    const end = document.getElementById(endId).value;
    // const data = "&start=" + encodeURIComponent(start) + "&end=" + encodeURIComponent(end);
    // const contentType = 'application/x-www-form-urlencoded';
    // // List<order>を取得
    // const resultResponse = await postFetch(url, data, token, contentType);
    // return await resultResponse.json();
    return await searchFetch(url, JSON.stringify({start:start, end:end}), token);
}

async function execFilterDisplay(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    switch (tab) {
        case "01":
            await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin);
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

    // 配送員登録画面のコード入力ボックスでエンターキーが押された時の処理を登録
    code14.addEventListener('keydown', function (e) { execCode14Changed(e); });
    // 配送員登録画面のコード入力ボックスのフォーカスが外れた時の処理を登録
    code14.addEventListener('blur', function (e) { execCode14Blur(e); });

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        await execFilterDisplay(e.currentTarget);
    }, false);

    // 郵便番号入力ボックスでエンターキーが押された時の処理を登録
    document.getElementById('order-postal-code').addEventListener('keydown', async function (e) {
        await getAddress(e, 'order-postal-code', 'order-full-address');                                                                                                                                                                                                                                                                                                                                                       ;
    });

    // エンターフォーカス処理をイベントリスナーに登録する
    setEnterFocus("form-01");

    // 画面更新
    await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin);
    // テーブルをソート可能にする
    makeSortable("table-01-content");
    // スクロール時のページトップボタン処理を登録する
    setPageTopButton("table-01-content");

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })
                    
    // テキストエリアを常に最終行に移動させる
    const textarea = document.getElementById('remarks');
    textarea.addEventListener('input', () => {
        textarea.scrollTop = textarea.scrollHeight;
    });
    textarea.addEventListener('keydown', (e) => {
        if (e.altKey && e.key === 'Enter') {
            e.preventDefault();
            textarea.scrollTop = textarea.scrollHeight;
        }
    });

    execSpecifyPeriod("today", 'start-date01', 'end-date01');

    // スピナー消去
    processingEnd();
});