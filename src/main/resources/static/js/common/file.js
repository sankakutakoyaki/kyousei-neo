"use strict"

// function createCsvThenDownload(text) {
//     // CSVファイルをダウンロード
//     const bom = new Uint8Array([0xef, 0xbb, 0xbf]);
//     const blob = new Blob([bom, text], { type: "text/csv" });
//     const objectUrl = URL.createObjectURL(blob);
//     const downloadLink = document.createElement("a");
//     downloadLink.download = getNowNoBreak() + ".csv";
//     downloadLink.href = objectUrl;
//     downloadLink.click();
//     downloadLink.remove();
// }

// // 削除処理
// async function deleteFiles(path, url) {
//     const data = "url=" + encodeURIComponent(path);
//     const resultResponse = await postFetch(url, data, token, 'application/x-www-form-urlencoded');
//     return await resultResponse.json();
// }
