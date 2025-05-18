"use strict"

/**
 * 年月日等の各要素を取得
 * @returns yyyyMMdd h:m
 */
function getNow() {
    const date = new Date();
    const y = date.getFullYear();
    const m = date.getMonth() + 1;
    const d = date.getDate();
    const h = date.getHours();
    const min = date.getMinutes();
    return y + "/" + ('0' + m).slice(-2) + "/" + ('0' + d).slice(-2) + ' ' + ('0' + h).slice(-2) + ":" + ('0' + min).slice(-2);
}

/**
* 年月日等の各要素を取得
* @returns yyyyMMddhhmm000000
*/
function getNowNoBreak() {
    const date = new Date();
    const y = date.getFullYear();
    const m = date.getMonth() + 1;
    const d = date.getDate();
    const h = date.getHours();
    const min = date.getMinutes();
    const milli = date.getMilliseconds();
    return y + ('0' + m).slice(-2) + ('0' + d).slice(-2) + ('0' + h).slice(-2) + ('0' + min).slice(-2) + ('00' + milli).slice(-3);
}

/**
 * 年月日等の各要素を取得
 * @returns yyyy-MM-dd hh:mm:ss.SSS:
 */
function getNowHyphen() {
    const date = new Date();
    const y = date.getFullYear();
    const m = date.getMonth() + 1;
    const d = date.getDate();
    const h = date.getHours();
    const min = date.getMinutes();
    const sec = date.getSeconds()
    const milli = date.getMilliseconds();
    return y + "-" + ('0' + m).slice(-2) + "-" + ('0' + d).slice(-2) + ' ' + ('0' + h).slice(-2) + ":" + ('0' + min).slice(-2) + ":" + ('0' + sec).slice(-2) + "." + ('00' + milli).slice(-3);
}

/**
 * 郵便番号から住所を取得
 * @param {*} e 
 */
async function getAddress(event, elmId, targetId) {
    if(event instanceof Event) {
        if (event.key != "Enter") return;
    } else {
        if (event != "Enter") return;
    }
    // if (dialog == null) return;
    // if(e == null || e.key === 'Enter'){
    //     if (e != null) e.preventDefault();

        const sourceElm = document.getElementById(elmId);
        if (sourceElm != null) {
            // 文字列から[-]を除去
            const modified = sourceElm.value.replace('-', '');
            // 修正した文字列が７桁の数値か確認
            if (modified.length != 7 || isNaN(modified) == true) return;
            // 郵便番号の定型に変換[000-0000]
            const code = modified.substr(0, 3) + "-" + modified.substr(3, 4);
            // 郵便番号で住所を検索
            const data = "postal_code=" + encodeURIComponent(code);
            const resultResponse = await postFetch('/address/get/postalcode', data, token, 'application/x-www-form-urlencoded');
            const result = await resultResponse.json();
            // 定型に修正した郵便番号を郵便番号入力ボックスに代入
            const targetElm = document.getElementById(targetId);
            sourceElm.value = result.postal_code;
            // targetElm.focus();
            // 検索結果によって処理を分岐
            if (result.address_id > 0) {
                targetElm.value = result.prefecture + result.city + result.town;
                targetElm.setSelectionRange(targetElm.value.length, targetElm.value.length);
            } else {
                targetElm.value = "";
            }
            
        }
    // }
}