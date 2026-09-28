"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const DailyCrewRepository = {
    async findBoardList(params) {
        return await RequestClient.request({ queryId: "dailyCrewBoardList", params });
    },

    async bulkCreate(params) {
        return await RequestClient.request({ queryId: "dailyCrewBulkCreate", params });
    },

    async reinitialize(params) {
        return await RequestClient.request({ queryId: "dailyCrewReinitialize", params });
    },
};