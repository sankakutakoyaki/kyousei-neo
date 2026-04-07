"use strict"

import { startProcessing } from "../dom/process.js";
import { processingEnd } from "../dom/process.js";
/**
 * 共通fetch
 */
export async function apiFetch(url, {
    method = "POST",
    data = null,
    allow404 = false,
    timeout = 15000,
    retry = 0
} = {}) {

    startProcessing();

    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), timeout);

    try {
        const headers = {};

        if (APP.security.csrfToken) {
            headers[APP.security.csrfHeader] = APP.security.csrfToken
        }

        let body = null;

        if (data instanceof FormData) {
            body = data;
        } else if (data && data.constructor === Object) {
            headers["Content-Type"] = "application/json";
            body = JSON.stringify(data);
        } else {
            body = data;
        }

        const response = await fetch(url, {
            method,
            headers,
            body,
            signal: controller.signal
        });

        if (allow404 && response.status === 404) {
            return null;
        }

        const ct = response.headers.get("content-type") || "";
        const cd = response.headers.get("content-disposition") || "";

        let result = null;

        try {
            if (ct.includes("application/json")) {
                result = await response.json();   // ★1回だけ
            } else if (ct.includes("application/") || ct.includes("text/csv")) {
                result = await response.blob();
            } else {
                result = await response.text();
            }
        } catch(e){
            result = null;
        }

        if (!response.ok) {
            await handleHttpError(response.status, result);
            return {
                ok: false,
                status: response.status,
                data: result
            };
        }

        return {
            ok: true,
            status: response.status,
            data: result?.data ?? result,
            message: result?.message ?? "",
            title: cd
        };
    } catch (err) {
        if (err.name === "AbortError") {
            throw new Error("通信がタイムアウトしました");
        }

        if (retry > 0) {
            return apiFetch(url, {
                method,
                data,
                // token,
                allow404,
                timeout,
                retry: retry - 1
            });
        }
        throw err;
    } finally {
        processingEnd();
    }
}

// /**
//  * 更新
//  * @param {*} url 
//  * @param {*} data 
//  * @param {*} token 
//  * @returns 
//  */
// export async function updateFetch(url, data, token) {
//     return apiFetch(url, {
//         method: "POST",
//         data,
//         token,
//         retry: 1
//     });
// }

// /**
//  * 取得
//  * @param {*} url 
//  * @param {*} data 
//  * @param {*} token 
//  * @returns 
//  */
// export async function searchFetch(url, data, token) {
//     return apiFetch(url, {
//         method: "POST",
//         data,
//         token,
//         allow404: true,
//         retry: 1
//     });
// }

// /**
//  * アップロード
//  * @param {*} url 
//  * @param {*} formData 
//  * @param {*} token 
//  * @returns 
//  */
// export async function formFetch(url, formData, token) {
//     return apiFetch(url, {
//         method: "POST",
//         data: formData,
//         token,
//         timeout: 60000
//     });
// }

/**
 * HTTPエラー共通処理
 * @param {*} status 
 */
export async function handleHttpError(status, json) {
    const message = json?.message;

    if (window.ApiErrorHandler) {
        return window.ApiErrorHandler(status, message);
    }
    console.error(status, message);
}

// async function handleHttpError(status, json) {
//     switch (status) {
//         case 401:
//             openMsgDialog("msg-dialog", json?.message || "ログインが必要です。", "red");
//             location.reload(); // 再ログイン
//             break;
//         case 403:
//             openMsgDialog("msg-dialog", json?.message || "権限がありません。", "red");
//             break;
//         case 400:
//             openMsgDialog("msg-dialog", json?.message || "入力内容に誤りがあります。", "red");
//             break;
//         case 404:
//             openMsgDialog("msg-dialog", json?.message || "処理が見つかりません。", "red");
//             break;
//         case 500:
//             openMsgDialog("msg-dialog", json?.message || "サーバーエラーが発生しました。", "red");
//             break;
//     }
// }