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
                execEdit(item.qualificationsId);
            }
        });

        createTableRow(row, item);
    });
}

// テーブル行を作成する
function createTableRow(newRow, item) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    // // ID
    // newRow.insertAdjacentHTML('beforeend', '<td name="id-cell" class="link-cell" onclick="execEdit(' + item.qualificationsId + ', this)">' + String(item.qualificationsId).padStart(4, '0') + '</td>');
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
    const cfg = MODE_CONFIG[tab];

    const resultResponse = await fetch(cfg.getUrl);
    const result = await resultResponse.json();

    if (resultResponse.ok){
        origin = result.data;
        // 画面更新
        await execFilterDisplay(tab);        
    }
}

// コードでフィルターする
function codeFilter(codeValue, keyName, list) {
    if (!codeValue) return list;
    
    const num = Number(codeValue);
    if (Number.isNaN(num)) return list;

    return list.filter(v => v[keyName] === num);
}

// 資格情報フィルター
function qualificationsFilter(filterValue, list) {
    if (filterValue === '0') return list;

    const num = Number(filterValue);
    return list.filter(v => v.qualificationMasterId === num);
}

// 一括フィルター
async function execFilterDisplay(tab) {
    const cfg = MODE_CONFIG[tab];

    const codeValue = document.getElementById(cfg.codeId).value;
    const filterValue = document.getElementById(cfg.filterId).value;

    const list = qualificationsFilter(
        filterValue,
        codeFilter(codeValue, cfg.ownerKeyName, origin)
    );

    await updateTableDisplay(cfg.tableId, cfg.footerId, cfg.searchId, list, createTableContent);
}

// companyCombo変更時の処理
function refleshCode() {
    const code01 = document.getElementById("code01");
    const name01 = document.getElementById("name01");
    code01.value = code01.value === name01.value ? code01.value: Number(name01.value) === 0 ? "": name01.value ;
}

/******************************************************************************************************* 入力画面 */

// 登録画面を開く
async function execEdit(id) {
    const panel = document.querySelector('.tab-panel.is-show');
    if (!panel) return;

    const tab = panel.dataset.panel
    const cfg = MODE_CONFIG[tab];

    tempEntity = {};

    const form = document.getElementById(cfg.formId);
    if (!form) return;

    const number = form.querySelector('[name="number"]');
    if (!number) return;

    if (id > 0) {
        const result = await searchFetch(cfg.getUrl + "/id", JSON.stringify({id:id}), token);
        if (result.ok) tempEntity = result.data;
    } else {
        tempEntity = structuredClone(formEntity);

        const elm = document.getElementById(cfg.targetId);
        const fil = document.getElementById("filter" + tab);
        const text = cfg.getValue(elm);
        const text2 = fil.options[fil.selectedIndex].text;
        if (text ==="" || text2 === "すべて") {
            openMsgDialog("msg-dialog", cfg.message, "red");
            return;
        }
        tempEntity[cfg.ownerKeyName] = elm.value;
        tempEntity.ownerName = text;
        tempEntity.qualificationMasterId = fil.value;
        tempEntity.qualificationName = text2;
    }

    // フォーム画面を開く
    applyFormConfig(form, tempEntity, FORM_CONFIG);

    // 入力フォームダイアログを開く
    openFormDialog(cfg.dialogId);
    setEnterFocus(cfg.formId);
    number.focus();
}    

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave() {
    const panel = document.querySelector('.tab-panel.is-show');
    const tab = panel?.dataset?.panel;
    if (!tab) return;

    const cfg = MODE_CONFIG[tab];

    const form = document.getElementById(cfg.formId);
    if (!form) return;

    const number = form.querySelector('[name="number"]');
    if (!number) return;

    if (!validateByConfig(form, ERROR_CONFIG)) {
        return;
    }

    const data = buildEntityFromElement(form, tempEntity, SAVE_CONFIG);console.log(data)
    // const result = await updateFetch("/api/qualifications/save", JSON.stringify(data), token);

    // if (result.ok && result.data !== null) {
    //     closeFormDialog(cfg.dialogId);

    //     await execUpdate(tab);
    //     scrollIntoTableList(cfg.tableId, result.data);
        
    //     openMsgDialog("msg-dialog", result.message, "blue");
    // } else {
    //     // number.value = null;
    //     setFocusElement("msg-dialog", number);
    // }

    // resetFormInput(tab);
}

// 入力フォームの内容をリセットする
function resetFormInput(tab) {
    const cfg = MODE_CONFIG[tab];
    resetFormInputValue(cfg.dialogId);
}

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
        createComboBoxWithTop(se, cfg.comboList, "すべて");
        se.addEventListener('change', async () => {
            await execFilterDisplay(tab);
        });

        await execUpdate(tab);
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
    };

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