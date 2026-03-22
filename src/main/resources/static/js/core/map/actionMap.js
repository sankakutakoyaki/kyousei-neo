"use strict"

import { switchTab } from "../ui/tab.js";

export const actionMap = {

    // =========================
    // 入力系
    // =========================
    filter: (c, el) => {
        if(!c) return;

        const v = el.value;
        const value = v === "" ? null : Number(v);

        c.filter(el.dataset.field, value);
    },

    search: (c, el) => {
        if(!c) return;
        c.search(el.value);
    },

    // =========================
    // UI系
    // =========================
    tab: (_, el) => switchTab(el),

    "select-on-focus": (_, el, e) => {
        if(e.type !== "focusin") return;
        el.select();
    },

    // =========================
    // CRUD
    // =========================
    create: (c, el) => {
        if(!c) return;
        c.create(el.dataset.form);
    },

    delete: (c) => {
        if(!c) return;
        c.deleteSelected();
    },

    download: (c) => {
        if(!c) return;
        c.downloadSelected();
    },

    reload: (c) => {
        if(!c) return;
        c.refresh();
    }

};