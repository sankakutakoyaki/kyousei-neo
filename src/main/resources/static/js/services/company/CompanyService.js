"use strict"

import { api } from "../../core/api/apiService.js";
import { CompanyRepository } from "../../repositories/company/CompanyRepository.js";
import { PageCacheService } from "../cache/PageCacheService.js";

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