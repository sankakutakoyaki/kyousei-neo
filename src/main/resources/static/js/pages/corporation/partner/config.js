"use strict"

// import { filterFactory } from "../../../core/filter/filterFactory.js";

export const tableConfig = {

    partnerCompany: {
        
        // tableId: "table-01",
        // footerId: "footer-01",
        // checkable: true,
        // idKey:"companyId",

        // dataSource: {
        //     type: "origin",
        //     data: APP.cache.companyOrigin
        // },

        // filters:{
        //     keyword: filterFactory.keyword()
        // },

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

    partnerEmployee: {

        // tableName: "employee",
        // tableId: "table-02",
        // footerId: "footer-02",
        // api: "/api/employee/get/list/partner",
        // checkable: true,
        // idKey:"employeeId",
        // deleteUrl: "/api/employee/delete",

        // dataSource: {
        //     type: "origin",
        //     data: APP.cache.employeeOrigin
        // },

        // filters:{
        //     companyId: filterFactory.equals("companyId")
        // },

        // requiredFilters:["companyId"],

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
};

// export const partnerConfig = {
//     // combo:[
//     //     {
//     //         comboId:"name01",            
//     //         comboList:APP.cache.companyComboList,
//     //         text: "",
//     //         onChange: async (e)=>{
//     //             partnerEmployeeController.filter("companyId", Number(e.target.value));
//     //         }
//     //     }
//     // ],

//     // search:[
//     //     {
//     //         searchId:"search-box-01",
//     //         searchTo: (e) => {
//     //             partnerCompanyController.search(e.target.value);
//     //         }
//     //     }
//     // ],

//     // kebabBtns: true,
//     // btns:[
//     //     {
//     //         btnId:"create-btn-01",
//     //         clickTo: () => { partnerCompanyController.create("form-01"); }
//     //     },
//     //     {
//     //         btnId:"create-btn-02",
//     //         clickTo: () => { partnerEmployeeController.create("form-02"); }
//     //     },
//     //     {
//     //         btnId:"delete-btn-01",
//     //         clickTo: () => { partnerCompanyController.deleteSelected(); }
//     //     },
//     //     {
//     //         btnId:"delete-btn-02",
//     //         clickTo: () => { partnerEmployeeController.deleteSelected(); }
//     //     },
//     //     {
//     //         btnId:"download-btn-01",
//     //         clickTo: () => { partnerCompanyController.downloadSelected(); }
//     //     },
//     //     {
//     //         btnId:"download-btn-02",
//     //         clickTo: () => { partnerEmployeeController.downloadSelected(); }
//     //     },
//     //     {
//     //         btnId:"reload-btn-01",
//     //         clickTo: () => { partnerCompanyController.refresh(); }
//     //     },
//     //     {
//     //         btnId:"reload-btn-02",
//     //         clickTo: () => { partnerEmployeeController.refresh(); }
//     //     }
//     // ],

//     resolve: true,
//     postal: true,
//     tabs: true,
//     enterfocus: true,

//     tables: [
//         {
//             tableId: "table-01",
//             footerId: "footer-01",
//             checkable: true,
//             idKey:"companyId",

//             dataSource: {
//                 type: "origin",
//                 data: APP.cache.companyOrigin
//             },

//             filters:{
//                 keyword: filterFactory.keyword()
//             },

//             columns: [
//                 {
//                     field: "companyId",
//                     label: "ID",
//                     sortable:true,
//                     class: "link-cell",
//                     format: (v) => String(v).padStart(4, "0")
//                 },
//                 {
//                     field:"nameKana",
//                     label: "名前",
//                     sortable: true,
//                     render: (item) => `
//                         <div class="name-cell">
//                             <div class="kana">${item.nameKana ?? "-----"}</div>
//                             <div>${item.name ?? "-----"}</div>
//                         </div>
//                     `
//                 },
//                 {
//                     field: "telNumber",
//                     label: "電話番号",
//                     sortable:true,
//                     default: "登録なし"
//                 },
//                 {
//                     field: "email",
//                     label: "メールアドレス",
//                     sortable:true,
//                     default: "登録なし"
//                 }
//             ]
//         },
//         {
//             tableName: "employee",
//             tableId: "table-02",
//             footerId: "footer-02",
//             api: "/api/employee/get/list/partner",
//             checkable: true,
//             idKey:"employeeId",
//             deleteUrl: "/api/employee/delete",

//             dataSource: {
//                 type: "origin",
//                 data: APP.cache.employeeOrigin
//             },

//             filters:{
//                 companyId: filterFactory.equals("companyId")
//             },

//             requiredFilters:["companyId"],

//             columns: [
//                 {
//                     field: "employeeId",
//                     label: "ID",
//                     sortable:true,
//                     class: "link-cell",
//                     format: (v) => String(v).padStart(4, "0")
//                 },
//                 {
//                     field: "code",
//                     label: "コード",
//                     sortable:true,
//                     default: ""
//                 },
//                 {
//                     field: "nameKana",
//                     label: "名前",
//                     sortable:true,
//                     render: (item) => `
//                         <span class="kana">${item.fullNameKana ?? "-----"}</span><br>
//                         <span>${item.fullName}</span>
//                     `
//                 },
//                 {
//                     field: "phoneNumber",
//                     label: "携帯番号",
//                     sortable:true,
//                     default: "登録なし"
//                 },
//                 {
//                     field: "companyName",
//                     label: "会社名",
//                     sortable:true,
//                     default: "登録なし"
//                 }
//             ]
//         }
//     ]
// };