"use strict";

import { RequestClient } from "../../../core/api/RequestClient.js";

export const RecycleItemRepository = {
    async search(params){
        const res = await RequestClient.request({
            queryId: "recycleItemList",
            params
        });
        return res.data ?? [];
    }
};