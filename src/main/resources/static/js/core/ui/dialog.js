"use strict"

import { resetEnterFocus } from "./enterfocus.js";

/**
 * フォームダイアログを開く
 * @param {*} dialogId 
 * @returns 
 */
export function openFormDialog(dialogId, options = {}) {

    const form = document.getElementById(dialogId);
    if (!form) return;

    form.onReset = options.onReset;

    const area = document.getElementById('form-dialog-area');
    if (!area) return;

    area.classList.add('dialog');
    form.classList.remove('none');

    const el = area.querySelector('[autofocus]');
    if (el) el.focus();

    const onSubmit = options.onSubmit ?? (async () => true);
    const onClose  = options.onClose  ?? (async () => closeFormDialog(dialogId));

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
export function openMsgDialog(msg, color, closeCallback = () => closeMsgDialog("msg-dialog")) {
    openMsg('msg-dialog', msg, color, null, closeCallback, false);
}

/**
 * コンフィルムダイアログ表示
 * @param {*} msg 
 * @param {*} color 
 * @param {*} okFunc 
 * @param {*} closeFunc 
 */
export function openConfilmDialog(msg, color, submitCallback, closeCallback = () => closeMsgDialog("msg-dialog")) {
    if (!submitCallback) return;

    openMsg('msg-dialog', msg, color, submitCallback, closeCallback, true);
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
function openMsg(dialogId, msg, color, submitCallback, closeCallback, isConfirm = false) {

    const dialog = dialogId instanceof HTMLElement ? dialogId: document.getElementById(dialogId);
    if (!dialog) return;

    setInertState(true);

    // dialog.classList.remove('none');

    const parent = document.getElementById('msg-dialog-area');
    parent.classList.add('dialog');

    const header = parent.querySelector('.dialog-header');
    header.className = "dialog-header " + color;

    const content = parent.querySelector('.msg-dialog-content');
    content.textContent = msg;

    dialog.classList.remove('none');

    setFooterButtons(dialog, isConfirm, submitCallback, closeCallback);

    // const submitBtn = dialog.querySelector('[name="submitBtn"]');
    // const cancelBtn = dialog.querySelector('[name="cancelBtn"]');

    // // イベント上書き（重要）
    // submitBtn.onclick = null;
    // cancelBtn.onclick = null;

    // if (isConfirm) {
    //     submitBtn.onclick = okFunc;
    //     cancelBtn.onclick = closeFunc;

    //     cancelBtn.style.display = "";
    //     cancelBtn.textContent = "いいえ";
    // } else {
    //     submitBtn.onclick = closeFunc;
    //     cancelBtn.style.display = "none";
    // }

    // submitBtn.textContent = "はい";
    // submitBtn.focus();

    // dialog.classList.remove('none');
}

/**
 * メッセージダイアログを閉じる
 * @param {ダイアログのID名} dialogId 
 * @param {イベント} e 
 */
export function closeMsgDialog(dialogId = "msg-dialog") {
    // メッセージ画面エリアからダイアログクラスを取り除く
    const mArea = document.getElementById('msg-dialog-area');
    if (mArea == null) return;
    mArea.classList.remove('dialog');

    // メッセージ画面に[none]クラスを付与して画面を消去する
    const dialog = document.getElementById(dialogId);
    if (dialog == null) return;
    dialog.classList.add('none');

    // 要素のクリック禁止を解除する
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
            await submitCallback(dialog); // ←ここ重要
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

    submitBtn.textContent = "はい";
    submitBtn.focus();
}
// function setFooterButtons(form, show, okCallback, cancelCallback){

//     const submitBtn = form.querySelector("[data-role='ok']");
//     const cancelBtn = form.querySelector("[data-role='cancel']");

//     if(submitBtn){
//         submitBtn.onclick = async () => {

//             // OK処理
//             if(okCallback){
//                 await okCallback(form);
//             }

//             // 閉じる
//             if(cancelCallback){
//                 cancelCallback();
//             }
//         };
//     }

//     if(cancelBtn){
//         cancelBtn.onclick = cancelCallback;
//     }
// }