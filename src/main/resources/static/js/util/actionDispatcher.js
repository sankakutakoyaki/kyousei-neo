"use strict"

import { getController } from "../controllers/controllers.js";

export function dispatchAction(e){
    const el = e.target.closest("[data-action]");
    if(!el) return;
    runAction(el, e);
}

// UI操作はコントローラー不要なので処理を分ける
const uiActions = {
    tab: handleTab,
    "select-on-focus": handleSelectOnFocus
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
        // const controller = resolveController(el);
        // controller?.updateButtons();
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

// function handleClose(el){
//     const dialog = el.closest(".dialog");
//     if(dialog){
//         dialog.classList.remove("is-show");
//     }
// }