"use strict"

// フォーカス遷移対象要素
let tabFocusElements = createTabFocusElements();


// ============================================================
// ページ全体のフォーカス遷移対象要素作成
// ============================================================
function createTabFocusElements() {

    let elements = filterTabFocusElements(
        document.querySelectorAll("*")
    );

    elements.sort((a, b) => {

        if (a.tabIndex === 0) return 1;
        if (b.tabIndex === 0) return -1;

        return a.tabIndex - b.tabIndex;
    });

    return elements;
}


// ============================================================
// フォーカス遷移対象要素フィルタリング
// ============================================================
function filterTabFocusElements(nodeList) {

    return Array.from(nodeList).filter(target => {

        if (target.nodeType !== Node.ELEMENT_NODE) {
            return false;
        }

        const targetTags = [
            "a",
            "input",
            "select",
            "textarea",
            "button"
        ];

        const isFocusable =
            (
                targetTags.includes(
                    target.tagName.toLowerCase()
                )
                ||
                target.hasAttribute("tabindex")
            )
            &&
            target.tabIndex >= 0;

        return isFocusable
            && !target.disabled
            && target.offsetParent !== null;
    });
}


// ============================================================
// フォーカス遷移対象を再設定
// ============================================================
export function resetEnterFocus() {

    tabFocusElements = createTabFocusElements();
}


// ============================================================
// data-enterfocus の設定
// ============================================================
export function setEnterFocus() {

    const areas = document.querySelectorAll(
        "[data-enterfocus]"
    );

    areas.forEach(area => {

        // 二重登録防止
        if (
            area.dataset.enterFocusInitialized === "true"
        ) {
            return;
        }

        area.dataset.enterFocusInitialized = "true";


        let isComposing = false;


        // ====================================================
        // IME変換開始
        // ====================================================
        area.addEventListener(
            "compositionstart",
            () => {
                isComposing = true;
            }
        );


        // ====================================================
        // IME変換終了
        // ====================================================
        area.addEventListener(
            "compositionend",
            () => {
                isComposing = false;
            }
        );


        // ====================================================
        // Enterキー
        // ====================================================
        area.addEventListener(
            "keydown",
            event => {

                if (event.key !== "Enter") {
                    return;
                }


                // search inputは通常動作
                if (event.target.type === "search") {
                    return;
                }


                // IME変換中
                if (isComposing) {
                    return;
                }


                // Enterの通常動作を抑止
                event.preventDefault();


                // =================================================
                // このエリア内だけのフォーカス対象を作成
                // =================================================
                let focusElements =
                    filterTabFocusElements(
                        area.querySelectorAll(
                            "input, textarea, select, button, a"
                        )
                    );


                // tabindex順
                focusElements.sort((a, b) => {

                    if (a.tabIndex === 0) return 1;
                    if (b.tabIndex === 0) return -1;

                    return a.tabIndex - b.tabIndex;
                });


                // 現在の要素
                const current =
                    event.target.closest(
                        "input, textarea, select, button, a"
                    );


                const arrayIndex =
                    focusElements.indexOf(current);


                // 対象外なら終了
                if (arrayIndex < 0) {
                    return;
                }


                // =================================================
                // textarea Alt+Enter
                // =================================================
                if (
                    event.target.tagName.toLowerCase()
                    === "textarea"
                    &&
                    event.altKey
                ) {

                    const currentSelectionStart =
                        event.target.selectionStart;

                    event.target.value =
                        event.target.value.substring(
                            0,
                            currentSelectionStart
                        )
                        +
                        "\n"
                        +
                        event.target.value.substring(
                            event.target.selectionEnd
                        );


                    event.target.selectionStart =
                        currentSelectionStart + 1;

                    event.target.selectionEnd =
                        currentSelectionStart + 1;

                    return;
                }


                // =================================================
                // Alt + Enter
                // =================================================
                if (
                    event.target.onclick !== null
                    &&
                    event.altKey
                ) {

                    event.target.click();

                    return;
                }


                // =================================================
                // buttonのEnter
                // =================================================
                if (
                    event.target.tagName.toLowerCase()
                    === "button"
                ) {

                    event.target.click();

                    return;
                }


                let nextElement = null;


                // =================================================
                // Enter → 次へ
                // =================================================
                if (!event.shiftKey) {

                    for (
                        let i = 1;
                        i < focusElements.length;
                        i++
                    ) {

                        let index =
                            arrayIndex + i;


                        // 最後まで行ったら先頭へ
                        if (
                            index >= focusElements.length
                        ) {

                            index -=
                                focusElements.length;
                        }


                        nextElement =
                            focusElements[index];


                        if (
                            nextElement.style.display
                                !== "none"
                            &&
                            (
                                nextElement.offsetParent
                                    !== null
                                ||
                                nextElement.style.position
                                    === "fixed"
                            )
                            &&
                            !isSkipFocusElement(
                                nextElement
                            )
                        ) {

                            nextElement.focus();

                            break;
                        }
                    }
                }


                // =================================================
                // Shift + Enter → 前へ
                // =================================================
                if (event.shiftKey) {

                    for (
                        let i = 1;
                        i < focusElements.length;
                        i++
                    ) {

                        let index =
                            arrayIndex - i;


                        // 先頭まで行ったら最後へ
                        if (index < 0) {

                            index +=
                                focusElements.length;
                        }


                        nextElement =
                            focusElements[index];


                        if (
                            nextElement.style.display
                                !== "none"
                            &&
                            (
                                nextElement.offsetParent
                                    !== null
                                ||
                                nextElement.style.position
                                    === "fixed"
                            )
                            &&
                            !isSkipFocusElement(
                                nextElement
                            )
                        ) {

                            nextElement.focus();

                            break;
                        }
                    }
                }

            }
        );
    });


    // ページ全体の対象も更新
    resetEnterFocus();
}


