"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const OrderWorkRepository = {
    async search(params){
        const res = await RequestClient.request({queryId:"orderWorkList", params});
        return res.data ?? [];
    },

    async find(params){
        const res = await RequestClient.request({queryId:"orderWorkDetail", params});
        return res.data?.[0] ?? null;
    },

    async save(params){
        return await RequestClient.request({queryId: "orderWorkSave", params});
    },

    async remove(params){
        return await RequestClient.request({queryId: "orderWorkDeleteByIds", params});
    },

    async download(params){
        return await RequestClient.request({queryId: "orderWorkCsv", params});
    }
};