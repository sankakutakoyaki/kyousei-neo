"use strict"

import { api } from "../../core/api/apiService";
import { OfficeRepository } from "../../repositories/company/OfficeRepository";
import { PageCacheService } from "../cache/PageCacheService";

export const OfficeService = {
    // async getCombo(){
    //     const res = await api.get("/api/office/client/combo" );
    //     return res.data ?? [];
    // },

    async refreshCombo(){
        const list = await OfficeRepository.fetchCombo();
        // APP.cache.page.officeComboList = list;
        PageCacheService.set("officeComboList", list);
        return list;

    }
};