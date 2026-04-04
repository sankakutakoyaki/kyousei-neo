"use strict"

import { initCombo } from "../core/init/initCombo.js";
import { smartFilterHandler } from "../core/behavior/filterHandler.js";
import { resolveController } from "../util/actionDispatcher.js";

const defaultConditions = {
    delete: (c) => c.dataTable?.hasSelection(),
    download: (c) => c.dataTable?.hasSelection()
};

const defaultActions = {
    create: (c) => c.form.open(),
    edit: (c, el) => c.openEdit(el.dataset.id),
    search: (c, el) => c.dataTable.set("keyword", el.value),
    filter: smartFilterHandler,
    delete: async (c) => c.deleteSelected(),
    download: async (c) => c.downloadSelected()
};

export class PageController {

    constructor(config){
        this.config = Object.freeze(config);
        this.key = config.key;
        this.state = {};
        this.conditions = config.conditions || {};

        this.dataTable = null;
        this.form = null;

        // // デフォルト状態処理
        // const defaultConditions = {
        //     delete: (c) => c.dataTable?.hasSelection(),
        //     download: (c) => c.dataTable?.hasSelection()
        // };

        // // ★ デフォルトアクション
        // this.defaultActions = {
        //     create: (controller) => controller.form.open(),

        //     edit: (controller, el) => {
        //         const id = el.dataset.id;
        //         controller.openEdit(id);
        //     },

        //     search: (controller, el) => {
        //         controller.dataTable.set("keyword", el.value);
        //     },

        //     filter: smartFilterHandler,

        //     delete: async (controller) => {
        //         await controller.deleteSelected();
        //     },

        //     download: async (controller) => {
        //         await controller.downloadSelected();
        //     }
        // };
    }

    init(config = {}){
        this.config = {
            ...this.config,
            ...config
        };
        this.actions = {
            ...defaultActions,
            ...(config.actions || {})
        };
        this.conditions = {
            ...defaultConditions,
            ...(config.conditions || {})
        };
        this.initComponents();
        this.initUI();
        this.updateButtons();
    }

    initComponents(){

        const { columns, data } = this.config;

        if(this.config.table){
            this.dataTable = this.config.table.create(this, columns);
            if(data){
                this.dataTable.model.setOrigin(data);
                this.dataTable.reload();
            } else {
                this.dataTable.initData(); // API
            }
        }
        if(this.config.form){
            this.form = this.config.form.create(this);
        }
    }

    initUI(){

        const { components } = this.config;

        if(components?.combo){
            initCombo();
        }
    }

    // -------------------------
    // 検索
    // -------------------------
    search(keyword){
        this.dataTable.set("keyword", keyword);
    }

    // -------------------------
    // UI操作
    // -------------------------
    create(){
        this.form.open();
    }

    async openEdit(id){
        await this.form.open(id);
    }

    async deleteSelected(){
        const ids = this.dataTable.model.getSelectedIds();
        if(!this.ensureSelection(ids)) return;
        // if(ids.length === 0){
        //     openMsgDialog("選択してください", "red");
        //     return;
        // }
        openConfilmDialog("削除しますか？", "blue",
            async () => {
                await this.executeDelete(ids);
            }
        );
    }

    async executeDelete(ids){
        closeMsgDialog();
        const result = await api.post(this.dataTable.api.delete, { ids });
        openMsgDialog(result.message, "blue");
        this.dataTable.model.removeByIds(ids);
        await this.refresh();
    }

    async downloadSelected(){
        const ids = this.dataTable.model.getSelectedIds();
        if(!this.ensureSelection(ids)) return;
        // if(ids.length === 0){
        //     openMsgDialog("選択してください", "red");
        //     return;
        // }

        const res = await api.post(this.dataTable.api.download, { ids });
        const blob = res.data;
        const url = URL.createObjectURL(blob);
        const fileName = `download_${formatDate(new Date(), "yyyyMMddHHmmss")}.csv`;

        const a = document.createElement("a");
        a.href = url;
        a.download = fileName;
        a.click();
        URL.revokeObjectURL(url);
    }

    ensureSelection(ids){
        if(ids.length === 0){
            openMsgDialog("選択してください", "red");
            return false;
        }
        return true;
    }

