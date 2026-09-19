"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const EmployeeShiftRepository = {
    async findWorkingList(params) {
        return await RequestClient.request({ queryId: "employeeShiftWorkingList", params });
    },

    async save(params) {
        return await RequestClient.request({ queryId: "employeeShiftSave", params });
    }
};