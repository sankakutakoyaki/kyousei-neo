"use strict"

import { getController } from "../controllers/controllers.js";
import { handleDateMove } from "../core/dom/dateActions.js";

export function dispatchAction(input){

    // DOMイベント
    if(input instanceof Event){
        const el = input.target.closest("[data-action]");
        if(!el) return;

        return runAction(el, input);
    }

    // 直接呼び出し
    const { action, target, data } = input;

    const targets = Array.isArray(target) ? target : [target];

    targets.forEach(name => {
        const controller = getController(name); // ★ここ修正
        if(!controller) return;

        runActionDirect(controller, action, data);
    });
}

// UI操作はコントローラー不要なので処理を分ける
const uiActions = {
    tab: handleTab,
    "select-on-focus": handleSelectOnFocus,
    "date-move": handleDateMove
};

export function runAction(el, e){

    const action = el.dataset.action;
    if(!action) return;

    // UIアクション
    if(uiActions[action]){
        uiActions[action](el, e);
        return;
    }

    const controller = resolveController(el);

    if(!controller){
        console.warn("controller not found:", {
            action: el.dataset.action,
            element: el
        });
        return;
    }

    const handler = controller.actions[action];

    let value;
    if(el.type === "checkbox"){
        value = el.checked;
    }else{
        value = el.value ?? el.dataset.value;
    }

    if(typeof handler === "function"){
        handler(controller, el, e, value);
        return;
    }

    if(typeof controller[action] === "function"){
        controller[action](value, el, e);
        return;
    }

    console.warn("action not found:", action);
}

export function runActionDirect(controller, action, data){

    const handler = controller.actions?.[action];

    if(typeof handler === "function"){
        handler(controller, data);
        return;
    }

    if(typeof controller[action] === "function"){
        controller[action](data);
        return;
    }
}

export function resolveController(el){
    const name =
        el.dataset.controller ||
        el.closest("[data-controller]")?.dataset.controller;

    return getController(name);
}

function handleTab(el){

    const group = el.dataset.group;
    const targetId = el.dataset.tab;

    if(!group || !targetId){
        console.warn("tab missing data:", el);
        return;
    }

    document.querySelectorAll(`[data-group="${group}"]`)
        .forEach(tab => tab.classList.remove("is-active"));

    el.classList.add("is-active");

    document.querySelectorAll(`[data-tab-content="${group}"]`)
        .forEach(panel => panel.classList.remove("is-show"));

    const target = document.getElementById(targetId);
    if(target){
        target.classList.add("is-show");
        const area = target.querySelector("[data-controller]");
        const name = area?.dataset.controller;
        const controller = getController(name);

        controller?.updateButtons(); // ★確実に呼ぶ
    }
}

function handleSelectOnFocus(el, e){
    if(e.type !== "focusin") return;
    el.select();
}