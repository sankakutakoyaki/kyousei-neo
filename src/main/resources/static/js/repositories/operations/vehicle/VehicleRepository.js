"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const VehicleRepository = {
    async search(params){
        const res = await RequestClient.request({queryId: "vehicleList", params});
        return res.data ?? [];
    },
    async find(params){
        const res = await RequestClient.request({queryId: "vehicleDetail", params});
        return res.data?.[0] ?? null;
    },
    async save(params){
        return await RequestClient.request({queryId: "vehicleSave", params});
    },
    async remove(params){
        return await RequestClient.request({queryId: "vehicleDeleteByIds", params});
    },
    async download(params){
        return await RequestClient.request({queryId: "vehicleCsv", params});
    }
};