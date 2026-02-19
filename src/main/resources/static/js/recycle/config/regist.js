"use strict"

const RESET_CONFIG = {
    "02": {
        useDate: document.getElementById('start-date02'),
        number: document.getElementById('number-box-02'),
        makerCode: document.getElementById("maker-code02"),
        makerName: document.getElementById("maker-name02"),
        itemCode: document.getElementById("item-code02"),
        itemName: document.getElementById("item-name02"),
    },
    "03": {
        useDate: document.getElementById('start-date03'),
        number: document.getElementById('number-box-03')
    },
    "04": {
        useDate: document.getElementById('start-date04'),
        number: document.getElementById('number-box-04')
    },
    "05": {
        useDate: document.getElementById('start-date05'),
        number: document.getElementById('number-box-05')
    },
}

const CODE_CONFIG = {
    maker: {
        list: makerComboList,
        codeKey: "number",
        idKey: "makerId",
        nameKey: "maker-name"
    },
    item: {
        list: itemComboList,
        codeKey: "code",
        idKey: "itemId",
        nameKey: "item-name"
    }
};

const MODE_CONFIG = {
    "01": {
        panel: document.querySelector('[data-panel="01"]'),
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01",
        startId: "start-date01",
        endId: "end-date01",
        url: "/api/recycle/get/between",
        number: document.getElementById('number-box-01'),
        start: document.getElementById("start-date01"),
    },
    "02": {
        panel: document.querySelector('[data-panel="02"]'),
        dateKey: "use",
        tableId: "table-02-content",
        footerId: "footer-02",
        regBtn: document.getElementById("regist-btn02"),
        number: document.getElementById('number-box-02'),
        makerCode: document.getElementById('maker-code02'),
        itemCode: document.getElementById('item-code02'),
        start: document.getElementById("start-date02"),
        maker: {
            code: document.getElementById("maker-code02"),
            name: document.getElementById("maker-name02"),
        },
        item: {
            code: document.getElementById("item-code02"),
            name: document.getElementById("item-name02"),
        },
        url: "/api/recycle/save/regist"
    },
    "03": {
        panel: document.querySelector('[data-panel="03"]'),
        dateKey: "delivery",
        tableId: "table-03-content",
        footerId: "footer-03",
        number: document.getElementById('number-box-03'),
        regBtn: document.getElementById("regist-btn03"),
        start: document.getElementById("start-date03"),
        url: "/api/recycle/save/delivery"
    },
    "04": {
        panel: document.querySelector('[data-panel="04"]'),
        dateKey: "shipping",
        tableId: "table-04-content",
        footerId: "footer-04",
        number: document.getElementById('number-box-04'),
        regBtn: document.getElementById("regist-btn04"),
        start: document.getElementById("start-date04"),
        url: "/api/recycle/save/shipping"
    },
    "05": {
        panel: document.querySelector('[data-panel="05"]'),
        dateKey: "loss",
        tableId: "table-05-content",
        footerId: "footer-05",
        number: document.getElementById('number-box-05'),
        regBtn: document.getElementById("regist-btn05"),
        start: document.getElementById("start-date05"),
        url: "/api/recycle/save/loss"
    },
    "06": {
        panel: document.querySelector('[data-panel="06"]'),
        tableId: "table-01-content",
        formId: "form-01",
        dialogId: "form-dialog-01",
        makerCode: document.getElementById('maker-code11'),
        itemCode: document.getElementById('item-code11'),
        maker: {
            code: document.getElementById("maker-code11"),
            name: document.getElementById("maker-name11"),
        },
        item: {
            code: document.getElementById("item-code11"),
            name: document.getElementById("item-name11"),
        },
    },
};

