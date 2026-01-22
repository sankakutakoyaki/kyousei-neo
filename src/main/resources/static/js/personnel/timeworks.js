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
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + item.full_name + '</span></td>');
            // 出勤時刻
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.start_time ?? "") + '</span></td>');
            // 退勤時刻
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.end_time ?? "") + '</span></td>');
            // 営業所
            newRow.insertAdjacentHTML('beforeend', '<td class="pc-style"><span>' + (item.office_name ?? "") + '</span></td>');
            break;
        case "02":
            // 出勤打刻
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.start_time ?? "") + '</span></td>');
            // 退勤打刻
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.end_time ?? "") + '</span></td>');
            // 出勤確定
            let zeroClassStart = item.comp_start_time === "00:00:00" ? " zero-time" : "";
            newRow.insertAdjacentHTML('beforeend', '<td><input tabindex="' + i++ + '" name="start"' +
                ' class="icon-del' + zeroClassStart + '" type="time" step="1" value="' + (item.comp_start_time ?? "") + '"' +
                ' onchange="cellChange(' + item.timeworks_id + ', this); zeroTimeCheck(this)"></td>');
            // 退勤確定
            let zeroClassEnd = item.comp_end_time === "00:00:00" ? " zero-time" : "";
            newRow.insertAdjacentHTML('beforeend', '<td><input tabindex="' + i++ + '" name="end"' +
                ' class="icon-del' + zeroClassEnd + '" type="time" step="1" value="' + (item.comp_end_time ?? "") + '"' +
                ' onchange="cellChange(' + item.timeworks_id + ', this); zeroTimeCheck(this)"></td>'
            );
            // 休憩
            newRow.insertAdjacentHTML('beforeend', '<td><input tabindex="' + i++ + '" name="rest"' +
                ' class="icon-del" type="time" value="' + (item.rest_time ?? "") + '"' +
                ' onchange="cellChange(' + item.timeworks_id + ', this)"></td>');
            break;
        case "03":
            // ID
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + item.employee_id + '</span></td>');
            // 担当者名
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + item.full_name + '</span></td>');
            // 総出勤日
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + item.total_working_date + ' 日</span></td>');
            // 総勤務時間
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + item.total_working_time + ' 時間</span></td>');
            break;
        case "04":
            // 開始日
            newRow.insertAdjacentHTML('beforeend', '<td><input class="icon-del" type="date" value="' + item.start_date + '" disabled></td>');
            // 終了日
            newRow.insertAdjacentHTML('beforeend', '<td><input class="icon-del" type="date" value="' + item.end_date + '" disabled></td>');
            // 事由
            newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.reason ?? "0") + '</span></td>');
            // 削除ボタン
            newRow.insertAdjacentHTML('beforeend', '<td><div onclick="execDeletePaidHolidayByEmployeeId(' + item.paid_holiday_id + ')" class="img-btn"><img src="icons/dust.png"></div></td>');
            break;
        case "05":
            if (item.situation == "有給") {
                newRow.insertAdjacentHTML('beforeend', '<td></td><td></td><td></td><td></td><td></td><td><span>有給</span></td><td></td>');
            } else {
                // 出勤打刻
                newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.start_time ?? "") + '</span></td>');
                // 退勤打刻
                newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.end_time ?? "") + '</span></td>');
                // 出勤確定
                newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.comp_start_time ?? "") + '</span></td>');
                // 退勤確定
                newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.comp_end_time ?? "") + '</span></td>');
                // 休憩
                newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.rest_time ?? "") + '</span></td>');
                // 確定
                if (item.state > 0) {
                    newRow.insertAdjacentHTML('beforeend', '<td><span>確定</span></td>');
                    // 確定を戻すボタン
                    newRow.insertAdjacentHTML('beforeend', '<td><div sec:authorize="hasAuthority("APPROLE_admin")"><div onclick="execReverse(' + item.timeworks_id + ')" class="img-btn"><img src="/icons/update.png"></img></div></div></td>');
                } else {
                    newRow.insertAdjacentHTML('beforeend', '<td></td><td></td>');
                }
            }
            break;
        default:
            break;
    }
}

// -------------------------------------------------------------------------------------------------------------------------------------- 確定タブ・一覧タブ

// async function updateTimeworks(list, self) {
//     await execListChange(self);
//     return await updateFetch('/api/timeworks/update/list', JSON.stringify(list), token);
// }