    scrollToRow(id){
        const row = document.querySelector(`[data-id="${id}"]`);
        row?.scrollIntoView({ block:"center" });
    }

    /**
     * ボタンの状態判定
     */
    isEnabled(action){
        const fn = this.conditions[action];
        if(!fn) return true;

        return fn(this);
    }

    /**
     * ボタン制御関数
     */
    updateButtons(){
        document.querySelectorAll("[data-action]").forEach(el => {
            const controller = resolveController(el);
            if(controller !== this) return;

            const action = el.dataset.action;
            const enabled = this.isEnabled(action);
            if("disabled" in el){
                el.disabled = !enabled;
            }
            el.classList.toggle("disabled", !enabled);
            el.style.pointerEvents = enabled ? "auto" : "none";
        });
    }
}

// export class PageController {

//     constructor(config){
//         this.config = Object.freeze(config);
//         this.key = config.key ?? null;

//         this.dataTable = null;
//         this.form = null;
//     }

//     init(options = {}){
//         this.initComponents(options);
//         // this.bindEvents();
//     }

//     initComponents({ columns } = {}){

//         if(this.config.table){
//             this.initTable(columns);
//         }

//         if(this.config.form){
//             this.initForm();
//         }
//     }

//     initTable(columns){
//         this.dataTable = this.config.table.create(this, columns);
//     }

//     initForm(){
//         this.form = this.config.form.create(this);
//     }

//     // -------------------------
//     // 検索
//     // -------------------------
//     search = (keyword) => {
//         this.dataTable.set("keyword", keyword);
//     }

//     // -------------------------
//     // 保存
//     // -------------------------
//     async save(form){
//         const id = await this.form.save(form);
//         await this.refresh(id);
//     }

//     // -------------------------
//     // 再取得
//     // -------------------------
//     async refresh(targetId){
//         const res = await api.get(this.selectUrl);
//         this.dataTable.setData(res.data);

//         if(targetId){
//             requestAnimationFrame(()=>{
//                 this.scrollToRow(targetId);
//             });
//         }
//     }

//     scrollToRow(id){
//         const row = document.querySelector(`[data-id="${id}"]`);
//         if(row){
//             row.scrollIntoView({ block:"center" });
//         }
//     }

//     // =========================
//     // 作成
//     // =========================
//     create(){
//         this.form.open();
//     }

//     // =========================
//     // 編集画面を開く
//     // =========================
//     async openEdit(id){
//         await this.form.open(id);
//     }

//     // =========================
//     // 削除（API連携）
//     // =========================

//     async deleteSelected(){
//         const ids = this.dataTable.model.getSelectedIds();
//         if(ids.length === 0){
//             openMsgDialog("選択してください", "red");
//             return;
//         }

//         openConfilmDialog("削除しますか？", "blue",
//             async () => {
//                 closeMsgDialog();

//                 const result = await api.post(this.deleteUrl, {ids:ids});
//                 openMsgDialog(result.message, "blue");

//                 this.dataTable.model.removeByIds(ids);
//                 await this.refresh();
//             }
//         );
//     }

//     async deleteById(id){
//         openConfilmDialog("削除しますか？", "red", async () => {
//             closeMsgDialog();
//            const result = await api.post(this.deleteUrl, {
//                 ids: [id]
//             });
//         });
//     }

//     async deleteCurrent(){
//         if(!this.currentEntity) return;

//         const id = this.currentEntity[this.key];
//         openConfilmDialog("削除しますか？", "red", async () => {
//             closeMsgDialog();
//             const result = await api.post(this.deleteUrl, {
//                 ids: [id]
//             });
//             openMsgDialog(result.message, "blue");
//             closeFormDialog(this.formId);
//             await this.refresh(id);
//         });
//     }

//     // =========================
//     // ダウンロードCSV（API連携）
//     // =========================

//     async downloadSelected(){
//         const ids = this.dataTable.model.getSelectedIds();
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
//     // フォームにセット
//     // =========================
//     fillForm(formId, data){
//         this.form.set(formId, data);
//     }

//     // =========================
//     // フォームクリア
//     // =========================
//     clearForm(){
//         this.form.clear();
//     }
// }