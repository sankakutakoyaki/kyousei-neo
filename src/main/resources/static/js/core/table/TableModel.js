"use strict"

export class TableModel {

    constructor(config){

        this.origin = null;

        this.state = {};
        this.filters = config.filters || {};
        this.requiredFilters = config.requiredFilters || [];

        this.sortKey = null;
        this.sortDir = "asc";

        this.selected = new Set(); // チェック選択

        this.page = 1;
        this.pageSize = config.pageSize || 50;

        this.result = [];
    }

    set(key,value){
        this.state[key] = value;
    }

    setSort(field,dir){
        this.sortKey = field;
        this.sortDir = dir;
    }

    compute(){
        for(const key of this.requiredFilters){
            if(this.state[key] == null){
                this.result = [];
                return;
            }
        }

        let list =
            typeof this.origin === "function"
            ? this.origin()
            : this.origin || [];

        if(list.length === 0){
            this.result = [];
            return;
        }

        list = this.applyFilter(list);
        list = this.applySort(list);
        list = this.applyPage(list);

        this.result = list;     
    }

    applyFilter(list){
        for(const [key,value] of Object.entries(this.state)){
            if(value == null || value === "") continue;

            const filter = this.filters[key];
            if(!filter) continue;

            list = list.filter(v => filter(v,value));
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

            const an = Number(av);
            const bn = Number(bv);

            if(!isNaN(an) && !isNaN(bn)){
                return (an - bn) * dir;
            }

            return String(av).localeCompare(String(bv)) * dir;
        });
    }

    applyPage(list){

        const start = (this.page - 1) * this.pageSize;
        return list.slice(start, start + this.pageSize);
    }

    getData(){
        return this.result;
    }

    toggleSelect(id){
        if(this.selected.has(id)){
            this.selected.delete(id);
        }else{
            this.selected.add(id);
        }
    }

    isSelected(id){
        return this.selected.has(id);
    }

    clearSelection(){
        this.selected.clear();
    }

    updateCell(id, field, value){
        const list = this.origin();
        const target = list.find(v =>
            String(v[this.config.idKey]) === String(id)
        );
        if(target){
            target[field] = value;
        }
    }

    getSelectedIds(){
        return Array.from(this.selected);
    }

    removeByIds(ids){
        const list = this.origin();
        const newList = list.filter(v =>
            !ids.includes(v[this.config.idKey])
        );
        this.origin = () => newList;
        this.clearSelection();
    }
}