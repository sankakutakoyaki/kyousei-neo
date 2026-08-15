"use strict"

import { TableModel } from "./TableModel.js";
import { renderTable } from "./tableRender.js";
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
        this.rowClass = config.rowClass;
        this.onRowClick = config.onRowClick;
        this.onDoubleClick = config.onDoubleClick;
        this.currentRowId = null;
        this.buildParams = config.buildParams;
        this.buildCsvParams = config.buildCsvParams;
        this.repository = config.repository;
        // 表示機能
        this.infiniteScroll = config.infiniteScroll ?? true;
        this.pageTopButton = config.pageTopButton ?? true;

        this.canSave = config.canSave;        
        this.tableEl = document.getElementById(this.tableId);        
        const userModel = config.model || {};
        this.model = new TableModel({
            idKey: this.idKey,
            // デフォルト + 上書き
            ...defaultModel,
            ...userModel,
            // filters: {
            //     ...defaultModel.filters,
            //     ...(userModel.filters || {})
            // }
            filters: userModel.filters ?? defaultModel.filters
        });
        if(config.data){
            this.model.setOrigin(config.data);
        }
        this.initEvents();
        // this.initInfiniteScroll();
        if(this.infiniteScroll){
            this.initInfiniteScroll();
        }
        this.initOutsideClick();
    }

    // 状態操作
    setData(list){
        this.model.setOrigin(list);
    }

    // 初期表示
    async initData(){
        await this.fetch();
        this.reload();
    }

    async fetch(){
        if(!this.repository) return;

        const params = this.buildParams ? this.buildParams(): {};
        const data = await this.repository.search(params);
        this.model.setOrigin(data ?? []);
    }

    async deleteByIds(ids){
        if(!this.repository?.remove) return;

        const result = await this.repository.remove({ids});
        await this.refresh();
        return result;
    }

    async downloadCsvByIds(ids){
        if(!this.repository?.download) return;

        const params = {...(this.buildCsvParams ? this.buildCsvParams(): {}), ids};
        const result = await this.repository.download(params);
        await this.refresh();
        return result;
    }

    reload(){
        this.model.compute(this.controller.state);
        this.render();
    }

    async refresh(id){
        await this.fetch();
        this.reload();
        if(id){
            requestAnimationFrame(() => {
                this.scrollToRow(id);
            });
        }
    }

    render(){
        renderTable(
            this.tableEl,
            {
                columns: this.columns,
                idKey: this.idKey,
                footerId: this.footerId,
                checkable: this.checkable,
                rowClass: this.rowClass,
                controller: this.controller,
                onRowClick: this.onRowClick,
                onDoubleClick: this.onDoubleClick,
                totalCount: this.model.getTotalCount(),
                pageTopButton: this.pageTopButton
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

    hasSelection(){
        return this.model.getSelectedIds().length > 0;
    }

    getSelectedIds(){
        return this.model.getSelectedIds();
    }

    findOriginById(id){
        return this.model.findOriginById(id);
    }

    // イベント
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
            // 選択解除
            this.tableEl.querySelectorAll(".selected").forEach(r => r.classList.remove("selected"));
            // 選択
            row.classList.add("selected");
            this.currentRowId = row.dataset.id;
            this.controller?.updateButtons();

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

    // 無限スクロール
    initInfiniteScroll(){
        const wrapper = this.tableEl.closest(".scroll-area");
        if(!wrapper) return;

        let loading = false;

        wrapper.addEventListener("scroll", () => {
            if(loading) return;

            const nearBottom =
                wrapper.scrollTop +
                wrapper.clientHeight >=
                wrapper.scrollHeight - 50;
            if(!nearBottom) return;

            loading = true;
            this.model.pageSize += 50;
            this.reload();
            requestAnimationFrame(() => {
                loading = false;
            });
        });
    }

    initOutsideClick(){
        document.addEventListener("click", (e) => {
            const table = this.tableEl.closest(".normal-table");
            if(table?.contains(e.target)) return;
            this.clearCurrentRow();
        });
    }

    scrollToRow(id){
        const row = this.tableEl.querySelector(`[data-id="${id}"]`);
        if(!row) return;

        row.scrollIntoView({behavior: "smooth", block: "center"});
    }

    getCurrentRowId(){
        return this.currentRowId;
    }

    clearCurrentRow(){
        this.currentRowId = null;
        this.tableEl.querySelectorAll(".selected").forEach(r => r.classList.remove("selected"));
        this.controller?.updateButtons();
    }
}