"use strict"

/******************************************************************************************************* 画面 */

// テーブル作成
function createTableContent(tableId, list) {
    const table = document.getElementById(tableId);
    const panel = table.closest('.tab-panel.is-show');
    const tab = panel.dataset.panel;

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "employeeId",
            onDoubleClick: (item) => {
                execEdit(item.employeeId, this);
            }
        });

        createTableRow(row, item, tab);
    });
}


/******************************************************************************************************* テーブル行作成 */

function createTableRow(newRow, item, tab) {
    switch (tab) {
        case "01":
            // 担当者名
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + item.fullName + '</span></td>');
            // 出勤時刻
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (formatTime(item.startDt) ?? "") + '</span></td>');
            // 退勤時刻
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (formatTime(item.endDt) ?? "") + '</span></td>');
            // 営業所
            newRow.insertAdjacentHTML('beforeend', '<td class="pc-style"><span>' + (item.officeName ?? "") + '</span></td>');
            break;
        default:
            break;
    }
}

/******************************************************************************************************* 日付変更 */

// コード入力ボックスでエンターを押した時の処理
async function execCodeChanged(e, tab) {
    if(e.key === 'Enter') {
        e.preventDefault();

        const config = MODE_CONFIG[tab];
        const codeBox = document.getElementById(config.codeId);

        codeBox.blur(e, tab);
    }
}

// コード入力ボックスからフォーカスが外れた時の処理
async function execCodeBlur(e, tab) {

    const config = MODE_CONFIG[tab];
    const codeBox = document.getElementById(config.codeId);
    const nameBox = document.getElementById(config.nameId);

    if (e.target.value == null || e.target.value.trim() === "") {
        nameBox.value = "";
        return;
    }

    tempEntity = await searchForEmployeeNameByCode("/api/timeworks/get/today/id", codeBox, nameBox);

    if (tempEntity != null && config.codeChange) config.codeChange(tempEntity);
}

// // フォーカス移動後にエンターかスペースで確定させる
// function handleButtonKey(e, action) {
//   if (e.key === 'Enter' || e.key === ' ') {
//     e.preventDefault();
//     action();
//   }
// }

// tab01で、開始時刻を打刻済みか確認してボタンのフォーカス先を決める
function checkTimeWorksStartSaved(entity) {
    if (entity != null && entity.state != "NOT_STARTED") {
        const endBtn = document.querySelector('button[name="end-btn"]');
        endBtn.focus();
    } else {
        const startBtn = document.querySelector('button[name="start-btn"]');
        startBtn.focus();
    }
}

/******************************************************************************************************* 画面更新 */

async function execUpdate() {
    const panel = document.querySelector('.tab-panel.is-show');
    if (panel == null) return;
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];

    // リスト取得
    const resultResponse = await fetch('/api/timeworks/get/today');
    const result = await resultResponse.json();
    origin = result.data;

    if (resultResponse.ok) {
        // 画面更新
        await updateTableDisplay(config.tableId, config.footerId, null, origin, createTableContent);

        const codeBox = document.getElementById(config.codeId);
        const nameBox = document.getElementById(config.nameId);
        codeBox.value = "";
        nameBox.value = "";
        codeBox.focus();
    }
}

// -------------------------------------------------------------------------------------------------------------------------------------- 初期化時の処理

window.addEventListener("load", async () => {
    // 1秒ごとにclock関数を呼び出す
    setInterval(clock, 1000);

    for (const tab of Object.keys(MODE_CONFIG)) {
        let config = MODE_CONFIG[tab];
        if (config != null) {
            // 打刻画面のコード入力ボックスでエンターキーが押された時の処理を登録
            document.getElementById(config.codeId).addEventListener('keydown', async function (e) { await execCodeChanged(e, tab); });
            // 打刻画面のコード入力ボックスのフォーカスが外れた時の処理を登録
            document.getElementById(config.codeId).addEventListener('blur', async function (e) { await execCodeBlur(e, tab); });
        }
    }

    document.querySelector('[name="start-btn"]')
    .addEventListener("click", () => setTimeworks("start"));

    document.querySelector('[name="end-btn"]')
    .addEventListener("click", () => setTimeworks("end"));

    await execUpdate();

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })
});