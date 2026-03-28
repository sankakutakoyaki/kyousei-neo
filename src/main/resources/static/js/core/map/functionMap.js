"use strict"

const functionMap = {
    open: (res) => {
        console.log("作成完了", res);
    },
    reloadTable: (c) => {
        c.refresh();
    }
};