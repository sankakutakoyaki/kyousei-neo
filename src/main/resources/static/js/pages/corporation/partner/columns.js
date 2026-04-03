"use strict"

export const createPartnerCompanyColumns = (controller) => [
    {
        field: "companyId",
        label: "ID",
        sortable:true,
        class: "link-cell",
        format: (v) => String(v).padStart(4, "0")
    },
    {
        field: "nameKana",
        label: "名前",
        sortable:true,
        render: (item) => `
            <span class="kana">${item.nameKana ?? "-----"}</span><br>
            <span>${item.name}</span>
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
];

export const createPartnerEmployeeColumns = (controller) => [
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
];