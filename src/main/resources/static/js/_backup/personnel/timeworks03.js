"use strict"

// // 選択した営業所IDで従業員の勤怠情報概要を取得してリスト作成
// async function createTimeworksSummaryList(tab, officeId) {
//     list03 = await getEmployeeTimeworksBetween(officeId, "start-date03", "end-date03", "/api/timeworks/summary/get/between/id");
//     createBetweenTable03Content(list03);
//     // チェックボックスの処理を再登録する
//     registCheckButtonClicked("table-03-content");
//     // エンターフォーカス処理をイベントリスナーに登録する
//     tabFocusElements = createTabFocusElements();
//     setEnterFocus("table-03");
// }

// -------------------------------------------------------------------------------------------------------------------------------------- ダウンロード
async function execDownloadCsv() {
    const start = document.getElementById("start-date03").value;
    const end = document.getElementById("end-date03").value;
    await downloadCsvByBetweenDate('table-03-content', '/api/timeworks/download/csv', start, end);
}

// 出力一覧表示用のテーブル作成
function createBetweenTable03Content(list) {
    const tbl = document.getElementById("table-03-content");
    deleteElements(tbl);
    let i = 0;
    list.forEach(function (item) {
        let newRow = tbl.insertRow();
        // ID（Post送信用）
        newRow.setAttribute('name', 'data-row');
        newRow.setAttribute('data-id', item.employee_id);
        // チェックセル
        newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
        // ID
        newRow.insertAdjacentHTML('beforeend', '<td><span>' + item.employee_id + '</span></td>');
        // 担当者名
        newRow.insertAdjacentHTML('beforeend', '<td><span>' + item.full_name + '</span></td>');
        // 総出勤日
        newRow.insertAdjacentHTML('beforeend', '<td><span>' + item.total_working_date + ' 日</span></td>');
        // 総勤務時間
        newRow.insertAdjacentHTML('beforeend', '<td><span>' + item.total_working_time + ' 時間</span></td>');
    });
    createTableFooter("footer-03", list);
};