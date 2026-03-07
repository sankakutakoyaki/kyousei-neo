"use strict"

import { FileManager } from "/js/file/fileManager.js";
import { FileStore } from "/js/file/fileStore.js";
import { FileUploader } from "/js/file/fileUploader.js";
import { CONFIG } from "/js/qualifications/qualificationsConfig.js";

let origin = [];
let tempEntity = {};
const fileManager = {};

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
    // 名前
    newRow.insertAdjacentHTML('beforeend', '<td><span class="kana">' + item.ownerNameKana + '</span><br><span>' + item.ownerName + '</span></td>');
    // 資格名
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.qualificationName ?? "登録なし") + '</span></td>');
    // 番号
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.number ?? "-----") + '</span></td>');
    // 有効期限
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.expiryDate ?? "") + '</span></td>');
    // ステータス
    newRow.insertAdjacentHTML('beforeend', '<td name="enabled-cell"><span>' + (item.status == "取得済み" && item.is_enabled == 0 ? "期限切れ": "") + '</span></td>');
}

/******************************************************************************************************* 画面更新 */

export async function execUpdate(tab) {
    const cfg = CONFIG.MODE[tab];

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
    const cfg = CONFIG.MODE[tab];

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
export function refleshCode(codeId, nameId) {
    const code = document.getElementById(codeId);
    const name = document.getElementById(nameId);
    code.value = code.value === name.value ? code.value: Number(name.value) === 0 ? "": name.value ;
    updateFilter03State();
}

/******************************************************************************************************* 入力画面 */

// 登録画面を開く
async function execEdit(id) {
    const panel = document.querySelector('.tab-panel.is-show');
    if (!panel) return;

    const tab = panel.dataset.panel
    const cfg = CONFIG.MODE[tab];

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
    applyFormConfig(form, tempEntity, CONFIG.FORM);

    // 入力フォームダイアログを開く
    openFormDialog(cfg.dialogId);
    setEnterFocus(cfg.formId);
    number.focus();
}    

/******************************************************************************************************* 保存 */

// 保存処理
export async function execSave() {
    const panel = document.querySelector('.tab-panel.is-show');
    const tab = panel?.dataset?.panel;
    if (!tab) return;

    const cfg = CONFIG.MODE[tab];

    const form = document.getElementById(cfg.formId);
    if (!form) return;

    const number = form.querySelector('[name="number"]');
    if (!number) return;

    if (!validateByConfig(form, CONFIG.ERROR)) {
        return;
    }

    const data = buildEntityFromElement(form, tempEntity, CONFIG.SAVE);
}

// 入力フォームの内容をリセットする
export function resetFormInput(tab) {
    const cfg = CONFIG.MODE[tab];
    resetFormInputValue(cfg.dialogId);
}

/******************************************************************************************************* 表示 */

export async function execOpen() {
    const panel = document.querySelector("[data-panel='03']");
    const chk = panel.querySelector('input[type="checkbox"]:checked');
    const cate = chk.dataset.cate;
    const cfg = CONFIG.FILE[cate];

    const qualifications = await getQualificationList(cfg);
    const area = document.getElementById(cfg.groupArea);
    const list = qualifications.data.map(v => ({number: v.qualificationsId, text: v.number, data: v.files[0]?.groupId}));
    // createComboBox(area, list);

    const list2 = qualifications.data
            .filter(v => { return v.qualificationsId === Number(cfg.parentValue()) })    
            .map(v => ({
                groupId: v.files[0]?.groupId,
                groupName: v.qualificationName,
                files: v.files
            }));

    const fileManager = new FileManager(cfg);
    FileStore.set(list2);
    fileManager.render();

    createComboBox(area, list);
}

export function refreshFileList() {
    const panel = document.querySelector("[data-panel='03']");
    const chk = panel.querySelector('input[type="checkbox"]:checked');
    const cate = chk.dataset.cate;
    const cfg = CONFIG.FILE[cate];
    const fileManager = new FileManager(cfg);
    fileManager.render();
}

// export async function execOpen(){

//     const panel = document.querySelector("[data-panel='03']");
//     const chk = panel.querySelector('input[type="checkbox"]:checked');
//     const cate = chk.dataset.cate;
//     const cfg = CONFIG.FILE[cate];

//     const qualifications = await getQualificationList(cfg);

//     initCombo(cfg, qualifications);

//     updateFiles(cfg, qualifications);

// }
// function initCombo(cfg, qualifications){

//     const area = document.getElementById(cfg.groupArea);

//     if(area.dataset.comboInit) return;

//     const list = qualifications.data.map(v => ({
//         number: v.qualificationsId,
//         text: v.number,
//         data: v.files[0]?.groupId
//     }));

//     createComboBox(area, list);

//     area.dataset.comboInit = "1";

// }
// export function updateFiles(cfg, qualifications){

//     const list2 = qualifications.data
//         .filter(v =>
//             v.qualificationsId === Number(cfg.parentValue())
//         )
//         .map(v => ({
//             groupId: v.files[0]?.groupId,
//             groupName: v.qualificationName,
//             files: v.files
//         }));

//     const fileManager = new FileManager(cfg);

//     FileStore.set(list2);

//     fileManager.render();

// }






// async function createGroupComboBox(cfg, parentValue, codeValue) {
//     const response = await fetch(
//         `${cfg.getParentUrl}/${parentValue}/${codeValue}`,
//         {
//             method: "POST",
//             headers: {
//                 "Content-Type": "application/json",
//                 "X-CSRF-TOKEN": token
//             }
//         }
//     );

//     const list = await response.json();
//     const comboList = list.data.map(g => ({
//         number: g.qualificationsId,
//         text: g.number
//     }));

//     const area = document.getElementById(cfg.groupArea);

//     if (area) { 
//         createComboBoxWithFunc(area, comboList, 
//             (value) => {
//                 selectedGroupId = value;
//                 highlightSelectedGroup();
//             }
//         );
//     }
// }

/******************************************************************************************************* アップロード */


/******************************************************************************************************* 削除 */


/******************************************************************************************************* 取得 */

async function getQualificationList(cfg) {

    const codeValue = cfg.codeValue();
    if (!codeValue || codeValue === "") return;

    const masterValue = cfg.masterValue();
    if (!masterValue || masterValue === "") return;

    const response = await fetch(`${cfg.getParentUrl}/${masterValue}/${codeValue}`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": token
            }
        }
    );

    return await response.json();
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {

    for (const tab of Object.keys(CONFIG.MODE)) {
        const cfg = CONFIG.MODE[tab];

        const fi = document.getElementById(cfg.filterId);
        if (fi) {
            createComboBoxWithTop(fi, cfg.comboList, cfg.comboListTop);
            fi.addEventListener('change', async () => {
                await cfg.codeChange();
            });
        }

        const se = document.getElementById(cfg.searchId);
        if (se)  {
            se.addEventListener('search', async () => {
                await execFilterDisplay(tab);
            }, false);
        }

        makeSortable(cfg.tableId);
    };

    setEnterFocus("form-01");
    initCompanyInputs(CONFIG.COMPANY);

    // 担当者コード入力時の処理
    for (const tab of Object.keys(CONFIG.ID)) {
        const cfg = CONFIG.ID[tab];

        const elm = document.getElementById(cfg.codeId);
        if (elm)  {
            elm.addEventListener('blur', async () => {
                await changeCodeToName(cfg, "/api/employee/get/id");
                updateFilter03State();
            });
        }

        setEnterFocus(cfg.area);
    }

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })

    const closeBtn = document.getElementById("viewer-close");
    if (closeBtn) {
        closeBtn.addEventListener("click", () =>
            closeFormDialog("form-fileviewer")
        );
    }

    document.querySelectorAll(".row-enable").forEach(cb => {
        cb.addEventListener("change", handleRowSelection);
    });

    ["code03", "code04"].forEach(id => {
        const el = document.getElementById(id);
        if (!el) return;

        ["input", "blur"].forEach(evt => {
            el.addEventListener(evt, updateFilter03State);
        });
    });

    // 初期状態も反映
    updateFilter03State();

    // setFileConfig('license');
});

