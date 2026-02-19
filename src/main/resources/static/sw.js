"use strict"

const CACHE_NAME = 'temperature-converter-v1';

self.addEventListener('install', event => {
    event.waitUntil((async () => {
        const cache = await caches.open(CACHE_NAME);
        await cache.addAll([
            '/',
            '/js/common/api.js',
            '/js/common/check.js',
            '/js/common/default.js',
            '/js/common/dialog.js',
            '/js/common/enterfocus.js',
            '/js/common/file.js',
            '/js/common/form.js',
            '/js/common/info.js',
            '/js/common/keyCaseConverter.js',
            '/js/common/list.js',
            '/js/common/name.js',
            '/js/common/table.js',
            '/js/common/time.js',
            '/css/common/button.css',
            '/css/common/color.css',
            '/css/common/default.css',
            '/css/common/dialog.css',
            '/css/common/form.css',
            '/css/common/hover.css',
            '/css/common/input.css',
            '/css/common/list.css',
            '/css/common/pc-style.css',
            '/css/common/sp-style.css',
            '/css/common/table.css'
        ]);
    })());
});

self.addEventListener('push', function (event) {
    console.log("✅ [Service Worker] Push event received!"); // ← ここで確認
    
    const data = event.data ? event.data.text() : '通知内容なし';

    event.waitUntil(
        self.registration.showNotification('お知らせ', {
            body: data,
            icon: '/icons/info.png' // 任意のアイコン
        })
    );
});

// プッシュ通知クリック時に遷移させる
self.addEventListener('notificationclick', () => {
    clients.openWindow('https://www.kyouseipro.com/')
});
