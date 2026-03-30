"use strict"

import { openMsgDialog, closeMsgDialog, openConfilmDialog, openFormDialog, closeFormDialog } from "../ui/dialog.js";
import { getTable } from "../init/initTable.js";
import { api } from "../api/apiService.js";
// import { formatDate } from "../../util/time.js";

export class TableController {

    constructor(config){
        this.tableId = config.tableId;
        this.formId = config.formId;
        this.selectUrl = config.selectUrl;
        this.saveParent = config.parent;
        this.key = config.key;
        this.state = {};

        this.tableEl = document.getElementById(this.tableId);

        this.config = config;

        this.currentEntity = null; // 編集対象
        this.isEdit = false;

        this.initEvents();
        
    }

    // =========================
    // 基本操作
    // =========================

    get table(){
        const table = getTable(this.tableId);
        if(!table){
            console.warn("table not ready:", this.tableId);
        }
        return table;
    }

    set(key,value){
        this.state[key] = value;
        this.table.set(key,value);
        this.table.reload();
    }

    setMany(obj){
        Object.entries(obj).forEach(([k,v])=>{
            this.table.set(k,v);
        });
        this.table.reload();
    }

    reload(){ this.table.reload(); }

    // =========================
    // 検索
    // =========================

    search(keyword){ this.set("keyword", keyword); }

    // =========================
    // フィルタ
    // =========================

    filter(key,value){ this.set(key,value); }

    // =========================
    // 初期イベント
    // =========================
    initEvents(){
        this.initRowClick();
    }

    // =========================
    // 行クリック（編集）
    // =========================
    initRowClick(){

        this.tableEl.addEventListener("click", (e) => {

            const row = e.target.closest("[data-id]");
            if (!row) return;

            const id = row.dataset.id;

            // 削除
            if(e.target.classList.contains("delete-btn")){
                this.deleteById(id);
                return;
            }

            // IDクリック
            if(e.target.classList.contains("id-cell")){
                this.openEdit(id);
                return;
            }
        });

        // ダブルクリック（編集）
        this.tableEl.addEventListener("dblclick", (e) => {
            const row = e.target.closest("[data-id]");
            if (!row) return;

            const id = row.dataset.id;
            this.openEdit(id);
        });
    }

    // =========================
    // 作成
    // =========================
    create(key){
        this.currentEntity = {};

        openFormDialog(key, {
            onSubmit: async (form) => {
                if(this.config.onSubmit){
                    await this.config.onSubmit.call(this, form, this.currentEntity);
                }
            }
        });
    }

    // =========================
    // 編集画面を開く
    // =========================
    async openEdit(id){
        const res = await api.get(`${this.selectUrl}/${id}`);
        const data = res.data;
        this.currentEntity = data;
        const self = this;
        this.fillForm(this.config.formId, data);
        openFormDialog(this.config.formId, {
            onSubmit: async function(form){
                if(self.config.onSubmit){
                    await self.config.onSubmit.call(self, form, self.currentEntity);
                }
            }
        });
    }

    // =========================
    // 新規
    // =========================
    openCreate(){
        this.currentEntity = {};
        this.isEdit = false;

        this.clearForm();
        openFormDialog(this.formId);
    }

    // =========================
    // 保存
    // =========================
    async save(form){

        if(!validate(form)) return;

        const edited = buildEntity(form, this.currentEntity);

        let payload;

        if(this.isEdit){

            const diff = diffEntity(this.currentEntity, edited);

            if(Object.keys(diff).length === 0){
                openMsgDialog("変更がありません", "red");
                return;
            }

            diff.id = this.currentEntity[this.key];
            diff.version = this.currentEntity.version;

            payload = diff;

        }else{
            payload = edited;
        }

        const result = await api.post(`/api/${this.saveParent}/save`, payload);

        if(result.ok){
            openMsgDialog(result.message, "blue");
        }

        closeFormDialog(this.formId);

        // 再読み込み＋スクロール
        await this.refresh(result.data);
    }

    // =========================
    // 削除（API連携）
    // =========================

    async deleteSelected(){
        const ids = this.table.model.getSelectedIds();
        if(ids.length === 0){
            openMsgDialog("選択してください", "red");
            return;
        }

        openConfilmDialog("削除しますか？", "blue",
            async () => {
                closeMsgDialog();

                const result = await api.post(this.deleteUrl, {ids:ids});
                openMsgDialog(result.message, "blue");

                this.tableEl.model.removeByIds(ids);
                this.tableEl.reload();
            }
        );
    }

    async deleteById(id){
        openConfilmDialog("削除しますか？", "red", async () => {
            closeMsgDialog();
           const result = await api.post(this.deleteUrl, {
                ids: [id]
            });
            this.tableEl.model.removeByIds([id]);
            this.tableEl.reload();
        });
    }

