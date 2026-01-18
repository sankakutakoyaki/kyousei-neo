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

    api: {
      list: "/api/timeworks/get/between/id",
      update: "/api/timeworks/update/list",
      confirm: "/api/timeworks/confirm"
    },

    editable: true,
    confirmable: true
  },

  "05": {
    tab: "05",
    tableId: "table-05-content",
    footerId: "footer-05",
    searchId: "search-box-05",

    api: {
      list: "/api/timeworks/get/between/id/all",
      reverse: "/api/timeworks/confirm/reverse"
    },

    editable: false,
    confirmable: false
  }
};