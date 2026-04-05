"use strict"

import { TableModel } from "./TableModel.js";
import { renderTable } from "./tableRender.js";
import { api } from "../api/apiService.js";
import { filterFactory } from "../../util/filterFactory.js";

const defaultModel = {
    pageSize: 50,
    filters: {
        keyword: filterFactory.keyword()
    }
};

export class DataTable {

    constructor(config){
        this.tableId = config.tableId;
        this.columns = config.columns;
        this.idKey = config.idKey;
        this.footerId = config.footerId;

        this.controller = config.controller;

        this.checkable = config.checkable;
        this.onRowClick = config.onRowClick;
        this.onDoubleClick = config.onDoubleClick;

        this.buildParams = config.buildParams;
        this.api = config.api || {};

        this.tableEl = document.getElementById(this.tableId);
        
        const userModel = config.model || {};

        this.model = new TableModel({
            idKey: this.idKey,

            // デフォルト + 上書き
            ...defaultModel,
            ...userModel,

            filters: {
                ...defaultModel.filters,
                ...(userModel.filters || {})
            }
        });

        if(config.data){
            this.model.setOrigin(config.data);
        }

        this.initEvents();
    }

    // -------------------------
    // 状態操作
    // -------------------------
    set(key,value){
        this.model.set(key,value);
        this.reload(); // ★ここで自動再描画
    }

    setMany(obj){
        Object.entries(obj).forEach(([k,v])=>{
            this.model.set(k,v);
        });
        this.reload();
    }

    setData(list){
        this.model.setOrigin(list);
        this.reload();
    }

    // 初期表示
    async initData(){
        await this.fetch();
        this.reload();
    }

    async fetch(){
        if(!this.api.select) return;

        const params = this.buildParams ? this.buildParams() : {};
        const res = await this.api.select(params);
        this.model.setOrigin(res.data ?? res);
    }

    reload(){
        this.model.compute();
        this.render();
    }

    async refresh(){
        await this.fetch();
        this.reload();
    }

    render(){
        renderTable(
            this.tableEl,
            {
                columns: this.columns,
                idKey: this.idKey,
                footerId: this.footerId,
                checkable: this.checkable
            },
            this.model.getViewData()
        );

        if(this.controller){
            this.controller.updateButtons();
        }
    }

    sort(field){
        this.model.toggleSort(field);
        this.reload();
    }

    async deleteByIds(ids){
        if(!this.api.delete) return;

        // const result = await this.api.delete(ids);
        const result = await api.post(this.api.delete, { ids });
        await this.refresh();

        return result;
    }
    // async deleteByIds(ids){
    //     if(!this.api.delete) return;

    //     const result = await this.api.delete({ ids });
    //     await this.refresh();
    //     return result;
    // }

    hasSelection(){
        return this.model.getSelectedIds().length > 0;
    }

    // -------------------------
    // イベント
    // -------------------------
    initEvents(){

        const table = this.tableEl.closest('.normal-table');
        if (!table) return;

        // ヘッダークリック（ソート）
        table.addEventListener("click",(e)=>{
            if(e.target.name === "all-chk-btn") return;

            const th = e.target.closest("th");
            if(!th) return;

            const field = th.dataset.field;
            if(field) this.sort(field);
        });

        table.addEventListener("click",(e)=>{
            if(e.target.name !== "all-chk-btn") return;
            
            const chkAll = e.target;
            const data = this.model.getData();
            if(chkAll.checked){
                data.forEach(v=>{
                    this.model.selected.add(v[this.idKey]);
                });
            }else{
                this.model.clearSelection();
            }
            this.reload();
        });

        // 行クリック
        this.tableEl.addEventListener("click",(e)=>{

            // checkbox判定
            const chk = e.target.closest('input[name="chk-box"]');
            if(chk){
                const id = Number(chk.dataset.id);
                this.model.toggleSelect(id);
                this.controller?.updateButtons();

                return;
            }

            const row = e.target.closest("[data-id]");
            if(!row) return;

            if(this.onRowClick){
                const id = row.dataset.id;
                const item = this.model.findById(id);
                this.onRowClick(item, row, e);
            }
        });

        // ダブルクリック
        this.tableEl.addEventListener("dblclick",(e)=>{
            const row = e.target.closest("[data-id]");
            if(!row) return;

            if(this.onDoubleClick){
                const id = row.dataset.id;
                const item = this.model.findById(id);
                this.onDoubleClick(item, row, e);
            }
        });
    }
}

// "use strict"

// import { TableModel } from "./TableModel.js";
// import { renderTable } from "./tableRender.js";
// // import { openConfilmDialog, openMsgDialog } from "../ui/dialog.js";

// export class DataTable {

//     constructor(config){
//         this.config = config;
//         this.tableEl = document.getElementById(config.tableId);
//         this.model = new TableModel(config);
//         this.initHeaderEvents();
//         this.initRowEvents();
//         this.initHeaderCheckbox();
//         this.initCellEdit();
//     }

//     async initData(){
//         const ds = this.config.dataSource;
//         if(!ds) return;

//         if(ds.type === "api"){
//             const res = await api.get(ds.url);
//             this.model.origin = () => res.data;
//         }

//         if(ds.type === "origin"){
//             this.model.origin = () => ds.data;
//         }

//         this.reload();
//     }

//     set(key,value){
//         this.model.set(key,value);
//     }

//     setData(list){
//         this.model.origin = () => list;
//         this.reload();
//     }
    
//     reload(){
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
//         this.updateHeaderCheckbox();
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

//     initHeaderEvents(){
//         const table = this.tableEl.closest('.normal-table');
//         if (!table) return;

