"use strict"

import { updateField } from "../../util/utils.js";
import { api } from "../api/apiService.js";
import { normalizeValue, getOptions } from "./valueNormalizer.js";
import { parsers } from "./parsers.js";

import { DialogService } from "../ui/dialog/DialogService.js";

const paramBuilders = {
    makerParams: () => ({state: APP.cache.common.state.INITIAL}),
    employeeParams: identifier => ({state: APP.cache.common.state.INITIAL, identifier})
};
const initializedInputs = new WeakSet();
const inputStates = new WeakMap();
const queryCaches = new WeakMap();

function invalidate(input) {
    const state = inputStates.get(input);
    if (state) { state.request++; state.pending = null; }
}
function groupsIn(area) {
    return [...(area.matches?.("[data-resolve]") ? [area] : []), ...area.querySelectorAll("[data-resolve]")];
}

export const DataResolver = {
    init(area = document) {
        groupsIn(area).forEach(group => {
            const idInput = group.querySelector("[data-resolve-id]");
            const nameField = group.querySelector("[data-resolve-name]");
            if (!idInput || initializedInputs.has(idInput)) return;
            initializedInputs.add(idInput);
            if (idInput.dataset.lastId == null && !idInput.value?.trim()) idInput.dataset.lastId = "";
            idInput.addEventListener("input", () => {
                invalidate(idInput);
                delete idInput.dataset.lastId;
                group._resolvedData = null;
                if(group.dataset.resolve === "api") {
                    if(nameField) this.clear(nameField);
                    group.dispatchEvent(new CustomEvent("resolved", {bubbles: true, detail: {data: null}}));
                }
            });
            idInput.addEventListener("change", () => invalidate(idInput));
            idInput.addEventListener("blur", () => this.resolve(group, group.dataset.resolve));
            if(group.dataset.resolveEnter === "true") {
                idInput.addEventListener("keydown", event => {
                    if(event.key !== "Enter" || event.isComposing || event.repeat) return;
                    event.preventDefault();
                    this.resolve(group, group.dataset.resolve);
                });
            }
            if (nameField?.tagName === "SELECT") {
                nameField.addEventListener("change", () => {
                    invalidate(idInput);
                    updateField(idInput, Number(nameField.value) === 0 ? "" : nameField.value);
                    idInput.dataset.lastId = idInput.value.trim();
                });
            }
        });
    },

    reset(area) {
        groupsIn(area).forEach(group => {
            const input = group.querySelector("[data-resolve-id]");
            if (!input) return;
            invalidate(input);
            group._resolvedData = null;
            input.dataset.lastId = input.value.trim();
            delete input.dataset.resolving;
            queryCaches.delete(group);
        });
    },

    async resolve(group, type) {
        const idInput = group.querySelector("[data-resolve-id]");
        const nameField = group.querySelector("[data-resolve-name]");
        if (!idInput || group.isConnected === false) return false;
        const id = idInput.value.trim();
        let state = inputStates.get(idInput);
        if (!state) { state = {request: 0, pending: null}; inputStates.set(idInput, state); }
        if (state.pending?.id === id) return state.pending.promise;
        if (idInput.dataset.lastId === id) return true;
        const request = ++state.request;
        const canApply = () => state.request === request && group.isConnected !== false
            && idInput.isConnected !== false && group.querySelector("[data-resolve-id]") === idInput
            && idInput.value.trim() === id
            && !group.closest?.("form")?.classList?.contains("none");
        if (!id) {
            group._resolvedData = null;
            if (nameField) this.clear(nameField);
            this.clear(group.querySelector("[data-role='hidden-id']"));
            idInput.dataset.lastId = "";
            return false;
        }
        if (nameField?.tagName === "SELECT") {
            const found = this.resolveSelect(nameField, id);
            if (found) idInput.dataset.lastId = id;
            else { idInput.value = ""; delete idInput.dataset.lastId; }
            return found;
        }
        const resolver = resolvers[type] || resolvers.default;
        if (type === "query") this.clear(group.querySelector("[data-role='hidden-id']"));
        const promise = (async () => {
            try {
                const found = await resolver.resolve({group, type, id, idInput, nameField, canApply});
                if (found && canApply()) idInput.dataset.lastId = idInput.value.trim();
                // Postal lookup may normalize the code before returning.
                else if (found && state.request === request && group.isConnected !== false) {
                    idInput.dataset.lastId = idInput.value.trim();
                }
                return found;
            } catch (error) {
                if (canApply()) {
                    group._resolvedData = null;
                    if (type === "query" || !resolvers[type]) applyName({group, nameField}, null);
                    DialogService.error(error.message || "入力値の取得に失敗しました。再度確認してください。");
                }
                return false;
            } finally {
                if (state.pending?.request === request) state.pending = null;
            }
        })();
        state.pending = {id, request, promise};
        return promise;
    },

    resolveSelect(select, id) {
        const found = [...select.options].some(option => option.value === id);
        updateField(select, found ? id : "");
        return found;
    },
    clear(field) { if (field) updateField(field, ""); }
};

function focusForRetry(input, canApply) {
    requestAnimationFrame(() => { if (canApply()) { input.focus(); input.select(); } });
}
function applyName({group, nameField}, data) {
    if (nameField) updateField(nameField, data?.[group.dataset.nameKey || "name"] ?? "");
    const hidden = group.querySelector("[data-role='hidden-id']");
    if (hidden) updateField(hidden, data?.[group.dataset.valueKey] ?? "");
}
function focusEnd(input) {
    if (!input) return;
    input.focus();
    input.setSelectionRange(input.value.length, input.value.length);
}

