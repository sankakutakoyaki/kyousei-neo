"use strict"

import { RequestClient } from "../../../core/api/RequestClient.js";

export const EmployeeRepository = {
    async search(params){
        const res = await RequestClient.request({queryId: "employeeList", params});
        return res.data ?? [];
    },

    async find(params){
        const res = await RequestClient.request({queryId: "employeeDetail", params});
        return res.data?.[0] ?? null;
    },

    async findWorkCategories(params) {
        return await RequestClient.request({queryId: "employeeWorkCategoryList", params});
    },

    async save(params){
        return await RequestClient.request({queryId: "employeeSave", params});
    },

    async remove(params){
        return await RequestClient.request({queryId: "employeeDeleteByIds", params});
    },

    async download(params){
        return await RequestClient.request({queryId: "employeeCsv", params});
    },

    async resolve(identifier) {
        return await RequestClient.request({queryId: "employeeResolve", params: {state: APP.cache.common.state.INITIAL, identifier}});
    },

    async findWorkCategoryMembers(params) {
        return await RequestClient.request({queryId: "employeeWorkCategoryMemberList", params});
    },
};