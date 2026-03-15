"use strict"

import {partnerCompanyController} from "./partnerController.js";
import {partnerEmployeeController} from "./partnerController.js";

export const partnerConfig = {

    combo:[
        {
            comboId:"name01",            
            comboList:APP.cache.companyComboList,
            text: "",
            onChange: async (e)=>{
                partnerEmployeeController.set("companyId", Number(e.target.value));
                partnerEmployeeController.reload();
            }
        }
    ],

    search:[
        {
            searchId:"search-box-01",
            searchTo: (e) => {
                partnerCompanyController.set("keyword", e.target.value);
                partnerCompanyController.reload();
            }
        }
    ],

    resolve: true,
    postal: true,
    tabs: true,
    enterfocus: true,

    tables: [
        {
            tableId: "table-01",
            // api: "/api/client/get/list",
            checkable: true,

            dataSource: {
                // type: "api",
                // url: "/api/client/get/list"
                type: "origin",
                data: APP.cache.companyOrigin
            },

            columns: [
                {
                    field: "companyId",
                    class: "link-cell",
                    format: (v) => String(v).padStart(4, "0")
                },
                {
                    render: (item) => `
                        <span class="kana">${item.nameKana ?? "-----"}</span><br>
                        <span>${item.name}</span>
                    `
                },
                {
                    field: "telNumber",
                    default: "登録なし"
                },
                {
                    field: "email",
                    default: "登録なし"
                }
            ]
        },
        {
            tableId: "table-02",
            api: "/api/employee/get/list/partner",
            checkable: true,

            dataSource: {
                type: "none",
                data: APP.cache.employeeOrigin
            },

            columns: [
                {
                    field: "employeeId",
                    class: "link-cell",
                    format: (v) => String(v).padStart(4, "0")
                },
                {
                    field: "code",
                    default: ""
                },
                {
                    render: (item) => `
                        <span class="kana">${item.fullNameKana ?? "-----"}</span><br>
                        <span>${item.fullName}</span>
                    `
                },
                {
                    field: "phoneNumber",
                    default: "登録なし"
                },
                {
                    field: "companyName",
                    default: "登録なし"
                }
            ]
        }
    ]
};