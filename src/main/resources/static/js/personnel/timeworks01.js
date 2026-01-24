"use strict"

// 勤怠データを保存。timeCategory(start or end)
async function setTimeworks(timeCategory) {
    if (entity.employeeId === 0) return;

    const t_cfg = TIMEWORKS_TIME_CONFIG[timeCategory];
    if (!t_cfg) return null;

    const position = await getLocation();

    entity[t_cfg.latKey] = position.coords.latitude;
    entity[t_cfg.lngKey] = position.coords.longitude;

    const result = await updateFetch('/api/timeworks/regist/today', JSON.stringify({dto:entity,category:timeCategory}), token);
    if (!result?.ok) return;

    await updateDisplay01();
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
}