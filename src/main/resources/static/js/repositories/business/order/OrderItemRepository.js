"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const OrderItemRepository = {
    // async search(params){
    //     const res = await RequestClient.request({queryId:"orderItemList", params});
    //     return res.data ?? [];
    // },
    async search(params){
        const queryId = params?.itemModel
            ? "orderItemListByItemModel"
            : "orderItemList";
        const res = await RequestClient.request({queryId, params});
        return res.data ?? [];
    },

    async searchByItemModel(params) {
        const res = await RequestClient.request({queryId: "orderItemListByItemModel", params});
        return res.data ?? [];
    },

    async find(params){
        const res = await RequestClient.request({queryId:"orderItemDetail", params});
        return res.data?.[0] ?? null;
    },

    async save(params){
        return await RequestClient.request({queryId: "orderItemSave", params});
    },

    async remove(params){
        return await RequestClient.request({queryId: "orderItemDeleteByIds", params});
    },

    async download(params){
        return await RequestClient.request({queryId: "orderItemCsv", params});
    }
};