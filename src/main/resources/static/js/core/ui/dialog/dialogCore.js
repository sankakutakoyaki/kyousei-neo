"use strict"

import { resetEnterFocus } from "../../form/components/enterfocus.js";

/**
 * フォームダイアログを開く
 * @param {*} dialogId 
 * @returns 
 */
export function openFormDialog(options = {}) {

    const {
        dialogId,
        onSubmit = async () => true,
        onClose = async () => closeFormDialog(dialogId),
        onReset
    } = options;

    const form = document.getElementById(dialogId);
    if (!form) return;

    form.onReset = onReset;

    const area = document.getElementById('form-dialog-area');
    if (!area) return;

    area.classList.add('dialog');
    form.classList.remove('none');

    const el = area.querySelector('[autofocus]');
    if (el) el.focus();

    setFooterButtons(form, true, onSubmit, onClose);

    setInertState(true);
    resetEnterFocus();
}

/**
 * フォームダイアログを閉じる
 * @param {ダイアログのID名} dialogId 
 * @param {イベント} e 
 */
export function closeFormDialog(dialogId, e) {
    if (e != null) {
        e.preventDefault();
    }

    // フォーム画面エリアからダイアログクラスを取り除く
    const area = document.getElementById('form-dialog-area');
    if (area == null) return;
    area.classList.remove('dialog');

    // フォーム画面に[none]クラスを付与して画面を消去する
    const form = document.getElementById(dialogId);
    if (form == null) return;
    form.classList.add('none');

    // 要素のクリック禁止を解除する
    setInertState(false);

    // 閉じる時にデータを消去する
    if(typeof form.onReset === "function"){
        form.onReset();
    }
}

/**
 * メッセージダイアログ表示
 * @param {本文} msg 
 * @param {タイトル} title 
 * @param {ヘッダーの色} headerColor 
 */
export function openMsgDialog(options = {}) {
    const {
        message,
        color,
        onClose = () => closeMsgDialog(),
    } = options;

    openMsg({
        dialogId: "msg-dialog",
        message,
        color,
        submitCallback: null,
        closeCallback: onClose,
        isConfirm: false,
    });
}
/**
 * コンフィルムダイアログ表示
 * @param {*} msg 
 * @param {*} color 
 * @param {*} submitFunc 
 * @param {*} closeFunc 
 */
export function openConfirmDialog(options = {}) {
    const {
        message,
        color,
        onSubmit,
        onClose = () => closeMsgDialog()
    } = options;

    if (!onSubmit) return;

    openMsg({
        dialogId: "msg-dialog",
        message,
        color,
        submitCallback: onSubmit,
        closeCallback: onClose,
        isConfirm: true
    });
}

/**
 * 
 * @param {*} dialogId 
 * @param {*} msg 
 * @param {*} color 
 * @param {*} closeFunc 
 * @param {*} okFunc 
 * @param {*} isConfirm 
 * @returns 
 */
function openMsg(options = {}) {
    const {
        dialogId,
        message,
        color,
        submitCallback,
        closeCallback,
        isConfirm = false
    } = options;

    const dialog = document.getElementById(dialogId);
    if (!dialog) return;

    setInertState(true);

    const parent = document.getElementById('msg-dialog-area');
    if (!parent) return;

    parent.classList.add('dialog');

    const header = parent.querySelector('.dialog-header');
    header.className = "dialog-header " + color;

    const content = parent.querySelector('.msg-dialog-content');
    content.textContent = message;

    dialog.classList.remove('none');

    setFooterButtons(dialog, isConfirm, submitCallback, closeCallback);
}

/**
 * メッセージダイアログを閉じる
 * @param {ダイアログのID名} dialogId 
 * @param {イベント} e 
 */
export function closeMsgDialog(dialogId = "msg-dialog") {

    const parent = document.getElementById('msg-dialog-area');
    if (parent){
        parent.classList.remove('dialog');
    }

    const dialog = document.getElementById(dialogId);
    if (dialog){
        dialog.classList.add('none');
    }
    setInertState(false);
}

/**
 * ボタンを使用不可にする
 * @param {*} state 
 */
export function setInertState(state) {
    const header = document.querySelector('.normal-header');
    if (header != null) header.inert = state;

    const aside = document.querySelector('.normal-sidebar');
    if (aside != null) aside.inert = state;

    const tab = document.querySelector('.tab-area');
    if (tab != null) tab.inert = state;

    const table = document.querySelector('.table-area');
    if (table != null) table.inert = state;
}

/**
 * ボタン連打防止
 * @param {*} button 
 * @param {*} asyncFunc 
 * @returns 
 */
export async function withInertButton(button, asyncFunc) {
    button.inert = true;
    try {
        const ok = await asyncFunc();
        return ok;
    } finally {
        button.inert = false;
    }
}

function setFooterButtons(dialog, isConfirm, submitCallback, closeCallback) {

    const submitBtn = dialog.querySelector('[name="submitBtn"]');
    const cancelBtn = dialog.querySelector('[name="cancelBtn"]');
    const closeBtn = dialog.querySelector('[name="closeBtn"]');

    const onSubmit = async () => {
        if(submitCallback){
            await submitCallback(dialog);
        }
    };

    const onClose = async () => {
        if(closeCallback){
            await closeCallback(dialog);
        }
    };

    if (isConfirm) {
        submitBtn.onclick = onSubmit;
        cancelBtn.onclick = onClose;
        cancelBtn.style.display = "";
        cancelBtn.textContent = "いいえ";
    } else {
        submitBtn.onclick = onClose;
        cancelBtn.style.display = "none";
    }

    closeBtn.onclick = onClose;

    submitBtn.dataset.scope = "form";
    submitBtn.textContent = "はい";
    submitBtn.focus();
}