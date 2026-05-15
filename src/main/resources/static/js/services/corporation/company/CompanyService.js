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

// "use strict"

// // import { api } from "../../core/api/apiService.js";
// import { CompanyRepository } from "../../../repositories/corporation/company/CompanyRepository.js";
// import { PageCacheService } from "../../cache/PageCacheService.js";

// export const CompanyService = {
//     // async getCombo(){
//     //     const res = await api.get("/api/company/client/combo" );
//     //     return res.data ?? [];
//     // },

//     async refreshCombo(){
//         const list = await CompanyRepository.fetchCombo();
//         // APP.cache.page.companyComboList = list;
//         PageCacheService.set("companyComboList", list);
//         return list;

//     },

//     async searchClient(params){
//         return await CompanyRepository.search("clientList", params);
//     },

//     async searchPartner(params){
//         return await CompanyRepository.search("companyList", params);
//     }
// };