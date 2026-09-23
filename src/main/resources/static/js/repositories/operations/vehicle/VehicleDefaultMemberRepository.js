"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const VehicleDefaultMemberRepository = {
    async findDispatchDefaultList(params) {
        return await RequestClient.request({ queryId: "vehicleDispatchDefaultList", params });
    },

    async findList(params) {
        return await RequestClient.request({ queryId: "vehicleDefaultMemberList", params });
    }
};