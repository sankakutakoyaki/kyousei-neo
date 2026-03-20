"use strict"

/**
 * メッセージダイアログ表示
 * @param {本文} msg 
 * @param {タイトル} title 
 * @param {ヘッダーの色} headerColor 
 */
export function openMsgDialog(msg, color, okFunc = null) {
    openMsg('msg-dialog-area', msg, color, closeMsgDialog("msg-dialog-area"), okFunc, false);
}

export function openConfilmDialog(msg, color, okFunc = null, closeFunc = closeMsgDialog("msg-dialog-area")) {
    openMsg('msg-dialog-area', msg, color, closeMsgDialog("msg-dialog-area"), okFunc, true);
}

function openMsg(dialogId, msg, color, closeFunc = null, okFunc = null, isConfilm = true) {
    const dialog = dialogId instanceof HTMLElement ? dialogId: document.getElementById(dialogId);
    if (!dialog) return;

    // ボタンを使用不可にする
    setInertState(true);

    // メッセージエリアにダイアログクラスを付与する
    const parent = document.getElementById('msg-dialog-area');
    parent.classList.add('dialog');

    // メッセージ画面から[none]クラスを取り除く
    dialog.classList.remove('none');

    // ヘッダーの色を指定する
    const header = parent.querySelector('.dialog-header');
    header.classList.add(color);

    // メッセージを代入する
    const content = parent.querySelector('.msg-dialog-content');
    content.textContent = msg;

    const okBtn = dialog.getElementById('okBtn');
    if (okBtn && isConfilm) {
        okBtn.addEventListener('click', okFunc);
        okBtn.textContent = "了解";
    }

    const closeBtns = dialog.querySelectorAll('closeBtn');
    if (!closeBtns) closeBtns.forEach(closeBtn => closeBtn.addEventListener('click', closeFunc));

    const cancelBtn = dialog.getElementById('cancelBtn');
    if (cancelBtn && isConfilm) {
        cancelBtn.textContent = "キャンセル";
    } else {
        cancelBtn.textContent = "了解";
    }

    // フォーカス指定のボタンにフォーカスを合わせる
    const focusBtn = dialog.querySelector('[name="focus-btn"]');
    if (focusBtn != null) focusBtn.focus();
}

/**
 * メッセージダイアログを閉じる
 * @param {ダイアログのID名} dialogId 
 * @param {イベント} e 
 */
export function closeMsgDialog(dialogId) {
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