"use strict"

const MODE_CONFIG = {
    "01": {
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01",
        category: categoryPartnerCode,
        categoryName: "company",
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
        category: categoryConstructCode,
        categoryName: "employee",
        dataId: "employeeId",
        dialogId: "form-dialog-02",
        formId: "form-02",
        entity: staffEntity,
        list: staffOrigin,
        url: "/api/employee/save"
    }
};

// const ID_CONFIG = {
//     "01": {
//         common: true,
//         fields: [
//             "tel-number",
//             "fax-number",
//             "postal-code",
//             "full-address",
//             "web-address",
//             "category"
//         ]
//     },
//     "02": {
//         common: true,
//         fields: [
//             "phone-number",
//             "postal-code",
//             "full-address",
//             "web-address",
//             "category",
//             "price"
//         ],
//         show: ["priceArea"]
//     }
// };
const FORM_CONFIG = {
    "01": [
        { name: 'name', key: 'name' },
        { name: 'name-kana', key: 'nameKana' },

        { name: 'tel-number', key: 'telNumber' },
        { name: 'postal-code', key: 'postalCode' },
        { name: 'full-address', key: 'fullAddress' },
        { name: 'email', key: 'email' },
        { name: 'web-address', key: 'webAddress' },
        { name: 'is-original-price', key: 'isOriginalPrice' },
    ],
    "02": [
        { name: 'code', key: 'code' },
        { name: 'account', key: 'account' },
        { name: 'company', key: 'companyId' },

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
    ]
};

const COMBO_CONFIG = [
    {
        selector: 'select[name="company"]',
        list: companyComboList,
        key: 'company'
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

// const SAVE_CONFIG = {
//     "01": {
//         formId: "form-01",
//         dialogId: "form-dialog-01",
//         tableId: "table-01-content",
//         url: "/api/company/save",
//         baseEntity: () => structuredClone(companyEntity),
//         // fields: {
//             // category: v => Number(v)
//         // }
//         fields: [
//             { name: 'category', key: 'category', number: true, zeroToNull: true, skipIfNull: true }
//         ]
//     },
//     "02": {
//         formId: "form-02",
//         dialogId: "form-dialog-02",
//         tableId: "table-02-content",
//         url: "/api/employee/save",
//         baseEntity: () => structuredClone(staffEntity),
//         // fields: {
//         //     staffId: v => Number(v),
//         //     officeId: v => Number(v),
//         //     phoneNumber: v => v.trim()
//         // }
//         fields: [
//             { name: 'staff-id', key: 'staffId', number: true, zeroToNull: true, skipIfNull: true },
//             { name: 'office', key: 'officeId', number: true, zeroToNull: true, skipIfNull: true },
//             { name: 'phone-number', key: 'phoneNumber', trim: true, emptyToNull: true, skipIfNull: true },
//         ]
//     }
// };

const UPDATE_FORM_CONFIG = {
    "01": [
        { name: 'name', key: 'name', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'name-kana', key: 'nameKana', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'email', key: 'email', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'tel-number', key: 'telNumber', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'postal-code', key: 'postalCode', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'full-address', key: 'fullAddress', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'web-address', key: 'webAddress', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'is-original-price', key: 'isOriginalPrice', number: true, zeroToNull: true, skipIfNull: true },
    ],
    "02": [
        { name: 'code', key: 'code', number: true, zeroToNull: true, skipIfNull: true },
        { name: 'account', key: 'account', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'company', key: 'companyId', number: true, zeroToNull: true, skipIfNull: true },

        { name: 'last-name', key: 'lastName', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'first-name', key: 'firstName', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'last-name-kana', key: 'lastNameKana', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'first-name-kana', key: 'firstNameKana', trim: true, emptyToNull: true, skipIfNull: true },

        { name: 'phone-number', key: 'phoneNumber', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'postal-code', key: 'postalCode', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'full-address', key: 'fullAddress', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'email', key: 'email', trim: true, emptyToNull: true, skipIfNull: true },

        { name: 'birthday', key: 'birthday', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'date-of-hire', key: 'dateOfHire', trim: true, emptyToNull: true, skipIfNull: true },

        { name: 'emergency-contact', key: 'emergencyContact', trim: true, emptyToNull: true, skipIfNull: true },
        { name: 'emergency-contact-number', key: 'emergencyContactNumber', trim: true, emptyToNull: true, skipIfNull: true },
    ]
};

const SAVE_FORM_CONFIG = {
    "01": UPDATE_FORM_CONFIG["01"].map(c => ({
        ...c,
        skipIfNull: false   // ← ここが重要
    })),
    "02": UPDATE_FORM_CONFIG["02"].map(c => ({
        ...c,
        skipIfNull: false   // ← ここが重要
    }))
}

// {
//     companyId: v => Number(v),
//     name: v => v.trim(),
    
//     nameKana: v => v.trim(),
//     email: v => v.trim(),
//     telNumber: v => v.trim(),
//     faxNumber: v => v.trim(),
//     postalCode: v => v.trim(),
//     fullAddress: v => v.trim(),
//     webAddress: v => v.trim(),
//     version: v => Number(v),
//     isOriginalPrice: v => v != null ? Number(v) : 0
// };

const ORIGIN_CONFIG = {
    company: {
        listUrl: "/api/partner/get/list",
        comboUrl: "/api/partner/get/combo",
        originKey: "companyOrigin",
        comboKey: "companyComboList",
        comboTargetIds: ["name01"]
    },
    staff: {
        listUrl: "/api/employee/get/list/partner",
        originKey: "staffOrigin"
    }
};

const COMPANY_UI_CONFIG = [
    {
        codeId: "code01",
        nameId: "name01",
        onChange: async () => {
            await updateStaffTableDisplay();
            refleshCode;
        }
    }
];
