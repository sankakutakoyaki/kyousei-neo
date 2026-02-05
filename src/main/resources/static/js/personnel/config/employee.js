"use strict"

const MODE_CONFIG = {
    "01": {
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01",
        category: categoryEmployeeCode
    },
    "02": {
        tableId: "table-02-content",
        footerId: "footer-02",
        searchId: "search-box-02",
        category: categoryParttimeCode
    }
};

const FORM_CONFIG = [
    { name: 'employee-id', key: 'employeeId' },
    { name: 'company-id', key: 'companyId' },

    { name: 'code', key: 'code', emptyIf: 0 },

    { name: 'category', key: 'category' },
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

    { name: 'birthday', key: 'birthday', emptyIf: '9999-12-31' },
    { name: 'date-of-hire', key: 'dateOfHire', emptyIf: '9999-12-31' },

    { name: 'emergency-contact', key: 'emergencyContact' },
    { name: 'emergency-contact-number', key: 'emergencyContactNumber' },

    { name: 'version', key: 'version' }
];

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

const SAVE_FORM_CONFIG = [
    { name: 'employee-id', key: 'employeeId' },
    { name: 'company-id', key: 'companyId' },

    { name: 'code', key: 'code', number: true, zeroToNull: true, skipIfNull: true },

    { name: 'category', key: 'category', number: true, zeroToNull: true, skipIfNull: true },
    { name: 'account', key: 'account', trim: true, emptyToNull: true, skipIfNull: true },

    { name: 'office', key: 'officeId', number: true, zeroToNull: true, skipIfNull: true },

    { name: 'last-name', key: 'lastName', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'first-name', key: 'firstName', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'last-name-kana', key: 'lastNameKana', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'first-name-kana', key: 'firstNameKana', trim: true, emptyToNull: true, skipIfNull: true },

    { name: 'phone-number', key: 'phoneNumber', trim: true },
    { name: 'postal-code', key: 'postalCode', trim: true },
    { name: 'full-address', key: 'fullAddress', trim: true },
    { name: 'email', key: 'email', trim: true },

    { name: 'gender', key: 'gender', number: true, zeroToNull: true, skipIfNull: true },
    { name: 'blood-type', key: 'bloodType', number: true, zeroToNull: true, skipIfNull: true },

    { name: 'birthday', key: 'birthday', emptyToNull: true, skipIfNull: true },
    { name: 'date-of-hire', key: 'dateOfHire', emptyToNull: true, skipIfNull: true },

    { name: 'emergency-contact', key: 'emergencyContact', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'emergency-contact-number', key: 'emergencyContactNumber', trim: true, emptyToNull: true, skipIfNull: true },

    { name: 'version', key: 'version', number: true, zeroToNull: true, skipIfNull: true },
];

const COMBO_CONFIG = [
    // {
    //   selector: 'select[name="company"]',
    //   list: companyComboList,
    //   key: 'companyId',
    //   onChange: (form, el) => createOfficeComboBox(form, el, officeList)
    // },
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