"use strict"

// -------------------------------------------------------------------------------------------------------------------------------------- 初期化時処理

window.addEventListener("load", async () => {

    // if ('serviceWorker' in navigator && 'PushManager' in window) {
    //     registerServiceWorker().catch(err => console.error('Error during registration:', err));
    // } else {
    //     console.warn('Push messaging is not supported in this browser.');
    // }
    // if (!employeeId || employeeId <= 0) {
    //     return; // ← これが重要
    // }

    document.getElementById('ai-form').addEventListener('submit', async e => {
        e.preventDefault(); // ★ form送信を止める

        const q = e.target.question.value;
        if (!q) return;

        const res = await searchFetch('/api/ai/mock', JSON.stringify({ question: q }), token);

        // const json = await res.json();
        document.getElementById('answer-area').textContent = res.data.answer;
    });
});