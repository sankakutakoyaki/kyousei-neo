"use strict"

const MODE_CONFIG = {
    "01": {
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01",
        category: categoryPartnerCode,
        categoryName: "company",
        dataId: "company_id",
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
        dataId: "employee_id",
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
        url: "/api/company/save",
        baseEntity: () => structuredClone(companyEntity),
        fields: {
            category: v => Number(v)
        }
    },
    "02": {
        formId: "form-02",
        dialogId: "form-dialog-02",
        url: "/api/employee/save",
        baseEntity: () => structuredClone(staffEntity),
        fields: {
            staff_id: v => Number(v),
            office_id: v => Number(v),
            phone_number: v => v.trim()
        }
    }
};

const COMMON_FIELDS = {
    company_id: v => Number(v),
    name: v => v.trim(),
    name_kana: v => v.trim(),
    email: v => v.trim(),
    tel_number: v => v.trim(),
    fax_number: v => v.trim(),
    postal_code: v => v.trim(),
    full_address: v => v.trim(),
    web_address: v => v.trim(),
    version: v => Number(v),
    is_original_price: v => v != null ? Number(v) : 0
};

const ORIGIN_CONFIG = {
    company: {
        listUrl: "/api/client/get/list",
        comboUrl: "/api/client/get/combo",
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
