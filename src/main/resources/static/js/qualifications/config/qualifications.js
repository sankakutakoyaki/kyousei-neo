"use strict"

const MODE_CONFIG = {
    "01": {
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01",
        dialogId: "form-dialog-01",
        formId: "form-01",
        codeId: "code01",
        nameId: "name01",
        filterId: "filter01",
        getUrl: "/api/qualifications/get/license",
        keyName: "name01",
        targetId: "name01",
        ownerKeyName: "companyId",
        getValue: (elm) => elm.options[elm.selectedIndex].text,
        comboList: licenseComboList
    },
    "02": {
        tableId: "table-02-content",
        footerId: "footer-02",
        searchId: "search-box-02",
        dialogId: "form-dialog-01",
        formId: "form-01",
        codeId: "code02",
        nameId: "name02",
        filterId: "filter02",
        getUrl: "/api/qualifications/get",
        changeNameId: "fullName",
        codeChange: async () => {
            await execFilterDisplay("02");
        },
        keyName: "name02",
        targetId: "name02",
        ownerKeyName: "employeeId",
        getValue: (elm) => elm.value,
        comboList: qualificationsComboList
    }
}

const COMPANY_UI_CONFIG = [
    {
        codeId: "code01",
        nameId: "name01",
        onChange: async () => {
            refleshCode();
            await execUpdate("01");
        }
    }
];

const FORM_CONFIG = [
    { name: 'owner-name', key: 'ownerName', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'qualifications-name', key: 'qualificationsName', trim: true, emptyToNull: true, skipIfNull: true },

    { name: 'number', key: 'number', trim: true, emptyToNull: true, skipIfNull: true },

    { name: 'acquisition-date', key: 'acquisitionDate', emptyToNull: true, skipIfNull: true },
    { name: 'expiry-date', key: 'expiryDate', emptyToNull: true, skipIfNull: true },
];

const SAVE_CONFIG = FORM_CONFIG.map(c => ({
    ...c,
    skipIfNull: false   // ← ここが重要
}));

const ERROR_CONFIG = [
    {
        selector: 'input[name="number"]',
        checks: [
            { test: v => v !== "", message: '番号を入力して下さい。'}
        ]
    },
    {
        selector: 'input[name="expiry-date"]',
        checks: [
            {
                test: (v, el) => {
                    const area = el.closest("form");
                    const acquisition = area.querySelector('input[name="acquisition-date"]').value;

                    if (!acquisition || !v) return true;

                    return v >= acquisition;
                },
                message: "有効期限は取得日以降の日付を入力してください。"
            }
        ]
    }
]