// ============================================================
// MutationObserver
// ============================================================
const observer =
    new MutationObserver(mutations => {

        let shouldReset = false;


        for (const mutation of mutations) {

            // 要素追加・削除
            if (
                filterTabFocusElements(
                    mutation.addedNodes
                ).length > 0
                ||
                filterTabFocusElements(
                    mutation.removedNodes
                ).length > 0
            ) {

                shouldReset = true;
                break;
            }


            // 属性変更
            if (
                mutation.type === "attributes"
            ) {

                shouldReset = true;
                break;
            }


            // 追加された要素の子孫
            for (
                const addedNode
                of mutation.addedNodes
            ) {

                if (
                    addedNode.nodeType
                    !== Node.ELEMENT_NODE
                ) {
                    continue;
                }


                if (
                    filterTabFocusElements(
                        addedNode.querySelectorAll("*")
                    ).length > 0
                ) {

                    shouldReset = true;
                    break;
                }
            }


            if (shouldReset) {
                break;
            }
        }


        if (shouldReset) {
            tabFocusElements =
                createTabFocusElements();
        }
    });


// ============================================================
// MutationObserver監視設定
// ============================================================
const config = {

    childList: true,
    subtree: true,
    attributes: true,

    attributeFilter: [
        "class",
        "tabindex",
        "disabled",
        "readonly",
        "style"
    ]
};


observer.observe(
    document.body,
    config
);


// ============================================================
// チェックボックスによるスキップ
// ============================================================
function isSkipFocusElement(target) {

    if (!target?.id) {
        return false;
    }


    const checkbox =
        document.querySelector(
            `[data-skip-target='${target.id}']`
        );


    return checkbox?.checked === true;
}

// "use strict"

// // フォーカス遷移対象要素
// let tabFocusElements = createTabFocusElements();

// // フォーカス遷移対象要素作成
// function createTabFocusElements() {
//     // フォーカス遷移対象要素をフィルタリング
//     let elements = filterTabFocusElements(document.querySelectorAll("*"));

//     // tabIndex属性でソート(ない場合は0とみなす)
//     elements.sort((a, b) => {
//         if (a.tabIndex === 0) return 1;
//         if (b.tabIndex === 0) return -1;
//         return a.tabIndex - b.tabIndex;
//     });

//     return elements;
// }

// // フォーカス遷移対象要素フィルタリング
// function filterTabFocusElements(nodeList) {
//     return Array.from(nodeList).filter(target => {

//         if (target.nodeType !== Node.ELEMENT_NODE) return false;

//         // <a><input><select><textarea><button>または正のtabindex属性を持つ場合は対象
//         const targetTags = ["a", "input", "select", "textarea", "button"];
//         const isFocusable = (targetTags.includes(target.tagName.toLowerCase()) || 
//             target.hasAttribute("tabindex")) && target.tabIndex >= 0;

//         return isFocusable
//             && !target.disabled   // ← これ追加
//             && target.offsetParent !== null; // 非表示除外（安全）
//     });
// }

// // フォーカス遷移対象要素を再設定
// export function resetEnterFocus() {
//     tabFocusElements = createTabFocusElements();
// }

// // keydownイベントリスナに登録
// export function setEnterFocus() {
//     const elms = document.querySelectorAll("[data-enterfocus]");
//     elms.forEach(area => {
//         let isComposing = false;
//         // IME の変換中
//         area.addEventListener("compositionstart", () => {
//             isComposing = true;
//         });
//         // IME の変換確定直後
//         area.addEventListener("compositionend", () => {
//             isComposing = false;
//         });
//         // keydownイベントリスナ
//         area.addEventListener("keydown", event => {
//             // Enterキー押下の場合
//             if (event.key === "Enter") {
//                 // search inputは通常動作させる
//                 if (event.target.type === "search") {
//                     return;
//                 }
//                 if (isComposing) {
//                     // 日本語確定の Enter → 無視
//                     return;
//                 }
//                 // 通常のキーイベントを抑止
//                 event.preventDefault();

//                 // イベント発生元要素がリスト内のどこにあるか
//                 // let arrayIndex = tabFocusElements.indexOf(event.target);
//                 const current = event.target.closest("input, textarea, select, button, a");
//                 let arrayIndex = tabFocusElements.indexOf(current);

