"use strict";

import { RequestClient } from "../../../core/request/RequestClient.js";

export const CompanyRepository = {
    async search(queryId, params){
        const res = await RequestClient.request({queryId, params});
        return res.data ?? [];
    },

    async find(queryId, params){
        const res = await RequestClient.request({queryId, params});
        return res.data?.[0] ?? null;
    },

    async save(queryId, params){
        return await RequestClient.request({queryId, params});
    },

    async remove(queryId, params){
        return await RequestClient.request({queryId, params});
    },

    async download(queryId, params){
        return await RequestClient.request({queryId, params});
    },

    async fetchCombo(){
        const res = await RequestClient.request({queryId: "companyCombo"});
        return res.data ?? [];
    }
};