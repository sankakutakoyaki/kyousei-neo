"use strict"

import { isMobileReadOnly, isWriteAction } from "../core/access/mobileReadOnly.js";
import { initCombo } from "../bootstrap/initCombo.js";
import { createInputComponent } from "../core/form/components/inputComponent.js";
import { smartFilterHandler } from "../core/behavior/filterHandler.js";
import { resolveController } from "../core/events/controllerResolver.js";
import { openMsgDialog, closeMsgDialog, openConfirmDialog } from "../core/ui/dialog/dialogCore.js";

const defaultPageConditions = {
    edit: (c) => c.hasSelection(),
    singleEdit: (c) => c.hasCurrentRow(),
    bulkEdit: (c) => c.hasSelection(),
    delete: (c) => c.hasSelection(),
    download: (c) => c.hasSelection(),
    save: (c, btn) => {
        const scope = btn.dataset.scope;
        // table save
        if(scope === "table"){
            return c.dataTable?.canSave?.();
        }
        // form save
        if(scope === "form"){
            return c.getActiveForm()?.canSubmit?.();
        }
        return false;
    }
};

const defaultActions = {
    create: (c) => c.openForm("detail", null, { bulkMode:false }),
    keywordSearch: (c, el) => {c.setKeyword(el.value); c.reload();},
    filter: smartFilterHandler,
    edit: async (c) => {const ids = c.getSelectedIds();
        if(!c.ensureSelection(ids)){return;}
        await c.openForm("detail", c.getSelectedId(), { bulkMode:false });
    },
    bulkEdit: async (c) => {
        const ids = c.getSelectedIds()
        if(!c.ensureSelection(ids)){ return; }
        await c.openForm("bulk", null, { bulkMode:true });
    },
    delete: async (c) => c.deleteSelected(),
    download: async (c) => c.downloadSelected(),
    save: async (c) => {
        const form = c.getDefaultForm();
        if(!form) return;
        await form.save(document.getElementById(form.formId));
    },
    singleEdit: async (c) => {
        const id = c.getCurrentRowId();
        if(!id){
            return;
        }
        await c.openForm("detail", id, { bulkMode:false });
    }
};

export class PageController {
    constructor(config){
        this.config = Object.freeze(config);
        this.key = config.key;
        this.state = {};
        this.actionLocks = new WeakSet();
        this.dataTable = null;
        this.forms = {};
        this.components = {};
        this.actionPointerEvents = new WeakMap();
        this.pageActions = config.actions || {};
        this.pageConditions = config.conditions || {};
    }

    init(config = {}){
        this.config = {
            ...this.config,
            ...config
        };
        this.pageActions = {
            ...defaultActions,
            ...(this.config.actions || {})
        };
        this.pageConditions = {
            ...defaultPageConditions,
            ...(this.config.conditions || {})
        };
        
        this.initUI();
        this.initButtonAutoUpdate();

        if (this.config.onInit) {
            this.config.onInit(this);
        }
        this.initComponents();        
        this.updateButtons();
    }

    initComponents(){
        const { columns, data } = this.config;

        if(this.config.table){
            this.dataTable = this.config.table.create(this, columns);
            if(data){
                this.dataTable.model.setOrigin(data);
                this.dataTable.reload();
            } else if(this.config.autoLoad !== false) {
                this.dataTable.initData(); // API
            }
        }
        if(this.config.forms){
            this.forms = {};
            Object.entries(this.config.forms).forEach(([key, formConfig]) => {
                this.forms[key] =
                    formConfig.create(this);
            });
        }
    }

    initUI(){
        const components = this.config.components;
        if(components?.combo){
            this.components.combo = initCombo(this);
        }
        if(components?.input){
            this.components.input = createInputComponent(this);
        }
    }

    initButtonAutoUpdate(){
        if(this.buttonUpdateHandler) return;
        this.buttonUpdateHandler = e => {
            if(resolveController(e.target) === this) this.updateButtons();
        };
        document.addEventListener("input", this.buttonUpdateHandler);
        document.addEventListener("change", this.buttonUpdateHandler);
    }

    dispose(){
        Object.values(this.forms).forEach(form => form.dispose?.());
        this.dataTable?.dispose?.();
        if(!this.buttonUpdateHandler) return;
        document.removeEventListener("input", this.buttonUpdateHandler);
        document.removeEventListener("change", this.buttonUpdateHandler);
        this.buttonUpdateHandler = null;
    }

    // 検索
    search(keyword){
        this.setKeyword(keyword);
        this.reload();
    }

    async deleteSelected(){
        const ids = this.getSelectedIds();
        if(!this.ensureSelection(ids)) return;

        openConfirmDialog({
            message:"削除しますか？",
            color:"blue",
            controller: this,
            onSubmit:async () => {
                await this.executeDelete(ids);
            }
        });
    }

    async reset(){
        this.clearState();
        this.components.input?.clear();
        this.components.combo?.clear();
        // combo再描画（データ更新後に効く）
        this.components.combo?.reload();
        await this.dataTable.refresh();
    }

    reload(){
        this.dataTable?.reload();
    }

