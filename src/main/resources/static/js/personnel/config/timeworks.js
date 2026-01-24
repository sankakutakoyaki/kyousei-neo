"use strict"

const TIMEWORKS_TIME_CONFIG = {
    start: {
        // dateTimeKey: "start_dt",
        latKey: "startLatitude",
        lngKey: "startLongitude"
    },
    end: {
        // dateTimeKey: "end_dt",
        latKey: "endLatitude",
        lngKey: "endLongitude"
    }
};

const TIMEWORKS_TAB_CONFIG = {
    "01": {
        tab: "01",
        containerId: "timeworks-container01",
        tableId: "table-01-content",
        footerId: "footer-01",

        // nameBox: "name",
        // codeBox: "code",
        // codeChange: (e) => checkTimeWorksStartSaved(e)
    },

    // "02": {
    //     tab: "02",
    //     tableId: "table-02-content",
    //     footerId: "footer-02",
    //     searchId: "search-box-02",
    //     sideStr: "timeworks-sidemenu02",
    //     officeMenu: "#employee-list-02.normal-list>li",
    //     sidemenu: "employee-list-02",

    //     api: {
    //         list: "/api/timeworks/get/between/id",
    //         update: "/api/timeworks/update/list",
    //         confirm: "/api/timeworks/confirm"
    //     },

    //     editable: true,
    //     confirmable: true,

    //     selectedId: "#employee-list-02 li.selected",
    //     createList: (tab, id) => createEmployeeTimeworksList(tab, id)
    // },

    // "03": {
    //     tab: "03",
    //     tableId: "table-03-content",
    //     selectedId: "[name='office']",
    //     // createList: (tab, id) => createTimeworksSummaryList(id),
    //     createList: (tab, id) => createEmployeeTimeworksList(tab, id),

    //     api: {
    //         list: "/api/timeworks/summary/get/between/id"
    //     }
    // },

    // "04": {
    //     tab: "04",
    //     tableId: "table-04-contentn"
    // },

    // "05": {
    //     tab: "05",
    //     tableId: "table-05-content",
    //     footerId: "footer-05",
    //     searchId: "search-box-05",
    //     sideStr: "timeworks-sidemenu05",
    //     officeMenu: "#employee-list-05.normal-list>li",
    //     sidemenu: "employee-list-05",

    //     api: {
    //         list: "/api/timeworks/get/between/id/all",
    //         reverse: "/api/timeworks/confirm/reverse"
    //     },

    //     editable: false,
    //     confirmable: false,

    //     selectedId: "#employee-list-05 li.selected",
    //     createList: (tab, id) => createEmployeeTimeworksList(tab, id)
    // }
};

const CODE_CONFIG = {
    "01": {
        nameId: "name",
        codeId: "code",
        codeChange: (e) => checkTimeWorksStartSaved(e)
    },
    // "02": {
    //     nameId: "name02",
    //     codeId: "code02",
    // }
};