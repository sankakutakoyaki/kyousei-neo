"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const DispatchAssignmentRepository = {

    async findList(params) {
        return await RequestClient.request({ queryId: "dispatchAssignmentList", params });
    },

    async save(params) {
        return await RequestClient.request({ queryId: "dispatchAssignmentSave", params });
    },

    async deleteByIds(ids) {
        return await RequestClient.request({ queryId: "dispatchAssignmentDeleteByIds", params: { ids }});
    }
};