"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const DispatchEmployeeRepository = {
    async findCandidateList(params) {
        return await RequestClient.request({queryId: "dispatchEmployeeCandidateList", params});
    }
};