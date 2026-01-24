"use strict"

const MODE_CONFIG = {
    "01": {
        dialogId: "form-dialog-01",
        formId: "form-01",
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01"
    }
};

const ERROR_CONFIG = {
    recycle_maker: [
        {
            selector: 'input[name="maker-code"]',
            focus: true,
            checks: [
                { test: v => v !== "", message: "コードを入力してください"},
                { test: v => {
                        const n = Number(v);
                        return !Number.isNaN(n) && n >= 1 && n <= 999;
                    }, message: "コードは1〜999の間で入力してください" }
            ]
        },
        {
            selector: 'select[name="maker-group"]',
            checks: [
                { test: v => v !== "0", message: "グループを選択してください"}
            ]
        },
        {
            selector: 'input[name="maker-name"]',
            checks: [
                { test: v => v.trim() !== "", message: "製造業者等名を入力してください"}
            ]
        },
        {
            selector: 'input[name="maker-abbr"]',
            checks: [
                { test: v => v.trim() !== "", message: "略称を入力してください"}
            ]
        }
    ]
}