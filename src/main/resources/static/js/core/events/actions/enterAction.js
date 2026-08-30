"use strict";

import { validators } from "../../behavior/validators.js";
import { formatters } from "../../behavior/formatters.js";

/**
 * Enter時の入力チェック・フォーマット
 *
 * @param {HTMLInputElement} el
 * @returns {boolean} OKならtrue
 */
export function validateEnterInput(el) {
    if (!(el instanceof HTMLInputElement)) {
        return true;
    }
    // readonly / disabled は対象外
    if (el.readOnly || el.disabled) {
        return true;
    }
    // enter-validate が指定されていない
    if (el.dataset.enterValidate === undefined) {
        return true;
    }
    const value = el.value?.trim();
    // 一旦エラー解除
    el.classList.remove("error");
    // 空なら通常動作
    if (!value) {
        return true;
    }
    const type = el.dataset.validate;
    if (!type) {
        return true;
    }
    const fn = validators[type];
    if (!fn) {
        return true;
    }
    // NG
    if (!fn(value)) {
        el.classList.add("error");
        requestAnimationFrame(() => {el.select();});
        return false;
    }
    // OK
    const formatter = formatters[type];
    if (formatter) {
        el.value = formatter(value);
    }
    return true;
}


/**
 * Enterキーによる入力チェック
 */
export function handleEnterValidate(e) {
    if (e.key !== "Enter") {
        return;
    }
    // IME確定中のEnterは処理しない
    if(e.isComposing || e.keyCode === 229) {
        return;
    }
    const el = e.target;
    if (!(el instanceof HTMLInputElement)) {
        return;
    }
    if(el.readOnly || el.disabled) return;

    if(el.dataset.enterValidate === undefined) return;

    if (!validateEnterInput(el)) {
        e.preventDefault();
    }
}

export function executeEnter(target) {
    if (!target) {
        return;
    }

    target.focus();
    target.dispatchEvent(new Event("input", {bubbles: true}));

    target.dispatchEvent(
        new KeyboardEvent("keydown", {
            key: "Enter",
            code: "Enter",
            keyCode: 13,
            which: 13,
            bubbles: true,
            cancelable: true
        })
    );
}