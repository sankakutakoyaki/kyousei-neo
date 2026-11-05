"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const EmployeeShiftRepository = {

    async findEmployees(params) {
        return await RequestClient.request({ queryId: "employeeShiftEmployeeList", params });
    },

    async findMonthList(params) {
        return await RequestClient.request({ queryId: "employeeShiftMonthList", params });
    },

    /**
     * 応援勤務登録用の既存シフト取得
     * 従業員IDと対象期間で検索する
     */
    async findPeriodList(params){
        return await RequestClient.request({ queryId: "employeeShiftPeriodList", params });
    },

    async findWorkingList(params) {
        return await RequestClient.request({ queryId: "employeeShiftWorkingList", params });
    },

    async save(params) {
        return await RequestClient.request({ queryId: "employeeShiftSave", params });
    },

    async batchSave(params) {
        return await RequestClient.request({ queryId: "employeeShiftBatchSave", params });
    },
};