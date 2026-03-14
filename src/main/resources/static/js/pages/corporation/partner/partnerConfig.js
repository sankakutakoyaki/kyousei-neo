"use strict"

import { partnerController } from "./partnerController.js";

export const partnerConfig = {

    combo:[
        {
            comboId:"name01",            
            comboList:APP.cache.companyComboList,
            text: "",
            onChange: async (e)=>{
                // const table = getTable("table-02");
                // const copy = structuredClone(APP.cache.employeeOrigin);
                // const list = copy.filter(v => { return v.companyId === Number(e.target.value)});
                // table.load(list);
                partnerController.set("companyId", Number(e.target.value));
                partnerController.reload();
            }
        }
    ],

    search:[
        {
            searchId:"search-box-01",
            searchTo: (e) => {
                // const table = getTable("table-01");
                // const copy = structuredClone(APP.cache.companyOrigin);
                // table.search("search-box-01", copy);
                partnerController.set("keyword", e.target.value);
                partnerController.reload();
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