"use strict"

import { api } from "../../core/api/apiService";

export const OfficeRepository = {
    async fetchCombo(){
        const res = await api.get("/api/office/client/combo");
        return res.data ?? [];
    }
};