"use strict"

/**
 * 更新用
 * @param {*} url 
 * @param {*} data 
 * @param {*} token 
 * @param {*} contentType 
 * @returns 
 */
async function updateFetch(url, data, token, contentType = "application/json") {
    const spinner = document.getElementById("loading");
    if (spinner) spinner.classList.remove("loaded");

    const response = await fetch(url, {
        method: "POST",
        headers: {
            "X-CSRF-TOKEN": token,
            "Content-Type": contentType,
        },
        body: data,
    });

    if (spinner) spinner.classList.add("loaded");

    let json = null;
    const ct = response.headers.get("content-type");
    if (ct && ct.includes("application/json")) {
        json = await response.json();
    }

    if (!response.ok) {
        await handleHttpError(response.status, json);
    }

    return {
        ok: response.ok,
        status: response.status,
        ...json
    };
}

/**
 * 検索用
 * @param {*} url 
 * @param {*} data 
 * @param {*} token 
 * @param {*} contentType 
 * @returns 
 */
async function searchFetch(url, data, token, contentType = "application/json") {
    const spinner = document.getElementById("loading");
    if (spinner) spinner.classList.remove("loaded");

    const response = await fetch(url, {
        method: "POST",
        headers: {
            "X-CSRF-TOKEN": token,
            "Content-Type": contentType,
        },
        body: data,
    });

    if (response.status === 404) {
        return null; // ← 正常
    }

    // if (!response.ok) {        
    //     handleHttpError(response);
    //     return await response.json();
    //     // throw new Error(`HTTP_ERROR_${response.status}`);
    // }

    if (spinner) spinner.classList.add("loaded");
    
    // // return await response.json();
    // const text = await response.text();
    // if (!text) return null;

    // return JSON.parse(text);
    let json = null;
    const ct = response.headers.get("content-type");
    if (ct && ct.includes("application/json")) {
        json = await response.json();
    }

    if (!response.ok) {
        await handleHttpError(response.status, json);
    }

    return json;
    // return {
    //     ok: response.ok,
    //     status: response.status,
    //     ...json
    // };
}

/**
 * HTTPエラー共通処理
 * @param {*} status 
 */
async function handleHttpError(status, json) {
    switch (status) {
        case 401:
            openMsgDialog("msg-dialog", json?.message || "ログインが必要です。", "red");
            location.reload(); // 再ログイン
            break;
        case 403:
            openMsgDialog("msg-dialog", json?.message || "権限がありません。", "red");
            break;
        case 400:
            openMsgDialog("msg-dialog", json?.message || "入力内容に誤りがあります。", "red");
            break;
        case 404:
            openMsgDialog("msg-dialog", json?.message || "処理が見つかりません。", "red");
            break;
        case 500:
            openMsgDialog("msg-dialog", json?.message || "サーバーエラーが発生しました。", "red");
            break;
    }
}