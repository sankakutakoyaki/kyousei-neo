"use strict"

import { api } from "../../core/api/apiService.js";

export const CompanyRepository = {
    async fetchCombo(){
        const res = await api.get("/api/company/client/combo");
        return res.data ?? [];
    }
};