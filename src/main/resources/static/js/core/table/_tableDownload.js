// "use strict"

// /** ここから以前のもの */
// /**
//  * 選択された要素のCSVファイルを作成してダウンロードする
//  * @param {*} parent 
//  * @param {*} downloaddata 
//  */
// async function downloadCsv(tableId, url) {
//     // 選択された要素を取得する
//     const data = getAllSelectedIds(tableId);
//     if (data) {
//         funcDownloadCsv(data, url);
//         // チェック状態を解除
//         turnOffAllCheckBtn(tableId);
//         detachmentCheckedToAllRow(tableId, false);
//     }
// }

// /**
//  * 選択された要素のCSVファイルを作成してダウンロードする
//  * @param {*} parent 
//  * @param {*} downloaddata 
//  */
// async function downloadCsvByBetweenDate(tableId, url, startStr, endStr) {
//     // 選択された要素を取得する
//     const data = getAllSelectedIds(tableId);
//     if (data) {
//         funcDownloadCsv({ids:data, start:startStr, end:endStr}, url);
//     }
// }

// /**
//  * CSVダウンロード処理部分
//  * @param {*} data 
//  * @param {*} url 
//  */
// async function funcDownloadCsv(data, url) {
//     startProcessing();

//     try {
//         const response = await fetch(url, {
//             method: "POST",
//             headers: {
//                 "Content-Type": "application/json",
//                 "X-CSRF-TOKEN": token
//             },
//             // body: JSON.stringify(data)
//             body: data
//         });

//         if (!response.ok) {
//             openMsgDialog("msg-dialog", "CSVの取得に失敗しました", "red");
//             return;
//         }

//         const blob = await response.blob();

//         const objectUrl = URL.createObjectURL(blob);
//         const downloadLink = document.createElement("a");
//         downloadLink.href = objectUrl;
//         downloadLink.download = getNowNoBreak() + ".csv";
//         downloadLink.click();
//         downloadLink.remove();

//         URL.revokeObjectURL(objectUrl);

//     } finally {
//         processingEnd();
//     }
// }
// /**
//  * 削除
//  * @param {*} tableId 
//  * @param {*} url 
//  */
// async function deleteTablelist(tableId, url) {
//     // 選択された要素を取得する
//     const data = getAllSelectedIds(tableId);
//     if (data) {
//         return await updateFetch(url, data, token);
//     }
// }