"use strict"

import { convertKey } from "../../util/keyCaseConverter.js";
export class TableModel {
    constructor(config){
        this.originData = [];
        this.filters = config.filters || {};
        this.requiredFilters = config.requiredFilters || [];
        this.idKey = config.idKey;
        this.sortKey = null;
        this.sortDir = "asc";
        this.selected = new Set();
        this.page = 1;
        this.pageSize = config.pageSize || 50;
        this.index = null;
        this.result = [];
    }

    // 基本
    setOrigin(list){
        this.originData = list || [];
        this.index = new Map(list.map(v => [String(v[this.idKey]), v]));
        this.clearSelection();
    }

    toggleSort(field){
        const key = convertKey(field, "kebab", "camel");
        if(this.sortKey === key){
            this.sortDir = this.sortDir === "asc" ? "desc" : "asc";
        } else {
            this.sortKey = key;
            this.sortDir = "asc";
        }
    }

    // 計算
    compute(state){
        for(const key of this.requiredFilters){
            if(state[key] == null){
                this.result = [];
                return;
            }
        }
        let list = [...this.originData];
        list = this.applyFilter(list, state);
        list = this.applySort(list);
        list = this.applyPage(list);

        this.result = list;
    }

    applyFilter(list, state){
        for(const [key, filterFn] of Object.entries(this.filters)){
            const kebab = convertKey(key, "camel", "kebab");
            const value =
                state[key] ??
                state.filters?.[key] ??
                state.filters?.[kebab];

            if(value == null || value === "") continue;
            list = list.filter(v => filterFn(v, value));
        }
        return list;
    }

    applySort(list){
        if(!this.sortKey) return list;

        const key = this.sortKey;
        const dir = this.sortDir === "asc" ? 1 : -1;
        return [...list].sort((a,b)=>{
            let av = a[key];
            let bv = b[key];
            if(av == null) return 1;
            if(bv == null) return -1;
            if(typeof av === "number" && typeof bv === "number"){
                return (av - bv) * dir;
            }
            return String(av).localeCompare(String(bv)) * dir;
        });
    }

    applyPage(list){
        const start = (this.page - 1) * this.pageSize;
        return list.slice(start, start + this.pageSize);
    }

    // 取得
    getData(){
        return this.result;
    }

    findById(id){
        return this.index.get(String(id));
    }

    getViewData(){
        return this.result.map(v => ({
            ...v,
            _selected: this.selected.has(v[this.idKey])
        }));
    }

    findOriginById(id){
        return this.originData.find(
            v => String(v[this.idKey]) === String(id)
        );
    }

    // 選択
    toggleSelect(id){
        if(this.selected.has(id)){
            this.selected.delete(id);
        }else{
            this.selected.add(id);
        }
    }

    getSelectedIds(){
        return Array.from(this.selected);
    }

    clearSelection(){
        this.selected.clear();
    }

    removeByIds(ids){
        const idSet = new Set(ids.map(String));

        this.originData = this.originData.filter(
            v => !idSet.has(String(v[this.idKey]))
        );
        this.index = new Map(
            this.originData.map(v => [String(v[this.idKey]), v])
        );

        this.clearSelection();
    }
}