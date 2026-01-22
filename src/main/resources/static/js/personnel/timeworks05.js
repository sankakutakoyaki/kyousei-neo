"use strict"


// 勤怠確定ボタンを押した時の処理
async function execReverse(id) {
    const result = await searchFetch("/api/timeworks/confirm/reverse", JSON.stringify({id:parseInt(id)}), token);
    if (result?.ok) {
        // 画面更新
        openMsgDialog("msg-dialog", result.message, "blue");
        const selectedId = document.querySelector("#employee-list-05 li.selected");
        if (selectedId != null) await createEmployeeTimeworksList("05", selectedId.dataset.id);
    // } else {
    //     openMsgDialog("msg-dialog", result.message, "red");
    }
}

// -------------------------------------------------------------------------------------------------------------------------------------- 取得

// コードから[paid_holiday]を取得して、名前を表示
async function searchForNameByCodeForPaidHoliday(e) {
    e.preventDefault();
    if (code02 == null || name02 == null) return;
    if (code02.value == "" || isNaN(code02.value)) {
        name02.value = "";
        return;
    }

    // [id=code]に入力されたコードから[employee]を取得して[id=name]に入力する
    // const data = "id=" + encodeURIComponent(parseInt(code02.value));
    // const url = '/api/employee/get/id';
    // const contentType = 'application/x-www-form-urlencoded';
    // // [employee]を取得
    // const resultResponse = await postFetch(url, data, token, contentType);
    // const entity02 = await resultResponse.json();

    const entity02 = await searchFetch(url, JSON.stringify({id:parseInt(code02.value)}), token);
    if (entity02.ok && entity02.employee_id > 0) {
        name02.value = entity02.full_name;
        start11.focus();
        return;
    } else {
        code02.value = ""
        name02.value = "";
        openMsgDialog("msg-dialog", "コードが登録されていません", 'red');
        return;
    }
}