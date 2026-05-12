"use strict"

import { api } from "../../core/api/apiService";
import { CompanyRepository } from "../../repositories/company/CompanyRepository";

export const CompanyService = {
    // async getCombo(){
    //     const res = await api.get("/api/company/client/combo" );
    //     return res.data ?? [];
    // },

    async refreshCombo(){
        const list = await CompanyRepository.fetchCombo();
        APP.cache.page.companyComboList = list;
        return list;

    }
};