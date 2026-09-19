"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const DispatchOrderRepository = {
    async findList(params) {
        return await RequestClient.request({ queryId: "dispatchOrderList", params });
    }
};