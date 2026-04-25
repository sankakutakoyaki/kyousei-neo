"use strict"

export const createEmployeeColumns = (controller) => [
    {
        field: "employeeId",
        label: "ID",
        sortable: true,
        class: "link-cell",
        format: (v) => String(v).padStart(4, "0")
    },
    {
        field: "code",
        label: "コード",
        sortable: true,
        zeroToNull: true,
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
        sortable: true,
        default: "登録なし"
    },
    {
        field: "officeName",
        label: "営業所",
        sortable: true,
        default: "登録なし"
    }
];