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
    // // 選択用チェックボックス
    // newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    // // ID
    // newRow.insertAdjacentHTML('beforeend', '<td name="id-cell" class="link-cell" onclick="execEdit(' + item.qualificationsId + ', this)">' + String(item.qualificationsId).padStart(4, '0') + '</td>');
    // 名前
    newRow.insertAdjacentHTML('beforeend', '<td><span class="kana">' + item.ownerNameKana + '</span><br><span>' + item.ownerName + '</span></td>');
    // 資格名
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.qualificationName ?? "登録なし") + '</span></td>');
    // 番号
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.number ?? "-----") + '</span></td>');
    // newRow.insertAdjacentHTML('beforeend', '<td data-status="' + item.status + '"><span>' + item.status + '</span></td>');
    // 有効期限
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.expiryDate ?? "") + '</span></td>');
    // ステータス
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

    const codeValue = document.getElementById(cfg.codeId)?.value;
    if (!codeValue) return;
    const filterValue = document.getElementById(cfg.filterId)?.value;
    if (!filterValue) return;

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

    const data = buildEntityFromElement(form, tempEntity, SAVE_CONFIG);
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

/******************************************************************************************************* 表示 */

async function execOpen(tab) {
    const cfg = FILE_CONFIG;
    const icfg = ID_CONFIG[tab];
console.log(icfg)
    if (!icfg.parentValue) return;
    // if (!icfg.groupValue) return;

    const response = await fetch(`${cfg.selectUrl}/${icfg.parentValue}`);
    const files = await response.json();

    if (files.length === 0) return;
    FileViewer.open(`${cfg.groupUrl}/$${files[0].groupId}`, i)
}

/******************************************************************************************************* アップロード */

function execUpload() {
    openFormDialog("form02");
}

/******************************************************************************************************* 削除 */


/******************************************************************************************************* 取得 */


/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {

    for (const tab of Object.keys(MODE_CONFIG)) {
        const cfg = MODE_CONFIG[tab];

        const fi = document.getElementById(cfg.filterId);
        if (fi) {
            createComboBoxWithTop(fi, cfg.comboList, "すべて");
            fi.addEventListener('change', async () => {
                await execFilterDisplay(tab);
            });
        }

        const se = document.getElementById(cfg.searchId);
        if (se)  {
            se.addEventListener('search', async () => {
                await execFilterDisplay(tab);
            }, false);
        }
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

    FileUI.init(FILE_CONFIG);

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })

    document.querySelectorAll(".row-enable").forEach(cb => {

        cb.addEventListener("change", e => {
            const allCheckboxes = document.querySelectorAll(".row-enable");
            if (e.target.checked) {
                // 他のチェックだけ外す
                allCheckboxes.forEach(other => {
                    if (other !== e.target) {
                        other.checked = false;
                        const otherArea = other.closest(".flex-area");
                        setCodeEnabled(otherArea, false);
                    }
                });
                const area = e.target.closest(".flex-area");
                setCodeEnabled(area, true);
            } else {
                // チェック外した場合
                const area = e.target.closest(".flex-area");
                setCodeEnabled(area, false);
            }
        });
    });
});

/**
 * チェックボックス変更時の処理
 * @param {*} area 
 * @param {*} enabled 
 */
function setCodeEnabled(area, enabled) {

    const codeInput = area.querySelector("input[name='code']");

    if (codeInput) {
        codeInput.readOnly = !enabled;
    }

    const nameInput = area.querySelector("input[name='name']");

    if (nameInput) {
        nameInput.readOnly = !enabled;
    }

    const nameSelect = area.querySelector("select[name='name']");

    if (nameSelect) {
        nameSelect.disabled = !enabled;
    }
}

// document.addEventListener("DOMContentLoaded", function () {

//     document.querySelectorAll(".row-enable").forEach(cb => {

//         cb.addEventListener("change", e => {

//             const allCheckboxes = document.querySelectorAll(".row-enable");

//             if (e.target.checked) {

//                 // 他のチェックだけ外す
//                 allCheckboxes.forEach(other => {

//                     if (other !== e.target) {
//                         other.checked = false;
//                         const otherArea = other.closest(".flex-area");
//                         setCodeEnabled(otherArea, false);
//                     }

//                 });

//                 console.log("checked");

//                 const area = e.target.closest(".flex-area");
//                 setCodeEnabled(area, true);

//             } else {
//                 // チェック外した場合
//                 const area = e.target.closest(".flex-area");
//                 setCodeEnabled(area, false);
//             }

//         });

//     });

// });