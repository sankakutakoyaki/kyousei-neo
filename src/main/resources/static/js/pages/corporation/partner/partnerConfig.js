"use strict"

import { partnerCompanyController } from "./partnerController.js";
import { partnerEmployeeController } from "./partnerController.js";
import { filterFactory } from "../../../core/filter/filterFactory.js";
import { getTable } from "../../../components/form/table.js";

export const partnerConfig = {
    combo:[
        {
            comboId:"name01",            
            comboList:APP.cache.companyComboList,
            text: "",
            onChange: async (e)=>{
                partnerEmployeeController.set("companyId", Number(e.target.value));
            }
        }
    ],

    search:[
        {
            searchId:"search-box-01",
            searchTo: (e) => {
                partnerCompanyController.set("keyword", e.target.value);
            }
        }
    ],

    btns:[
        {
            btnId:"delete-btn-01",
            clickTo: (e) => {
                const table = getTable("table-01");
                table.deleteSelected();
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
            footerId: "footer-01",
            checkable: true,
            idKey:"companyId",
            deleteUrl: "/api/company/delete",

            dataSource: {
                type: "origin",
                data: APP.cache.companyOrigin
            },

            filters:{
                keyword: filterFactory.keyword()
            },

            columns: [
                {
                    field: "companyId",
                    label: "ID",
                    sortable:true,
                    class: "link-cell",
                    format: (v) => String(v).padStart(4, "0")
                },
                {
                    field:"nameKana",
                    label: "名前",
                    sortable: true,
                    render: (item) => `
                        <div class="name-cell">
                            <div class="kana">${item.nameKana ?? "-----"}</div>
                            <div>${item.name ?? "-----"}</div>
                        </div>
                    `
                },
                {
                    field: "telNumber",
                    label: "電話番号",
                    sortable:true,
                    default: "登録なし"
                },
                {
                    field: "email",
                    label: "メールアドレス",
                    sortable:true,
                    default: "登録なし"
                }
            ]
        },
        {
            tableName: "employee",
            tableId: "table-02",
            footerId: "footer-02",
            api: "/api/employee/get/list/partner",
            checkable: true,
            idKey:"employeeId",
            deleteUrl: "/api/employee/delete",

            dataSource: {
                type: "origin",
                data: APP.cache.employeeOrigin
            },

            filters:{
                companyId: filterFactory.equals("companyId")
            },

            requiredFilters:["companyId"],

            columns: [
                {
                    field: "employeeId",
                    label: "ID",
                    sortable:true,
                    class: "link-cell",
                    format: (v) => String(v).padStart(4, "0")
                },
                {
                    field: "code",
                    label: "コード",
                    sortable:true,
                    default: ""
                },
                {
                    field: "nameKana",
                    label: "名前",
                    sortable:true,
                    render: (item) => `
                        <span class="kana">${item.fullNameKana ?? "-----"}</span><br>
                        <span>${item.fullName}</span>
                    `
                },
                {
                    field: "phoneNumber",
                    label: "携帯番号",
                    sortable:true,
                    default: "登録なし"
                },
                {
                    field: "companyName",
                    label: "会社名",
                    sortable:true,
                    default: "登録なし"
                }
            ]
        }
    ]
};