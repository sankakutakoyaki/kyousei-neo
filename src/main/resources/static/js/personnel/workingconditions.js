/******************************************************************************************************* 入力画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const table = document.getElementById(tableId);
    const panel = table.closest('.tab-panel');
    const tab = panel.dataset.panel;

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "working_conditions_id",
            onDoubleClick: (item) => {
                execEdit(item.employee_id, item.full_name, tab);
            }
        });

        createTableRow(row, item);
    });
}
// function createTableContent(tableId, list) {
//     const tbl = document.getElementById(tableId);
//     list.forEach(function (item) {
//         let newRow = tbl.insertRow();
//         // ID（Post送信用）
//         newRow.setAttribute('name', 'data-row');
//         newRow.setAttribute('data-id', item.working_conditions_id);
//         // シングルクリック時の処理
//         newRow.onclick = function (e) {
//             if (!e.currentTarget.classList.contains('selected')){
//                 // すべての行の選択状態を解除する
//                 detachmentSelectClassToAllRow(tbl, false);
//                 // 選択した行にセレクトクラスを付与する
//                 const result = addSelectClassToRow(e.currentTarget);
//             } else {
//                 // 選択済みの要素をクリックした時の処理
//                 const clickedTd = e.target.closest("td");
//                 // 取得したTDの処理
//             }
//         }
//         // ダブルクリック時の処理
//         newRow.ondblclick = function (e) { 
//             // チェックボックスの動作を停止させる
//             e.preventDefault();
//             // 選択済みの場合
//             if (!e.currentTarget.classList.contains('selected')){
//                 // すべての行の選択状態を解除する
//                 detachmentSelectClassToAllRow(tableId, false);
//                 // 選択した行にセレクトクラスを付与する
//                 const result = addSelectClassToRow(e.currentTarget);
//             }
            
//             // フォーム入力画面を表示する
//             switch (tableId) {
//                 case "table-01-content":
//                     execEdit(item.employee_id, item.full_name, this);
//                     break;
//                 case "table-02-content":
//                     execEdit(item.employee_id, item.full_name, this);
//                     break;
//                 default:
//                     break;
//             }
//         }

//         switch (tableId) {
//             case "table-01-content":
//                 createTableRow(newRow, item, "01");
//                 break;
//             case "table-02-content":
//                 createTableRow(newRow, item, "02");
//                 break;
//             default:
//                 break;
//         }

//     });
// }

// テーブル行を作成する
function createTableRow(newRow, item) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    // ID
    newRow.insertAdjacentHTML('beforeend', '<td name="id-cell" class="link-cell" onclick="execEdit(' + item.employee_id + ', \'' + item.full_name + '\', this)">' + String(item.employee_id).padStart(4, '0') + '</td>');
    // 名前
    newRow.insertAdjacentHTML('beforeend', '<td name="name-cell"><span class="kana">' + item.full_name_kana + '</span><br><span>' + item.full_name + '</span></td>');
    // 営業所名
    newRow.insertAdjacentHTML('beforeend', '<td name="office-cell"><span>' + (item.office_name ?? "登録なし") + '</span></td>');
}

/******************************************************************************************************* 入力画面 */

