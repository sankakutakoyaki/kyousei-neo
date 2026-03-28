"use strict"

import { openMsgDialog, closeMsgDialog, openConfilmDialog, openFormDialog } from "../ui/dialog.js";
import { getTable } from "../init/initTable.js";
import { api } from "../api/apiService.js";
import { formatDate } from "../../util/time.js";

export class TableController {

    constructor(config){
        this.tableId = config.tableId;
        this.selectUrl = config.selectUrl;
        this.deleteUrl = config.deleteUrl;
        this.downloadUrl = config.downloadUrl;
        this.state = {};
        this.config = config;
    }

    // =========================
    // 基本操作
    // =========================

    get table(){ return getTable(this.tableId); }

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
    // 作成
    // =========================

    // create(key){ openFormDialog(key); }
    create(key){
        openFormDialog(key, {
            onSubmit: async (form) => {
                if(this.config.onSubmit){
                    await this.config.onSubmit.call(this, form);
                }
            }
        });
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
                // openMsgDialog(result.message, "blue");

                this.table.model.removeByIds(ids);
                this.table.reload();
            }
        );
    }

    // =========================
    // ダウンロードCSV（API連携）
    // =========================

    async downloadSelected(){
        const ids = this.table.model.getSelectedIds();
        if(ids.length === 0){
            openMsgDialog("選択してください", "red");
            return;
        }

        const res = await api.post(this.downloadUrl, { ids });
        const blob = res.data;
        const url = URL.createObjectURL(blob);
        const fileName = `download_${formatDate(new Date(), "yyyyMMddHHmmss")}.csv`;

        const a = document.createElement("a");
        a.href = url;
        a.download = fileName;
        a.click();
        URL.revokeObjectURL(url);
    }

    // =========================
    // 更新
    // =========================

    async refresh(){
        const result = await api.get(this.selectUrl);
        this.table.setData(result.data);
    }

    // =========================
    // 選択操作
    // =========================

    clearSelection(){
        this.table.model.clearSelection();
        this.table.reload();
    }
}