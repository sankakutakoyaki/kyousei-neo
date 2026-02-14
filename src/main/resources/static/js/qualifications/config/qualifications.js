"use strict"

const MODE_CONFIG = {
    "01": {
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01"
    },
    // "02": {
    //     codeBox: "code-box-01",
    //     nameBox: "name-box-01",
    // },
    // "03": {
    //     codeBox: "code-box-02",
    //     nameBox: "name-box-02",
    // }
}

const CODE_CONFIG = [
    {
        area: "code-area-01",
        codeId: "code-box-01",
        nameId: "name-box-01",
        onChange: async () => {
            getQualifications;
        }
    },
    {
        area: "code-area-02",
        codeId: "code-box-02",
        nameId: "name-box-02",
        onChange: async () => {
            getQualifications;
        }
    }
];