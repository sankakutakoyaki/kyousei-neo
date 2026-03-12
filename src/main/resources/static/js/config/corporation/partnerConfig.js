"use strict"

// export const search = [
//     {
//         searchId: "search-box-01",
//         searchTo: () => { console.log("search") }
//     }
// ]

// export const combo = [
//     {
//         comboId: "name01",
//         onChange: () => { console.log("combo") },
//         comboList: window.APP.page.companyComboList
//     }
// ]
export const partnerConfig = {

    combo:[
        {
            comboId:"name01",            
            comboList:APP.page.companyComboList,
            text: "",
            onChange: () => { console.log("combo") },
        }
    ],

    search:[
        {
            searchId:"search-box-01",
            searchTo: () => { console.log("search") }
        }
    ],

    resolve: true,
    postal: true,
    tabs: true,
    enterfocus: true

};