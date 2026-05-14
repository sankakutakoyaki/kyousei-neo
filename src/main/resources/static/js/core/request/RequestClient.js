"use strict"

import { api } from "../api/apiService";

export const RequestClient = {
    async request({queryId, params = {}}){
        return await api.request({queryId, params});
    }
};