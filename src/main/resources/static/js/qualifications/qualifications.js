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

/******************************************************************************************************* 画面更新 */

async function execUpdate(tab) {
    // const result = await getQualifications(tab);
    const cfg = MODE_CONFIG[tab];
    const result = await fetch(cfg.getUrl);

    if (result.ok){
        origin = result.data;
        // 画面更新
        await execFilterDisplay(tab);        
    }
}

// コードでフィルターする
async function codeFilter(codeId, list, keyName) {
    const cfg = MODE_CONFIG["02"];
    const code = document.getElementById(codeId);
    if (code.value == "") return list;
    if (isNaN(Number(code.value))) return list;

    return list.filter(value => value[keyName] === Number(code.value));
}

// 資格情報フィルター
function qualificationFilter(filterId, list) {
    const combo = document.getElementById(filterId);
    let searchList;
    if (combo.value === "0") {
        // 0 の場合は全件表示
        searchList = list;
    } else {
        searchList = list.filter(value => value.qualificationMasterId === Number(combo.value));
    }
    return searchList;
}

// 一括フィルター
async function execFilterDisplay(tab) {
    const cfg = MODE_CONFIG[tab];
    let list = structuredClone(origin);
    list = codeFilter(cfg.codeId, cfg.keyName, list);
    list = filterQualificationCombo(cfg.filterId, list);
    await updateTableDisplay(cfg.tableId, cfg.footerId, cfg.searchId, list, createTableContent);
}

// companyCombo変更時の処理
function refleshCode() {
    const code01 = document.getElementById("code01");
    const name01 = document.getElementById("name01");
    code01.value = code01.value === name01.value ? code01.value: Number(name01.value) === 0 ? "": name01.value ;
}


/******************************************************************************************************* 保存 */


/******************************************************************************************************* ダウンロード */


/******************************************************************************************************* アップロード */


/******************************************************************************************************* 削除 */


/******************************************************************************************************* 取得 */


/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {
    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        await execFilterDisplay("01");
    }, false);

    for (const tab of Object.keys(MODE_CONFIG)) {
        const cfg = MODE_CONFIG[tab];

        const se = document.getElementById(cfg.filterId);
        if (!se) return;
        createComboBoxWithTop(se, comboList, "すべて");
        se.addEventListener('change', async () => {
            await execFilterDisplay(tab);
        });
    };

    initCompanyInputs();

    // 担当者コード入力時の処理
    const cfg02 = MODE_CONFIG["02"];
    const el = document.getElementById(cfg02.codeId);
    if (el) {
        el.addEventListener('blur', async () => {
            await changeCodeToName(cfg02, "/api/employee/get/id");
            await execFilterDisplay("02");
        });
    }

    // エンターフォーカス処理をイベントリスナーに登録する
    setEnterFocus("form-01");
    setEnterFocus("code-area");

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })
});