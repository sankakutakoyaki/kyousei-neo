"use strict";

import { RequestClient } from "../../../core/api/RequestClient.js";

export const RecycleMakerRepository = {
    async search(params){
        const res = await RequestClient.request({queryId:"recycleMakerList", params});
        return res.data ?? [];
    },

    async find(params){
        const res = await RequestClient.request({queryId:"recycleMakerDetail", params});
        return res.data?.[0] ?? null;
    },

    async save(params){
        return await RequestClient.request({queryId:"recycleMakerSave", params});
    },

    async remove(params){
        return await RequestClient.request({queryId:"recycleMakerDeleteByIds", params});
    },

    async download(params){
        return await RequestClient.request({queryId:"recycleMakerCsv", params});
    }
};