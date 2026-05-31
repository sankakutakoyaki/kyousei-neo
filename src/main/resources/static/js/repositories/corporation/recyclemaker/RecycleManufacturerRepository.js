"use strict";

import { RequestClient } from "../../../core/api/RequestClient.js";

export const RecycleManufacturerRepository = {
    async search(params){
        const res = await RequestClient.request({queryId:"recycleManufacturerList", params});
        return res.data ?? [];
    },

    async find(params){
        const res = await RequestClient.request({queryId:"recycleManufacturerDetail", params});
        return res.data?.[0] ?? null;
    },

    async save(params){
        return await RequestClient.request({queryId:"recycleManufacturerSave", params});
    },

    async remove(params){
        return await RequestClient.request({queryId:"recycleManufacturerDeleteByIds", params});
    },

    async download(params){
        return await RequestClient.request({queryId:"recycleManufacturerCsv", params});
    }
};