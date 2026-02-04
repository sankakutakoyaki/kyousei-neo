"use strict"

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
        // const data = "postal_code=" + encodeURIComponent(code);
        const result = await searchFetch('/api/address/get/postalcode', JSON.stringify({id:parseInt(code)}), token);
        // const result = await resultResponse.json();
        if (result.ok) {
            // 定型に修正した郵便番号を郵便番号入力ボックスに代入
            const targetElm = document.getElementById(targetId);
            sourceElm.value = result.data.postal_code;
            // targetElm.focus();
            // 検索結果によって処理を分岐
            if (result.data.address_id > 0) {
                targetElm.value = result.data.prefecture + result.data.city + result.data.town;
                targetElm.setSelectionRange(targetElm.value.length, targetElm.value.length);
            } else {
                targetElm.value = "";
            }
        }
    }
    // }
}

/**
 * 現在地を取得する
 * @returns latitude:緯度　longitude:経度
 */
async function getLocation() {
    // Geolocationのサポートを確認
    if ("geolocation" in navigator) {
    return new Promise((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(resolve, reject)
    })
    } else {
    console.error("このブラウザはGeolocationをサポートしていません。");
    }
}

async function getEntityByCode(url, code) {
    // スピナー表示
    // startProcessing();
    // const data = "id=" + encodeURIComponent(parseInt(code));
    // const contentType = 'application/x-www-form-urlencoded';
    const result = await searchFetch(url, JSON.stringify({id:parseFloat(code)}), token);
    // スピナー消去
    // processingEnd();
    if (result.ok) {
        return result.data;
    } else {
        return null;
    }
}

// コードからemployeeを取得
async function getEmployeeByCode(code) {
    getEntityByCode("/api/employee/get/id", code);
    // // スピナー表示
    // startProcessing();
    // const data = "id=" + encodeURIComponent(parseInt(code));
    // const url = '/employee/get/id';
    // const contentType = 'application/x-www-form-urlencoded';
    // const resultResponse = await postFetch(url, data, token, contentType);
    // // スピナー消去
    // processingEnd();
    // if (resultResponse.ok) {
    //     return await resultResponse.json();
    // } else {
    //     return null;
    // }
}

// コードからwork_itemを取得
async function getWorkContentByCode(code) {
    getEntityByCode("/api/work_content/get/id", code);
}