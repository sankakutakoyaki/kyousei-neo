"use strict"

/**
 * リスト検索
 * @param {*} boxId 
 * @returns 
 */
export function searchBoxFilter(boxId, list) {
    // 検索ボックスを取得
    const box = boxId instanceof HTMLElement ? boxId: document.getElementById(boxId);
    if (!box) return list;

    // 検索ワードを取得
    const words = box.value;
    // if (words.length == 0) return origin;
    if (words.length == 0) return list;

    // 空白区切りで配列を作成
    let list_word = words.split(/\s+/);
    list_word.forEach(word => {
        const result = list.filter(val => {
            let res = false;
            Object.values(val).forEach(item => {
                // item を文字列に変換して判定
                const strItem = String(item);
                if (strItem.includes(word)) res = true;
            });
            // 要素の中に[true]が一つでもあれば要素を返す
            if (res) return val;
        });
        list = result;
    });
    return list;
}