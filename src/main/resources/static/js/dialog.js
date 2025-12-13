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

    // 要素がクリック禁止になっている場合は解除する
    const elm = document.querySelector('.normal-body');
    if (elm != null) elm.inert = false;

    // autofocus属性が付いているアイテムをフォーカスする
    const focusBox = document.querySelector('input[autofocus]');
    if (focusBox != null) focusBox.focus();
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
    const element = document.querySelector('.normal-body');
    if (element != null) element.inert = true;

    // ダイアログが重なる場合は背景を透明にする
    const check = element.querySelector('.dialog');
    if (check != null) {
        parent.classList.add('trans');
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
    // const arrayMsg = msg.replace(/\s+/, '<br>');
    // content.textContent = arrayMsg;
    content.textContent = msg;

    // OKボタンにフォーカスを合わせる
    dialog.querySelector('button[name="focus-btn"]').focus();
}

/**
 * メッセージダイアログを閉じる
 * @param {ダイアログのID名} dialogId 
 * @param {イベント} e 
 */
function closeMsgDialog(dialogId, e, focusId) {
    if (e != null) {
        e.preventDefault();
    }

    // メッセージ画面エリアからダイアログクラスを取り除く
    const area = document.getElementById('msg-dialog-area');
    if (area == null) return;
    area.classList.remove('dialog');

    // メッセージ画面に[none]クラスを付与して画面を消去する
    const form = document.getElementById(dialogId);
    if (form == null) return;
    form.classList.add('none');

    // 要素がクリック禁止になっている場合は解除する
    const elm = document.querySelector('.normal-body');
    if (elm != null) elm.inert = false;

    // autofocus属性が付いているアイテムをフォーカスする
    const focusBox = document.querySelector('input[autofocus]');
    if (focusBox != null) focusBox.focus();
}

function setFocusElement(msgId, elm) {
    const dialog = document.getElementById(msgId);
    const btn =dialog.querySelector('button[name="focus-btn');
    btn.addEventListener('click', function () { elm.focus(); });
}