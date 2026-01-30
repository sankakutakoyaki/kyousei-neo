"use strict"

const MODE_CONFIG = {
    "01": {
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01",
        category: categoryPartnerCode,
        categoryName: "company",
        dataId: "companyId",
        formDialogId: "form-dialog-01",
        formId: "form-01",
        entity: companyEntity,
        list: companyOrigin
    },
    "02": {
        tableId: "table-02-content",
        footerId: "footer-02",
        searchId: "search-box-02",
        category: categoryConstructCode,
        categoryName: "staff",
        dataId: "employeeId",
        formDialogId: "form-dialog-02",
        formId: "form-02",
        entity: staffEntity,
        list: staffOrigin
    }
};

const ID_CONFIG = {
    "01": {
        common: true,
        fields: [
            "tel-number",
            "fax-number",
            "postal-code",
            "full-address",
            "web-address",
            "category"
        ]
    },
    "02": {
        common: true,
        fields: [
            "phone-number",
            "postal-code",
            "full-address",
            "web-address",
            "category",
            "price"
        ],
        show: ["priceArea"]
    }
};

const SAVE_CONFIG = {
    "01": {
        formId: "form-01",
        dialogId: "form-dialog-01",
        tableId: "table-01-content",
        url: "/api/company/save",
        baseEntity: () => structuredClone(companyEntity),
        fields: {
            category: v => Number(v)
        }
    },
    "02": {
        formId: "form-02",
        dialogId: "form-dialog-02",
        tableId: "table-02-content",
        url: "/api/employee/save",
        baseEntity: () => structuredClone(staffEntity),
        fields: {
            staffId: v => Number(v),
            officeId: v => Number(v),
            phoneNumber: v => v.trim()
        }
    }
};

const COMMON_FIELDS = {
    companyId: v => Number(v),
    name: v => v.trim(),
    nameKana: v => v.trim(),
    email: v => v.trim(),
    telNumber: v => v.trim(),
    faxNumber: v => v.trim(),
    postalCode: v => v.trim(),
    fullAddress: v => v.trim(),
    webAddress: v => v.trim(),
    version: v => Number(v),
    isOriginalPrice: v => v != null ? Number(v) : 0
};

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
