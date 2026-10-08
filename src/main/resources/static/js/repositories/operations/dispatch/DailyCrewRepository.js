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

    async findMemberCandidates(params) {
        return await RequestClient.request({ queryId: "dailyCrewMemberCandidateList", params });
    },

    async addMember(params) {
        return await RequestClient.request({ queryId: "dailyCrewMemberAdd", params });
    },

    async saveMembers(params){
        return await RequestClient.request({ queryId: "dailyCrewMemberSave", params });
    },

    async remove(params){
        return await RequestClient.request({ queryId: "dailyCrewDelete", params });
    }
};