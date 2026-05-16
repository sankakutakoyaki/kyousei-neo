"use strict"

const MODE_CONFIG = {
    "01": {
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01",
        formId: "form-01",
        category: categoryCodes.FULLTIME
    },
    "02": {
        tableId: "table-02-content",
        footerId: "footer-02",
        searchId: "search-box-02",
        formId: "form-01",
        category: categoryCodes.PARTTIME
    }
};

const FORM_CONFIG = [
    { name: 'code', key: 'code' },
    { name: 'account', key: 'account' },

    { name: 'office', key: 'officeId' },

    { name: 'last-name', key: 'lastName' },
    { name: 'first-name', key: 'firstName' },
    { name: 'last-name-kana', key: 'lastNameKana' },
    { name: 'first-name-kana', key: 'firstNameKana' },

    { name: 'phone-number', key: 'phoneNumber' },
    { name: 'postal-code', key: 'postalCode' },
    { name: 'full-address', key: 'fullAddress' },
    { name: 'email', key: 'email' },

    { name: 'birthday', key: 'birthday' },
    { name: 'date-of-hire', key: 'dateOfHire' },

    { name: 'emergency-contact', key: 'emergencyContact' },
    { name: 'emergency-contact-number', key: 'emergencyContactNumber' }
];

const UPDATE_FORM_CONFIG = [
    { name: 'code', key: 'code', number: true, emptyToNull: true, skipIfNull: true },
    { name: 'account', key: 'account', trim: true, emptyToNull: true, skipIfNull: true },

    { name: 'office', key: 'officeId', number: true, zeroToNull: true, skipIfNull: true },

    { name: 'last-name', key: 'lastName', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'first-name', key: 'firstName', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'last-name-kana', key: 'lastNameKana', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'first-name-kana', key: 'firstNameKana', trim: true, emptyToNull: true, skipIfNull: true },

    { name: 'phone-number', key: 'phoneNumber', trim: true, emptyToNull: true },
    { name: 'postal-code', key: 'postalCode', trim: true, emptyToNull: true },
    { name: 'full-address', key: 'fullAddress', trim: true, emptyToNull: true },
    { name: 'email', key: 'email', trim: true, emptyToNull: true },

    { name: 'gender', key: 'gender', number: true, zeroToNull: true, skipIfNull: true },
    { name: 'blood-type', key: 'bloodType', number: true, zeroToNull: true, skipIfNull: true },

    { name: 'birthday', key: 'birthday', emptyToNull: true, skipIfNull: true },
    { name: 'date-of-hire', key: 'dateOfHire', emptyToNull: true, skipIfNull: true },

    { name: 'emergency-contact', key: 'emergencyContact', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'emergency-contact-number', key: 'emergencyContactNumber', trim: true, emptyToNull: true, skipIfNull: true },
];

const SAVE_FORM_CONFIG = UPDATE_FORM_CONFIG.map(c => ({
    ...c,
    skipIfNull: false   // ← ここが重要
}));

const COMBO_CONFIG = [
    {
        selector: 'select[name="office"]',
        list: officeComboList,
        key: 'officeId'
    },
    {
        selector: 'select[name="gender"]',
        list: genderComboList,
        key: 'gender'
    },
    {
        selector: 'select[name="blood-type"]',
        list: bloodTypeComboList,
        key: 'bloodType'
    }
];

// const ERROR_CONFIG = {
//     "form-01": [
//         {
//             selector: 'input[name="last-name"]',
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
//         },
//         {
//             selector: 'input[name="web-address"]',
//             checks: [
//                 { test: v => !checkWebAddress(v), message: 'WEBアドレスに誤りがあります'}
//             ]
//         }
//     ]
// }
const ERROR_CONFIG = {
    "form-01": {
        rules: [
            rule(
                'input[name="last-name"]',
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
            ),
            rule(
                'input[name="web-address"]',
                webAddress('WEBアドレスに誤りがあります')
            )
        ]
    }
};