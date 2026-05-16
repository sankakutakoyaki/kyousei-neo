"use strict"

import { updateField } from "../../util/utils.js";
import { api } from "../api/apiService.js";
import { normalizeValue, getOptions } from "./valueNormalizer.js";
import { parsers } from "./parsers.js";

const paramBuilders = {
    makerParams: () => ({
        state: APP.cache.common.state.INITIAL
    })
};

export const DataResolver = {
    init(area = document) {
        const groups = area.querySelectorAll("[data-resolve]");

        groups.forEach(group => {
            const type = group.dataset.resolve;
            const idInput = group.querySelector("[data-resolve-id]");
            const nameField = group.querySelector("[data-resolve-name]");

            if (!idInput || !nameField) return;

            /* ID → Name */
            idInput.addEventListener("blur", async () => {
                this.resolve(group, type);
            });

            /* Name(select) → ID */
            if (nameField.tagName === "SELECT") {
                nameField.addEventListener("change", () => {
                    updateField(idInput, nameField.value);
                    if (!nameField.value) {
                        this.clear(nameField);
                    }
                });
            }
        });
    },

    async resolve(group, type) {
        const idInput = group.querySelector("[data-resolve-id]");
        const nameField = group.querySelector("[data-resolve-name]");

        const id = idInput.value.trim();

        // 空ならクリア
        if (!id) {
            this.clear(nameField);
            return;
        }

        // 同じIDなら処理しない
        if (idInput.dataset.lastId === id) return;
        idInput.dataset.lastId = id;

        // SELECTはここで処理
        if (nameField.tagName === "SELECT") {
            const found = this.resolveSelect(nameField, id);
            if (!found) {
                idInput.value = "";
            }
            return;
        }

        // それ以外はresolverへ
        const resolver = resolvers[type] || resolvers.default;

        await resolver.resolve({
            group,
            type,
            id,
            idInput,
            nameField,
            clear: this.clear
        });
    },

    resolveSelect(select, id) {
        for (const option of select.options) {
            if (option.value === id) {

                updateField(select, id);
                return true;
            }
        }
        updateField(select, "");
        return false;
    },

    clear(field) {
        updateField(field, "");
    }
};

function focusEnd(input) {
    if (!input) return;
    input.focus();
    const len = input.value.length;
    input.setSelectionRange(len, len);
}

export function resolveSubmitValue(el, value){
    const mode = el.dataset.submit;
    let v;
    switch(mode){
        // 無視
        case "none":
            return undefined;
        // option.dataset.xxx
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
    async resolve({
        type,
        id,
        nameField,
        idInput,
        group,
        clear
    }) {
        try {
            const res = await api.get(`/api/${type}/${id}`);
            const data = res?.data;
            if (!data || !data.data) {
                clear(nameField);
                // hidden clear
                const hidden =
                    group?.querySelector("[data-role='hidden-id']");
                if(hidden){
                    hidden.value = "";
                }
                requestAnimationFrame(() => {
                    idInput.focus();
                    idInput.select();
                });
                return;
            }
            // name
            nameField.value = data.name ?? "";
            // hidden value
            const valueKey = group?.dataset.valueKey;
            if(valueKey){
                const hidden =
                    group.querySelector("[data-role='hidden-id']");
                if(hidden){
                    hidden.value = data[valueKey] ?? "";
                }
            }
        } catch (e) {
            console.error(e);
            clear(nameField);
        }
    }
};

const queryResolver = {
    cacheMap: {},

    async resolve({ group, id, idInput, nameField, clear }) {
        const queryId = group.dataset.queryId;
        const builderName = group.dataset.paramBuilder;
        const params = builderName && paramBuilders[builderName]
            ? paramBuilders[builderName]()
            : {};
        const cacheKey = queryId + ":" + JSON.stringify(params);
        try {
            if (!this.cacheMap[cacheKey]) {
                const res = await api.request({
                    queryId,
                    params,
                    showProcessing: false
                });
                this.cacheMap[cacheKey] = res?.data || [];
            }

            const list = this.cacheMap[cacheKey];
            const idKey = group.dataset.idKey || "id";
            const nameKey = group.dataset.nameKey || "name";
            const found = list.find(x =>
                String(x[idKey]) === String(id)
            );

            if (!found) {
                clear(nameField);
                // hiddenも消す
                const hidden =
                    group.querySelector("[data-role='hidden-id']");
                if(hidden){
                    hidden.value = "";
                }
                requestAnimationFrame(() => {
                    idInput.focus();
                    idInput.select();
                });
                return;
            }
            // 表示名
            nameField.value = found?.[nameKey] ?? "";
            // ★ 追加部分
            const valueKey = group.dataset.valueKey;
            if(valueKey){
                const hidden =
                    group.querySelector("[data-role='hidden-id']");
                if(hidden){
                    hidden.value = found?.[valueKey] ?? "";
                }
            }
        } catch (e) {
            console.error(e);
            clear(nameField);
        }
    }
};

/**
 * 郵便番号検索
 */
const postalResolver = {
    async resolve({ id, idInput, group }) {
        const res = await api.post(
            "/api/address/get/postalcode",
            { value: id }
        );

        const data = res?.data;
        const postalInput = idInput;
        const addressInput = group.querySelector('[name="full-address"]');
        // データなし
        if (!data || !data.data || (Array.isArray(data) && data.length === 0)) {
            postalInput.value = "";
            postalInput.focus();
            return;
        }
        // APIが配列 or 単体どちらでも対応
        const address = Array.isArray(data) ? data[0] : data;
        // データあり
        postalInput.dataset.resolving = "true";
        // 郵便番号
        postalInput.value = address.postalCode ?? postalInput.value;
        // 住所
        if (addressInput) {
            const full = address.fullAddress
                ?? (address.prefecture || "") +
                   (address.city || "") +
                   (address.town || "");
            addressInput.value = full;
            focusEnd(addressInput);
        }
        delete postalInput.dataset.resolving;
    }
};

/**
 * リサイクル券番号
 */
export function recycleResolver(row){
    const input = row.querySelector('[name="recycle-number"]');
    if(!input) return;

    input.addEventListener("input", () => {
            let v = input.value.replace(/\D/g, "").slice(0, 13);
            if(v.length > 10){
                v = v.slice(0,4) + "-" + v.slice(4,10) + "-" + v.slice(10);
            } else if(v.length > 4){
                v = v.slice(0,4) + "-" + v.slice(4);
            }
            input.value = v;
        }
    );
}

const resolvers = {
    postal: postalResolver,
    recycle: recycleResolver,
    query: queryResolver,
    default: defaultResolver
};