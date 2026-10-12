"use strict";

import { api } from "../../../core/api/apiService.js";
import { RequestClient } from "../../../core/api/RequestClient.js";

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

    async fetchCombo(scope = "client"){
        if(scope !== "client" && scope !== "partner"){
            throw new Error(`未対応の会社コンボ種別: ${scope}`);
        }
        const res = await api.get(`/api/company/${scope}/combo`);
        return res.data ?? [];
    }
};