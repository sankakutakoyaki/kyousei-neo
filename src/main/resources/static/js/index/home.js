// -------------------------------------------------------------------------------------------------------------------------------------- 個人勤怠
// async function updateDisplay(result) {
//     if (result != null && result.data == -1) {
//         openMsgDialog("msg-dialog", result.message, 'red');
//     } else {
//         await searchForNameByCode(employeeId);
//     }
// }

// // コードから[timeworks]を取得
// async function searchForNameByCode(id) {
//     // [id=code]に入力されたコードから[timeworks]を取得して[id=name]に入力する
//     const data = "id=" + encodeURIComponent(parseInt(id));
//     const url = '/api/timeworks/get/today/id';
//     const contentType = 'application/x-www-form-urlencoded';
//     // [timeworks]を取得
//     const resultResponse = await postFetch(url, data, token, contentType);
//     entity = await resultResponse.json();

//     document.getElementById('start-time').textContent = "00:00:00";
//     document.getElementById('end-time').textContent = "00:00:00";
//     if (entity && entity.employee_id > 0) {
//         if (entity.start_time != null) document.getElementById('start-time').textContent = entity.start_time;
//         if (entity.end_time != null) document.getElementById('end-time').textContent = entity.end_time;
//     } 
// }

// -------------------------------------------------------------------------------------------------------------------------------------- 初期化時処理
window.addEventListener("load", async () => {
    if ('serviceWorker' in navigator && 'PushManager' in window) {
        registerServiceWorker().catch(err => console.error('Error during registration:', err));
    } else {
        console.warn('Push messaging is not supported in this browser.');
    }
    if (!employeeId || employeeId <= 0) {
        return; // ← これが重要
    }
    // await updateDisplay();
});