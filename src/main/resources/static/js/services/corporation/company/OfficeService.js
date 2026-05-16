"use strict"

import { OfficeRepository } from "../../../repositories/corporation/company/OfficeRepository.js";
import { PageCacheService } from "../../cache/PageCacheService.js";

export const OfficeService = {
    async refreshCombo(){
        const list = await OfficeRepository.fetchCombo();
        PageCacheService.set("officeComboList", list);
        return list;

    }
};