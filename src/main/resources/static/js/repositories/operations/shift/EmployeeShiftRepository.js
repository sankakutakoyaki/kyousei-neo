"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const EmployeeShiftRepository = {

    async findEmployees(params) {
        return await RequestClient.request({ queryId: "employeeShiftEmployeeList", params });
    },

    async findMonthList(params) {
        return await RequestClient.request({ queryId: "employeeShiftMonthList", params });
    },

    async findWorkingList(params) {
        return await RequestClient.request({ queryId: "employeeShiftWorkingList", params });
    },

    async save(params) {
        return await RequestClient.request({ queryId: "employeeShiftSave", params });
    }
};