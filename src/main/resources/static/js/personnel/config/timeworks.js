"use strict"

const TIMEWORKS_TIME_CONFIG = {
    start: {
        timeKey: "start_time",
        compTimeKey: "comp_start_time",
        latKey: "start_latitude",
        lngKey: "start_longitude"
    },
    end: {
        timeKey: "end_time",
        compTimeKey: "comp_end_time",
        latKey: "end_latitude",
        lngKey: "end_longitude"
    }
};

const TIMEWORKS_TAB_CONFIG = {
    "02": {
        tab: "02",
        tableId: "table-02-content",
        footerId: "footer-02",
        searchId: "search-box-02",
        sideStr: "timeworks-sidemenu02",
        officeMenu: "#employee-list-02.normal-list>li",
        sidemenu: "employee-list-02",

        api: {
            list: "/api/timeworks/get/between/id",
            update: "/api/timeworks/update/list",
            confirm: "/api/timeworks/confirm"
        },

        editable: true,
        confirmable: true,

        selectedId: "#employee-list-02 li.selected",
        createList: (tab, id) => createEmployeeTimeworksList(tab, id)
    },

    "03": {
        tab: "03",
        selectedId: "[name='office']",
        createList: (tab, id) => createTimeworksSummaryList(id),

        api: {
            list: "/api/timeworks/summary/get/between/id"
        }
    },

    "05": {
        tab: "05",
        tableId: "table-05-content",
        footerId: "footer-05",
        searchId: "search-box-05",
        sideStr: "timeworks-sidemenu05",
        officeMenu: "#employee-list-05.normal-list>li",
        sidemenu: "employee-list-05",

        api: {
            list: "/api/timeworks/get/between/id/all",
            reverse: "/api/timeworks/confirm/reverse"
        },

        editable: false,
        confirmable: false,

        selectedId: "#employee-list-05 li.selected",
        createList: (tab, id) => createEmployeeTimeworksList(tab, id)
    }
};