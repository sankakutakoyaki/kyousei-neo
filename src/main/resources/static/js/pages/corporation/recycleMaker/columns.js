"use strict"

export const createRecycleMakerColumns = () => [
    {
        field: "code",
        label: "コード",
        sortable: true,
        format: (v) => String(v).padStart(3, "0")
    },
    {
        field: "name",
        label: "製造業者等名",
        sortable: true,
        default: "登録なし"
    },
    {
        field: "abbrName",
        label: "略称",
        sortable: true,
        default: "登録なし"
    },
    {
        field: "groupName",
        label: "グループ",
        sortable: true,
        default: "登録なし"
    }
];