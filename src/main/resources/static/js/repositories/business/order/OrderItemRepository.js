"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const OrderItemRepository = {
    async search(params){
        const res = await RequestClient.request({queryId: "orderItemList", params});
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
    },

    async arrival(params){
        return await RequestClient.request({queryId: "orderItemArrival", params});
    },

    async create(params){
        return await RequestClient.request({queryId: "orderItemCreate", params});
    },
};