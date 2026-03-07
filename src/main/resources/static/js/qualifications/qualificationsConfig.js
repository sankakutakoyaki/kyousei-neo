"use strict"

import { refleshCode } from "/js/qualifications/qualifications.js";
import { execOpen } from "/js/qualifications/qualifications.js";
import { updateFiles } from "/js/qualifications/qualifications.js";

export const MODE = {
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
        comboList: licenseComboList,
        comboListTop: "すべt",
        message: "会社名と許認可を選択して下さい。",
        codeChange: async () => {
            await execFilterDisplay("01");
        },
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
        comboList: qualificationsComboList,
        comboListTop: "すべt",
        message: "担当者と資格を選択して下さい。"
    },
    "03": {
        filterId: "filter03",
        comboList: licenseComboList,
        comboListTop: "",
        codeChange: async () => {
            // await updateFiles(cfg);
            await execOpen();
        },
    }
}

export const ID = {
    employee: {
        area: "id-area01",
        codeId: "code02",
        nameId: "name02",
        changeNameId: "fullName",
        getUrl: "/api/files/select/license",
        codeChange: async () => {
            // const cfg= ID_CONFIG["employee"];
            // await changeCodeToName(cfg, "/api/employee/get/id");
            await execFilterDisplay("02");
        }
    },
    qualifications: {
        area: "id-area03",
        codeId: "code04",
        nameId: "name04",
        changeNameId: "fullName",
        // getUrl: "/api/files/select/qualifications",
        // codeChange: async () => {
        //     // const cfg = ID_CONFIG["qualifications"];
        //     // await changeCodeToName(cfg, "/api/employee/get/id");
        //     // await execListDisplay("qualifications");
        // }
    }
}

export const COMPANY = [
    {
        codeId: "code01",
        nameId: "name01",
        onChange: async () => {
            refleshCode("code01", "name01");
            await execUpdate("01");
        }
    },
    {
        codeId: "code03",
        nameId: "name03",
        onChange: async () => {
            refleshCode("code03", "name03");
        }
    }
];

export const CHK = {
    license: {
        filterId: "filter03",
        comboList: licenseComboList,
    },
    qualifications: {
        filterId: "filter03",
        comboList: qualificationsComboList,
    }
}

export const FORM = [
    { name: 'owner-name', key: 'ownerName', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'qualification-name', key: 'qualificationName', trim: true, emptyToNull: true, skipIfNull: true },

    { name: 'number', key: 'number', trim: true, emptyToNull: true, skipIfNull: true },

    { name: 'acquisition-date', key: 'acquisitionDate', emptyToNull: true, skipIfNull: true },
    { name: 'expiry-date', key: 'expiryDate', emptyToNull: true, skipIfNull: true },
];

export const SAVE = FORM.map(c => ({
    ...c,
    skipIfNull: false   // ← ここが重要
}));

export const ERROR = {
    common: [
        rule(
            'input[name="number"]',
            required('番号を入力して下さい。', true)
        ),
        rule(
            'input[name="expiry-date"]',
            dateAfterOrEqual(
                'input[name="acquisition-date"]',
                "有効期限は取得日以降の日付を入力してください。"
            )
        )
    ]
};

export const FILE = {
    license: {
        /* API */
        selectUrl: "/api/files/select/license",
        uploadUrl: "/api/files/upload/license",
        fileViewUrl: "/api/files/file/view/license",
        deleteUrl: "/api/files/file/delete",
        renameFileUrl: "/api/files/file/rename",
        renameGroupUrl: "/api/files/group/rename",
        getParentUrl: "api/qualifications/get/license/master",

        /* 表示 */
        viewType: "list",
        grouping: true,
        isAdmin: true,

        /* DOM */
        loadBtnId: "load-btn",
        fileInputId: "file-input",
        dropAreaId: "file-list",
        viewerFileNameId: "viewer-file-name",

        listId: "file-list",
        groupArea: "filter04",
        // groupTitle: "filter04",
        codeValue: () => document.getElementById("code03")?.value,
        masterValue: () => document.getElementById("filter03")?.value,
        parentType: "license",
        parentValue: () => document.getElementById("filter04")?.value,
        groupValue: () => document.getElementById("filter04")?.dataset.data,
        afterRender: execOpen,
        // groupValue: () => {
        //     const id = document.getElementById("filter04")?.dataset.data;
        //     if (!id || id == "undefind") {
        //         return null
        //     } else {
        //         return id;
        //     }
        // },

        viewerId: "form-fileviewer",
        viewerBodyId: "viewer-body",
        prevBtnId: "viewer-prev",
        nextBtnId: "viewer-next",
        closeBtnId: "viewer-close"
    },
    qualifications: {
        /* API */
        selectUrl: "/api/files/select/qualifications",
        uploadUrl: "/api/files/upload/qualifications",
        fileViewUrl: "/api/files/file/view/qualifications",
        deleteUrl: "/api/files/file/delete",
        renameFileUrl: "/api/files/file/rename",
        renameGroupUrl: "/api/files/group/rename",
        getParentUrl: "api/qualifications/get/qualifications/master",

        /* 表示 */
        viewType: "list",
        grouping: true,
        isAdmin: true,

        /* DOM */
        loadBtnId: "load-btn",
        fileInputId: "file-input",
        dropAreaId: "group-id",
        viewerFileNameId: "viewer-file-name",

        listId: "file-list",
        groupArea: "filter04",
        // groupTitle: "filter04",
        codeValue: () => document.getElementById("code03")?.value,
        masterValue: () => document.getElementById("filter03")?.value,
        parentType: "qualification",
        parentValue: () => document.getElementById("filter04")?.value,
        groupValue: () => document.getElementById("filter04")?.dataset.data,
        afterRender: execOpen,

        viewerId: "form-fileviewer",
        viewerBodyId: "viewer-body",
        prevBtnId: "viewer-prev",
        nextBtnId: "viewer-next",
        closeBtnId: "viewer-close"
    }

}

export const CONFIG = {
    MODE,
    ID,
    COMPANY,
    CHK,
    FORM,
    SAVE,
    ERROR,
    FILE
};
