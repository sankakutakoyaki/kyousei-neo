"use strict"

/******************************************************************************************************* 取得・検証 */

/**
 * Numberからrecycleを取得
 * @param {*} number 
 * @returns 
 */
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
    return await searchFetch(url, data, token, contentType);
}

/**
 * コードからrecycle_makerを取得
 * @param {*} code 
 * @returns 
 */
async function getMakerByCode(code) {
    // // スピナー表示
    // startProcessing();
    const data = "code=" + encodeURIComponent(parseInt(code));
    const url = '/api/recycle/maker/get/code';
    const contentType = 'application/x-www-form-urlencoded';
    return await searchFetch(url, data, token, contentType);
}

/**
 * コードからrecycle_itemを取得
 * @param {*} code 
 * @returns 
 */
async function getItemByCode(code) {
    // // スピナー表示
    // startProcessing();
    const data = "code=" + encodeURIComponent(parseInt(code));
    const url = '/api/recycle/item/get/code';
    const contentType = 'application/x-www-form-urlencoded';
    return await searchFetch(url, data, token, contentType);
}

/**
 * お問合せ管理票番号の桁数と数値のみかどうかを確認
 * @param {*} num 
 * @returns 変数がNullか空白ならNullを返す。不正な文字列ならFalseを返す。正しければ13桁の文字列を返す
 */
function checkNumber(num) {
    if (num == null || num === "" || typeof num !== "string") return null;

    // const number = removeEdgeA(num);
    // 数値以外を消去
    const number = num.replace(/\D/g, "");

    // 13桁で数値のみかチェック
    if (number.length !== 13 || !/^\d+$/.test(number)) {
        return false;
    }
    return number;
}

/**
 * 0000-00000000-0の形にする
 * @param {*} num 
 * @returns 
 */
function moldingNumber(num) {
    const number = checkNumber(num);
    // 正しい場合のみ成形
    if (number == null || !number) {
        return "";
    }
    return number.replace(/(\d{4})(\d{8})(\d{1})/, "$1-$2-$3");
}

// // 両端の'a'を削除する
// function removeEdgeA(number) {
//     number = number.toLowerCase();

//     if (number.startsWith('a') && number.endsWith('a')) {
//         return number.slice(1, -1);
//     }
//     return number;
// }