    async executeDelete(ids){
        closeMsgDialog();
        const result = await this.dataTable.deleteByIds(ids);
        openMsgDialog({
            message: `${result.data.count ?? 0}件削除しました`,
            color:"blue"
        });
        // ★ 汎用フック
        if(this.config.onDeleted){
            this.config.onDeleted(ids, result);
        }
    }

    async downloadSelected(){
        const ids = this.getSelectedIds();
        if(!this.ensureSelection(ids)) return;

        const res = await this.dataTable.downloadCsvByIds(ids);
        const blob = res.data;
        const url = URL.createObjectURL(blob);
        const disposition = res.title;

        let fileName = "download.csv";
        if (disposition) {
            const match = disposition.match(/filename="(.+)"/);
            if (match) {
                fileName = match[1];
            }
        }

        const a = document.createElement("a");
        a.href = url;
        a.download = fileName;
        a.click();
        URL.revokeObjectURL(url);
    }

    ensureSelection(ids){
        if(ids.length === 0){
            openMsgDialog({
                message:"選択してください",
                color:"red"
            });
            return false;
        }
        return true;
    }

    // ボタンの状態判定
    isEnabled(actions, el){
        return (actions?.split(/\s+/).filter(Boolean) ?? []).every(action => {
            const condition = this.pageConditions[action];
            return !condition || Boolean(condition(this, el));
        });
    }

    // ボタン制御関数
    updateButtons(){
        this.updateActionButtons();
        this.updateFormButtons();
    }

    updateActionButtons(){
        document.querySelectorAll("[data-action]").forEach(el => {
            if(el.dataset.actionManaged === "false"){return;}
            
            const controller = resolveController(el);
            if(controller !== this) return;

            const actions = el.dataset.action?.split(/\s+/).filter(Boolean) ?? [];
            // Elements without conditions retain HTML/component-managed state.
            if(!actions.some(action => this.pageConditions[action])) return;
            if(!this.actionPointerEvents.has(el)) this.actionPointerEvents.set(el, el.style.pointerEvents);
            const enabled = this.isEnabled(el.dataset.action, el);

            if("disabled" in el){
                el.disabled = !enabled;
            }

            el.classList.toggle("disabled", !enabled);
            el.setAttribute("aria-disabled", String(!enabled));
            el.style.pointerEvents = enabled ? this.actionPointerEvents.get(el) : "none";
        });
    }

    updateFormButtons(){
        const dialog = document.getElementById("form-dialog-area");

        // dialog開いてないなら何もしない
        if(!dialog || !dialog.classList.contains("dialog")) return;

        const controller = resolveController(dialog);
        if(!controller) return;

        const submitBtn = dialog.querySelector('[name="submitBtn"]');
        if(!submitBtn) return;

        const form = controller.getActiveForm?.();

        // FormController管理ではないダイアログは
        // PageController側ではボタン制御しない
        if(!form){
            submitBtn.disabled = false;
            submitBtn.classList.remove("disabled");
            return;
        }

        const enabled = form.canSubmit?.() ?? true;

        submitBtn.disabled = !enabled;
        submitBtn.classList.toggle("disabled", !enabled);
    }

    async openForm(name, data = null, options = {}){
        if(options.bulkMode != null){
            this.setBulkMode(options.bulkMode);
        }
        const form = this.forms?.[name];
        if(!form){
            throw new Error(
                `form not found : ${name}`
            );
        }
        return await form.open(data);
    }

    getActiveForm(){
        return Object.values(this.forms).find(form => !form.isHidden?.());
    }

    getDefaultForm(){
        const name = this.config.defaultFormName ?? "detail";
        return this.forms?.[name];
    }

    getSelectedId(){
        const ids = this.getSelectedIds();
        return ids[0] ?? null;
    }

    getSelectedIds(){
        return this.dataTable ?.getSelectedIds?.() ?? [];
    }

    getKeyword(){
        return this.state.keyword ?? "";
    }

    setKeyword(value){
        this.state.keyword = value;
    }

    async refresh(targetId = null){
        if(!this.dataTable){
            return [];
        }
        const data = await this.dataTable.refresh(targetId);
        this.updateButtons();
        return data;
    }
    
    isBulkMode(){
        return !!this.state.bulkMode;
    }

    setBulkMode(value){
        this.state.bulkMode = !!value;
        this.updateButtons();
    }

    async executeAction(name, payload = null){
        if (isMobileReadOnly(this.key) && isWriteAction(name)) return;
        const fn = this.pageActions?.[name];
        if(!fn){
            return;
        }
        const lock = payload?.dataset?.actionLock === "true";
        if(lock && this.actionLocks.has(payload)) return;
        if(lock) this.actionLocks.add(payload);
        try {return await fn(this, payload);}
        finally {if(lock) this.actionLocks.delete(payload);}
    }

    getFilter(key){
        return this.state.filters?.[key];
    }

    setFilter(key, value){
        if(!this.state.filters){
            this.state.filters = {};
        }
        this.state.filters[key] = value;
    }

    clearState(){
        this.state = {};
    }

    hasSelection(){
        return this.dataTable ?.hasSelection?.() ?? false;
    }

    canSave() {
        return this.config.canSave?.();
    }

    hasCurrentRow(){
        return !!this.dataTable?.getCurrentRowId();
    }

    getCurrentRowId(){
        return this.dataTable?.getCurrentRowId();
    }
}