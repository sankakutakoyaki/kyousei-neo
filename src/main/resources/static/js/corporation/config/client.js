"use strict"

const MODE_CONFIG = {
    // "01": {
    //     tableId: "table-01-content",
    //     footerId: "footer-01",
    //     searchId: "search-box-01",
    //     category: categoryPartnerCode,
    //     categoryName: "company",
    //     dataId: "company_id",
    //     formDialogId: "form-dialog-01",
    //     formId: "form-01",
    //     entity: companyEntity
    // },
    "02": {
        tableId: "table-02-content",
        footerId: "footer-02",
        searchId: "search-box-02",
        category: categoryShipperCode,
        categoryName: "company",
        dataId: "company_id",
        formDialogId: "form-dialog-01",
        formId: "form-01",
        entity: companyEntity
    },
    "03": {
        tableId: "table-03-content",
        footerId: "footer-03",
        searchId: "search-box-03",
        category: categorySupplierCode,
        categoryName: "company",
        dataId: "company_id",
        formDialogId: "form-dialog-01",
        formId: "form-01",
        entity: companyEntity
    },
    "04": {
        tableId: "table-04-content",
        footerId: "footer-04",
        searchId: "search-box-04",
        category: categoryServiceCode,
        categoryName: "company",
        dataId: "company_id",
        formDialogId: "form-dialog-01",
        formId: "form-01",
        entity: companyEntity
    },
    "05": {
        tableId: "table-05-content",
        footerId: "footer-05",
        searchId: "search-box-05",
        categoryName: "office",
        dataId: "office_id",
        formDialogId: "form-dialog-01",
        formId: "form-01",
        entity: officeEntity
    },
    "06": {
        tableId: "table-06-content",
        footerId: "footer-06",
        searchId: "search-box-06",
        categoryName: "staff",
        dataId: "staff_id",
        formDialogId: "form-dialog-02",
        formId: "form-02",
        entity: staffEntity
    },
    "07": {
        tableId: "table-07-content",
        footerId: "footer-07",
        searchId: "search-box-07",
        category: categoryTransportCode,
        categoryName: "company",
        dataId: "company_id",
        formDialogId: "form-dialog-01",
        formId: "form-01",
        entity: companyEntity
    }
};

const ID_CONFIG = {
    // "01": {
    //     common: true,
    //     fields: [
    //         "tel-number",
    //         "fax-number",
    //         "postal-code",
    //         "full-address",
    //         "web-address",
    //         "category"
    //     ]
    // },
    "02": {
        common: true,
        fields: [
            "tel-number",
            "fax-number",
            "postal-code",
            "full-address",
            "web-address",
            "category",
            "price"
        ],
        show: ["priceArea"]
    },
    "03": {
        common: true,
        fields: [
            "tel-number",
            "fax-number",
            "postal-code",
            "full-address",
            "web-address",
            "category"
        ],
    },
    "04": {
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
    "05": {
        common: true,
        fields: [
            "tel-number",
            "fax-number",
            "postal-code",
            "full-address",
            "web-address"
        ]
    },
    "06": {
        common: true,
        fields: [
            "tel-number",
            "fax-number",
            "postal-code",
            "full-address",
            "web-address",
            "office-id"
        ],
        init: (config, entity) => {
            createFormOfficeComboBox(config.formId, entity.office_id);
        }
    },
    "07": {
        common: false,
        fields: [
            "category",
            "staff-id",
            "office-id",
            "phone-number"
        ]
    }
};

const SAVE_CONFIG = {
    "06": {
        formId: "form-02",
        dialogId: "form-dialog-02",
        url: "/api/staff/save",
        baseEntity: () => structuredClone(staffEntity),
        fields: {
            staff_id: v => Number(v),
            office_id: v => Number(v),
            phone_number: v => v.trim()
        }
    },
    "05": {
        formId: "form-01",
        dialogId: "form-dialog-01",
        url: "/api/office/save",
        baseEntity: () => structuredClone(officeEntity),
        fields: {
            office_id: v => Number(v)
        }
    },
    "default": {
        formId: "form-01",
        dialogId: "form-dialog-01",
        url: "/api/company/save",
        baseEntity: () => structuredClone(companyEntity),
        fields: {
            category: v => Number(v)
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
        comboTargetIds: ["name-box-01", "name-box-02"]
    },
    office: {
        listUrl: "/api/office/get/list",
        comboUrl: "/api/office/get/combo",
        originKey: "officeOrigin",
        comboKey: "officeComboList",
        comboTargetIds: ["name-box-03"]
    }
};

const COMPANY_UI_CONFIG = [
    {
        codeId: "code-box-01",
        nameId: "name-box-01",
        onChange: updateOfficeTableDisplay
    },
    {
        codeId: "code-box-02",
        nameId: "name-box-02",
        onChange: async () => {
            await updateStaffTableDisplay();
            createOfficeComboBoxFromClient();
        }
    }
];
