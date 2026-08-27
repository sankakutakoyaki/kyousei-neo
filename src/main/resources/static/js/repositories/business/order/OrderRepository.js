"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const OrderRepository = {
    async search(params){
        const res = await RequestClient.request({queryId:"orderList", params});
        return res.data ?? [];
    },

    async find(params){
        const res = await RequestClient.request({queryId:"orderDetail", params});
        return res.data?.[0] ?? null;
    },

    async save(params){
        return await RequestClient.request({queryId: "orderSave", params});
    },

    async remove(params){
        return await RequestClient.request({queryId: "orderDeleteByIds", params});
    },

    async download(params){
        return await RequestClient.request({queryId: "orderCsv", params});
    },

    async findItems(params){
        const res = await RequestClient.request({queryId: "orderItemFormList", params});
        return res.data ?? [];
    },

    async findWorks(params) {
        const res = await RequestClient.request({queryId: "orderWorkFormList", params});
        return res.data ?? [];
    }
};