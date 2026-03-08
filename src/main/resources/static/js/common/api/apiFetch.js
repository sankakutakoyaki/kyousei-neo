"use strict"

/**
 * 共通fetch
 */
async function apiFetch(url, {
    method = "POST",
    data = null,
    token,
    allow404 = false,
    timeout = 15000,
    retry = 0
} = {}) {

    const spinner = document.getElementById("loading");
    if (spinner) spinner.classList.remove("loaded");

    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), timeout);

    try {

        const headers = {};

        if (token) {
            headers["X-CSRF-TOKEN"] = token;
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

        clearTimeout(timer);

        if (allow404 && response.status === 404) {
            return null;
        }

        if (!response.ok) {

            let err = null;

            try {
                err = await response.json();
            } catch {}

            await handleHttpError(response.status, err);
        }

        const ct = response.headers.get("content-type") || "";

        let result;

        if (ct.includes("application/json")) {

            result = await response.json();

        } else if (
            ct.includes("application/octet-stream") ||
            ct.includes("application/pdf") ||
            ct.includes("application/zip")
        ) {

            result = await response.blob();

        } else {

            result = await response.text();

        }

        return {
            ok: true,
            status: response.status,
            data: result?.data ?? result,
            message: result?.message ?? ""
        };

    } catch (err) {

        if (err.name === "AbortError") {
            throw new Error("通信がタイムアウトしました");
        }

        if (retry > 0) {

            return apiFetch(url, {
                method,
                data,
                token,
                allow404,
                timeout,
                retry: retry - 1
            });

        }

        throw err;

    } finally {

        clearTimeout(timer);

        if (spinner) spinner.classList.add("loaded");

    }
}

/**
 * 更新
 * @param {*} url 
 * @param {*} data 
 * @param {*} token 
 * @returns 
 */
async function updateFetch(url, data, token) {
    return apiFetch(url, {
        method: "POST",
        data,
        token,
        retry: 1
    });
}

/**
 * 取得
 * @param {*} url 
 * @param {*} data 
 * @param {*} token 
 * @returns 
 */
async function searchFetch(url, data, token) {
    return apiFetch(url, {
        method: "POST",
        data,
        token,
        allow404: true,
        retry: 1
    });
}

/**
 * アップロード
 * @param {*} url 
 * @param {*} formData 
 * @param {*} token 
 * @returns 
 */
async function formFetch(url, formData, token) {
    return apiFetch(url, {
        method: "POST",
        data: formData,
        token,
        timeout: 60000
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