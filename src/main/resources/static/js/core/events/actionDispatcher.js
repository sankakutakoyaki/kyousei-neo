"use strict"

import { getController } from "../../controllers/controllers";

export async function dispatchAction({
    target,
    action,
    payload = null
}){
    if(!target || !action){
        return;
    }

    const targets =
        Array.isArray(target)
            ? target
            : [target];

    for(const key of targets){
        const controller = getController(key);
        if(!controller){
            continue;
        }
        await controller.executeAction(
            action,
            payload
        );
    }
}

// "use strict"

// import { uiActions } from "./actions/uiActions.js";
// import { resolveController } from "./controllerResolver.js";

// export function dispatchAction(input){

//     if(input instanceof Event){
//         const el = input.target.closest("[data-action]");
//         if(!el) return;

//         const actionAttr = el.dataset.action;
//         if(!actionAttr) return;

//         const actions = actionAttr.split(/\s+/);

//         for(const action of actions){

//             // UIアクション
//             if(uiActions[action]){
//                 uiActions[action](el, input);
//                 continue;
//             }

//             const controller = resolveController(el);
//             if(!controller) continue;

//             controller.actions?.[action]?.(controller, el, input);
//         }

//         return;
//     }

//     // // DOMイベント
//     // if(input instanceof Event){
//     //     const el = input.target.closest("[data-action]");
//     //     if(!el) return;

//     //     const action = el.dataset.action;

//     //     // UIアクション
//     //     if(uiActions[action]){
//     //         return uiActions[action](el, input);
//     //     }

//     //     const controller = resolveController(el);
//     //     if(!controller) return;

//     //     return controller.actions?.[action]?.(controller, el, input);
//     // }

//     // 直接呼び出し（アプリイベント）
//     const { action, target, data } = input;
//     const targets = Array.isArray(target) ? target : [target];

//     targets.forEach(name => {
//         const controller = getController(name);
//         controller?.actions?.[action]?.(controller, data);
//     });
// }