"use strict"

// /**
//  * 更新用
//  * @param {*} url 
//  * @param {*} data 
//  * @param {*} token 
//  * @param {*} contentType 
//  * @returns 
//  */
// async function updateFetch(url, data, token, contentType = "application/json") {
//     const spinner = document.getElementById("loading");
//     if (spinner) spinner.classList.remove("loaded");

//     const response = await fetch(url, {
//         method: "POST",
//         headers: {
//             "X-CSRF-TOKEN": token,
//             "Content-Type": contentType,
//         },
//         body: data,
//     });

//     if (spinner) spinner.classList.add("loaded");

//     let json = null;
//     const ct = response.headers.get("content-type");
//     if (ct && ct.includes("application/json")) {
//         json = await response.json();
//     }

//     if (!response.ok) {
//         await handleHttpError(response.status, json);
//     }

//     return {
//         ok: response.ok,
//         status: response.status,
//         data: json, 
//         message: json?.message ?? ""
//     };
// }

// /**
//  * 検索用
//  * @param {*} url 
//  * @param {*} data 
//  * @param {*} token 
//  * @param {*} contentType 
//  * @returns 
//  */
// async function searchFetch(url, data, token, contentType = "application/json") {
//     const spinner = document.getElementById("loading");
//     if (spinner) spinner.classList.remove("loaded");

//     const response = await fetch(url, {
//         method: "POST",
//         headers: {
//             "X-CSRF-TOKEN": token,
//             "Content-Type": contentType,
//         },
//         body: data,
//     });

//     if (response.status === 404) {
//         return null; // ← 正常
//     }

//     if (spinner) spinner.classList.add("loaded");
    
//     let json = null;
//     const ct = response.headers.get("content-type");
//     if (ct && ct.includes("application/json")) {
//         json = await response.json();
//     }

//     if (!response.ok) {
//         await handleHttpError(response.status, json);
//     }

//     return {
//         ok: response.ok,
//         status: response.status,
//         data: json ?? null,
//         message: json?.message ?? ""
//     };
// }

/**
 * 共通関数
 * @param {*} url 
 * @param {*} param1 
 * @returns 
 */
async function apiFetch(url, {
    method = "POST",
    data = null,
    token,
    contentType = "application/json",
    allow404 = false
} = {}) {

    const spinner = document.getElementById("loading");
    if (spinner) spinner.classList.remove("loaded");

    try {
        const response = await fetch(url, {
            method,
            headers: {
                "X-CSRF-TOKEN": token,
                "Content-Type": contentType,
            },
            body: data,
        });

        if (allow404 && response.status === 404) {
            return null;
        }

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
            data: json.data ?? null,
            message: json?.message ?? ""
        };

    } finally {
        if (spinner) spinner.classList.add("loaded");
    }
}

/**
 * 更新
 * @param {*} url 
 * @param {*} data 
 * @param {*} token 
 * @param {*} contentType 
 * @returns 
 */
async function updateFetch(url, data, token, contentType = "application/json") {
    return apiFetch(url, {
        method: "POST",
        data,
        token,
        contentType,
        allow404: false
    });
}

/**
 * 取得
 * @param {*} url 
 * @param {*} data 
 * @param {*} token 
 * @param {*} contentType 
 * @returns 
 */
async function searchFetch(url, data, token, contentType = "application/json") {
    return apiFetch(url, {
        method: "POST",
        data,
        token,
        contentType,
        allow404: true
    });
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