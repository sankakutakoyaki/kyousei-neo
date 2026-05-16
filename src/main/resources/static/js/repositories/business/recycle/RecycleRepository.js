"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const RecycleRepository = {
    async search(params){
        const res = await RequestClient.request({queryId:"recycleList", params});
        return res.data ?? [];
    },

    async find(params){
        const res = await RequestClient.request({queryId:"recycleDetail", params});
        return res.data?.[0] ?? null;
    },

    async save(params){
        return await RequestClient.request({queryId: "recycleSave", params});
    },

    async remove(params){
        return await RequestClient.request({queryId: "recycleDeleteByIds", params});
    },

    async download(params){
        return await RequestClient.request({queryId: "recycleCsv", params});
    }
};