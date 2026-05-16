"use strict";

import { RequestClient } from "../../../core/api/RequestClient.js";

export const StaffRepository = {
    async search(params){
        const res = await RequestClient.request({queryId: "staffList", params});
        return res.data ?? [];
    },

    async find(params){
        const res = await RequestClient.request({queryId: "staffDetail",params});
        return res.data?.[0] ?? null;
    },

    async save(params){
        return await RequestClient.request({queryId: "staffSave", params});
    },

    async remove(params){
        return await RequestClient.request({queryId: "staffDeleteByIds", params});
    },

    async download(params){
        return await RequestClient.request({queryId: "staffCsv", params});
    }
};