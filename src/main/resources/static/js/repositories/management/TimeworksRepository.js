"use strict"

import { api } from "../../core/api/apiService.js";

export const TimeworksRepository = {
    async search() {
        const res = await api.get("/api/timeworks/list");
        return res.data ?? [];
    },

    async findSelf() {
        const res = await api.get("/api/timeworks/self");
        return res.data;
    },

    async stamp(employeeId, stampType) {
        return api.post("/api/timeworks/stamp", {employeeId, stampType});
    },

    // 本人打刻時に取得できた位置情報を任意で送信する
    async stampSelf(stampType, location = null) {
        return api.post("/api/timeworks/stamp/self", {
            stampType,
            ...(location ?? {})
        });
    },

    async searchManagement(params) {
        const query = new URLSearchParams();
        Object.entries(params).forEach(([key, value]) => {
            if (value !== null && value !== undefined && value !== "") query.set(key, value);
        });
        const res = await api.get(`/api/timeworks/admin/list?${query}`);
        return res.data ?? [];
    },

    async findEmployeeCombo(officeId = null) {
        const suffix = officeId ? `?officeId=${encodeURIComponent(officeId)}` : "";
        const res = await api.get(`/api/timeworks/admin/employees${suffix}`);
        return res.data ?? [];
    },

    async updateTimes(params) {
        return api.post("/api/timeworks/admin/update", params);
    }
};
