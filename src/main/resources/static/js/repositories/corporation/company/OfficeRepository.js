"use strict";

import { RequestClient } from "../../../core/request/RequestClient.js";

export const OfficeRepository = {
    async search(params){
        const res = await RequestClient.request({queryId:"clientOfficeList", params});
        return res.data ?? [];
    },

    async find(params){
        const res = await RequestClient.request({queryId:"officeDetail", params});
        return res.data?.[0] ?? null;
    },

    async save(params){
        return await RequestClient.request({queryId:"officeSave", params});
    },

    async remove(params){
        return await RequestClient.request({queryId:"officeDeleteByIds", params});
    },

    async download(params){
        return await RequestClient.request({queryId:"officeCsv", params});
    },

    async fetchCombo(){
        const res = await RequestClient.request({queryId:"officeCombo"});
        return res.data ?? [];
    }
};


// "use strict"

// // import { api } from "../../core/api/apiService.js";
// import { RequestClient } from "../../../core/request/RequestClient.js";

// export const OfficeRepository = {
//     async fetchCombo(){
//         // const res = await api.get("/api/office/client/combo");
//         const res = await RequestClient.request({queryId: "officeCombo"});
//         return res.data ?? [];
//     }
// };