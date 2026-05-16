"use strict"

import { api } from "./apiService.js";

export const RequestClient = {
    async request({queryId, params = {}}){
        return await api.request({queryId, params});
    }
};