/**
 * 行の選択を処理する
 * @param {*} e 
 */
function handleRowSelection(e) {

    const cate = e.target.dataset.cate;
    const cfg = CONFIG.CHK[cate];
    const filter = document.getElementById(cfg.filterId);
    if (!filter) return;

    createComboBoxWithTop(filter, cfg.comboList, "");

    const allCheckboxes = document.querySelectorAll(".row-enable");
    if (e.target.checked) {
        allCheckboxes.forEach(other => {
            if (other !== e.target) {
                other.checked = false;
                setCodeEnabled(other.closest(".flex-area"), false);
            }
        });
        setCodeEnabled(e.target.closest(".flex-area"), true);
    } else {
        setCodeEnabled(e.target.closest(".flex-area"), false);
    }

    setFileConfig(cate);
    resetEnterFocus();
    updateFilter03State();

}

// function setFileConfig(cate) {

//     if (!fileManager[cate]) {
//         fileManager[cate] = new FileManager(CONFIG.FILE[cate]);
//     }

//     const manager = fileManager[cate];

//     document
//         .getElementById("file-input")
//         .addEventListener("change", async (e)=>{

//             const file = e.target.files[0];
//             await manager.upload(file);

//     });
// }

function setFileConfig(cate){

    if (!fileManager[cate]) {
        fileManager[cate] = new FileManager(CONFIG.FILE[cate]);
    }

    const manager = new FileManager(CONFIG.FILE[cate]);

    // manager.load();

    const dropArea = document.getElementById("drop-area");

    if(dropArea){
        FileUploader.attachDrop(
            dropArea,
            manager
        );
    }

    const fileInput = document.getElementById("file-input");

    if(fileInput){
        FileUploader.attachInput(
            fileInput,
            manager
        );
    }

}
/**
 * チェックボックス変更時の処理
 * @param {*} area 
 * @param {*} enabled 
 */
function setCodeEnabled(area, enabled) {

    const codeInput = area.querySelector("input[name='code']");
    if (codeInput) {
        codeInput.disabled = !enabled;
        codeInput.value = null;
    }

    const nameInput = area.querySelector("input[name='name']");
    if (nameInput) {
        // nameInput.disabled = !enabled;
        nameInput.value = null;
    }

    const nameSelect = area.querySelector("select[name='name']");
    if (nameSelect) {
        nameSelect.disabled = !enabled;
        nameSelect.value = null;
    }

    if (enabled) {
        area.classList.add("select");
    } else {
        if (area.classList.contains("select")) area.classList.remove("select")
    }
}

/**
 * #code03 または #code04 のどちらかが未入力なら、#filter03 を操作不可（disabled）にする
 * @returns 
 */
function updateFilter03State() {

    const code03 = document.getElementById("code03")?.value.trim();
    const name03 = document.getElementById("name03")?.value;
    const code04 = document.getElementById("code04")?.value.trim();
    const name04 = document.getElementById("name04")?.value.trim();
    const filter03 = document.getElementById("filter03");

    if (!filter03) return;

    // どちらか未入力なら無効化
    filter03.disabled = !((code03 && name03 > 0) || (code04 && name04));

    if (filter03.disabled) {
        filter03.selectedIndex = 0;
    }
}