// const ERROR_CONFIG = {
//     recycle: {
//         common: [
//             {
//                 selector: 'input[name="number"]',
//                 focus: true,
//                 checks: [
//                     { test: v => v !== "", message: 'お問合せ管理票番号を入力して下さい'}
//                 ]
//             }
//         ],
//         regist: [
//             {
//                 selector: 'select[name="company"]',
//                 checks: [
//                     { test: v => v !== "0", message: '小売業者を選択して下さい'}
//                 ]
//             },
//             {
//                 selector: 'input[name="maker-code"]',
//                 checks: [
//                     { test: v => v !== "", message: '製造業者等名を選択して下さい'}
//                 ]
//             },
//             {
//                 selector: 'input[name="item-code"]',
//                 checks: [
//                     { test: v => v !== "", message: '品目・料金区分を選択して下さい'}
//                 ]
//             },
//             {
//                 selector: 'input[name="use-date"]',
//                 checks: [
//                     { test: v => v !== "", message: '使用日を入力して下さい'}
//                 ]
//             }
//         ],
//         delivery: [
//             {
//                 selector: 'input[name="delivery-date"]',
//                 checks: [
//                     { test: v => v !== "", message: '引渡日を入力して下さい'}
//                 ]
//             }
//         ],
//         shipping: [
//             {
//                 selector: 'input[name="shipping-date"]',
//                 checks: [
//                     { test: v => v !== "", message: '発送日を入力して下さい'}
//                 ]
//             }
//         ],
//         loss: [
//             {
//                 selector: 'input[name="loss-date"]',
//                 checks: [
//                     { test: v => v !== "", message: 'ロス処理日を入力して下さい'}
//                 ]
//             }
//         ]
//     }
// }
const ERROR_CONFIG = {
    recycle: {
        common: [
            rule(
                'input[name="number"]',
                required('お問合せ管理票番号を入力して下さい', true)
            )
        ],
        modes: {
            use: [
                rule(
                    'select[name="company"]',
                    notValue("0", '小売業者を選択して下さい')
                ),
                rule(
                    'input[name="maker-code"]',
                    required('製造業者等名を選択して下さい')
                ),
                rule(
                    'input[name="item-code"]',
                    required('品目・料金区分を選択して下さい')
                ),
                rule(
                    'input[name="use-date"]',
                    required('使用日を入力して下さい')
                )
            ],
            delivery: [
                rule(
                    'input[name="delivery-date"]',
                    required('引渡日を入力して下さい')
                )
            ],
            shipping: [
                rule(
                    'input[name="shipping-date"]',
                    required('発送日を入力して下さい')
                )
            ],
            loss: [
                rule(
                    'input[name="loss-date"]',
                    required('ロス処理日を入力して下さい')
                )
            ]
        }
    }
};

const FIELD_MAP = {
    version: 'version',
    recycle_number: 'recycle-number',
    molding_number: 'molding-number',
    recycling_fee: 'recycling_fee'
};

const DATE_FIELDS = [
    { key: 'use_date', name: 'use-date' },
    { key: 'delivery_date', name: 'delivery-date' },
    { key: 'shipping_date', name: 'shipping-date' },
    { key: 'loss_date', name: 'loss-date' }
];

const SAVE_FORM_CONFIG = {
    "02": [
        { name: 'company', key: 'companyId' },
        { name: 'office', key: 'officeId' },
        { name: 'use-date', key: 'useDate' }
    ],
    "03": [
        { name: 'disposal-site', key: 'disposalSiteId' },
        { name: 'delivery-date', key: 'deliveryDate' }
    ],
    "04": [
        { name: 'shipping-date', key: 'shippingDate' }
    ],
    "05": [
        { name: 'loss-date', key: 'lossDate' }
    ],
    "06": [
        { name: 'company', key: 'companyId', number: true, zeroToNull: true, skipIfNull: true },
        { name: 'office', key: 'officeId', number: true, zeroToNull: true, skipIfNull: true },
        { name: 'maker-code', key: 'makerCode', number: true, emptyToNull: true, skipIfNull: true },
        { name: 'item-code', key: 'itemCode', number: true, emptyToNull: true, skipIfNull: true },

        { name: 'use-date', key: 'useDate', emptyToNull: true, skipIfNull: true },
        { name: 'delivery-date', key: 'deliveryDate', emptyToNull: true, skipIfNull: true },
        { name: 'shipping-date', key: 'shippingDate', emptyToNull: true, skipIfNull: true },
        { name: 'loss-date', key: 'lossDate', emptyToNull: true, skipIfNull: true },

        { name: 'slip-number', key: 'slipNumber', emptyToNull: true, skipIfNull: true }
    ]
}

const COMPANY_UI_CONFIG = [
    {
        codeId: "company-code02",
        nameId: "company02",
        onChange: async () => {
            await updateOfficeCombo("02");
        }
    },
    {
        codeId: "disposal-site-code03",
        nameId: "disposal-site03",
        onChange: async () => {}
    },
    {
        codeId: "company-code11",
        nameId: "company11",
        onChange: async () => {
            await updateOfficeCombo("06");
        }
    }
];

const COMPANY_COMBO_CONFIG = {
    "02": {
        codeId: "company-code02",
        nameId: "company02",
        officeId: "office02"
    },
    "03": {
        codeId: "disposal-site-code03",
        nameId: "disposal-site03",
    },
    "06": {
        codeId: "company-code11",
        nameId: "company11",
        officeId: "office11"
    }
}