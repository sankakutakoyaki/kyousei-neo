"use strict"

/**
 * フォームダイアログを開く
 * @param {*} dialogId 
 * @returns 
 */
export function openFormDialog(dialogId) {
    const form = dialogId instanceof HTMLElement ? dialogId: document.getElementById(dialogId);
    if (form == null) return;

    // フォームエリアにダイアログクラスを付与する
    const area = document.getElementById('form-dialog-area');
    if (area == null) return;
    area.classList.add('dialog');

    // フォーム画面から[none]クラスを取り除く
    // const form = document.getElementById(dialogId);
    // if (form == null) return;
    form.classList.remove('none');

    const el = area.querySelector('[autofocus]');
    if (el) el.focus();
    
    setInertState(true);
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
}

/**
 * メッセージダイアログ表示
 * @param {本文} msg 
 * @param {タイトル} title 
 * @param {ヘッダーの色} headerColor 
 */
export function openMsgDialog(msg, color, closeFunc = () => closeMsgDialog("msg-dialog")) {
    openMsg('msg-dialog', msg, color, closeFunc, null, false);
}

/**
 * コンフィルムダイアログ表示
 * @param {*} msg 
 * @param {*} color 
 * @param {*} okFunc 
 * @param {*} closeFunc 
 */
export function openConfilmDialog(msg, color, okFunc, closeFunc = () => closeMsgDialog("msg-dialog")) {
    if (!okFunc) return;

    openMsg('msg-dialog', msg, color, closeFunc, okFunc, true);
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
function openMsg(dialogId, msg, color, closeFunc, okFunc, isConfirm = true) {

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

    const okBtn = dialog.querySelector('#okBtn');
    const cancelBtn = dialog.querySelector('#cancelBtn');

    // イベント上書き（重要）
    okBtn.onclick = null;
    cancelBtn.onclick = null;

    if (isConfirm) {
        okBtn.onclick = okFunc;
        cancelBtn.onclick = closeFunc;

        cancelBtn.style.display = "";
        cancelBtn.textContent = "いいえ";
    } else {
        okBtn.onclick = closeFunc;
        cancelBtn.style.display = "none";
    }

    okBtn.textContent = "はい";
    okBtn.focus();

    dialog.classList.remove('none');
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