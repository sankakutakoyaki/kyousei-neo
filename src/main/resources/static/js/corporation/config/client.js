"use strict"

const MODE_CONFIG = {
    "01": {
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01",
        category: categoryCodes.SHIPPER,
        domain: "company",
        dataId: "companyId",
        dialogId: "form-dialog-01",
        formId: "form-01",
        entity: companyEntity,
        list: companyOrigin,
        url: "/api/company/save"
    },
    "02": {
        tableId: "table-02-content",
        footerId: "footer-02",
        searchId: "search-box-02",
        category: categoryCodes.SUPPLIER,
        domain: "company",
        dataId: "companyId",
        dialogId: "form-dialog-01",
        formId: "form-01",
        entity: companyEntity,
        list: companyOrigin,
        url: "/api/company/save"
    },
    "03": {
        tableId: "table-03-content",
        footerId: "footer-03",
        searchId: "search-box-03",
        category: categoryCodes.FACILITY,
        domain: "company",
        dataId: "companyId",
        dialogId: "form-dialog-01",
        formId: "form-01",
        entity: companyEntity,
        list: companyOrigin,
        url: "/api/company/save"
    },
    "04": {
        tableId: "table-04-content",
        footerId: "footer-04",
        searchId: "search-box-04",
        category: categoryCodes.TRANSPORT,
        domain: "company",
        dataId: "companyId",
        dialogId: "form-dialog-01",
        formId: "form-01",
        entity: companyEntity,
        list: companyOrigin,
        url: "/api/company/save"
    },
    "05": {
        tableId: "table-05-content",
        footerId: "footer-05",
        searchId: "search-box-05",
        domain: "office",
        dataId: "officeId",
        dialogId: "form-dialog-01",
        formId: "form-01",
        entity: officeEntity,
        list: officeOrigin,
        codeBox: "code-box-51",
        nameBox: "name-box-51",
        url: "/api/office/save"
    },
    "06": {
        tableId: "table-06-content",
        footerId: "footer-06",
        searchId: "search-box-06",
        domain: "staff",
        dataId: "staffId",
        dialogId: "form-dialog-02",
        formId: "form-02",
        entity: staffEntity,
        list: staffOrigin,
        codeBox: "code-box-61",
        nameBox: "name-box-61",
        nameBox2: "name-box-62",
        url: "/api/staff/save"
    }
};

const COMBO_CONFIG = [
    {
        selector: 'select[name="office"]',
        list: officeComboList,
        key: 'office'
    }
];

const COMMON_COMPANY_FIELDS = [
    { name: 'name', key: 'name', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'name-kana', key: 'nameKana', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'tel-number', key: 'telNumber', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'postal-code', key: 'postalCode', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'full-address', key: 'fullAddress', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'email', key: 'email', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'web-address', key: 'webAddress', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'is-original-price', key: 'isOriginalPrice', number: true, zeroToNull: true, skipIfNull: true },
];

const FORM_CONFIG = {
    "01": COMMON_COMPANY_FIELDS,
    "02": COMMON_COMPANY_FIELDS,
    "03": COMMON_COMPANY_FIELDS,
    "04": COMMON_COMPANY_FIELDS,
    "05": [
        ...COMMON_COMPANY_FIELDS,
        { name: 'company-name', key: 'companyName', trim: true, emptyToNull: true }
    ],
    "06": [
        { name: 'company-name', key: 'companyName', trim: true, emptyToNull: true },
        { name: 'office-name',  key: 'officeName', trim: true, emptyToNull: true, defaultValue: '-----' },
        { name: 'name', key: 'name', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'name-kana', key: 'nameKana', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'phone-number', key: 'phoneNumber', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'email', key: 'email', trim: true, emptyToNull: true, skipIfNull: true },
    ]
};

