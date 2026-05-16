"use strict"

/******************************************************************************************************* 入力画面 */

// 登録画面を開く
export async function execEdit(id) {
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