export function resolveSubmitValue(el, value){
    const mode = el.dataset.submit;
    let v;
    switch(mode){
        case "none":
            return undefined;
        case "dataset": {
            const option = el.selectedOptions?.[0];
            const key = el.dataset.submitKey;
            v = option?.dataset?.[key] ?? "";
            break;
        }
        // checkbox
        default: {
            if(el.type === "checkbox"){
                v = el.checked;
            } else {
                v = value;
            }
        }
    }
    // ★ parser
    const validateType = el.dataset.validate;
    if(validateType){
        const parser = parsers[validateType];
        if(parser){
            v = parser(v);
        }
    }
    // ★ 共通normalize
    return normalizeValue(
        v,
        getOptions(el)
    );
}

const defaultResolver = {
    async resolve(context) {
        const {type, id, idInput, canApply} = context;
        const res = await api.get(`/api/${type}/${encodeURIComponent(id)}`);
        if (!canApply()) return false;
        const data = Array.isArray(res?.data) ? res.data[0] : res?.data;
        applyName(context, data);
        if (!data) { focusForRetry(idInput, canApply); return false; }
        return true;
    }
};

async function queryList(group, queryId, params) {
    const key = queryId + ":" + JSON.stringify(params);
    let entry = queryCaches.get(group);
    if (!entry || entry.key !== key) {
        entry = {key};
        entry.promise = api.request({queryId, params, showProcessing: false}).then(res => {
            if (!Array.isArray(res?.data)) throw new Error("取得結果の形式が不正です");
            return res.data;
        }).catch(error => {
            if (queryCaches.get(group) === entry) queryCaches.delete(group);
            throw error;
        });
        queryCaches.set(group, entry);
    }
    return entry.promise;
}
const queryResolver = {
    async resolve(context) {
        const {group, id, idInput, canApply} = context;
        const builder = paramBuilders[group.dataset.paramBuilder];
        const list = await queryList(group, group.dataset.queryId, builder ? builder() : {});
        if (!canApply()) return false;
        const found = list.find(item => String(item[group.dataset.idKey || "id"]) === id);
        applyName(context, found);
        if (!found) { queryCaches.delete(group); focusForRetry(idInput, canApply); return false; }
        return true;
    }
};
const postalResolver = {
    async resolve({id, idInput, group, canApply}) {
        const res = await api.post("/api/address/get/postalcode", {value: id});
        if (!canApply()) return false;
        const address = Array.isArray(res?.data) ? res.data[0] : res?.data;
        if (!address) { idInput.focus(); return false; }
        const addressInput = group.querySelector('[name="full-address"]');
        idInput.value = address.postalCode ?? idInput.value;
        if (addressInput) {
            updateField(addressInput, address.fullAddress ?? (address.prefecture || "") + (address.city || "") + (address.town || ""));
            focusEnd(addressInput);
        }
        return true;
    }
};

async function resolveProduct(context, type) {
    const {group, id, idInput, canApply} = context;
    const res = type === "item" ? await api.post("/api/item/get/jancode", {value: id}) : null;
    const list = type === "work" ? await queryList(group, "workMasterList", {state: APP.cache.common.state.INITIAL}) : null;
    if (!canApply()) return false;
    const data = type === "work" ? list.find(work => String(work.workCode) === id)
        : Array.isArray(res?.data) ? res.data[0] : res?.data;
    const scope = group.closest?.("form") ?? group.closest?.(".tab-panel") ?? group;
    const fields = type === "item"
        ? {"item-name": "itemName", "item-maker": "itemMaker", "item-model": "itemModel"}
        : {"order-work-name": "workName", "order-work-price": "workPrice"};
    for (const [name, key] of Object.entries(fields)) {
        const field = scope.querySelector(`[name="${name}"]`);
        if (field) updateField(field, data?.[key] ?? "");
    }
    const quantity = scope.querySelector(type === "item" ? '[name="item-quantity"]' : '[name="order-work-quantity"]');
    if (!data) {
        if (type === "work") queryCaches.delete(group);
        DataResolver.clear(quantity);
        focusForRetry(idInput, canApply);
        return false;
    }
    quantity?.focus(); quantity?.select();
    return true;
}
const apiResolver = {
    async resolve(context) {
        const {group, id, idInput, nameField, canApply} = context;
        const url = group.dataset.resolveUrl;
        if(!url) throw new Error("入力値の取得先が設定されていません。");
        const query = new URLSearchParams({[group.dataset.paramKey || "value"]: id});
        const res = await api.get(`${url}?${query}`);
        if(!canApply()) return false;
        const data = res?.data;
        group._resolvedData = data ?? null;
        if(nameField) nameField.value = data?.[group.dataset.nameKey || "name"] ?? "";
        group.dispatchEvent(new CustomEvent("resolved", {bubbles: true, detail: {data: data ?? null}}));
        if(!data) requestAnimationFrame(() => {if(canApply()) {idInput.focus(); idInput.select();}});
        return Boolean(data);
    }
};

const resolvers = {
    api: apiResolver,
    postal: postalResolver,
    item: {resolve: context => resolveProduct(context, "item")},
    work: {resolve: context => resolveProduct(context, "work")},
    query: queryResolver,
    default: defaultResolver
};