//                 // イベント発生元要素がリスト内に存在する場合
//                 if (arrayIndex >= 0) {
//                     // <textarea>でのAlt+Enter
//                     if (event.target.tagName.toLowerCase() === "textarea" && event.altKey) {
//                         // 通常のキーイベント(改行)
//                         // 現在のキャレット位置を取得
//                         let currentSelectionStart = event.target.selectionStart;

//                         // キャレット位置に改行を挿入
//                         event.target.value = event.target.value.substr(0, currentSelectionStart) + "\n" + event.target.value.substr(event.target.selectionEnd);

//                         // キャレット位置を元の位置に変更
//                         event.target.selectionStart = currentSelectionStart + 1;
//                         event.target.selectionEnd = currentSelectionStart + 1;
//                         return;
//                     }
//                     // onclick属性が設定された要素でのAlt+Enter
//                     if (event.target.onclick !== null && event.altKey) {
//                         // 通常のキーイベント(クリック)
//                         event.target.click();
//                         return;
//                     }
//                     // onclick属性が設定されたbutton要素でのEnter
//                     if (event.target.tagName.toLowerCase() === "button") {
//                         // 通常のキーイベント(クリック)
//                         event.target.click();
//                         return;
//                     }
//                     let nextElement;
//                     // Enter(順送り)
//                     if (!event.shiftKey) {
//                         // イベント発生要素以外のフォーカス遷移対象要素を昇順に取得
//                         for (let i = 1; i < tabFocusElements.length; i++) {
//                             if (arrayIndex + i < tabFocusElements.length) {
//                                 // 最後の要素まで
//                                 nextElement = tabFocusElements[arrayIndex + i];
//                             } else {
//                                 // 最後の要素以降は最初の要素に戻る
//                                 nextElement = tabFocusElements[arrayIndex + i - tabFocusElements.length]
//                             }

//                             // display:noneでない場合はフォーカスして終了
//                             if (nextElement.style.display !== "none" && (nextElement.offsetParent !== null || nextElement.style.position === "fixed") && !isSkipFocusElement(nextElement)) {
//                                 nextElement.focus();
//                                 break;
//                             }
//                         }
//                     }
//                     // Shift+Enter(逆送り)
//                     if (event.shiftKey) {
//                         // イベント発生要素以外のフォーカス遷移対象要素を降順に取得
//                         for (let i = 1; i < tabFocusElements.length; i++) {
//                             if (arrayIndex - i >= 0) {
//                                 // 最初の要素まで
//                                 nextElement = tabFocusElements[arrayIndex - i];
//                             } else {
//                                 // 最初の要素以降は最後の要素に戻る
//                                 nextElement = tabFocusElements[arrayIndex - i + tabFocusElements.length]
//                             }

//                             // display:noneでない場合はフォーカスして終了
//                             if (nextElement.style.display !== "none" && (nextElement.offsetParent !== null || nextElement.style.position === "fixed") && !isSkipFocusElement(nextElement)) {
//                                 nextElement.focus();
//                                 break;
//                             }
//                         }
//                     }
//                 }
//             }
//         });
//     })
// }

// // mutation observer
// const observer = new MutationObserver(mutations => {
//     MUTATIONS: for (let mutation of mutations) {
//         // 追加/削除された要素の判定
//         if (filterTabFocusElements(mutation.addedNodes).length > 0 ||
//             filterTabFocusElements(mutation.removedNodes).length > 0) {

//             tabFocusElements = createTabFocusElements(); // ← 代入！
//             break MUTATIONS;
//         }
//         if (mutation.type === "attributes") {
//             tabFocusElements = createTabFocusElements();
//             break MUTATIONS;
//         }        
//         // 追加された要素の子孫要素の判定
//         for (let addedNode of mutation.addedNodes) {
//             // エレメントノード以外を除外
//             if (addedNode.nodeType === Node.ELEMENT_NODE) {
//                 if (filterTabFocusElements(addedNode.querySelectorAll("*")).length > 0) {
//                     // 追加された要素がフォーカス遷移対象の場合は再作成
//                     tabFocusElements = createTabFocusElements();
//                     break MUTATIONS;
//                 }
//             }
//         }
//         // 削除された要素の子孫要素の判定
//         for (let removeNode of mutation.removedNodes) {
//             // エレメントノード以外を除外
//             if (removeNode.nodeType === Node.ELEMENT_NODE) {
//                 if (filterTabFocusElements(removeNode.querySelectorAll("*")).length > 0) {
//                     // 削除された要素がフォーカス遷移対象の場合は再作成
//                     tabFocusElements = createTabFocusElements();
//                     break MUTATIONS;
//                 }
//             }
//         }
//     }
// });

// // mutation observer監視設定
// const config = {
//     childList: true,
//     subtree: true,
//     attributes: true,
//     attributeFilter: ["class", "tabindex", "disabled", "readonly", "style"]
// };

// // mutation observer監視開始
// observer.observe(document.body, config);

// //　チェックボックスでスキップ設定
// function isSkipFocusElement(target){
//     if(!target?.id){
//         return false;
//     }
//     const checkbox = document.querySelector(
//         `[data-skip-target='${target.id}']`
//     );
//     return checkbox?.checked === true;
// }