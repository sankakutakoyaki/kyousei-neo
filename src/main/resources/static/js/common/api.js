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

    if (!response.ok) {
        handleHttpError(response.status);
        throw new Error(`HTTP_ERROR_${response.status}`);
    }

    if (spinner) spinner.classList.add("loaded");

    return await response.json();
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

    if (!response.ok) {
        handleHttpError(response.status);
        throw new Error(`HTTP_ERROR_${response.status}`);
    }

    if (spinner) spinner.classList.add("loaded");
    
    return await response.json();
}

/**
 * HTTPエラー共通処理
 * @param {*} status 
 */
function handleHttpError(status) {
    switch (status) {
        case 401:
            openMsgDialog("msg-dialog", "ログインが必要です。", "red");
            location.href = "/";
            break;
        case 403:
            openMsgDialog("msg-dialog", "権限がありません。", "red");
            break;
        case 400:
            openMsgDialog("msg-dialog", "入力内容に誤りがあります。", "red");
            break;
        case 404:
            openMsgDialog("msg-dialog", "処理が見つかりません。", "red");
            break;
        case 500:
            openMsgDialog("msg-dialog", "サーバーエラーが発生しました。", "red");
            break;
    }
}
// /**
//  * Post送信する
//  * @param {アドレス} url 
//  * @param {送信するデータ} data 
//  * @param {トークン} token 
//  * @param {コンテントタイプ} contentType 
//  * @returns　json
//  */
// // async function postFetch(url, data, token, contentType = "application/json") {
// //     const spinner = document.getElementById("loading");
// //     if (spinner) spinner.classList.remove("loaded");

// //     try {
// //         const response = await fetch(url, {
// //             method: "POST",
// //             headers: {
// //                 "X-CSRF-TOKEN": token,
// //                 "Content-Type": contentType,
// //             },
// //             body: data,
// //         });

// //         if (!response.ok) {
// //             switch (response.status) {
// //                 case 400:
// //                     openMsgDialog("msg-dialog", "入力内容に誤りがあります。", "red");
// //                     break;
// //                 case 401:
// //                     openMsgDialog("msg-dialog", "ログインが必要です。", "red");
// //                     location.href = "/login";
// //                     break;
// //                 case 403:
// //                     openMsgDialog("msg-dialog", "権限がありません。", "red");
// //                     break;
// //                 case 404:
// //                     openMsgDialog("msg-dialog", "処理が見つかりません。", "red");
// //                     break;
// //                 case 500:
// //                     openMsgDialog("msg-dialog", "サーバーエラーが発生しました。", "red");
// //                     break;
// //                 default:
// //                     openMsgDialog("msg-dialog", `エラーが発生しました (${response.status})`, "red");
// //             }
// //             return null;
// //         }

// //         let result;
// //         try {
// //             result = await response.json();
// //         } catch {
// //             openMsgDialog("msg-dialog", "サーバーから不正な応答が返されました。", "red");
// //             return null;
// //         }

// //         if (!result.success) {
// //             handleResultError(result);
// //             return null;
// //         }

// //         return result;

// //     } catch (e) {
// //         return handleFetchError(e);

// //     } finally {
// //         if (spinner) spinner.classList.add("loaded");
// //     }
// // }
// async function postFetch(url, data, token, contentType = "application/json") {
//     const spinner = document.getElementById("loading");
//     if (spinner) spinner.classList.remove("loaded");

//     try {
//         const response = await fetch(url, {
//             method: "POST",
//             headers: {
//                 "X-CSRF-TOKEN": token,
//                 "Content-Type": contentType,
//             },
//             body: data,
//         });

//         // ★ HTTPエラーはここで完全に止める
//         if (!response.ok) {
//             switch (response.status) {
//                 case 401:
//                     openMsgDialog("msg-dialog", "ログインが必要です。", "red");
//                     location.href = "/";
//                     break;
//                 case 403:
//                     openMsgDialog("msg-dialog", "権限がありません。", "red");
//                     break;
//                 case 400:
//                     openMsgDialog("msg-dialog", "入力内容に誤りがあります。", "red");
//                     break;
//                 case 404:
//                     openMsgDialog("msg-dialog", "処理が見つかりません。", "red");
//                     break;
//                 case 500:
//                     openMsgDialog("msg-dialog", "サーバーエラーが発生しました。", "red");
//                     break;
//                 default:
//                     openMsgDialog("msg-dialog", `エラーが発生しました (${response.status})`, "red");
//             }

//             throw new Error(`HTTP_ERROR_${response.status}`);
//         }

//         const result = await response.json();

//         // success がある場合だけチェック
//         if (result && typeof result.success === "boolean") {
//             // ★ 業務エラーも例外にする
//             if (!result.success) {
//                 handleResultError(result);
//                 throw new Error("BUSINESS_ERROR");
//             }
//         }
//         return result;

//     } finally {
//         if (spinner) spinner.classList.add("loaded");
//     }
// }

// /**
//  * fetch 共通エラーハンドリング
//  */
// function handleFetchError(e) {
//     if (e.name === "AbortError") {
//         openMsgDialog("msg-dialog", "通信がタイムアウトしました。", "red");
//         return null;
//     }

//     if (e instanceof TypeError) {
//         openMsgDialog("msg-dialog", "サーバーに接続できません。ネットワーク状態を確認してください。", "red");
//         return null;
//     }

//     openMsgDialog("msg-dialog", "予期しないエラーが発生しました。", "red");
//     return null;
// }

// function handleResultError(result) {
//     openMsgDialog(
//         "msg-dialog",
//         result.message ?? "処理に失敗しました。",
//         "red"
//     );
// }