// 勤務条件登録画面を開く
async function execEdit(id, name, tab) {

    // // スピナー表示
    // startProcessing();

    // // 入力フォームダイアログを開く
    // openFormDialog("form-dialog-01");

    // フォーム画面を取得
    const form = document.getElementById('form-dialog-01');   

    // 選択されたIDのエンティティを取得
    // const data = "id=" + encodeURIComponent(parseInt(id));
    // const resultResponse = await postFetch('/api/working_conditions/get/employeeid', data, token, 'application/x-www-form-urlencoded');
    // const result = await resultResponse.json();

    const result = await searchFetch('/api/working_conditions/get/employeeid', JSON.stringify({id:parseInt(id)}), token);
    if (!result?.ok) return;

    let entity = {};
    if (result.working_conditions_id > 0) {
        entity = structuredClone(result);
    } else {
        entity = structuredClone(formEntity);
        const config = MODE_CONFIG[tab];
        entity.category = config.category;
        // switch (tab) {
        //     case "01":
        //         entity.category = categoryEmployeeCode;
        //         break;
        //     case "02":
        //         entity.category = categoryParttimeCode;
        //         break;
        //     default:
        //         break;
        // }
    }

    const employeeName = document.getElementById('employee-name');
    employeeName.textContent = String(id).padStart(4, '0') + " : " + name;

    // form.querySelector('[name="working-conditions-id"]').value = entity.working_conditions_id;
    // form.querySelector('[name="employee-id"]').value = id;
    // form.querySelector('[name="category"]').value = entity.category;

    // form.querySelector('[name="base-salary"]').value = entity.base_salary;
    // form.querySelector('[name="trans-cost"]').value = entity.trans_cost;
    // form.querySelector('[name="basic-start-time"]').value = entity.basic_start_time;                
    // form.querySelector('[name="basic-end-time"]').value = entity.basic_end_time;
    // form.querySelector('[name="version"]').value = entity.version;

    // // 支払い方法コンボボックス
    // const paymentMethodArea = form.querySelector('select[name="payment-method"]');
    // createComboBox(paymentMethodArea, paymentMethodComboList);
    // setComboboxSelected(paymentMethodArea, entity.payment_method);
    // // 給与形態コンボボックス
    // const payTypeArea = form.querySelector('select[name="pay-type"]');
    // createComboBox(payTypeArea, payTypeComboList);
    // setComboboxSelected(payTypeArea, entity.pay_type);
    const ctx = {
        employeeId: id,
        employeeName: name
    };

    applyViewConfig(ctx, entity, VIEW_CONFIG);
    // applyFormConfig(form, entity, ctx);
    // applyComboConfig(form, entity);
    applyFormConfig(form, entity, FORM_CONFIG);
    applyComboConfig(form, entity, COMBO_CONFIG);

    // 入力フォームダイアログを開く
    openFormDialog("form-dialog-01");
    // // スピナー消去
    // processingEnd();
}

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave() {
    const form = document.getElementById('form-01');
    // エラーチェック
    if (formDataCheck(form) == false) {
        return;
    } else {
        // const formData = new FormData(form);
        // const formdata = structuredClone(formEntity);
        // formdata.working_conditions_id = Number(formData.get('working-conditions-id'));
        // formdata.employee_id = Number(formData.get('employee-id'));
        // formdata.category = Number(formData.get('category'));
        // formdata.payment_method = formData.get('payment-method');
        // formdata.pay_type = formData.get('pay-type');
        // if (formData.get('base-salary') == "") {
        //     formdata.base_salary = 0;
        // } else {
        //     formdata.base_salary = formData.get('base-salary');
        // }
        // if (formData.get('trans-cost') == "") {
        //     formdata.trans_cost = 0;
        // } else {
        //     formdata.trans_cost = formData.get('trans-cost');
        // }
        // const start = formData.get('basic-start-time');
        // if (start == null || start == "") {
        //     formdata.basic_start_time = "00:00";
        // } else {
        //     formdata.basic_start_time = start;
        // }
        // const end = formData.get('basic-end-time');
        // if (end == null || end == "") {
        //     formdata.basic_end_time = "00:00";
        // } else {
        //     formdata.basic_end_time = end;
        // }
        // formdata.version = formData.get('version');

        // formdata.user_name = user.account == null ? "kyousei@kyouseibin.com": user.account;

        const formdata = applySaveConfig(form, formEntity);
        formdata.user_name = user.account == null ? 'kyousei@kyouseibin.com': user.account;

        // 保存処理
        // const resultResponse = await postFetch("/api/working_conditions/save", JSON.stringify(formdata), token, "application/json");
        // const result = await resultResponse.json();
        const result = await postFetch("/api/working_conditions/save", JSON.stringify(formdata), token);
        if (result?.ok) {
            // let tableId;
            // switch (formdata.category) {
            //     case categoryEmployeeCode:
            //         tableId = "table-01-content";
            //         break;
            //     case categoryParttimeCode:
            //         tableId = "table-02-content";
            //         break;
            //     default:
            //         break;
            // }
            
            // 画面更新
            openMsgDialog("msg-dialog", result.message, "blue");
            await execUpdate();
            // 追加・変更行に移動
            const tableId = getTableIdByCategory(formdata.category);
            scrollIntoTableList(tableId, result.id);
        // } else {
        //     openMsgDialog("msg-dialog", result.message, "red");
        }
        // ダイアログを閉じる
        closeFormDialog('form-dialog-01');
    }
}


function getTableIdByCategory(category) {
    return Object.values(MODE_CONFIG)
        .find(c => c.category === category)
        ?.tableId ?? null;
}

// 入力チェック
function formDataCheck(area) {
    let msg = "";
    // 基本給が入力されていないとFalseを返す
    const salary = area.querySelector('input[name="base-salary"]');
    if (salary != null && salary.value == "") msg += '\n基本給または時給が入力されていません';
    // 交通費が入力されていないとFalseを返す
    const trans = area.querySelector('input[name="trans-cost"]');
    if (trans != null && trans.value == "") msg += '\n交通費が入力されていません';
    // 始業時間が入力されていないとFalseを返す
    const start = area.querySelector('input[name="phone-number"]');
    if (start != null && start.value == "") msg += '\n始業時間が入力されていません';
    // 終業時間が入力されていないとFalseを返す
    const end = area.querySelector('input[name="postal-code"]');
    if (end != null && end.value == "") msg += '\n終業時間が入力されていません';
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
    // let result;
    // switch (tab) {
    //     case "01":
    //         result = await deleteTablelist('table-01-content', '/api/working_conditions/delete');
    //         break;
    //     case "02":
    //         result = await deleteTablelist('table-02-content', '/api/working_conditions/delete');
    //         break;
    //     default:
    //         break;
    // }

    const config = MODE_CONFIG[tab];
    const result = await deleteTablelist(config.tableId, '/api/working_conditions/delete');

    if (result?.ok) {                
        await execUpdate();
        openMsgDialog("msg-dialog", result.message, "blue");
    // } else {
    //     openMsgDialog("msg-dialog", result.message, "red");
    }
}


