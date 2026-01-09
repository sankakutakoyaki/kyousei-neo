"use strict"

/******************************************************************************************************* 取得・検証 */

// Numberからrecycleを取得
async function existsRecycleByNumber(number) {
    // // スピナー表示
    // startProcessing();
    const data = "num=" + encodeURIComponent(number);
    const url = "/api/recycle/exists/number";
    const contentType = 'application/x-www-form-urlencoded';
    // const resultResponse = await postFetch(url, data, token, contentType);
    // // スピナー消去
    // processingEnd();
    // if (resultResponse.ok) {
    //     return await resultResponse.json();
    // } else {
    //     return null;
    // }
    return await serachFetch(url, data, token, contentType);
}

// コードからrecycle_makerを取得
async function getMakerByCode(code) {
    // // スピナー表示
    // startProcessing();
    const data = "code=" + encodeURIComponent(parseInt(code));
    const url = '/api/recycle/maker/get/code';
    const contentType = 'application/x-www-form-urlencoded';
    // const resultResponse = await postFetch(url, data, token, contentType);
    // // // スピナー消去
    // // processingEnd();
    // if (resultResponse.ok) {
    //     return await resultResponse.json();
    // } else {
    //     return null;
    // }
    return await searchFetch(url, data, token, contentType);
}


// コードからrecycle_itemを取得
async function getItemByCode(code) {
    // // スピナー表示
    // startProcessing();
    const data = "code=" + encodeURIComponent(parseInt(code));
    const url = '/api/recycle/item/get/code';
    const contentType = 'application/x-www-form-urlencoded';
    // const resultResponse = await postFetch(url, data, token, contentType);
    // // // スピナー消去
    // // processingEnd();
    // if (resultResponse.ok) {
    //     return await resultResponse.json();
    // } else {
    //     return null;
    // }
    return await searchFetch(url, data, token, contentType);
}

// お問合せ管理票番号の桁数と数値かどうかを確認
function checkNumber(numberBox) {
    if (numberBox != null) {
        const num = numberBox.value;
        if (num === "") return;
console.log(num)
        // const number = removeEdgeA(num);
        const number = num.replace(/\D/g, "");
        // 文字列で13桁かチェック
        if (typeof number !== "string" || number.length !== 13 || !/^\d+$/.test(number)) {
            return "";
        }
        return number;
    } else {
        return "";
    }
}

// 0000-00000000-0の形にする
function moldingNumber(numberBox) {
    const number = checkNumber(numberBox);
    // 正しい場合のみ成形
    if (number == "") {
        return "";
    }
    return number.replace(/(\d{4})(\d{8})(\d{1})/, "$1-$2-$3");
}

// 両端の'a'を削除する
function removeEdgeA(number) {
    number = number.toLowerCase();

    if (number.startsWith('a') && number.endsWith('a')) {
        return number.slice(1, -1);
    }
    return number;
}