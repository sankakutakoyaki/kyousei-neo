"use strict"

/******************************************************************************************************* 入力画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const table = document.getElementById(tableId);

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "qualificationsId",
            onDoubleClick: (item) => {
                execEdit(item.employeeId, this);
            }
        });

        createTableRow(row, item);
    });
}

// テーブル行を作成する
function createTableRow(newRow, item, tab) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    // ID
    newRow.insertAdjacentHTML('beforeend', '<td name="id-cell" class="link-cell" onclick="execEdit(' + item.ownerId + ', this)">' + String(item.ownerId).padStart(4, '0') + '</td>');
    // 名前
    newRow.insertAdjacentHTML('beforeend', '<td name="name-cell"><span class="kana">' + item.ownerNameKana + '</span><br><span>' + item.ownerName + '</span></td>');
    // 資格名
    newRow.insertAdjacentHTML('beforeend', '<td name="qualification-name-cell"><span>' + (item.qualificationName ?? "登録なし") + '</span></td>');
    // 取得状況
    newRow.insertAdjacentHTML('beforeend', '<td name="status-cell" data-status="' + item.status + '"><span>' + item.status + '</span></td>');
    // 有効期限
    newRow.insertAdjacentHTML('beforeend', '<td name="expiry-cell"><span>' + (item.expiryDate ?? "") + '</span></td>');
    // 有効
    newRow.insertAdjacentHTML('beforeend', '<td name="enabled-cell"><span>' + (item.status == "取得済み" && item.is_enabled == 0 ? "期限切れ": "") + '</span></td>');
}

/******************************************************************************************************* 保存 */


/******************************************************************************************************* ダウンロード */


/******************************************************************************************************* アップロード */


/******************************************************************************************************* 削除 */


/******************************************************************************************************* 取得 */

// コードから担当者と登録資格を取得してリストに表示する
async function getQualifications(codeId, nameId) {
    // if ((e.key === "Enter") || e === "Enter") {
        const code = document.getElementById(codeId);
        const name = document.getElementById(nameId);

        if (code.value == "") return;
        if (isNaN(parseInt(code.value))) return;
        
        const panel = code.closest('.tab-panel');
        const tab = panel.dataset.panel;

        const data = JSON.stringify({primaryId:parseInt(code.value), secondaryId:parseInt(owner_category)});
        const result = await searchFetch("/api/qualifications/get/id", data, token);

        
}

/******************************************************************************************************* 画面更新 */

async function execUpdate(tab) {
    let getUrl = "";
    // リスト取得
    if (owner_category == 0) {
        // 従業員の場合
        getUrl = "/api/qualifications/get";
    } else {
        // 会社の場合
        getUrl = "/api/qualifications/get/license";
    }
    const resultResponse = await fetch(getUrl);
    const result = await resultResponse.json();
    
    if (resultResponse.ok){
        origin = result.data;
        // 画面更新
        await execFilterDisplay(tab);        
    }
}

// 資格情報フィルター
function filterQualificationCombo(list) {
    const qualification = document.getElementById('qualification-search');
    let searchList;
    if (qualification.value === "0") {
        // 0 の場合は全件表示
        searchList = list;
    } else {
        searchList = list.filter(value => value.qualificationMasterId === Number(qualification.value));
    }
    return searchList;
}

// 取得状況フィルター
function filterStatusCombo(list) {
    const status = document.getElementById('status-search');
    let searchList;
    if (status.value === "すべて") {
        // すべて の場合は全件表示
        searchList = list;
    } else {
        searchList = list.filter(value => value.status === status.value);
    }
    return searchList;
}

// 一括フィルター
async function execFilterDisplay(tab) {
    const cfg = MODE_CONFIG[tab];
    let list = structuredClone(origin);
    list = filterQualificationCombo(list);
    list = filterStatusCombo(list);
    await updateTableDisplay(cfg.tableId, cfg.footerId, cfg.searchId, list, createTableContent);
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {
    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        await execFilterDisplay("01");
    }, false);

    Object.values(CODE_CONFIG).forEach(cfg => {
        setEnterFocus(cfg.area);
        const el = document.getElementById(cfg.codeId);
        if (!el) return;

        el.addEventListener('blur', async () => {
            await getQualifications(cfg.codeId, cfg.nameId);
        });
    });

    // 検索用資格コンボボックスを作成する
    const qualificationSearchArea = document.getElementById('qualification-search');
    createComboBoxWithTop(qualificationSearchArea, qualificationComboList, "すべて");
    qualificationSearchArea.addEventListener('change', async () => {
        await execFilterDisplay("01");
    });

    // 検索用状況コンボボックスを作成する
    const statusSearchArea = document.getElementById('status-search');
    statusSearchArea.addEventListener('change', async () => {
        await execFilterDisplay("01");
    });

    // エンターフォーカス処理をイベントリスナーに登録する
    setEnterFocus("form-01");
    // 画面更新
    await execUpdate("01");

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })
});