// 一覧表示用のリスト取得
async function getEmployeeTimeworksBetween(id, startId, endId, url) {
    const start = document.getElementById(startId).value;
    const end = document.getElementById(endId).value;
    return await searchFetch(url, JSON.stringify({id:parseInt(id), start:start, end:end}), token);
}

// サイドバーの従業員選択メニュー作成（tab②・tab５）
function createEmployeeList(tab) {
    const config = TIMEWORKS_TAB_CONFIG[tab];
    if (!config.officeMenu) return;

    const form = document.getElementById(config.sideStr);
    if (!form) {
        const office = form.querySelector('select[name="office"]');

        const list = origin.filter(value => value.office_id === Number(office.value));
        createSidebarEmployeeList(tab, list);
    }
}

// サイドバーの従業員選択メニューコンテンツ部分作成
function createSidebarEmployeeList(tab, list) {
    const config = TIMEWORKS_TAB_CONFIG[tab];
    const sidemenu = document.getElementById(config.sidemenu);

    list.forEach(function(item) {
        let li = createListItemWithSelection(item.employee_id, item.full_name, sidemenu);
        li.addEventListener('click', () => {
            createEmployeeTimeworksList(tab, item.employee_id);
        })
        sidemenu.appendChild(li);
    });
}

// 選択した従業員IDで勤怠情報を取得してリスト作成
async function createEmployeeTimeworksList(tab, id) {
    const result = await getTimeworksByTab(tab, id);
    if (result.ok) {
        const config = TIMEWORKS_TAB_CONFIG[tab];
        createTableContent(config.tableId, result);

        setEnterFocusFromEmployeeTimeworkList();
    } 
}

async function getTimeworksByTab(tab, id) {
    const cfg = TIMEWORKS_TAB_CONFIG[tab];
    if (!cfg) return [];

    return await getEmployeeTimeworksBetween(id, `start-date${tab}`, `end-date${tab}`, cfg.api.list);
}

// -------------------------------------------------------------------------------------------------------------------------------------- 日付変更
// 日付ボックスが変更された時の処理
async function execListChange(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const config = TIMEWORKS_TAB_CONFIG[tab];
    const selectedId = document.querySelector(config.selectedId);
    await createEmployeeTimeworksList(tab, selectedId.dataset.id);

    // switch (tab) {
    //     case "02":
    //         const selectedId = document.querySelector("#employee-list-02 li.selected");
    //         if (selectedId != null) {
    //             await createEmployeeTimeworksList(tab, selectedId.dataset.id);
    //         }
    //         break;
    //     case "03":
    //         const selectedItem = panel.querySelector("[name='office']");
    //         if (selectedItem != null) {
    //             // createTimeworksSummaryList(tab, selectedItem.value);
    //             await createEmployeeTimeworksList(tab, selectedId.dataset.id);
    //         }
    //         break;
    //     case "05":
    //         const selectedId2 = document.querySelector("#employee-list-05 li.selected");
    //         if (selectedId2 != null) {
    //             await createEmployeeTimeworksList(tab, selectedId2.dataset.id);
    //         }
    //         break;
    //     default:
    //         break;
    // }
}

// コード入力ボックスでエンターを押した時の処理
async function execCodeChanged(e, tab) {
    if(e.key === 'Enter') {
        e.preventDefault();

        // const config = TIMEWORKS_TAB_CONFIG[tab];
        const config = CODE_CONFIG[tab];
        const codeBox = document.getElementById(config.codeId);
        const nameBox = document.getElementById(config.nameId);

        if (e == null) return;
        // ボックスが空白なら処理しない
        if (e.target.value == null || e.target.value.trim() === "") {
            nameBox.value = "";
            return;
        }
        codeBox.blur(e, tab);
    }
}