//         table.addEventListener("click",(e)=>{
//             const th = e.target.closest("th");
//             if(!th) return;

//             const field = th.dataset.field;
//             if(!field) return;

//             this.sort(field);
//         });
//     }

    // initHeaderCheckbox(){
    //     const table = this.tableEl.closest('.normal-table');
    //     if (!table) return;

    //     table.addEventListener("click",(e)=>{
    //         if(e.target.name !== "all-chk-btn") return;
    //         const chkAll = e.target;
    //         const data = this.model.getData();
    //         if(chkAll.checked){
    //             data.forEach(v=>{
    //                 this.model.selected.add(v[this.config.idKey]);
    //             });
    //         }else{
    //             this.model.clearSelection();
    //         }
    //         this.render();
    //     });
    // }

//     initRowEvents(){
//         this.tableEl.addEventListener("click",(e)=>{
//             // checkbox
//             if(e.target.name === "chk-box"){
//                 const id = Number(e.target.dataset.id);
//                 this.model.toggleSelect(id);
//                 this.updateHeaderCheckbox();
//                 return;
//             }
//             // row
//             const row = e.target.closest("tr[data-id]");
//             if(!row) return;

//             this.selectRow(row);
//         });

//         this.tableEl.addEventListener("dblclick",(e)=>{
//             const row = e.target.closest("tr[data-id]");
//             if(!row) return;

//             this.selectRow(row);
//             if(this.config.onDoubleClick){
//                 const id = row.dataset.id;
//                 const item = this.findItem(id);
//                 this.config.onDoubleClick(item, row, e);
//             }
//         });
//     }

//     initCellEdit(){
//         this.tableEl.addEventListener("click",(e)=>{
//             const td = e.target.closest("td.editable");
//             if(!td) return;
//             // checkboxなど除外
//             if(e.target.name === "chk-box") return;
//             // すでに編集中
//             if(td.querySelector("input,select")) return;

//             this.startCellEdit(td);
//         });
//     }


//     startCellEdit(td){
//         const originalText = td.textContent.trim();
//         const originalValue = td.dataset.value;
//         const editType = td.dataset.editType || "text";

//         let editor;
//         let canceled = false;

//         if(editType === "select"){
//             editor = document.createElement("select");
//             const key = td.dataset.optionsKey;
//             const options = SELECT_OPTIONS[key] || [];
//             options.forEach(opt=>{
//                 const option = document.createElement("option");
//                 option.value = opt.number;
//                 option.textContent = opt.text;
//                 if(String(opt.number) === originalValue){
//                     option.selected = true;
//                 }
//                 editor.appendChild(option);
//             });
//         }else{
//             editor = document.createElement("input");
//             editor.type = "text";
//             editor.value = originalText === "-----" ? "" : originalText;
//         }

//         editor.classList.add("normal-input");
//         editor.style.width = "100%";
//         td.textContent = "";
//         td.appendChild(editor);
//         editor.focus();
//         if(editor instanceof HTMLInputElement) editor.select();

//         editor.addEventListener("keydown",(e)=>{
//             if(e.isComposing) return;

//             if(e.key === "Escape"){
//                 canceled = true;
//                 this.restoreCell(td, originalText, originalValue);
//                 return;
//             }
//             if(e.key === "Enter"){
//                 e.preventDefault();
//                 editor.blur();
//             }
//         });

//         editor.addEventListener("blur",()=>{
//             if(canceled) return;
//             this.saveEditor(td, editor, originalText);
//         });
//     }

//     updateHeaderCheckbox(){
//         const table = this.tableEl.closest(".normal-table");
//         if(!table) return;

//         const chkAll = table.querySelector("[name='all-chk-btn']");
//         if(!chkAll) return;

//         const list = this.model.getData();

//         if(list.length === 0){
//             chkAll.checked = false;
//             chkAll.indeterminate = false;
//             return;
//         }

//         let checkedCount = 0;
//         list.forEach(v=>{
//             if(this.model.isSelected(v[this.config.idKey])){
//                 checkedCount++;
//             }
//         });

//         if(checkedCount === 0){
//             chkAll.checked = false;
//             chkAll.indeterminate = false;
//         }
//         else if(checkedCount === list.length){
//             chkAll.checked = true;
//             chkAll.indeterminate = false;
//         }
//         else{
//             chkAll.checked = false;
//             chkAll.indeterminate = true; // ←中間状態
//         }
//     }

//     selectRow(row){
//         const rows = this.tableEl.querySelectorAll("tr[data-id]");
//         rows.forEach(r=>{
//             r.classList.remove("selected");
//         });
//         row.classList.add("selected");
//     }

//     findItem(id){
//         const list = this.model.origin(); // 状況に応じて originとgetDate()を使い分ける
//         return list.find(v =>
//             String(v[this.config.idKey]) === String(id)
//         );
//     }
    
//     restoreCell(td, text, value) {
//         td.textContent = text || '-----';
//         if (value !== undefined) {
//             td.dataset.value = value;
//         }
//     }

//     saveEditor(td, editor, currentValue){

//         const row = td.closest("tr");
//         const id = row.dataset.id;
//         const field = td.dataset.field;

//         let value;

//         if (editor instanceof HTMLSelectElement) {
//             const opt = editor.selectedOptions[0];
//             value = opt.value;
//             td.dataset.value = value;
//             td.textContent = opt.textContent;
//         } else {
//             value = editor.value?.trim();
//             td.textContent = value || currentValue || '-----';
//         }

//         this.updateCell(id, field, value);
//     }
// }