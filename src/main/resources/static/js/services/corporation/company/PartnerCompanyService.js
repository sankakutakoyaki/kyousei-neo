"use strict"

import { CompanyRepository } from "../../../repositories/corporation/company/CompanyRepository.js";

export const PartnerCompanyService = {
    async search(params = {}){
        return await CompanyRepository.search({
            ...params,
            category: APP.cache.common.companyCategory.PARTNER
        });
    }
};
