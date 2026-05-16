"use strict"

let list04 = [];

// -------------------------------------------------------------------------------------------------------------------------------------- 有給

// 選択した営業所IDと年で従業員の勤怠情報概要を取得してリスト作成
async function createPaidHolidayList(officeId, year) {
    list04 = await getEmployeePaidHolidayFromYear(officeId, year, "/api/timeworks/paidholiday/get/year");
    createTable04Content(list04);
    // チェックボックスの処理を再登録する
    registCheckButtonClicked("table-04-content");
    // エンターフォーカス処理をイベントリスナーに登録する
    tabFocusElements = createTabFocusElements();
    setEnterFocus("table-04");
}

// 出力一覧表示用のテーブル作成
function createTable04Content(list) {
    const tbl = document.getElementById("table-04-content");
    deleteElements(tbl);
    let i = 0;
    list.forEach(function (item) {
        let newRow = tbl.insertRow();
        // ID（Post送信用）
        newRow.setAttribute('name', 'data-row');
        newRow.setAttribute('data-id', item.employee_id);
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
            const year = document.querySelector('div[data-panel="04"] select[name="year"]');
            displayPaidHolidayList(item.employee_id, year.value);
        }
        // ID
        newRow.insertAdjacentHTML('beforeend', '<td><span>' + item.employee_id + '</span></td>');
        // 担当者名
        newRow.insertAdjacentHTML('beforeend', '<td><span>' + item.full_name + '</span></td>');
        // 使用日数
        newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.total_days ?? "0") + ' 日</span></td>');
    });
    createTableFooter("footer-04", list);
};

// 有給休暇一覧表示用のリスト取得
async function getEmployeePaidHolidayFromYear(id, year, url) {
    return await searchFetch(url, JSON.stringify({number:id, text:year}), token);
}

// 申請入力ダイアログを開く ---------------------------------
function execPaidApplicationEdit() {
    const form = document.getElementById('form-01');
    const id = form.querySelector('input[name="employee-id"]');
    id.value = "";
    const name = form.querySelector('input[name="employee-name"]');
    name.value = "";
    const start = form.querySelector('input[name="start-date"]');
    start.value = "";
    const end = form.querySelector('input[name="end-date"]');
    end.value = "";
    const reason = form.querySelector('textarea[name="reason"]');
    reason.value = "";
    
    // 入力フォームダイアログを開く
    openFormDialog("form-dialog-01");
    code02.focus();
}

// 新規申請を保存する
async function execApplication() {
    const form = document.getElementById('form-01');
    const formdata = {};
    // エラーチェック
    if (formDataCheck(form) == false) {
        return;
    } else {
        const formData = new FormData(form);
        formdata.employee_id = formData.get('employee-id');
        formdata.start_date = formData.get('start-date');
        const end = formData.get('end-date');
        formdata.end_date = end == "" ? formdata.start_date: end;
        formdata.reason = formData.get('reason').trim();

        // 保存処理
        const resultResponse = await postFetch("/api/timeworks/paidholiday/save", JSON.stringify(formdata), token, "application/json");
        const result = await resultResponse.json();
        if (result.success) {
            // 画面更新
            openMsgDialog("msg-dialog", result.message, "blue");
            const office = document.querySelector('div[data-panel="04"] select[name="office"]');
            const year = document.querySelector('div[data-panel="04"] select[name="year"]');
            await createPaidHolidayList(office.value, year.value)
            // 追加・変更行に移動
            scrollIntoTableList('table-04', result.data);
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
    // コードが入力されていないとFalseを返す
    const account = area.querySelector('input[name="employee_id"]');
    if (account != null && account.value == "") msg += '\n申請者が入力されていません';
    const start = area.querySelector('input[name="start-date"]');
    if (start != null && start.value == "") msg += '\n開始日が入力されていません';
    const end = area.querySelector('input[name="end-date"]');
    if (start && end) {
        const startDate = new Date(start.value);
        const endDate = new Date(end.value);
        if (startDate > endDate) {
            msg += '\n終了日が開始日より前に設定されています';
        }
    }
    // エラーが一つ以上あればエラーメッセージダイアログを表示する
    if (msg != "") {
        openMsgDialog("msg-dialog", msg, "red");
        return false;
    }
    return true;
}


// 有給申請リストダイアログを開く ---------------------------------
async function displayPaidHolidayList(id, year) {
    await createPaidHolidayListByEmployeeId(id, year);
    openFormDialog("form-dialog-02");
}

// 選択した従業員IDと年で従業員の勤怠情報概要を取得してリスト作成
async function createPaidHolidayListByEmployeeId(employeeId, year) {
    list15 = await getEmployeePaidHolidayFromYear(employeeId, year, "/api/timeworks/paidholiday/get/employeeid");
    createTable05Content(list15, year);
    // チェックボックスの処理を再登録する
    registCheckButtonClicked("table-15-content");
    // エンターフォーカス処理をイベントリスナーに登録する
    tabFocusElements = createTabFocusElements();
    setEnterFocus("table-15");
}

// 従業員の申請一覧表示用のテーブル作成
function createTable05Content(list, year) {
    const tbl = document.getElementById("table-15-content");
    deleteElements(tbl);
    let i = 0;
    list.forEach(function (item) {
        let newRow = tbl.insertRow();
        // ID（Post送信用）
        newRow.setAttribute('name', 'data-row');
        newRow.setAttribute('data-id', item.paid_holiday_id);
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
        // 開始日
        newRow.insertAdjacentHTML('beforeend', '<td><input class="icon-del" type="date" value="' + item.start_date + '" disabled></td>');
        // 終了日
        newRow.insertAdjacentHTML('beforeend', '<td><input class="icon-del" type="date" value="' + item.end_date + '" disabled></td>');
        // 事由
        newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.reason ?? "0") + '</span></td>');
        // 削除ボタン
        newRow.insertAdjacentHTML('beforeend', '<td><div onclick="execDeletePaidHolidayByEmployeeId(' + item.paid_holiday_id + ')" class="img-btn"><img src="icons/dust.png"></div></td>');
    });
    createTableFooter("footer-04", list);
};

// 選択した営業所IDと年で従業員の勤怠情報概要を取得してリスト作成
async function createPaidHolidayList(officeId, year) {
    list04 = await getEmployeePaidHolidayFromYear(officeId, year, "/api/timeworks/paidholiday/get/year");
    createTable04Content(list04);
    // チェックボックスの処理を再登録する
    registCheckButtonClicked("table-04-content");
    // エンターフォーカス処理をイベントリスナーに登録する
    tabFocusElements = createTabFocusElements();
    setEnterFocus("table-04");
}

async function execDeletePaidHolidayByEmployeeId(id) {
    // const data = "id=" + encodeURIComponent(parseInt(id));
    // const url = '/api/timeworks/paidholiday/delete/id';
    // const contentType = 'application/x-www-form-urlencoded';
    // const resultResponse = await postFetch(url, data, token, contentType);
    // const result = await resultResponse.json();

    const result = await updateFetch('/api/timeworks/paidholiday/delete/id', JSON.stringify({id:parseInt(id)}), token);
    if (result?.ok) {
        // 画面更新
        openMsgDialog("msg-dialog", result.message, "blue");
        const office = document.querySelector('div[data-panel="04"] select[name="office"]');
        const year = document.querySelector('div[data-panel="04"] select[name="year"]');
        await createPaidHolidayList(office.value, year.value)
    // } else {
    //     openMsgDialog("msg-dialog", result.message, "red");
    }
    // ダイアログを閉じる
    closeFormDialog('form-dialog-02');
}
