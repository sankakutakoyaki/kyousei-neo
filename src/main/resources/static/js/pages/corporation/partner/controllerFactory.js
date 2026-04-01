"use strict"

import { TableController } from "../../../core/table/TableController.js";
import { closeFormDialog } from "../../../core/ui/dialog.js";
// import { execSave } from "../../components/form/entity.js";

export const controllerFactory = {
    partnerCompany: () => new TableController({
        tableId:"table-01",
        formId:"form-01",
        key:"companyId",
        findUrl: "/api/company/get/id",
        selectUrl: "/api/partner/get/list",
        deleteUrl: "/api/company/delete",
        downloadUrl: "/api/company/download/csv",
        saveUrl: "/api/company/save",

        table: {
            checkable: true,
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
            ],

            onDoubleClick: function(item){
                this.openEdit(item.companyId);
            }
        }
    }),

    partnerEmployee: () => new TableController({
        tableId:"table-02",
        formId:"form-02",
        key:"employeeId",
        findUrl: "/api/employee/get/id",
        selectUrl: "/api/employee/get/list/partner",
        deleteUrl: "/api/employee/delete",
        downloadUrl: "/api/employee/download/csv",
        saveUrl: "/api/employee/save",
        // onSubmit: async function(form){
        //     await this.save(form);
        // },
        onDoubleClick: function(item){
            this.openEdit(item.employeeId);
        }
    })
}