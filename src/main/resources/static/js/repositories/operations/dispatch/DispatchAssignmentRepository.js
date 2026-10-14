"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const DispatchAssignmentRepository = {

    async findList(params) {
        return await RequestClient.request({ queryId: "dispatchAssignmentList", params });
    },
    async findByOrderId(orderId) {
        return await RequestClient.request({ queryId: "dispatchAssignmentListByOrderId", params: {orderId, state: APP.cache.common.state.INITIAL} });
    },
    async save(params) {
        return await RequestClient.request({ queryId: "dispatchAssignmentSave", params });
    },
    async deleteByIds(ids) {
        return await RequestClient.request({ queryId: "dispatchAssignmentDeleteByIds", params: { ids }});
    },
    async findNextVisitOrder(params) {
        return await RequestClient.request({ queryId: "dispatchAssignmentNextVisitOrder", params});
    },
    async reorder(dailyCrewId, items) {
        return await RequestClient.request({ queryId: "dispatchAssignmentReorder", params: {dailyCrewId, items} });
    },
    async reset(params) {
        return await RequestClient.request({ queryId: "dispatchAssignmentReset", params });
    },
    async create(params) {
        return await RequestClient.request({ queryId: "dispatchAssignmentCreate", params });
    },
    async move(params) {
        return await RequestClient.request({ queryId: "dispatchAssignmentMove", params });
    },
    async remove({dispatchAssignmentId, version}) {
        return await RequestClient.request({ queryId: "dispatchAssignmentRemove", params: {dispatchAssignmentId, version} });
    },

};