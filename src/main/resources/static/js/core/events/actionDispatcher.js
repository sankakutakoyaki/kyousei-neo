"use strict"

export function dispatchAction(input){

    // DOMイベント
    if(input instanceof Event){
        const el = input.target.closest("[data-action]");
        if(!el) return;

        const action = el.dataset.action;

        // UIアクション
        if(uiActions[action]){
            return uiActions[action](el, input);
        }

        const controller = resolveController(el);
        if(!controller) return;

        return controller.actions?.[action]?.(controller, el, input);
    }

    // 直接呼び出し（アプリイベント）
    const { action, target, data } = input;

    const targets = Array.isArray(target) ? target : [target];

    targets.forEach(name => {
        const controller = getController(name);
        controller?.actions?.[action]?.(controller, data);
    });
}