"use strict"

const MODE_CONFIG = {
    "01": {
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01",
        codeId: "code01",
        nameId: "name01",
        filterId: "filter01",
        getUrl: "/api/qualifications/get/license",
        keyName: "companyId"
    },
    "02": {
        tableId: "table-02-content",
        footerId: "footer-02",
        searchId: "search-box-02",
        codeId: "code02",
        nameId: "name02",
        filterId: "filter02",
        getUrl: "/api/qualifications/get",
        changeNameId: "fullName",
        codeChange: async () => {
            await execFilterDisplay("02");
        },
        keyName: "employee_id"
    }
}

// const CATEGORY_CONFIG = {
//     0:{
//         getUrl: "/api/qualifications/get"
//     },
//     1:{
//         getUrl: "/api/qualifications/lisense/get"
//     }
// }

const COMPANY_UI_CONFIG = [
    {
        codeId: "code01",
        nameId: "name01",
        onChange: async () => {
            refleshCode();
            await execUpdate("01");
        }
    }
];