"use strict"

export const createRecycleMakerColumns = () => [
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

export const createRecycleManufacturerColumns = () => [
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

// export const createRecyclePriceColumns = () => [
//     {
//         field: "code",
//         label: "コード",
//         sortable: true,
//         format: (v) => String(v).padStart(3, "0")
//     },
//     {
//         field: "name",
//         label: "略称",
//         sortable:true,
//         render: (item) => `
//             <span class="kana">${item.kana ?? "-----"}</span><br>
//             <span>${item.name}</span>
//         `
//     },
//     {
//         field: "itemName",
//         label: "品目",
//         sortable: true,
//         default: "登録なし"
//     },
//     {
//         field: "price",
//         label: "価格",
//         sortable: true,
//         default: "登録なし"
//     },
// ];
// export function createRecyclePriceColumns(items){

//     const columns = [
//         {
//             field: "code",
//             label: "コード",
//             sortable: true,
//             format: v => String(v).padStart(3, "0")
//         },
//         {
//             field: "makerName",
//             label: "メーカー",
//             sortable: true
//         }
//     ];

//     columns.push(
//         ...items.map(item => ({
//             field: `price_${item.recycleItemId}`,
//             label: item.name,
//             sortable: false,

//             render: row => {
//                 const cell =
//                     row.prices?.[item.recycleItemId];

//                 return `
//                     <input
//                         type="number"
//                         value="${cell?.price ?? ""}"
//                         data-price-id="${cell?.recyclePriceId ?? ""}"
//                         data-maker-id="${row.recycleMakerId}"
//                         data-item-id="${item.recycleItemId}"
//                     >
//                 `;
//             }
//         }))
//     );


//     return columns;
// }
export function createRecyclePriceColumns(items){

    const columns = [

        {

            field: "code",

            label: "コード",

            sortable: true,

            format: v => String(v).padStart(3, "0")

        },

        {

            field: "name",

            label: "略称",

            sortable: true,

            render: item => `

                <span class="kana">

                    ${item.kana ?? "-----"}

                </span><br>

                <span>${item.name}</span>

            `

        }

    ];
        columns.push(

        ...items.map(item => ({

            field: `price_${item.recycleItemId}`,

            label: item.name,

            sortable: false,

            default: "",
            format: v => {

                if(v == null || v === "") return "";

                return Number(v).toLocaleString("ja-JP");

            },
            class:

                item.recycleItemId <= 1

                    ? "aircon-column"

                    : item.recycleItemId <= 3

                    ? "tv-column"

                    : item.recycleItemId <= 5

                    ? "fridge-column"

                    : "washer-column"

        }))

    );

    return columns;

}