const CACHE_NAME = `temperature-converter-v1`;

self.addEventListener('install', event => {
    event.waitUntil((async () => {
        const cache = await caches.open(CACHE_NAME);
        cache.addAll([
            '/',
            '/js/default.js',
            '/js/dialog.js',
            '/js/enterfocus.js',
            '/js/file.js',
            '/js/form.js',
            '/js/info.js',
            '/js/push.js',
            '/js/table.js',
            '/css/item/button.css',
            '/css/item/color.css',
            '/css/item/dialog.css',
            '/css/item/form.css',
            '/css/item/hover.css',
            '/css/item/input.css',
            '/css/item/pc-style.css',
            '/css/item/sp-style.css',
            '/css/item/table.css',
            '/css/default.css',
            '/css/employee.css',
            '/css/personnel.css',
            '/css/working_conditions.css'
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
