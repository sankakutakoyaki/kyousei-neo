"use strict"

// import { api } from "../../core/api/apiService.js";
import { RequestClient } from "../../core/request/RequestClient.js";

export const OfficeRepository = {
    async fetchCombo(){
        // const res = await api.get("/api/office/client/combo");
        const res = await RequestClient.request({queryId: "officeCombo"});
        return res.data ?? [];
    }
};