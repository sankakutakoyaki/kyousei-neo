"use strict"

/**
 * 郵便番号から住所を取得
 * @param {*} e 
 */
async function getAddress(event, elmId, targetId) {

    if (event && event.key && event.key !== "Enter") return;

    const sourceElm = document.getElementById(elmId);
    if (!sourceElm) return;

    const modified = sourceElm.value.replace(/-/g, '');

    if (!/^\d{7}$/.test(modified)) return;

    const code = modified.slice(0,3) + "-" + modified.slice(3);

    try {
        const result = await searchFetch(
            '/api/address/get/postalcode',
            JSON.stringify({ value: code }),
            token
        );

        if (!result.ok || !result.data) return;

        const targetElm = document.getElementById(targetId);
        if (!targetElm) return;

        sourceElm.value = result.data.postalCode;

        if (result.data.addressId > 0) {
            targetElm.value =
                result.data.prefecture +
                result.data.city +
                result.data.town;

            targetElm.setSelectionRange(
                targetElm.value.length,
                targetElm.value.length
            );
        } else {
            targetElm.value = "";
        }

    } catch (e) {
        console.error("住所検索エラー:", e);
    }
}

/**
 * 現在地を取得する
 * @returns latitude:緯度　longitude:経度
 */
async function getLocation() {
    if (!("geolocation" in navigator)) {
        alert("このブラウザは位置情報をサポートしていません。");
        return null;
    }

    return new Promise((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(
            position => {
                resolve(position);
            },
            error => {
                alert("位置情報取得エラー: " + error.message);
                reject(error);
            },
            {
                enableHighAccuracy: true,
                timeout: 10000,
                maximumAge: 0
            }
        );
    });
}

async function getEntityByCode(url, code) {
    const result = await searchFetch(url, JSON.stringify({id:parseFloat(code)}), token);
    if (result.ok) {
        return result.data;
    } else {
        return null;
    }
}

// コードからemployeeを取得
async function getEmployeeByCode(code) {
    getEntityByCode("/api/employee/get/id", code);
}

// コードからwork_itemを取得
async function getWorkContentByCode(code) {
    getEntityByCode("/api/work_content/get/id", code);
}