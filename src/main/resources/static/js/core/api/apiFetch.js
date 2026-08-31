"use strict"

import { startProcessing } from "../dom/loading.js";
import { processingEnd } from "../dom/loading.js";
import { DialogService } from "../ui/dialog/DialogService.js";

/**
 * 共通fetch
 */
export async function apiFetch(url, {
    method = "POST",
    data = null,
    allow404 = false,
    timeout = 15000,
    retry = 0,
    showProcessing = true
} = {}) {

    if(showProcessing){
        startProcessing();
    }

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
        } else if (data && (data.constructor === Object || Array.isArray(data))) {
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
            // data: result?.data ?? result,
            data: Object.prototype.hasOwnProperty.call(
                result ?? {},
                "data"
            )
                ? result.data
                : result,
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
                allow404,
                timeout,
                retry: retry - 1
            });
        }
        throw err;
    } finally {
        if(showProcessing){
            processingEnd();
        }
    }
}

/**
 * HTTPエラー共通処理
 * @param {*} status 
 */
// export async function handleHttpError(status, json) {
//     const message = json?.message;

//     if (window.ApiErrorHandler) {
//         return window.ApiErrorHandler(status, message);
//     }
//     if(status >= 500){
//         DialogService.error("システムエラーが発生しました");
//     } else {
//         DialogService.error(message);
//     }
// }
export async function handleHttpError(status, json) {
    const message =
        json?.message ||
        "エラーが発生しました";

    const error = new Error(message);

    error.status = status;
    error.field = json?.field ?? null;
    error.fields = json?.fields ?? [];

    throw error;
}
