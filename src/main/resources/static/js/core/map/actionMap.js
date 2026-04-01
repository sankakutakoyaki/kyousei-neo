"use strict"

import { switchTab } from "../ui/tab.js";

export const actionMap = {
    // テーブル画面をフィルターする
    filter: (c, el) => {
        if(!c) return;

        const v = el.value;
        const value = v === "" ? null : Number(v);

        c.filter(el.dataset.field, value);
    },
    // サーチボックスでフィルターする
    search: (c, el) => {
        if(!c) return;
        c.search(el.value);
    },
    // タブ切り替え
    tab: (_, el) => switchTab(el),
    // ケバブ 新規
    create: (c) => c?.create(),
    // ケバブ 削除
    delete: (c) => c?.deleteSelected(),
    // ケバブ ダウンロード
    download: (c) => c?.downloadSelected(),
    //　ケバブ 更新
    reload: (c) => c?.refresh()
};