    async deleteCurrent(){
        if(!this.currentEntity) return;

        const id = this.currentEntity[this.config.key];
        openConfilmDialog("削除しますか？", "red", async () => {
            closeMsgDialog();
            const result = await api.post(this.deleteUrl, {
                ids: [id]
            });
            openMsgDialog(result.message, "blue");
            closeFormDialog(this.config.formId);
            await this.refresh(id);
        });
    }

    // =========================
    // フォームにセット
    // =========================
    fillForm(formId, data){

        const form = document.getElementById(formId);

        Object.entries(data).forEach(([key, value]) => {

            const name = convertKey(key, "camel", "kebab");
            const el = form.elements[name];

            if(!el) return;

            if(el.type === "checkbox"){
                el.checked = !!value;
            }else{
                el.value = value ?? "";
            }
        });
    }

    // =========================
    // フォームクリア
    // =========================
    clearForm(){
        const form = document.getElementById(this.formId);

        [...form.elements].forEach(el => {
            if(!el.name) return;

            if(el.type === "checkbox"){
                el.checked = false;
            }else{
                el.value = "";
            }
        });
    }

    // =========================
    // 再読み込み
    // =========================
    async refresh(targetId){

        const result = await api.get(this.selectUrl);
        this.table.setData(result.data);
        // this.table.reload();

        if(targetId){
            requestAnimationFrame(() => {
                this.scrollToRow(targetId);
            });
        }
    }

    // =========================
    // 行スクロール
    // =========================
    scrollToRow(id){

        const row = this.tableEl.querySelector(`[data-id="${id}"]`);
        if(!row) return;

        row.scrollIntoView({
            behavior: "smooth",
            block: "center"
        });

        row.classList.add("highlight");
        setTimeout(() => row.classList.remove("highlight"), 2000);
    }
}

// export class TableController {

//     constructor(config){
//         this.tableId = config.tableId;
//         this.tableEl = document.getElementById(config.tableId);
//         this.selectUrl = config.selectUrl;
//         this.deleteUrl = config.deleteUrl;
//         this.downloadUrl = config.downloadUrl;
//         this.state = {};
//         this.config = config;
//     }

//     // =========================
//     // 基本操作
//     // =========================

//     get table(){ return getTable(this.tableId); }

//     set(key,value){
//         this.state[key] = value;
//         this.table.set(key,value);
//         this.table.reload();
//     }

//     setMany(obj){
//         Object.entries(obj).forEach(([k,v])=>{
//             this.table.set(k,v);
//         });
//         this.table.reload();
//     }

//     reload(){ this.table.reload(); }

//     // =========================
//     // 検索
//     // =========================

//     search(keyword){ this.set("keyword", keyword); }

//     // =========================
//     // フィルタ
//     // =========================

//     filter(key,value){ this.set(key,value); }

//     // =========================
//     // 作成
//     // =========================

//     // create(key){ openFormDialog(key); }
//     create(key){
//         openFormDialog(key, {
//             onSubmit: async (form) => {
//                 if(this.config.onSubmit){
//                     await this.config.onSubmit.call(this, form);
//                 }
//             }
//         });
//     }

//     // =========================
//     // 削除（API連携）
//     // =========================

//     async deleteSelected(){
//         const ids = this.table.model.getSelectedIds();
//         if(ids.length === 0){
//             openMsgDialog("選択してください", "red");
//             return;
//         }

//         openConfilmDialog("削除しますか？", "blue",
//             async () => {
//                 closeMsgDialog();

//                 const result = await api.post(this.deleteUrl, {ids:ids});
//                 // openMsgDialog(result.message, "blue");

//                 this.table.model.removeByIds(ids);
//                 this.table.reload();
//             }
//         );
//     }

//     // =========================
//     // ダウンロードCSV（API連携）
//     // =========================

//     async downloadSelected(){
//         const ids = this.table.model.getSelectedIds();
//         if(ids.length === 0){
//             openMsgDialog("選択してください", "red");
//             return;
//         }

//         const res = await api.post(this.downloadUrl, { ids });
//         const blob = res.data;
//         const url = URL.createObjectURL(blob);
//         const fileName = `download_${formatDate(new Date(), "yyyyMMddHHmmss")}.csv`;

//         const a = document.createElement("a");
//         a.href = url;
//         a.download = fileName;
//         a.click();
//         URL.revokeObjectURL(url);
//     }

//     // =========================
//     // 更新
//     // =========================

//     async refresh(targetId){
//         const result = await api.get(this.selectUrl);
//         this.table.setData(result.data);

//         if(targetId){
//             requestAnimationFrame(() => {
//                 this.scrollToRow(targetId);
//             });
//         }
//     }

//     scrollToRow(id){
//         const row = this.tableEl.querySelector(`[data-id="${id}"]`);
//         if(!row) return;

//         row.scrollIntoView({
//             behavior: "smooth",
//             block: "center"
//         });
//         // row.classList.add("highlight");
//         // setTimeout(() => row.classList.remove("highlight"), 2000);
//     }
    
//     // =========================
//     // 選択操作
//     // =========================

//     clearSelection(){
//         this.table.model.clearSelection();
//         this.table.reload();
//     }
// }