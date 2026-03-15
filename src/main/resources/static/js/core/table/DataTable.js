"use strict"

import {TableModel} from "./TableModel.js";
import {renderTable} from "./tableRender.js"

export class DataTable {

    constructor(config){

        this.config = config;
        this.tableEl = document.getElementById(config.tableId);

        this.model = new TableModel(config);

        this.initHeaderSort();
    }

    async initData(){

        const ds = this.config.dataSource;
        if(!ds) return;

        if(ds.type === "api"){
            const res = await api.get(ds.url);
            this.model.origin = () => res.data;
        }

        if(ds.type === "origin"){
            this.model.origin = () => ds.data;
        }

        this.reload();
    }

    set(key,value){
        this.model.set(key,value);
    }

    reload(){

        this.model.compute();
        this.render();

    }

    render(){

        renderTable(
            this.tableEl,
            this.config,
            this.model.getData(),
            this
        );

    }

    sort(field){

        const dir =
            this.model.sortKey === field &&
            this.model.sortDir === "asc"
                ? "desc"
                : "asc";

        this.model.setSort(field,dir);

        this.reload();

    }

    initHeaderSort(){

        const table = this.tableEl.closest("table");
        if(!table) return;

        const headers = table.querySelectorAll("th[data-field]");

        headers.forEach(th=>{

            const field = th.dataset.field;

            th.addEventListener("click",()=>{

                this.sort(field);

            });

        });

    }

}

// import { TableModel } from "./TableModel.js";
// import { renderTable } from "./tableRender.js";

// export class DataTable {

//     constructor(config){
//         this.config = config;
//         this.tableEl = document.getElementById(config.tableId);
//         this.model = new TableModel(config);

//         this.initHeaderSort();
//     }

//     async initData(){
//         await this.loadData();
//         this.reload();
//     }

//     async loadData(){
//         const ds = this.config.dataSource;
//         if(!ds) return;

//         if(ds.type === "api"){
//             const res = await api.get(ds.url);
//             this.model.origin = () => res.data;
//         }

//         if(ds.type === "origin"){
//             this.model.origin = () => ds.data;
//         }
//     }

//     set(key,value){
//         this.model.set(key,value);
//     }

//     setData(list){
//         this.model.origin = () => list;
//         this.reload();
//     }

//     async reload(){
//         if(this.config.dataSource?.type === "api"){
//             await this.loadData();
//         }
//         this.model.compute();
//         this.render();
//     }

//     render(){
//         renderTable(
//             this.tableEl,
//             this.config,
//             this.model.getData(),
//             this
//         );
//     }

//     sort(field){
//         const dir =
//             this.model.sortKey === field &&
//             this.model.sortDir === "asc"
//                 ? "desc"
//                 : "asc";

//         this.model.setSort(field,dir);

//         this.reload();

//     }

//     initHeaderSort(){
//         const table = this.tableEl.closest("table");
//         if(!table) return;

//         const headers = table.querySelectorAll("th[data-field]");
//         headers.forEach(th=>{
//             const field = th.dataset.field;
//             th.addEventListener("click",()=>{
//                 const current = th.dataset.sort;
//                 const dir = current === "asc" ? "desc" : "asc";
//                 this.model.setSort(field,dir);
//                 this.updateSortIndicator(table,th,dir);
//                 this.reload();
//             });
//         });
//     }

//     updateSortIndicator(table,activeTh,dir){
//         const headers = table.querySelectorAll("th[data-field]");

//         headers.forEach(th=>{
//             th.dataset.sort = "";
//             th.classList.remove("sorted-asc","sorted-desc");
//         });

//         activeTh.dataset.sort = dir;

//         if(dir === "asc"){
//             activeTh.classList.add("sorted-asc");
//         }else{
//             activeTh.classList.add("sorted-desc");
//         }
//     }
// }


// // import {TableStore} from "./TableStore.js";
// // import {renderTable} from "./tableRender.js";

// // export class DataTable {
// //     constructor(config){
// //         this.config = config;
// //         this.tableEl = document.getElementById(config.tableId);
// //         this.store = new TableStore();
// //     }

// //     load(list){
// //         this.store.setData(list);
// //         this.render();
// //     }

// //     render(){
// //         renderTable(
// //             this.tableEl,
// //             this.config,
// //             this.store.getData(),
// //             this
// //         );
// //     }

// //     refresh(filters=[]){
// //         this.store.applyFilters(filters);
// //         this.render();
// //     }
// // }