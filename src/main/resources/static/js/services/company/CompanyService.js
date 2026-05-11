"use strict"

import { api } from "../../core/api/apiService";

export const CompanyService = {
    async getCombo(){
        const res = await api.get("/api/company/client/combo" );
        return res.data ?? [];
    },

    async refreshCombo(){
        const list = await this.getCombo();
        APP.cache.page.companyComboList = list;
        return list;
    }
};