"use strict"

// const ID_CONFIG = {
//     regist: {
//         number: document.getElementById('number11'),
//         recycleNumber: document.getElementById('recycle-number11'),
//         moldingNumber: document.getElementById('molding-number11'),
//         makerCode: document.getElementById('maker-code11'),
//         itemCode: document.getElementById('item-code11'),
//         makerName: document.getElementById('maker-name11'),
//         itemName: document.getElementById('item-name11'),
//         makerId: document.getElementById('maker-id11'),
//         itemId: document.getElementById('item-id11'),
//         date: document.getElementById('use-date11'),
//         recycleId: document.getElementById('recycle-id11'),
//         version: document.getElementById('version11'),
//         regBtn: document.getElementById('regist-btn11')
//     },
//     delivery: {
//         number: document.getElementById('number12'),
//         recycleNumber: document.getElementById('recycle-number12'),
//         moldingNumber: document.getElementById('molding-number12'),
//         date: document.getElementById('delivery-date12'),
//         recycleId: document.getElementById('recycle-id12'),
//         version: document.getElementById('version12'),
//         regBtn: document.getElementById('regist-btn12')
//     },
//     shipping: {
//         number: document.getElementById('number13'),
//         recycleNumber: document.getElementById('recycle-number13'),
//         moldingNumber: document.getElementById('molding-number13'),
//         date: document.getElementById('shipping-date13'),
//         recycleId: document.getElementById('recycle-id13'),
//         version: document.getElementById('version13'),
//         regBtn: document.getElementById('regist-btn13')
//     },
//     loss: {
//         number: document.getElementById('number14'),
//         recycleNumber: document.getElementById('recycle-number14'),
//         moldingNumber: document.getElementById('molding-number14'),
//         date: document.getElementById('loss-date14'),
//         recycleId: document.getElementById('recycle-id14'),
//         version: document.getElementById('version14'),
//         regBtn: document.getElementById('regist-btn14')
//     },
//     edit: {
//         number: document.getElementById('number15'),
//         recycleNumber: document.getElementById('recycle-number15'),
//         moldingNumber: document.getElementById('molding-number15'),
//         makerCode: document.getElementById('maker-code15'),
//         itemCode: document.getElementById('item-code15'),
//         makerName: document.getElementById('maker-name15'),
//         itemName: document.getElementById('item-name15'),
//         makerId: document.getElementById('maker-id15'),
//         itemId: document.getElementById('item-id15'),
//         useDate: document.getElementById('use-date15'),
//         deliveryDate: document.getElementById('delivery-date15'),
//         shippingDate: document.getElementById('shipping-date15'),
//         lossDate: document.getElementById('loss-date15'),
//         recycleId: document.getElementById('recycle-id15'),
//         version: document.getElementById('version15'),
//         regBtn: document.getElementById('regist-btn15')
//     }
// };
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
        list: makerComboList,          // 先読み済み
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
        start: document.getElementById("start-date01"),
    },
    "02": {
        panel: document.querySelector('[data-panel="02"]'),
        dateKey: "use",
        tableId: "table-02-content",
        footerId: "footer-02",
        regBtnId: "regist-btn02",
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
    },
    "03": {
        panel: document.querySelector('[data-panel="03"]'),
        dateKey: "delivery",
        tableId: "table-03-content",
        footerId: "footer-03",
        number: document.getElementById('number-box-03'),
        regBtnId: "regist-btn03",
        start: document.getElementById("start-date03")
    },
    "04": {
        panel: document.querySelector('[data-panel="04"]'),
        dateKey: "shipping",
        tableId: "table-04-content",
        footerId: "footer-04",
        number: document.getElementById('number-box-04'),
        regBtnId: "regist-btn04",
        start: document.getElementById("start-date04")
    },
    "05": {
        panel: document.querySelector('[data-panel="05"]'),
        dateKey: "loss",
        tableId: "table-05-content",
        footerId: "footer-05",
        number: document.getElementById('number-box-05'),
        regBtnId: "regist-btn05",
        start: document.getElementById("start-date05")
    }
};

