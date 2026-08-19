"use strict"

export const createItemMasterColumns = () => [
    {
        field: "code",
        label: "コード",
        sortable: true,
        format: (v) => String(v).padStart(3, "0")
    },
    {
        field: "groupName",
        label: "グループ",
        sortable: true,
        default: "登録なし"
    },
    {
        field: "name",
        label: "名前",
        sortable:true,
        render: (item) => `
            <span class="kana">${item.kana ?? "-----"}</span><br>
            <span>${item.name}</span>
        `
    }
];

export const createWorkMasterColumns = () => [
    {
        field: "code",
        label: "コード",
        sortable: true,
        format: (v) => String(v).padStart(3, "0")
    },
    {
        field: "groupName",
        label: "グループ",
        sortable: true,
        default: "登録なし"
    },
    {
        field: "name",
        label: "製造業者等名",
        sortable:true,
        render: (item) => `
            <span class="kana">${item.kana ?? "-----"}</span><br>
            <span>${item.name}</span>
        `
    },
    {
        field: "abbrName",
        label: "略称",
        sortable:true,
        render: (item) => `
            <span class="kana">${item.abbrKana ?? "-----"}</span><br>
            <span>${item.abbrName}</span>
        `
    }
];