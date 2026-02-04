"use strict"

const MODE_CONFIG = {
    "01": {
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01",
        category: categoryShipperCode,
        categoryName: "company",
        dataId: "companyId",
        formDialogId: "form-dialog-01",
        formId: "form-01",
        entity: companyEntity
    },
    "02": {
        tableId: "table-02-content",
        footerId: "footer-02",
        searchId: "search-box-02",
        category: categorySupplierCode,
        categoryName: "company",
        dataId: "companyId",
        formDialogId: "form-dialog-01",
        formId: "form-01",
        entity: companyEntity
    },
    "03": {
        tableId: "table-03-content",
        footerId: "footer-03",
        searchId: "search-box-03",
        category: categoryFacilityCode,
        categoryName: "company",
        dataId: "companyId",
        formDialogId: "form-dialog-01",
        formId: "form-01",
        entity: companyEntity
    },
    "04": {
        tableId: "table-04-content",
        footerId: "footer-04",
        searchId: "search-box-04",
        category: categoryTransportCode,
        categoryName: "company",
        dataId: "companyId",
        formDialogId: "form-dialog-01",
        formId: "form-01",
        entity: companyEntity
    },
    "05": {
        tableId: "table-05-content",
        footerId: "footer-05",
        searchId: "search-box-05",
        categoryName: "office",
        dataId: "officeId",
        formDialogId: "form-dialog-01",
        formId: "form-01",
        entity: officeEntity,
        codeBox: "code-box-51",
        nameBox: "name-box-51"
    },
    "06": {
        tableId: "table-06-content",
        footerId: "footer-06",
        searchId: "search-box-06",
        categoryName: "staff",
        dataId: "staffId",
        formDialogId: "form-dialog-02",
        formId: "form-02",
        entity: staffEntity,
        codeBox: "code-box-61",
        nameBox: "name-box-61",
        nameBox2: "name-box-62"
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
            "category",
            "price"
        ],
        show: ["priceArea"]
    },
    "02": {
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
    "03": {
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
    "04": {
        common: false,
        fields: [
            "category",
            "staff-id",
            "office-id",
            "phone-number"
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
            "staff-id",
            "office-id",
            "company-name"
        ],
        init: (config, entity) => {
            createFormOfficeComboBox(config.formId, entity.officeId);
        }
    }
};

const SAVE_CONFIG = {
    "05": {
        formId: "form-01",
        dialogId: "form-dialog-01",
        url: "/api/office/save",
        baseEntity: () => structuredClone(officeEntity),
        fields: {
            officeId: v => Number(v)
        }
    },
    "06": {
        formId: "form-02",
        dialogId: "form-dialog-02",
        url: "/api/staff/save",
        baseEntity: () => structuredClone(staffEntity),
        fields: {
            staffId:    { convert: Number },           // name = staff-id
            officeId:   { name: 'office', convert: Number },
            phoneNumber:{ convert: v => v.trim() }    // name = phone-number
        }
    },
    "default": {
        formId: "form-01",
        dialogId: "form-dialog-01",
        url: "/api/company/save",
        baseEntity: () => structuredClone(companyEntity),
        // fields: {
        //     category: v => Number(v)
        // }
        fields: {
            category: {
                convert: v => Number(v)
            }
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
        listUrl: "/api/client/get/list",
        comboUrl: "/api/client/get/combo",
        originKey: "companyOrigin",
        comboKey: "companyComboList",
        comboTargetIds: ["name-box-51", "name-box-61"]
    },
    office: {
        listUrl: "/api/office/get/list",
        comboUrl: "/api/office/get/combo",
        originKey: "officeOrigin",
        comboKey: "officeComboList",
        comboTargetIds: ["name-box-62"]
    }
};

const COMPANY_UI_CONFIG = [
    {
        codeId: "code-box-51",
        nameId: "name-box-51",
        onChange: async () => {
            await updateOfficeTableDisplay();
        }
    },
    {
        codeId: "code-box-61",
        nameId: "name-box-61",
        onChange: async () => {
            await updateStaffTableDisplay();
            createOfficeComboBoxFromClient();
        }
    }
];
