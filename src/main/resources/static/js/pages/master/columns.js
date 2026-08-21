"use strict"

export const createItemMasterColumns = () => [
    {
        field: "janCode",
        label: "JANコード",
        sortable: true,
        default: "登録なし"
    },
    {
        field: "itemMaker",
        label: "メーカー",
        sortable: true,
        default: "登録なし"
    },
    {
        field: "itemName",
        label: "商品名",
        sortable: true,
        default: "登録なし"
    },
    {
        field: "itemModel",
        label: "型番",
        sortable:true,
        default: "登録なし"
    }
];

export const createWorkMasterColumns = () => [
    {
        field: "workCode",
        label: "コード",
        sortable: true,
        default: "登録なし"
    },
    {
        field: "workName",
        label: "作業名",
        sortable: true,
        default: "登録なし"
    },
    {
        field: "workPrice",
        label: "金額",
        sortable:true,
        format: v => {
            if(v == null || v === "") return "";
            return Number(v).toLocaleString("ja-JP");
        },
    }
];