const ERROR_CONFIG = {
    recycle: {
        common: [
            {
                selector: 'input[name="recycle-number"]',
                focus: true,
                checks: [
                    { test: v => v !== "", message: 'お問合せ管理票番号を入力して下さい'}
                ]
            }
        ],
        regist: [
            {
                selector: 'select[name="company"]',
                checks: [
                    { test: v => v !== "0", message: '小売業者を選択して下さい'}
                ]
            },
            {
                selector: 'input[name="maker-code"]',
                checks: [
                    { test: v => v !== "", message: '製造業者等名を選択して下さい'}
                ]
            },
            {
                selector: 'input[name="item-code"]',
                checks: [
                    { test: v => v !== "", message: '品目・料金区分を選択して下さい'}
                ]
            },
            {
                selector: 'input[name="use-date"]',
                checks: [
                    { test: v => v !== "", message: '使用日を入力して下さい'}
                ]
            }
        ],
        delivery: [
            {
                selector: 'input[name="delivery-date"]',
                checks: [
                    { test: v => v !== "", message: '引渡日を入力して下さい'}
                ]
            }
        ],
        shipping: [
            {
                selector: 'input[name="shipping-date"]',
                checks: [
                    { test: v => v !== "", message: '発送日を入力して下さい'}
                ]
            }
        ],
        loss: [
            {
                selector: 'input[name="loss-date"]',
                checks: [
                    { test: v => v !== "", message: 'ロス処理日を入力して下さい'}
                ]
            }
        ]
    }
}

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

const DEFAULT_DATE = "9999-12-31";

const SAVE_FORM_CONFIG = {
    "02": [
        { name: 'company', key: 'companyId' },
        { name: 'office', key: 'officeId' },
        { name: 'use-date', key: 'useDate' }
    ],
    "03": [
        { name: 'delivery-date', key: 'deliveryDate' }
    ],
    "04": [
        { name: 'shipping-date', key: 'shippingDate' }
    ],
    "05": [
        { name: 'loss-date', key: 'lossDate' }
    ],
}
// const SAVE_FORM_CONFIG = [
//     { name: 'employee-id', key: 'employeeId' },
//     { name: 'company-id', key: 'companyId' },

//     { name: 'code', key: 'code', emptyTo: 0, number: true },

//     { name: 'category', key: 'category', number: true },
//     { name: 'account', key: 'account', trim: true },

//     { name: 'office', key: 'officeId', number: true },

//     { name: 'last-name', key: 'lastName', trim: true },
//     { name: 'first-name', key: 'firstName', trim: true },
//     { name: 'last-name-kana', key: 'lastNameKana', trim: true },
//     { name: 'first-name-kana', key: 'firstNameKana', trim: true },

//     { name: 'phone-number', key: 'phoneNumber', trim: true },
//     { name: 'postal-code', key: 'postalCode', trim: true },
//     { name: 'full-address', key: 'fullAddress', trim: true },
//     { name: 'email', key: 'email', trim: true },

//     { name: 'gender', key: 'gender' },
//     { name: 'blood-type', key: 'bloodType' },

//     { name: 'birthday', key: 'birthday', emptyTo: '9999-12-31' },
//     { name: 'date-of-hire', key: 'dateOfHire', emptyTo: '9999-12-31' },

//     { name: 'emergency-contact', key: 'emergencyContact', trim: true },
//     { name: 'emergency-contact-number', key: 'emergencyContactNumber', trim: true },

//     { name: 'version', key: 'version', number: true }
// ];
// const RECYCLE_COMBO_CONFIG = [
//     {
//         type: 'company',
//         handler: (form, entity) => {
//         setCompanyComboBox(form, entity, companyComboList, officeComboList);
//         }
//     },
//     {
//         selector: 'select[name="disposal-site"]',
//         list: disposalSiteComboList,
//         key: 'disposal_site_id',
//         withTop: true
//     }
// ];

const COMPANY_UI_CONFIG = [
    {
        codeId: "company-code02",
        nameId: "company02",
        onChange: async () => {
            await updateOfficeCombo("02");
        }
    }
];

const COMPANY_COMBO_CONFIG = {
    "02": {
        // area: document.querySelector('[data-panel="02"]'),
        codeId: "company-code02",
        nameId: "company02",
        officeId: "office02"
    },
    "06": {
        // area: document.querySelector('[data-panel="06"]'),
        codeId: "company-code11",
        nameId: "company11",
        officeId: "office11"
    }
}