// コード入力ボックスからフォーカスが外れた時の処理
async function execCodeBlur(e, tab) {
    e.preventDefault();

    // const config = TIMEWORKS_TAB_CONFIG[tab];
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

// tab01で、開始時刻を打刻済みか確認してボタンのフォーカス先を決める
function checkTimeWorksStartSaved(entity) {
    const startBtn = document.querySelector('button[name="start-btn"]');
    const endBtn = document.querySelector('button[name="end-btn"]');

    if (entity != null) {
        if (entity.start_time != null) {
            endBtn.focus();
        } else {
            startBtn.focus();
        }
    }
}

// // コード入力ボックスでエンターを押した時の処理
// async function execCodeChanged(e, tab) {
//     // e.preventDefault();

//     if(e.key === 'Enter') {
//         e.preventDefault();

//         const config = TIMEWORKS_TAB_CONFIG[tab];
//         // const codeBox = document.getElementById(config.codeBox);
//         const nameBox = document.getElementById(config.nameBox);

//         if (e == null) return;
//         // ボックスが空白なら処理しない
//         if (e.target.value == null || e.target.value.trim() === "") {
//             nameBox.value = "";
//             return;
//         }
//         // await searchForEmployeeNameByCode("/api/employee/get/id", codeBox, nameBox);
//         // codeBox.blur(e, tab);
//     }

//     // if(e.key === 'Enter'){
//     //     e.preventDefault();
//     //     execCodeBlur(e, tab);
//     // }
// }

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
    // // 打刻画面のコード入力ボックスでエンターキーが押された時の処理を登録
    // document.getElementById('code').addEventListener('keydown', async function (e) { await execCodeChanged(e, "01"); });
    // // 打刻画面のコード入力ボックスのフォーカスが外れた時の処理を登録
    // document.getElementById('code').addEventListener('blur', async function (e) { await execCodeBlur(e, "01"); });
    // setEnterFocus("timeworks-container01");
    // // 有給申請フォームのコード入力ボックスでエンターキーが押された時の処理を登録
    // document.getElementById('code02').addEventListener('keydown', function (e) { execCode02Changed(e); });
    // // 有給申請フォームのコード入力ボックスのフォーカスが外れた時の処理を登録
    // document.getElementById('code02').addEventListener('blur', function (e) { execCode02Blur(e); });

    // const form01 = document.getElementById('timeworks-sidemenu02');

    // const officeArea01 = form01.querySelector('select[name="office"]');
    // createComboBox(officeArea01, officeList);
    // setComboboxSelected(officeArea01, officeId);
    // officeArea01.onchange = function() { createEmployeeList("02"); };

    // const officeArea02 = document.querySelector('div[data-panel="03"] select[name="office"]');
    // createComboBoxWithTop(officeArea02, officeList, "すべて");
    // setComboboxSelected(officeArea02, officeId);
    // officeArea02.onchange = function() { createTimeworksSummaryList("03", this.value); };

    // const officeArea03 = document.querySelector('div[data-panel="04"] select[name="office"]');
    // createComboBoxWithTop(officeArea03, officeList, "すべて");
    // setComboboxSelected(officeArea03, officeId);
    // officeArea03.onchange = function() { 
    //     const year = document.querySelector('div[data-panel="04"] select[name="year"]');
    //     createPaidHolidayList(this.value, year.value); 
    // };

    // const form02 = document.getElementById('timeworks-sidemenu05');
    // const officeArea04 = form02.querySelector('select[name="office"]');
    // createComboBox(officeArea04, officeList);
    // setComboboxSelected(officeArea04, officeId);
    // officeArea04.onchange = function() { createEmployeeList("05"); };

    // const yearArea01 = document.querySelector('div[data-panel="04"] select[name="year"]');
    // createComboBox(yearArea01, yearList);
    // setComboboxSelected(yearArea01, thisYear);
    // yearArea01.onchange = function() {
    //     const office = document.querySelector('div[data-panel="04"] select[name="office"]');
    //     createPaidHolidayList(office.value, this.value);
    // };

    // createEmployeeList("02");
    // createEmployeeList("05");

    // // 日付ボックスを設定
    // let monthStr = "";
    // switch (Number(officeId)) {
    //     case 1000:
    //         monthStr = "half-month";
    //         break;
    //     case 1001:
    //         monthStr = "half-month";
    //         break;
    //     case 1002:
    //         monthStr = "this-month";
    //         break;
    //     case 1003:
    //         monthStr = "this-month";
    //         break;
    //     default:
    //         monthStr = "this-month";
    //         break;
    // }
    // execSpecifyPeriod(monthStr, 'start-date02', 'end-date02');
    // execSpecifyPeriod(monthStr, 'start-date03', 'end-date03');
    // execSpecifyPeriod(monthStr, 'start-date05', 'end-date05');

    await updateDisplay01();
    // // createTimeworksSummaryList("03", officeId);
    // createEmployeeTimeworksList("03", officeId);
    // createPaidHolidayList(officeId, thisYear)
    // createTableFooter("footer-02", null);
    // createTableFooter("footer-03", null);
    // createTableFooter("footer-04", null);
    // createTableFooter("footer-05", null);

    // // エンターフォーカス処理をイベントリスナーに登録する
    // setEnterFocus("form-01");

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })
});