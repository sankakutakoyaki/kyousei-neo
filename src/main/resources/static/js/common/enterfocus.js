"use strict"

// フォーカス遷移対象要素
let tabFocusElements = createTabFocusElements();

// フォーカス遷移対象要素作成
function createTabFocusElements() {
    // フォーカス遷移対象要素をフィルタリング
    let elements = filterTabFocusElements(document.querySelectorAll("*"));

    // tabIndex属性でソート(ない場合は0とみなす)
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
        // エレメントノード以外を除外
        if (target.nodeType !== Node.ELEMENT_NODE) {
            return false;
        }

        // <a><input><select><textarea><button>または正のtabindex属性を持つ場合は対象
        const targetTags = ["a", "input", "select", "textarea", "button"];
        // const targetTags = ["a", "input", "select", "textarea"];
        return targetTags.includes(target.tagName.toLowerCase()) || (target.hasAttribute("tabindex") && target.tabIndex >= 0)
    });
}

// フォーカス遷移対象要素を再設定
function resetEnterFocus() {
    tabFocusElements = createTabFocusElements();
}

// keydownイベントリスナに登録
function setEnterFocus(areaId) {
    let area;
    if(areaId instanceof HTMLElement) {
        area = areaId;
    } else {
        area = document.getElementById(areaId);
    }
    if (area == null) return;

    let isComposing = false;

    // IME の変換中
    area.addEventListener("compositionstart", () => {
        isComposing = true;
    });

    // IME の変換確定直後
    area.addEventListener("compositionend", () => {
        isComposing = false;
    });

    // keydownイベントリスナ
    area.addEventListener("keydown", event => {
        // Enterキー押下の場合
        if (event.key === "Enter") {
            // search inputは通常動作させる
            if (event.target.type === "search") {
                return;
            }

            if (isComposing) {
                // 日本語確定の Enter → 無視
                return;
            }
            // 通常のキーイベントを抑止
            event.preventDefault();

            // イベント発生元要素がリスト内のどこにあるか
            let arrayIndex = tabFocusElements.indexOf(event.target);

            // イベント発生元要素がリスト内に存在する場合
            if (arrayIndex >= 0) {
                // <textarea>でのAlt+Enter
                if (event.target.tagName.toLowerCase() === "textarea" && event.altKey) {
                    // 通常のキーイベント(改行)
                    // 現在のキャレット位置を取得
                    let currentSelectionStart = event.target.selectionStart;

                    // キャレット位置に改行を挿入
                    event.target.value = event.target.value.substr(0, currentSelectionStart) + "\n" + event.target.value.substr(event.target.selectionEnd);

                    // キャレット位置を元の位置に変更
                    event.target.selectionStart = currentSelectionStart + 1;
                    event.target.selectionEnd = currentSelectionStart + 1;
                    return;
                }

                // onclick属性が設定された要素でのAlt+Enter
                if (event.target.onclick !== null && event.altKey) {
                    // 通常のキーイベント(クリック)
                    event.target.click();
                    return;
                }

                // onclick属性が設定されたbutton要素でのEnter
                if (event.target.tagName.toLowerCase() === "button") {
                    // 通常のキーイベント(クリック)
                    event.target.click();
                    return;
                }

                let nextElement;
                // Enter(順送り)
                if (!event.shiftKey) {
                    // イベント発生要素以外のフォーカス遷移対象要素を昇順に取得
                    for (let i = 1; i < tabFocusElements.length; i++) {
                        if (arrayIndex + i < tabFocusElements.length) {
                            // 最後の要素まで
                            nextElement = tabFocusElements[arrayIndex + i];
                        } else {
                            // 最後の要素以降は最初の要素に戻る
                            nextElement = tabFocusElements[arrayIndex + i - tabFocusElements.length]
                        }

                        // display:noneでない場合はフォーカスして終了
                        if (nextElement.style.display !== "none" && (nextElement.offsetParent !== null || nextElement.style.position === "fixed")) {
                            nextElement.focus();
                            break;
                        }
                    }
                }

                // Shift+Enter(逆送り)
                if (event.shiftKey) {
                    // イベント発生要素以外のフォーカス遷移対象要素を降順に取得
                    for (let i = 1; i < tabFocusElements.length; i++) {
                        if (arrayIndex - i >= 0) {
                            // 最初の要素まで
                            nextElement = tabFocusElements[arrayIndex - i];
                        } else {
                            // 最初の要素以降は最後の要素に戻る
                            nextElement = tabFocusElements[arrayIndex - i + tabFocusElements.length]
                        }

                        // display:noneでない場合はフォーカスして終了
                        if (nextElement.style.display !== "none" && (nextElement.offsetParent !== null || nextElement.style.position === "fixed")) {
                            nextElement.focus();
                            break;
                        }
                    }
                }
            }
        }
    });
}



// mutation observer
const observer = new MutationObserver(mutations => {
    MUTATIONS: for (let mutation of mutations) {
        // 追加/削除された要素の判定
        if (filterTabFocusElements(mutation.addedNodes).length > 0 || filterTabFocusElements(mutation.removedNodes).length > 0 ) {
            // 追加/削除された要素がフォーカス遷移対象の場合は再作成
            createTabFocusElements();
            break MUTATIONS;
        }

        // 追加された要素の子孫要素の判定
        for (let addedNode of mutation.addedNodes) {
            // エレメントノード以外を除外
            if (addedNode.nodeType === Node.ELEMENT_NODE) {
                if (filterTabFocusElements(addedNode.querySelectorAll("*")).length > 0) {
                    // 追加された要素がフォーカス遷移対象の場合は再作成
                    createTabFocusElements();
                    break MUTATIONS;
                }
            }
        }

        // 削除された要素の子孫要素の判定
        for (let removeNode of mutation.removedNodes) {
            // エレメントノード以外を除外
            if (removeNode.nodeType === Node.ELEMENT_NODE) {
                if (filterTabFocusElements(removeNode.querySelectorAll("*")).length > 0) {
                    // 削除された要素がフォーカス遷移対象の場合は再作成
                    createTabFocusElements();
                    break MUTATIONS;
                }
            }
        }
    }
});

// mutation observer監視設定
const config = {
    childList: true,
    subtree: true
};

// mutation observer監視開始
observer.observe(document.body, config);
