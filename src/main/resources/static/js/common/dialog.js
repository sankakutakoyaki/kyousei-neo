"use strict"

/**
 * フォームダイアログを開く
 * @param {*} dialogId 
 * @returns 
 */
function openFormDialog(dialogId) {
    // フォームエリアにダイアログクラスを付与する
    const area = document.getElementById('form-dialog-area');
    if (area == null) return;
    area.classList.add('dialog');

    // フォーム画面から[none]クラスを取り除く
    const form = document.getElementById(dialogId);
    if (form == null) return;
    form.classList.remove('none');

    setInertState(true);
}

/**
 * フォームダイアログを閉じる
 * @param {ダイアログのID名} dialogId 
 * @param {イベント} e 
 */
function closeFormDialog(dialogId, e) {
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
 * @param {ボタンを含んだフッター} elmFooter 
 */
function openMsgDialog(dialogId, msg, color) {
    let dialog;
    if(dialogId instanceof HTMLElement) {
        dialog = dialogId;
    } else {
        dialog = document.getElementById(dialogId);
    }
    if (dialog == null) return;

    // ボタンを使用不可にする
    setInertState(true);

    // ダイアログが重なる場合は背景を透明にする
    const check = document.querySelector('.dialog');
    if (check != null) {
        check.classList.add('trans');
    }

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

    // OKボタンにフォーカスを合わせる
    const focusBtn = dialog.querySelector('[name="focus-btn"]');
    if (focusBtn != null) focusBtn.focus();
}

/**
 * メッセージダイアログを閉じる
 * @param {ダイアログのID名} dialogId 
 * @param {イベント} e 
 */
function closeMsgDialog(dialogId, e) {
    if (e != null) {
        e.preventDefault();
    }

    // メッセージ画面エリアからダイアログクラスを取り除く
    const mArea = document.getElementById('msg-dialog-area');
    if (mArea == null) return;
    const fArea = document.getElementById('form-dialog-area');
    // ダイアログが重なっている場合の処理
    if (!fArea.querySelector(".dialog")) {
        mArea.classList.remove('dialog');
        fArea.classList.remove('trans');
    }

    // メッセージ画面に[none]クラスを付与して画面を消去する
    const dialog = document.getElementById(dialogId);
    if (dialog == null) return;
    dialog.classList.add('none');

    // 要素のクリック禁止を解除する
    setInertState(false);
}

/**
 * 閉じた時のフォーカス先を指定する
 * @param {*} msgId 
 * @param {*} elm 
 */
function setFocusElement(msgId, elm) {
    const dialog = document.getElementById(msgId);
    const btn =dialog.querySelector('[name="focus-btn');
    btn.onclick = (e) => {
        closeMsgDialog(msgId, e);// 上書きされて消えるため再度設定
        elm.focus();
    };
}

// ボタンを使用不可にする
function setInertState(state) {
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
async function withInertButton(button, asyncFunc) {
    button.inert = true;
    try {
        const ok = await asyncFunc();
        return ok;
    } finally {
        button.inert = false;
    }
}