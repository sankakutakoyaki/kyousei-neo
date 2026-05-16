"use strict";

import { CompanyRepository } from "../../../repositories/corporation/company/CompanyRepository.js";

export const CompanyService = {
    async search(params){
        return await CompanyRepository.search("companyList", params);
    },

    async find(params){
        return await CompanyRepository.find("companyDetail", params);
    },

    async save(params){
        return await CompanyRepository.save("companySave", params);
    },

    async remove(params){
        return await CompanyRepository.remove("companyDeleteByIds", params);
    },

    async download(params){
        return await CompanyRepository.download("companyCsv", params);
    },

    async fetchCombo(){
        return await CompanyRepository.fetchCombo("companyCombo");
    }
};