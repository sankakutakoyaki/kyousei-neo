"use strict";

import { RequestClient } from "../../../core/request/RequestClient.js";

export const OfficeRepository = {
    async search(params){
        const res = await RequestClient.request({queryId:"clientOfficeList", params});
        return res.data ?? [];
    },

    async find(params){
        const res = await RequestClient.request({queryId:"officeDetail", params});
        return res.data?.[0] ?? null;
    },

    async save(params){
        return await RequestClient.request({queryId:"officeSave", params});
    },

    async remove(params){
        return await RequestClient.request({queryId:"officeDeleteByIds", params});
    },

    async download(params){
        return await RequestClient.request({queryId:"officeCsv", params});
    },

    async fetchCombo(){
        const res = await RequestClient.request({queryId:"officeCombo"});
        return res.data ?? [];
    }
};