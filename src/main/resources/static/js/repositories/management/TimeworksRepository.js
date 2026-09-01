"use strict"

import { api } from "../../../core/api/apiService.js";

const query = (params = {}) => {
    const search = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== "") search.set(key, value);
    });
    const suffix = search.toString();
    return suffix ? `?${suffix}` : "";
};

export const TimeworksRepository = {
    async search(params = {}) {
        const res = await api.get(`/api/timeworks/list${query(params)}`);
        return res.data ?? [];
    },

    async findToday() {
        const res = await api.get("/api/timeworks/today");
        return res.data;
    },

    async stamp(stampType) {
        return api.post("/api/timeworks/stamp", {stampType});
    }
};
