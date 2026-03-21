// "use strict"

// import { resolveEngine } from "./resolveEngine.js";

// export const UiEngine = {

//     init() {

//         resolveEngine.init();
//         this.enterFocus();

//     },

//     enterFocus() {

//         const inputs = document.querySelectorAll("[data-enter-focus]");

//         inputs.forEach(input => {

//             input.addEventListener("keydown", (e) => {

//                 if (e.key !== "Enter") return;

//                 const target = document.querySelector(
//                     `[data-focus="${input.dataset.enterFocus}"]`
//                 );

//                 if (target) target.focus();

//             });

//         });

//     }

// };

// window.ApiErrorHandler = (status, message) => {

//     switch (status) {

//         case 401:
//             openMsgDialog("msg-dialog", message || "ログインが必要です", "red");
//             location.reload();
//             break;

//         case 403:
//             openMsgDialog("msg-dialog", message || "権限がありません", "red");
//             break;

//         case 500:
//             openMsgDialog("msg-dialog", message || "サーバーエラー", "red");
//             break;

//     }

// };