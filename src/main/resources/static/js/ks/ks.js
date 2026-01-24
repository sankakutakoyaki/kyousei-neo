"use strict"

/******************************************************************************************************* 入力画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const tbl = document.getElementById(tableId);
    list.forEach(function (item) {
        let newRow = tbl.insertRow();
        // ID（Post送信用）
        newRow.setAttribute('name', 'data-row');
        newRow.setAttribute('data-id', item.ks_sales_id);
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

        createTable01Row(newRow, item);
    });
}

/******************************************************************************************************* テーブル行作成 */

// テーブル行を作成する
function createTable01Row(newRow, item) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    // 配送センター
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.store_name ?? "-----") + '</span></td>');
    // 会社名
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.staff_company ?? "-----") + '</span></td>');
    // 担当者名
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.staff_name_1 ?? "-----") + '</span></td>');
    // 金額
    newRow.insertAdjacentHTML('beforeend', '<td class="text-right"><span>' + (item.amount ?? 0).toLocaleString('ja-JP') + '</span></td>');
    // 比率
    newRow.insertAdjacentHTML('beforeend', '<td class="text-right"><span>' + (item.rate ?? "0") + '%</span></td>');
}

/******************************************************************************************************* ダウンロード */

async function execDownloadCsv(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    let result;
    switch (tab) {
        case "01":
            result = await downloadCsv('table-01-content', '/api/ks/sales/download/csv');
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

async function execDateSearch(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    switch (tab) {
        case "01":
            // リスト取得
            origin01 = await getKsSalesBetween("start-date01", "end-date01", "/api/ks/sales/get/between/staff");
            storeComboList01 = createStoreCombo(origin01);
            createComboBoxValueString(storeCombo01, storeComboList01);
            const list = setRate(origin01);
            // 画面更新
            await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list);

            break;
        default:
            break;
    }
}

// 売上比率を設定
function setRate(list) {
    const totalAmount = list.reduce((sum, item) => sum + (item.amount ?? 0), 0);

    // rate を計算して各オブジェクトに追加
    return list.map(item => ({
        ...item,
        rate: totalAmount > 0 ? ((item.amount ?? 0) / totalAmount * 100).toFixed(1) : '0.0'
    }));
}

// 取得したリストを店舗でフィルターかける
function createStoreCombo(list) {
    // Mapでstore_nameをキーにして最後の値を上書き
    const storeMap = new Map();
        list.forEach(item => {
        storeMap.set(item.store_name, item.store_name); // 値はstore_nameだけ
    });

    // 配列化して昇順ソート
    const uniqueStores = Array.from(storeMap.values())
        .sort((a, b) => a.localeCompare(b))  // 名前で昇順
        .map((name, index) => ({
            number: index + 1,   // 1から順番にカウント
            text: name
    }));
    uniqueStores.unshift({ number: '', text: '' });

    return uniqueStores;
}

// 一覧表示用のリスト取得
async function getKsSalesBetween(startId, endId, url) {
    // // スピナー表示
    // startProcessing();
    const start = document.getElementById(startId).value;
    const end = document.getElementById(endId).value;
    // const data = "&start=" + encodeURIComponent(start) + "&end=" + encodeURIComponent(end);
    // const contentType = 'application/x-www-form-urlencoded';
    // List<OrderItem>を取得
    // const resultResponse = await searchFetch(url, JSON.stringify({start:start, end:end}), token);
    // // // スピナー消去
    // // processingEnd();
    // return await resultResponse.json();
    return await searchFetch(url, JSON.stringify({start:start, end:end}), token);
}

// 
async function execFilterDisplay(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    switch (tab) {
        case "01":
            const result = execFilterStore(tab);
            const list = setRate(result);
            await updateTableDisplay("table-01-content", "footer-01", "search-box-01", list);
            break;
        default:
            break;
    }
}

// 店舗でフィルターをかける
function execFilterStore(tab) {
    switch (tab) {
        case "01":
            const selectText = storeCombo01.options[storeCombo01.selectedIndex].text;
            if (selectText == "") {
                return origin01;
            } else {
                return origin01.filter(value => { return value.store_name == selectText });
            }
        default:
            break;
    }
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {
    hamburgerItemAddSelectClass('.header-title', 'list');
    hamburgerItemAddSelectClass('.normal-sidebar', 'ks');
    // スピナー表示
    startProcessing();

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        await execFilterDisplay(e.currentTarget);
    }, false);

    // センターボックス選択時の処理
    storeCombo01.addEventListener('change', async function(e) {
        await execFilterDisplay(e.currentTarget);
    }, false);

    // センターフィルターコンボボックス
    storeComboList01 = createStoreCombo(origin01);
    createComboBoxValueString(storeCombo01, storeComboList01);

    execSpecifyPeriod("this-month", 'start-date01', 'end-date01');

    // 画面更新
    await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin01);
    // テーブルをソート可能にする
    makeSortable("table-01-content");

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })

    // スピナー消去
    processingEnd();
});