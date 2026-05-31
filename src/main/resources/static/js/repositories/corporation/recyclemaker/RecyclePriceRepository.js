"use strict";

import { RequestClient } from "../../../core/api/RequestClient.js";

export const RecyclePriceRepository = {
    async search(params){
        const res = await RequestClient.request({queryId:"recyclePriceList", params});
        return res.data ?? [];
    },

    async find(params){
        const res = await RequestClient.request({queryId:"recyclePriceDetail", params});
        return res.data?.[0] ?? null;
    },

    async save(params){
        return await RequestClient.request({queryId:"recyclePriceSave", params});
    },

    async remove(params){
        return await RequestClient.request({queryId:"recyclePriceDeleteByIds", params});
    },

    async download(params){
        return await RequestClient.request({queryId:"recyclePriceCsv", params});
    }
};