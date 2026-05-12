"use strict"

import { api } from "../../core/api/apiService";
import { CompanyRepository } from "../../repositories/company/CompanyRepository";
import { PageCacheService } from "../cache/PageCacheService";

export const CompanyService = {
    // async getCombo(){
    //     const res = await api.get("/api/company/client/combo" );
    //     return res.data ?? [];
    // },

    async refreshCombo(){
        const list = await CompanyRepository.fetchCombo();
        // APP.cache.page.companyComboList = list;
        PageCacheService.set("companyComboList", list);
        return list;

    }
};