export const createStaffColumns = (controller) => [
    {
        field: "staffId",
        label: "ID",
        sortable: true,
        class: "link-cell",
        format: (v) => String(v).padStart(4, "0")
    },
    {
        field: "fullNameKana",
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
        field: "companyName",
        label: "会社名",
        sortable: true,
        default: "登録なし"
    },
    {
        field: "officeName",
        label: "支店名",
        sortable: true,
        default: "登録なし"
    }
];