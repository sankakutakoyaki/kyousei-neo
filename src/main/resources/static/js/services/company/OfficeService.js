"use strict"

import { api } from "../../core/api/apiService";

export const OfficeService = {
    async getCombo(){
        const res = await api.get("/api/office/client/combo" );
        return res.data ?? [];
    },

    async refreshCombo(){
        const list = await this.getCombo();
        APP.cache.page.companyComboList = list;
        return list;
    }
};