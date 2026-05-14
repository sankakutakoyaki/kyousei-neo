"use strict"

// import { api } from "../../core/api/apiService.js";
import { RequestClient } from "../../core/request/RequestClient.js";

export const CompanyRepository = {
    async fetchCombo(){
        // const res = await api.get("/api/company/client/combo");
        const res = await RequestClient.request({queryId: "companyCombo"});
        return res.data ?? [];
    }
};