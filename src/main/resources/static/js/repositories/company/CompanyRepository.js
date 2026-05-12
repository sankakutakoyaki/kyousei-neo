"use strict"

import { api } from "../../core/api/apiService";

export const CompanyRepository = {
    async fetchCombo(){
        const res = await api.get("/api/company/client/combo");
        return res.data ?? [];
    }
};