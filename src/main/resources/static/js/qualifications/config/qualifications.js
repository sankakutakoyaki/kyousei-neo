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
        comboList: licenseComboList,
        message: "会社名と許認可を選択して下さい。"
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
        message: "担当者と資格を選択して下さい。"
    },
    "03": {
    //     // tableId: "table-02-content",
    //     // footerId: "footer-02",
    //     // searchId: "search-box-02",
    //     // dialogId: "form-dialog-01",
    //     // formId: "form-01",
    //     // codeId: "code02",
    //     // nameId: "name02",
        filterId: "filter03",
        getUrl: "/api/files/select/license",
    //     // changeNameId: "fullName",
    //     // codeChange: async () => {
    //     //     await execFilterDisplay("02");
    //     // },
    //     // keyName: "name02",
    //     // targetId: "name02",
    //     // ownerKeyName: "employeeId",
    //     // getValue: (elm) => elm.value,
        comboList: licenseComboList,
    //     // message: "担当者と資格を選択して下さい。"
    },
    "04": {
        codeId: "code04",
        nameId: "name04",
        filterId: "filter04",
        getUrl: "/api/files/select/qualifications",
    //     // changeNameId: "fullName",
    //     // codeChange: async () => {
    //     //     await execFilterDisplay("02");
    //     // },
    //     // keyName: "name02",
    //     // targetId: "name02",
    //     // ownerKeyName: "employeeId",
    //     // getValue: (elm) => elm.value,
        comboList: qualificationsComboList,
    //     // message: "担当者と資格を選択して下さい。"
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
    },
    {
        codeId: "code03",
        nameId: "name03",
        onChange: async () => {
            // refleshCode();
            // await execUpdate("03");
        }
    }
];

const FORM_CONFIG = [
    { name: 'owner-name', key: 'ownerName', trim: true, emptyToNull: true, skipIfNull: true },
    { name: 'qualification-name', key: 'qualificationName', trim: true, emptyToNull: true, skipIfNull: true },

    { name: 'number', key: 'number', trim: true, emptyToNull: true, skipIfNull: true },

    { name: 'acquisition-date', key: 'acquisitionDate', emptyToNull: true, skipIfNull: true },
    { name: 'expiry-date', key: 'expiryDate', emptyToNull: true, skipIfNull: true },
];

const SAVE_CONFIG = FORM_CONFIG.map(c => ({
    ...c,
    skipIfNull: false   // ← ここが重要
}));

const ERROR_CONFIG = {
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

// const FILE_CONFIG = {
//     listId: "file-list",
//     selectUrl: "/api/files/qualifications",
//     uploadUrl: "/api/files/file/upload/qualifications",
//     renameUrl: "/api/files/group/rename",
//     fileViewUrl: "/api/files/file/get/qualifications",
//     groupUrl: "/api/files/group/list",
//     parentType: "qualificaiotns",
//     parentId: "filter01",
//     groupId: "group-id",
//     groupName: "group-name",
//     csrfToken: token,
// }
// const FILE_CONFIG = {

//     /* API */
//     selectUrl: "/api/files/select/qualification",
//     uploadUrl: "/api/files/upload/qualification",
//     fileViewUrl: "/api/files/file/get/qualification",
//     deleteUrl: "/api/files/file/delete",
//     renameFileUrl: "/api/files/file/rename",
//     renameGroupUrl: "/api/files/group/rename",

//     /* 表示 */
//     viewType: "list",
//     grouping: true,
//     isAdmin: true,

//     /* DOM */
//     loadBtnId: "load-btn",
//     fileInputId: "file-input",
//     dropAreaId: "file-list",
//     viewerFileNameId: "viewer-file-name",

//     listId: "file-list",
//     groupArea: "group-id",

//     viewerId: "form-fileviewer",
//     viewerBodyId: "viewer-body",
//     prevBtnId: "viewer-prev",
//     nextBtnId: "viewer-next",
//     closeBtnId: "viewer-close"
// };

// const ID_CONFIG = {
//     "01": {
//         key: "license",
//         /* 親ID */
//         parentValue: document.getElementById("code01")?.value,

//         /* グループID */
//         groupValue: document.getElementById("filter01")?.value,
//     },
//     "02": {
//         key: "qualifications",
//         /* 親ID */
//         parentValue: document.getElementById("code02")?.value,

//         /* グループID */
//         groupValue: document.getElementById("filter02")?.value,
//     }
// }
const FILE_CONFIG = {
    /* 親ID */
    parentValue: () =>
        document.getElementById("code03")?.value,

    /* グループID */
    groupValue: () =>
        document.getElementById("code04")?.value,

    /* API */
    selectUrl: "/api/files/select/qualifications",
    uploadUrl: "/api/files/upload/qualifications",
    fileViewUrl: "/api/files/file/get/qualifications",
    deleteUrl: "/api/files/file/delete",
    renameFileUrl: "/api/files/file/rename",
    renameGroupUrl: "/api/files/group/rename",

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
    groupArea: "group-id",

    viewerId: "form-fileviewer",
    viewerBodyId: "viewer-body",
    prevBtnId: "viewer-prev",
    nextBtnId: "viewer-next",
    closeBtnId: "viewer-close"
}

// const ERROR_CONFIG = {
//     common: [
//         {
//             selector: 'input[name="number"]',
//             checks: [
//                 { test: v => v !== "", message: '番号を入力して下さい。'}
//             ]
//         },
//         {
//             selector: 'input[name="expiry-date"]',
//             checks: [
//                 {
//                     test: (v, el) => {
//                         const area = el.closest("form");
//                         const acquisition = area.querySelector('input[name="acquisition-date"]').value;

//                         if (!acquisition || !v) return true;

//                         return v >= acquisition;
//                     },
//                     message: "有効期限は取得日以降の日付を入力してください。"
//                 }
//             ]
//         }
//     ]
// }
