"use strict"

/******************************************************************************************************* 画面 */

// 確定・一覧表示用のテーブル作成
function createTableContent(tableId, list) {
    const table = document.getElementById(tableId);
    const panel = table.closest('.tab-panel');
    const tab = panel.dataset.panel;

    let i = 0;
    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "timeworks_id"
        });

        createTableRow(item, row, tab, i);
    });
};

/******************************************************************************************************* テーブル行作成 */

function createTableRow(item, newRow, tab, i) {
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

// -------------------------------------------------------------------------------------------------------------------------------------- 日付変更

// コード入力ボックスでエンターを押した時の処理
async function execCodeChanged(e, tab) {
    if(e.key === 'Enter') {
        e.preventDefault();

        const config = CODE_CONFIG[tab];
        const codeBox = document.getElementById(config.codeId);

        codeBox.blur(e, tab);
    }
}

// コード入力ボックスからフォーカスが外れた時の処理
async function execCodeBlur(e, tab) {
    // e.preventDefault();

    const config = CODE_CONFIG[tab];
    const codeBox = document.getElementById(config.codeId);
    const nameBox = document.getElementById(config.nameId);

    if (e.target.value == null || e.target.value.trim() === "") {
        nameBox.value = "";
        return;
    }

    entity = await searchForEmployeeNameByCode("/api/timeworks/get/today/id", codeBox, nameBox);

    if (entity != null && config.codeChange) config.codeChange(entity);
}

// フォーカス移動後にエンターかスペースで確定させる
function handleButtonKey(e, action) {
  if (e.key === 'Enter' || e.key === ' ') {
    e.preventDefault();
    action();
  }
}

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

// -------------------------------------------------------------------------------------------------------------------------------------- 初期化時の処理
window.addEventListener("load", async () => {
    // 1秒ごとにclock関数を呼び出す
    setInterval(clock, 1000);

    for (const tab of Object.keys(CODE_CONFIG)) {
        let config = CODE_CONFIG[tab];
        if (config != null) {
            // 打刻画面のコード入力ボックスでエンターキーが押された時の処理を登録
            document.getElementById(config.codeId).addEventListener('keydown', async function (e) { await execCodeChanged(e, tab); });
            // 打刻画面のコード入力ボックスのフォーカスが外れた時の処理を登録
            document.getElementById(config.codeId).addEventListener('blur', async function (e) { await execCodeBlur(e, tab); });
        }
    }

    await updateDisplay01();

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })
});