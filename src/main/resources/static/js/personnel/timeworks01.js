"use strict"

async function setTimeworks(timeCategory) {
    const cfg = TIMEWORKS_TIME_CONFIG[timeCategory];
    if (!cfg) return null;

    const dateTime = new Date().toISOString().slice(0, 16);
    const time = getTimeNow();
    const position = await getLocation();

    entity[cfg.dateTimeKey] = dateTime;
    entity[cfg.timeKey] = time;
    entity[cfg.compTimeKey] = time;
    entity[cfg.latKey] = position.coords.latitude;
    entity[cfg.lngKey] = position.coords.longitude;

    const result = await updateFetch('/api/timeworks/regist/today', JSON.stringify(entity), token);
    if (!result?.ok) return;

    await updateDisplay01();
    // const response = await fetch('/api/timeworks/get/today');
    // const list = await response.json();

    // const config = TIMEWORKS_TAB_CONFIG[tab];
    // // 画面更新
    // await updateTableDisplay(config.tableId, config.footerId, "", list, createTableContent);

    // const codeBox = document.getElementById(config.codeBox);
    // const nameBox = document.getElementById(config.nameBox);
    // codeBox.value = "";
    // nameBox.value = "";
    // codeBox.focus();
}

function zeroTimeCheck(self) {
    if (self.value === "00:00:00") {
        self.classList.add("zero-time");
    } else {
        self.classList.remove("zero-time");
    }
}

// -------------------------------------------------------------------------------------------------------------------------------------- 時計作成

function clock() {
    // 現在の日時・時刻の情報を取得
    const d = new Date();    
    // 年を取得
    let year = d.getFullYear();
    // 月を取得
    let month = d.getMonth() + 1;
    // 日を取得
    let date = d.getDate();
    // 曜日を取得
    let dayNum = d.getDay();
    const weekday = ["(日)", "(月)", "(火)", "(水)", "(木)", "(金)", "(土)"];
    let day = weekday[dayNum];
    // 時を取得
    let hour = d.getHours();
    // 分を取得
    let min = d.getMinutes();
    // 秒を取得
    let sec = d.getSeconds();

    // 1桁の場合は0を足して2桁に
    month = month < 10 ? "0" + month : month;
    date = date < 10 ? "0" + date : date;
    hour = hour < 10 ? "0" + hour : hour;
    min = min < 10 ? "0" + min : min;
    sec = sec < 10 ? "0" + sec : sec;

    // 日付・時刻の文字列を作成
    let today = `${year}年${month}月${date}日 ${day}`;
    let time = `${hour}:${min}:${sec}`;

    // 文字列を出力
    document.querySelector(".clock-date").innerText = today;
    document.querySelector(".clock-time").innerText = time;
};

// -------------------------------------------------------------------------------------------------------------------------------------- 更新処理

async function updateDisplay01() {
    const config = TIMEWORKS_TAB_CONFIG["01"];
    const response = await fetch('/api/timeworks/get/today');
    const list = await response.json();

    // 画面更新
    await updateTableDisplay(config.tableId, config.footerId, "", list, createTableContent);

    const codeConfig = CODE_CONFIG["01"];
    const codeBox = document.getElementById(codeConfig.codeId);
    const nameBox = document.getElementById(codeConfig.nameId);
    codeBox.value = "";
    nameBox.value = "";
    codeBox.focus();
    // if (result != null && result.data == -1) {
    //     openMsgDialog("msg-dialog", result.message, 'red');
    //     code.value = "";
    //     name.value = "";
    //     code.focus();
    //     return;
    // }

    // startProcessing();

    // リストデータ取得
    // const response = await fetch('/api/timeworks/get/today');
    // const list = await response.json();

    // // 画面更新
    // await updateTableDisplay("table-01-content", "footer-01", "", list, createTableContent);
    
    // if (code == null || name == null) return;
    // code.value = "";
    // name.value = "";
    // code.focus();

    // processingEnd();
}

// // コードから[timeworks]を取得して、名前を表示
// async function searchForNameByCode(e) {
//     e.preventDefault();
//     if (code == null || name == null) return;
//     if (code.value == "" || isNaN(code.value)) {
//         name.value = "";
//         return;
//     }

//     const entity = await searchFetch(url, JSON.stringify({id:parseInt(code.value)}), token);
//     if (entity.ok && entity.employee_id > 0) {
//         name.value = entity.full_name;
//         checkTimeWorksStartSaved(entity);
//         return;
//     } else {
//         code.value = ""
//         name.value = "";
//         openMsgDialog("msg-dialog", "コードが登録されていません", 'red');
//         return;
//     }
// }

// // コード入力ボックスからフォーカスが外れた時の処理
// function execCodeBlur(e) {
//     if (e.target.value == "") {
//         name.value = "";
//         return;
//     }
//     searchForNameByCode(e);
// }

// // コード入力ボックスでエンターを押した時の処理
// function execCodeChanged(e) {
//     if (e == null) return;
//     // ボックスが空白なら処理しない
//     if (e.target.value == "") {
//         name.value = "";
//         return;
//     }
//     if(e.key === 'Enter'){
//         e.preventDefault();
//         // searchForNameByCode(e);
//         execCodeBlur(e);
//     }
// }

// -------------------------------------------------------------------------------------------------------------------------------------- 出勤退勤確認

// function checkTimeWorksStartSaved(entity) {
//     if (entity != null) {
//         if (entity.start_time != null) {
//             endBtn.focus();
//         } else {
//             startBtn.focus();
//         }
//     }
// }

// -------------------------------------------------------------------------------------------------------------------------------------- 打刻リスト画面作成

// function createTableContent(tableId, list) {
//     const tbl = document.getElementById(tableId);
//     deleteElements(tbl);
//     list.forEach(function (item) {
//         let newRow = tbl.insertRow();
//         // ID（Post送信用）
//         newRow.setAttribute('name', 'data-row');
//         newRow.setAttribute('data-id', item.timeworks_id);
//         // 担当者名
//         newRow.insertAdjacentHTML('beforeend', '<td><span>' + item.full_name + '</span></td>');
//         // 出勤時刻
//         newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.start_time ?? "") + '</span></td>');
//         // 退勤時刻
//         newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.end_time ?? "") + '</span></td>');
//         // 営業所
//         newRow.insertAdjacentHTML('beforeend', '<td class="pc-style"><span>' + (item.office_name ?? "") + '</span></td>');
//     });
// };
