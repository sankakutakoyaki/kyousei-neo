"use strict"

export const createClientCompanyColumns = (controller) => [
    {
        field: "companyId",
        label: "ID",
        sortable: true,
        class: "link-cell",
        format: (v) => String(v).padStart(4, "0")
    },
    {
        field: "nameKana",
        label: "名前",
        sortable: true,
        render: (item) => `
            <span class="kana">${item.nameKana ?? "-----"}</span><br>
            <span>${item.name}</span>
        `
    },
    {
        field: "telNumber",
        label: "電話番号",
        sortable: true,
        default: "登録なし"
    },
    {
        field: "email",
        label: "メールアドレス",
        sortable: true,
        default: "登録なし"
    }
];

export const createOfficeColumns = (controller) => [
    {
        field: "companyId",
        label: "ID",
        sortable: true,        
        format: (v) => String(v).padStart(4, "0")
    },
    {
        field: "companyName",
        label: "会社名",
        sortable: true,
        default: "登録なし"
    },
    {
        field: "officeId",
        label: "ID",
        sortable: true,
        class: "link-cell",
        format: (v) => String(v).padStart(4, "0")
    },
    {
        field: "nameKana",
        label: "名称",
        sortable:true,
        render: (item) => `
            <span class="kana">${item.nameKana ?? "-----"}</span><br>
            <span>${item.name}</span>
        `
    },
    {
        field: "telNumber",
        label: "電話番号",
        sortable: true,
        default: "登録なし"
    }
];