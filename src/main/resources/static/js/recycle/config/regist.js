"use strict"

const ID_CONFIG = {
    regist: {
        number: document.getElementById('number11'),
        recycleNumber: document.getElementById('recycle-number11'),
        moldingNumber: document.getElementById('molding-number11'),
        makerCode: document.getElementById('maker-code11'),
        itemCode: document.getElementById('item-code11'),
        makerName: document.getElementById('maker-name11'),
        itemName: document.getElementById('item-name11'),
        makerId: document.getElementById('maker-id11'),
        itemId: document.getElementById('item-id11'),
        date: document.getElementById('use-date11'),
        recycleId: document.getElementById('recycle-id11'),
        version: document.getElementById('version11'),
        regBtn: document.getElementById('regist-btn11')
    },
    delivery: {
        number: document.getElementById('number12'),
        recycleNumber: document.getElementById('recycle-number12'),
        moldingNumber: document.getElementById('molding-number12'),
        date: document.getElementById('delivery-date12'),
        recycleId: document.getElementById('recycle-id12'),
        version: document.getElementById('version12'),
        regBtn: document.getElementById('regist-btn12')
    },
    shipping: {
        number: document.getElementById('number13'),
        recycleNumber: document.getElementById('recycle-number13'),
        moldingNumber: document.getElementById('molding-number13'),
        date: document.getElementById('shipping-date13'),
        recycleId: document.getElementById('recycle-id13'),
        version: document.getElementById('version13'),
        regBtn: document.getElementById('regist-btn13')
    },
    loss: {
        number: document.getElementById('number14'),
        recycleNumber: document.getElementById('recycle-number14'),
        moldingNumber: document.getElementById('molding-number14'),
        date: document.getElementById('loss-date14'),
        recycleId: document.getElementById('recycle-id14'),
        version: document.getElementById('version14'),
        regBtn: document.getElementById('regist-btn14')
    },
    edit: {
        number: document.getElementById('number15'),
        recycleNumber: document.getElementById('recycle-number15'),
        moldingNumber: document.getElementById('molding-number15'),
        makerCode: document.getElementById('maker-code15'),
        itemCode: document.getElementById('item-code15'),
        makerName: document.getElementById('maker-name15'),
        itemName: document.getElementById('item-name15'),
        makerId: document.getElementById('maker-id15'),
        itemId: document.getElementById('item-id15'),
        useDate: document.getElementById('use-date15'),
        deliveryDate: document.getElementById('delivery-date15'),
        shippingDate: document.getElementById('shipping-date15'),
        lossDate: document.getElementById('loss-date15'),
        recycleId: document.getElementById('recycle-id15'),
        version: document.getElementById('version15'),
        regBtn: document.getElementById('regist-btn15')
    }
};
const MODE_CONFIG = {
    "01": {
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01"
    },
    regist: {
        dialogId: "form-dialog-01",
        formId: "form-01",
        dateInput: () => useDate11,
        tableId: "table-11-content",
        footerId: "footer-11",
        regBtnId: "regist-btn11"
    },
    delivery: {
        dialogId: "form-dialog-02",
        formId: "form-02",
        dateInput: () => deliveryDate12,
        tableId: "table-12-content",
        footerId: "footer-12",
        regBtnId: "regist-btn12"
    },
    shipping: {
        dialogId: "form-dialog-03",
        formId: "form-03",
        dateInput: () => shippingDate13,
        tableId: "table-13-content",
        footerId: "footer-13",
        regBtnId: "regist-btn13"
    },
    loss: {
        dialogId: "form-dialog-04",
        formId: "form-04",
        dateInput: () => lossDate14,
        tableId: "table-14-content",
        footerId: "footer-14",
        regBtnId: "regist-btn14"
    },
    edit: {
        dialogId: "form-dialog-05",
        formId: "form-05",
        dateInput: () => editDate15,
        regBtnId: "regist-btn15"
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
        ],
        edit: [
            // regist と同じなので再利用も可能（後述）
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