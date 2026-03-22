"use strict"

// import { init as initCombo } from "../components/form/combo.js";
// import { init as initSearch } from "../components/form/search.js";
// import { init as initTab } from "../components/ui/tab.js";
// import { init as initFocus } from "../components/form/enterfocus.js";
// import { init as initTable } from "../components/form/table.js";
// import { init as initKebabBtns } from "../components/form/kebabBtns.js";
// import { DataResolver } from "../core/data/DataResolver.js";
import { getController } from "./map/controllerMap.js";
// import { switchTab } from "./ui/tab.js";
import { actionMap } from "./map/actionMap.js";
import { init as initTable } from "./ui/table.js";

export function initPage(){

    initTable();

    document.addEventListener("input", handleAction);
    document.addEventListener("click", handleAction);
    document.addEventListener("search", handleAction);
    document.addEventListener("focusin", handleAction);

}

function handleAction(e){

    const el = e.target.closest("[data-action]");
    if(!el) return;

    const action = el.dataset.action;

    const fn = actionMap[action];
    if(!fn) return;

    const target = el.dataset.target;
    const controller = target ? getController(target) : null;

    fn(controller, el, e);
}

// export function initPage(){

//     document.addEventListener("input", (e) => {
//         const el = e.target.closest("[data-action]");
//         if(!el) return;

//         const action = el.dataset.action;
//         const target = el.dataset.target;

//         const controller = getController(target);
//         if(!controller) return;

//         switch(action){
//             case "filter":
//                 controller.filter(
//                     el.dataset.field,
//                     el.value === "" ? null : Number(el.value)
//                 );
//                 break;
//         }
//     });

//     document.addEventListener("searh", (e) => {
//         const el = e.target.closest("[data-action]");
//         if(!el) return;

//         const action = el.dataset.action;console.log(action)
//         const target = el.dataset.target;

//         const controller = getController(target);
//         if(!controller) return;

//         switch(action){
//             case "search":
//                 controller.search(el.value);
//                 break;
//         }
//     });

//     document.addEventListener("click", (e) => {
//         const el = e.target.closest("[data-action]");
//         if(!el) return;

//         const action = el.dataset.action;

//         switch(action){
//             case "resolve":
//                 resolveAddress(el);
//                 break;
//             case "postal":
//                 execPostalSearch(el);
//                 break;
//             case "tab":
//                 switchTab(el);
//                 break;
//         }
//     });
    
//     document.addEventListener("click", (e) => {
//         const el = e.target.closest("[data-action]");
//         if(!el) return;

//         const action = el.dataset.action;
//         const target = el.dataset.target;

//         const controller = getController(target);
//         if(!controller) return;

//         switch(action){
//             case "create":
//                 controller.create(el.dataset.form);
//                 break;
//             case "delete":
//                 controller.deleteSelected();
//                 break;
//             case "download":
//                 controller.downloadSelected();
//                 break;
//             case "reload":
//                 controller.refresh();
//                 break;
//         }
//     });
// }






// export async function initPage(config, area = document){

//     // コンボボックス
//     if (config.combo) {
//         initCombo(area, config.combo);
//     }

//     // ID → NAME 解決
//     if (config.resolve) {
//         DataResolver.init(area);
//     }

//     // 検索ボックス
//     if(config.search){
//         initSearch(area, config.search);
//     }

//     // ボタン
//     if(config.kebabBtns){
//         initKebabBtns();
//     }

//     // タブ
//     if(config.tabs){
//         initTab(area);
//     }

//     // フォーカス移動
//     if(config.enterfocus){
//         initFocus(area);
//     }

//     // テーブル
//     if(config.tables) {
//         await initTable(config.tables);
//     }
// }