const SAVE_FORM_CONFIG = {
    "01": FORM_CONFIG["01"].map(c => ({
        ...c,
        skipIfNull: false
    })),
    "02": FORM_CONFIG["02"].map(c => ({
        ...c,
        skipIfNull: false
    })),
    "03": FORM_CONFIG["03"].map(c => ({
        ...c,
        skipIfNull: false
    })),
    "04": FORM_CONFIG["04"].map(c => ({
        ...c,
        skipIfNull: false
    })),
    "05": FORM_CONFIG["05"].map(c => ({
        ...c,
        skipIfNull: false
    })),
    "06": FORM_CONFIG["06"].map(c => ({
        ...c,
        skipIfNull: false 
    }))
}

const COMPANY_UI_CONFIG = [
    {
        codeId: "code-box-51",
        nameId: "name-box-51",
        onChange: async () => {
            execUpdate();
        }
    },
    {
        codeId: "code-box-61",
        nameId: "name-box-61",
        onChange: async () => {
            execUpdate();
            createOfficeComboBoxFromClient();
        }
    }
];

// const ERROR_CONFIG = {
//     "form-01": [
//         {
//             selector: 'input[name="name"]',
//             checks: [
//                 { test: v => v !== "", message: '名称が入力されていません'}
//             ]
//         },
//         {
//             selector: 'input[name="tel-number"]',
//             checks: [
//                 { test: v => !checkPhoneNumber(v), message: '電話番号に誤りがあります'}
//             ]
//         },
//         {
//             selector: 'input[name="fax-number"]',
//             checks: [
//                 { test: v => !checkPhoneNumber(v), message: 'FAX番号に誤りがあります'}
//             ]
//         },
//         {
//             selector: 'input[name="postal-code"]',
//             checks: [
//                 { test: v => !checkPhoneNumber(v), message: '郵便番号に誤りがあります'}
//             ]
//         },
//         {
//             selector: 'input[name="email"]',
//             checks: [
//                 { test: v => !checkMailAddress(v), message: 'メールアドレスに誤りがあります'}
//             ]
//         },
//         {
//             selector: 'input[name="web-address"]',
//             checks: [
//                 { test: v => !checkWebAddress(v), message: 'WEBアドレスに誤りがあります'}
//             ]
//         }
//     ],
//     "form-02": [
//         {
//             selector: 'input[name="name"]',
//             checks: [
//                 { test: v => v !== "", message: '名前が入力されていません'}
//             ]
//         },
//         {
//             selector: 'input[name="phone-number"]',
//             checks: [
//                 { test: v => !checkPhoneNumber(v), message: '電話番号に誤りがあります'}
//             ]
//         },
//         {
//             selector: 'input[name="postal-code"]',
//             checks: [
//                 { test: v => !checkPhoneNumber(v), message: '郵便番号に誤りがあります'}
//             ]
//         },
//         {
//             selector: 'input[name="email"]',
//             checks: [
//                 { test: v => !checkMailAddress(v), message: 'メールアドレスに誤りがあります'}
//             ]
//         }
//     ]
// }
const ERROR_CONFIG = {
    "form-01": {
        rules: [
            rule(
                'input[name="name"]',
                required('名称が入力されていません', true)
            ),
            rule(
                'input[name="tel-number"]',
                phone('電話番号に誤りがあります')
            ),
            rule(
                'input[name="fax-number"]',
                phone('FAX番号に誤りがあります')
            ),
            rule(
                'input[name="postal-code"]',
                postalCode('郵便番号に誤りがあります')
            ),
            rule(
                'input[name="email"]',
                email('メールアドレスに誤りがあります')
            ),
            rule(
                'input[name="web-address"]',
                webAddress('WEBアドレスに誤りがあります')
            )
        ]
    },

    "form-02": {
        rules: [
            rule(
                'input[name="name"]',
                required('名前が入力されていません', true)
            ),
            rule(
                'input[name="phone-number"]',
                phone('電話番号に誤りがあります')
            ),
            rule(
                'input[name="postal-code"]',
                postalCode('郵便番号に誤りがあります')
            ),
            rule(
                'input[name="email"]',
                email('メールアドレスに誤りがあります')
            )
        ]
    }
};