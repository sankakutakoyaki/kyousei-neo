"use strict"

// フォーカス遷移対象要素
let tabFocusElements = createTabFocusElements();

// EnterキーがIME確定操作かどうか
function isImeEnter(event) {
    return (event.isComposing === true || event.keyCode === 229);
}

// ページ全体のフォーカス遷移対象要素作成
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

// フォーカス遷移対象要素フィルタリング
function filterTabFocusElements(nodeList) {
    return Array.from(nodeList).filter(target => {
        if (target.nodeType !== Node.ELEMENT_NODE) {
            return false;
        }

        const targetTags = ["a", "input", "select", "textarea", "button"];
        const isFocusable = (targetTags.includes(target.tagName.toLowerCase()) || target.hasAttribute("tabindex")) && target.tabIndex >= 0;

        return isFocusable
            && !target.disabled
            && target.offsetParent !== null;
    });
}

// フォーカス遷移対象を再設定
export function resetEnterFocus() {
    tabFocusElements = createTabFocusElements();
}

// data-enterfocus の設定
export function setEnterFocus() {
    const areas = document.querySelectorAll("[data-enterfocus]");
    areas.forEach(area => {
        if (area.dataset.enterFocusInitialized === "true") {
            return;
        }
        area.dataset.enterFocusInitialized = "true";
        area.addEventListener("keydown", event => {
                if (event.key !== "Enter") {
                    return;
                }
                // search inputは通常動作
                if (event.target.type === "search") {
                    return;
                }
                // IME確定のEnterはフォーカス移動しない
                // Safari対策
                if (isImeEnter(event)) {
                    return;
                }
                // Enterの通常動作を抑止
                event.preventDefault();
                // このエリア内だけのフォーカス対象を作成
                let focusElements = filterTabFocusElements(
                    area.querySelectorAll("input, textarea, select, button, a")
                );
                focusElements.sort((a, b) => {
                    if (a.tabIndex === 0) return 1;
                    if (b.tabIndex === 0) return -1;
                    return a.tabIndex - b.tabIndex;
                });
                // 現在の要素
                const current = event.target.closest("input, textarea, select, button, a");
                const arrayIndex = focusElements.indexOf(current);
                if (arrayIndex < 0) {
                    return;
                }
                // textarea Alt+Enter
                if (event.target.tagName.toLowerCase() === "textarea" && event.altKey) {
                    const currentSelectionStart = event.target.selectionStart;
                    event.target.value = event.target.value.substring(0, currentSelectionStart) + "\n" +
                        event.target.value.substring(event.target.selectionEnd);
                    event.target.selectionStart = currentSelectionStart + 1;
                    event.target.selectionEnd = currentSelectionStart + 1;
                    return;
                }
                // Alt + Enter
                if (event.target.onclick !== null && event.altKey) {
                    event.target.click();
                    return;
                }
                // buttonのEnter
                if (event.target.tagName.toLowerCase() === "button") {
                    event.target.click();
                    return;
                }
                let nextElement = null;
                // Enter → 次へ
                if (!event.shiftKey) {
                    for (let i = 1; i < focusElements.length; i++) {
                        let index = arrayIndex + i;
                        if (index >= focusElements.length) {
                            index -= focusElements.length;
                        }

                        nextElement = focusElements[index];
                        if (nextElement.style.display !== "none" &&
                            (nextElement.offsetParent !== null || nextElement.style.position === "fixed") &&
                            !isSkipFocusElement(nextElement)) 
                        {
                            nextElement.focus();
                            break;
                        }
                    }
                }
                // Shift + Enter → 前へ
                if (event.shiftKey) {
                    for (let i = 1; i < focusElements.length; i++) {
                        let index = arrayIndex - i;
                        if (index < 0) {
                            index += focusElements.length;
                        }
                        nextElement = focusElements[index];
                        if (nextElement.style.display !== "none" &&
                            (nextElement.offsetParent !== null ||nextElement.style.position === "fixed") &&
                            !isSkipFocusElement(nextElement)
                        ) {
                            nextElement.focus();
                            break;
                        }
                    }
                }
            }
        );
    });
    resetEnterFocus();
}

// MutationObserver
const observer = new MutationObserver(mutations => {
    let shouldReset = false;
    for (const mutation of mutations) {
        if (filterTabFocusElements(mutation.addedNodes).length > 0 ||
            filterTabFocusElements(mutation.removedNodes).length > 0
        ) {
            shouldReset = true;
            break;
        }

        if (mutation.type === "attributes") {
            shouldReset = true;
            break;
        }

        for (const addedNode of mutation.addedNodes) {
            if (addedNode.nodeType !== Node.ELEMENT_NODE) {
                continue;
            }

            if (filterTabFocusElements(addedNode.querySelectorAll("*")).length > 0){
                shouldReset = true;
                break;
            }
        }

        if (shouldReset) {
            break;
        }
    }

    if (shouldReset) {
        tabFocusElements = createTabFocusElements();
    }
});

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

// チェックボックスによるスキップ

function isSkipFocusElement(target) {
    if (!target?.id) {
        return false;
    }
    const checkbox = document.querySelector(`[data-skip-target='${target.id}']`);
    return checkbox?.checked === true;
}