/******************************************************************************************************* ダウンロード */

async function execDownloadCsv(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];
    await downloadCsv(config.tableId, '/api/working_conditions/download/csv');
    // let result;
    // switch (tab) {
    //     case "01":
    //         result = await downloadCsv('table-01-content', '/api/working_conditions/download/csv');
    //         break;
    //     case "02":
    //         result = await downloadCsv('table-02-content', '/api/working_conditions/download/csv');
    //         break;
    //     default:
    //         break;
    // }
}

/******************************************************************************************************* 画面更新 */

async function execUpdate() {
    const resultResponse = await fetch('/working_conditions/get/list');
    origin = await resultResponse.json();

    // 画面更新
    // const list01 = origin.filter(function(value) { return value.category == categoryEmployeeCode });
    // await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list01);
    // const list02 = origin.filter(function(value) { return value.category == categoryParttimeCode });
    // await updateTableDisplay("table-02-content", "footer-02", "search-box-02", list02);

    for (const mode of Object.values(MODE_CONFIG)) {
        const list = origin.filter(v => v.category === mode.category);
        await updateTableDisplay(mode.tableId, mode.footerId, mode.searchId, list, createTableContent);
    }
}

/******************************************************************************************************* 画面更新 */

// // テーブルリスト画面を更新する
// async function updateTableDisplay(tableId, footerId, searchId, list) {
//     // フィルター処理
//     const result = filterDisplay(searchId, list);
//     // リスト画面を初期化
//     deleteElements(tableId);
//     // リスト作成
//     createTableContent(tableId, result);
//     // フッター作成
//     createTableFooter(footerId, list);
//     // チェックボタン押下時の処理を登録する
//     registCheckButtonClicked(tableId);
//     // すべて選択ボタンをオフにする
//     turnOffAllCheckBtn(tableId);
//     // テーブルのソートをリセットする
//     resetSortable(tableId);
//     // スクロール時のページトップボタン処理を登録する
//     setPageTopButton(tableId);
//     // テーブルにスクロールバーが表示されたときの処理を登録する
//     document.querySelectorAll('.scroll-area').forEach(el => {
//         toggleScrollbar(el);
//     });
// }

async function execFilterDisplay(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    const config = MODE_CONFIG[tab];
    const list = origin.filter(function(value) { return value.category == config.category });
    await updateTableDisplay(config.tableId, config.footerId, config.searchId, list, createTableContent);
    // let list = [];
    // switch (tab) {
    //     case "01":
    //         list = origin.filter(function(value) { return value.category == categoryEmployeeCode });
    //         await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list);
    //         break;
    //     case "02":
    //         list = origin.filter(function(value) { return value.category == categoryParttimeCode });
    //         await updateTableDisplay("table-02-content", "footer-02", "search-box-02", list);
    //         break;
    //     default:
    //         break;
    // }
}

// データを category ごとに一度だけ分ける
function groupByCategory(list) {
  return list.reduce((map, v) => {
    (map[v.category] ??= []).push(v);
    return map;
  }, {});
}

// テーブル初期表示
async function renderAllTables(origin) {
    const grouped = groupByCategory(origin);

    for (const mode of Object.values(MODE_CONFIG)) {
        const list = grouped[mode.category] ?? [];
        
        makeSortable(mode.tableId);
        await updateTableDisplay(mode.tableId, mode.footerId, mode.searchId, list, createTableContent);

        // // テーブル共通初期化
        // makeSortable(mode.tableId);
        // setPageTopButton(mode.tableId);
    }
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async  () => {
    // スピナー表示
    startProcessing();

    // // 検索ボックス入力時の処理
    // document.getElementById('search-box-01').addEventListener('search', async function(e) {
    //     await execFilterDisplay(e.currentTarget);
    // }, false);
    // document.getElementById('search-box-02').addEventListener('search', async function(e) {
    //     await execFilterDisplay(e.currentTarget);
    // }, false);

    // エンターフォーカス処理をイベントリスナーに登録する
    setEnterFocus("form-01");

    registerSearchEvents();
    await renderAllTables(origin);
    // // 画面更新
    // const list01 = origin.filter(function(value) { return value.category == categoryEmployeeCode });
    // await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list01);
    // // テーブルをソート可能にする
    // makeSortable("table-01-content");
    // // スクロール時のページトップボタン処理を登録する
    // setPageTopButton("table-01-content");
    // const list02 = origin.filter(function(value) { return value.category == categoryParttimeCode });
    // await updateTableDisplay("table-02-content", "footer-02", "search-box-02", list02);
    // // テーブルをソート可能にする
    // makeSortable("table-02-content");
    // // スクロール時のページトップボタン処理を登録する
    // setPageTopButton("table-02-content");

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })

    // スピナー消去
    processingEnd();
});