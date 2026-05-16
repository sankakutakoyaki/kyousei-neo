"use strict"

// Subscriptionをサーバーに登録
async function saveSubscriptionToServer(subscription) {
    // subscriptionオブジェクトを確認
    const json = JSON.stringify(subscription);
    const push = JSON.parse(json);

    // p256dh: ECDH公開鍵
    const p256dh = subscription.getKey('p256dh');
    const auth = subscription.getKey('auth');

    // 必要であればbase64urlエンコードしてサーバーに送信
    const p256dhBase64Url = arrayBufferToBase64Url(p256dh);
    const authBase64Url = arrayBufferToBase64Url(auth);

    // サーバーへ送信するデータを構築
    const subscriptionData = {
        endpoint: push.endpoint,
        expirationTime: push.expirationTime,
        p256dh: p256dhBase64Url,
        auth: authBase64Url,
        username: user_name
    };

    const response = await fetch('/api/push/subscribe', {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': token,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(subscriptionData)
    });

    if (!response) {
        console.error('サーバーへのサブスクリプションの送信に失敗しました');
    } else {
        console.info('サーバーへのサブスクリプションの送信が完了しました');
    }
}

// // メッセージを送信
// async function sendSubscriptionToServer() {
//     // console.log("テキストを送信中...");
//     const msg = document.getElementById('push-msg-text');
//     if (!msg.value) {
//         // console.log("テキストを入力してください");
//         return;
//     }
//     const registration = await navigator.serviceWorker.getRegistration();
//     const subscription = await registration.pushManager.getSubscription();
//     const json = JSON.stringify(subscription);
//     const push = JSON.parse(json);
//     // console.log('endpoint: ' + push.endpoint);

//     const data = "message=" + encodeURIComponent(msg.value) + "&csrftoken=" + encodeURIComponent(token);
//     await postFetch('/api/push/send', data, token, 'application/x-www-form-urlencoded');
// }

// Push 通知を購読する関数
async function subscribeToPush(registration) {
    try {
        let subscription = await registration.pushManager.getSubscription();
        if (subscription) {
            try {
                // console.log("VAPID 公開鍵を取得中...");
                const applicationServerKey = base64UrlToUint8Array(await getVapidPublicKey());
                // console.log("公開鍵のバイト数:", applicationServerKey.length);
                // console.log("先頭バイト:", applicationServerKey[0].toString(16));

                // console.log("Push サブスクリプションを登録中...");
                subscription = await registration.pushManager.subscribe({
                    userVisibleOnly: true,
                    applicationServerKey: applicationServerKey
                });
                console.log("Subscription 登録成功:", subscription);

            } catch (error) {
                console.error('Subscription 登録失敗:', error);
                return false;
            }

            // サーバーにサブスクリプションデータを送信
            await saveSubscriptionToServer(subscription);
        }
    } catch (error) {
        console.error("Subscription 登録失敗: ", error);
        if (permission === 'denied') {
            alert('Push通知が拒否されているようです。ブラウザの設定からPush通知を有効化してください');
            return false;
        }
    }
    // }
}

// Service Worker を登録する関数
async function registerServiceWorker() {
    try {
        const registration = await navigator.serviceWorker.register('/sw.js');
        // console.log('Service Worker registered successfully:', registration);

        // Service Worker が登録されていない場合
        if (!registration || !registration.pushManager) {
            // console.error('PushManager is not available.');
            return;
        }

        // サービスワーカーが準備完了するまで待機
        await navigator.serviceWorker.ready;

        await subscribeToPush(registration);
    } catch (error) {
        console.error('Service Worker Registration Failed:', error);
    }
}

// プッシュ通知の登録を解除する
async function unsubscribeButtonHandler() {
    const registration = await navigator.serviceWorker.getRegistration();
    const subscription = await registration.pushManager.getSubscription();

    if (!registration) {
        console.error("Service Worker is not registered.");
        return;
    }

    if (!subscription) {
        console.warn("No active push subscription found.");
        return;
    }

    const unsubscribed = await subscription.unsubscribe();
    if (unsubscribed) {
        console.info("Successfully unsubscribed from push notifications.");
    } else {
        console.error("Failed to unsubscribe.");
    }

    fetch('/api/push/remove-subscription', {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': token,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ endpoint: subscription.endpoint })
    });
}

// サーバーからVAPID公開鍵を取得する
async function getVapidPublicKey() {
    const response = await fetch('/api/push/vapid-public-key').then(response => response.text());  // サーバーから取得
    return response;
}

// // Base64 URL 形式の公開鍵を Uint8Array に変換する関数
function base64UrlToUint8Array(base64Url) {
    const padding = "=".repeat((4 - (base64Url.length % 4)) % 4);
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/") + padding;
    const rawData = atob(base64);
    const uint8Array = new Uint8Array([...rawData].map((c) => c.charCodeAt(0)));
    // console.log("base64UrlKey: " + base64Url)
    // console.log("base64Binary: " + uint8Array)
    return uint8Array;
}

// ArrayBufferをBase64URLに変換する関数
function arrayBufferToBase64Url(buffer) {
    const base64 = btoa(String.fromCharCode.apply(null, new Uint8Array(buffer)));
    return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

// 通知の権限をリクエストする関数
async function requestNotificationPermission() {
    // ユーザーに通知の権限をリクエスト
    if (Notification.permission !== "granted") {
        Notification.requestPermission().then(function (permission) {
            if (permission === "granted") {
                console.log("Notification permission granted!");
            } else {
                console.log("Notification permission denied.");
            }
        });
    }
}
