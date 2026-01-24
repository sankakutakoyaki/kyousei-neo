"use strict"

/**
 * 年月日等の各要素を取得
 * @returns yyyy-MM-dd
 */
function getDate() {
    const date = new Date();
    const y = date.getFullYear();
    const m = date.getMonth() + 1;
    const d = date.getDate();
    return y + "-" + ('0' + m).slice(-2) + "-" + ('0' + d).slice(-2);
}

/**
 * 年月日等の各要素を取得
 * @returns yyyyMMdd h:m
 */
// function getNow() {
//     const date = new Date();
//     const y = date.getFullYear();
//     const m = date.getMonth() + 1;
//     const d = date.getDate();
//     const h = date.getHours();
//     const min = date.getMinutes();
//     return y + "-" + ('0' + m).slice(-2) + "-" + ('0' + d).slice(-2) + ' ' + ('0' + h).slice(-2) + ":" + ('0' + min).slice(-2);
// }
function getNow() {
    const d = new Date();
    const pad = n => String(n).padStart(2, '0');

    return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ` +
           `${pad(d.getHours())}:${pad(d.getMinutes())}`;
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
    const sec = date.getSeconds();
    const milli = date.getMilliseconds();
    return y + "-" + ('0' + m).slice(-2) + "-" + ('0' + d).slice(-2) + ' ' + ('0' + h).slice(-2) + ":" + ('0' + min).slice(-2) + ":" + ('0' + sec).slice(-2) + "." + ('00' + milli).slice(-3);
}

/**
 * 時刻の各要素を取得
 * @returns hh:mm:ss:
 */
function getTimeNow() {
    const date = new Date();
    const h = date.getHours();
    const min = date.getMinutes();
    const sec = date.getSeconds();
    return ('0' + h).slice(-2) + ":" + ('0' + min).slice(-2) + ":" + ('0' + sec).slice(-2);
}


function formatTime(value) {
    if (!value) return "";

    const d = new Date(value);
    const h = String(d.getHours()).padStart(2, '0');
    const m = String(d.getMinutes()).padStart(2, '0');

    return `${h}:${m}`;
}