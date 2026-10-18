"use strict";

const INTERVAL = 30 * 60 * 1000;

let timer = null;
let running = false;
let lastCheck = Date.now();

export async function initSessionKeepAlive() {
    try {
        const response = await fetch("/api/session/info", {
            credentials: "same-origin",
            cache: "no-store"
        });

        if (response.status === 401) {
            redirectToLogin();
            return;
        }

        if (!response.ok) return;

        const result = await response.json();

        if (result.keepAlive) {
            startSessionKeepAlive();
        }
    } catch (error) {
        console.warn("セッション情報の取得に失敗しました", error);
    }
}

function startSessionKeepAlive() {
    if (timer) return;

    timer = setInterval(() => {
        checkSession();
    }, INTERVAL);

    document.addEventListener("visibilitychange", () => {
        if (!document.hidden && Date.now() - lastCheck >= INTERVAL) {
            checkSession();
        }
    });
}

async function checkSession() {
    if (running) return;

    running = true;

    try {
        const response = await fetch("/api/session/keepalive", {
            credentials: "same-origin",
            cache: "no-store"
        });

        if (response.status === 401) {
            stopSessionKeepAlive();
            redirectToLogin();
            return;
        }

        if (response.ok) {
            lastCheck = Date.now();
        }
    } catch (error) {
        console.warn("セッション維持通信に失敗しました", error);
    } finally {
        running = false;
    }
}

export function stopSessionKeepAlive() {
    if (timer) {
        clearInterval(timer);
        timer = null;
    }
}

function redirectToLogin() {
    window.location.replace("/");
}