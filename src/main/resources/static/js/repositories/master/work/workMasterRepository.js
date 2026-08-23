"use strict";

import { RequestClient } from "../../../core/api/RequestClient.js";

export const WorkMasterRepository = {
    async search(params) {
        const res = await RequestClient.request({queryId: "workMasterList", params});
        return res.data ?? [];
    },

    async find(params) {
        const res = await RequestClient.request({queryId: "workMasterDetail", params});
        return res.data?.[0] ?? null;
    },

    async save(params) {
        return await RequestClient.request({queryId: "workMasterSave", params});
    },

    async remove(params) {
        return await RequestClient.request({queryId: "workMasterDeleteByIds", params});
    },

    async download(params) {
        return await RequestClient.request({queryId: "workmMasterCsv", params});
    }
};