"use strict";

import { RequestClient } from "../../../core/api/RequestClient.js";

export const ItemMasterRepository = {
    async search(params) {
        const res = await RequestClient.request({queryId: "itemMasterList", params});
        return res.data ?? [];
    },

    async findByJanCode(params) {
        const res = await RequestClient.request({queryId: "itemMasterFindByJanCode", params});
        return res.data?.[0] ?? null;
    },

    async find(params) {
        const res = await RequestClient.request({queryId: "itemMasterDetail", params});
        return res.data?.[0] ?? null;
    },

    async save(params) {
        return await RequestClient.request({queryId: "itemMasterSave", params});
    },

    async remove(params) {
        return await RequestClient.request({queryId: "itemMasterDeleteByIds", params});
    },

    async download(params) {
        return await RequestClient.request({queryId: "itemMasterCsv", params});
    }
};