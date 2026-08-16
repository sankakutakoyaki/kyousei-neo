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

            // if (!idInput || !nameField) return;
            if (!idInput) return;

            /* ID → Resolve */
            idInput.addEventListener("blur", async () => {
                this.resolve(group, type);
            });

            /* Name(select) → ID */
            if (nameField?.tagName === "SELECT") {
                nameField.addEventListener("change", () => {
                    const value = nameField.value;
                    updateField(idInput, Number(value) === 0 ? "": value
                    );
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
            delete idInput.dataset.lastId;
            // this.clear(nameField);
            if (nameField) {
                this.clear(nameField);
            }
            return;
        }

        // 同じIDなら処理しない
        if (idInput.dataset.lastId === id) return;
        idInput.dataset.lastId = id;

        // SELECTはここで処理
        if (nameField?.tagName === "SELECT") {
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
                const hidden = group.querySelector("[data-role='hidden-id']");
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
                const hidden = group.querySelector("[data-role='hidden-id']");
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
        const res = await api.post("/api/address/get/postalcode", { value: id });
        const data = res?.data;

        const postalInput = idInput;
        const addressInput = group.querySelector('[name="full-address"]');
        // データなし
        // if (!data || !data.data || (Array.isArray(data) && data.length === 0)) {
        if (!data || (Array.isArray(data) && data.length === 0)) {
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

const itemResolver = {
    async resolve({ id, idInput, group }) {
        try {
            const res = await api.post("/api/item/get/jancode", { value: id });
            const data = res?.data;

            const panel = group.closest(".tab-panel");
            const itemName = panel?.querySelector('[name="item-name"]');
            const itemMaker = panel?.querySelector('[name="item-maker"]');
            const itemModel = panel?.querySelector('[name="item-model"]');
            const itemQuantity = panel?.querySelector('[name="item-quantity"]');

            // データなし
            if (!data) {
                // 商品情報をクリア
                if (itemName) {
                    itemName.value = "";
                }

                if (itemMaker) {
                    itemMaker.value = "";
                }

                if (itemModel) {
                    itemModel.value = "";
                }

                if (itemQuantity) {
                    itemQuantity.value = "";
                }

                // JANコードもクリア
                idInput.value = "";

                requestAnimationFrame(() => {
                    idInput.focus();
                    idInput.select();
                });

                return;
            }

            // 商品情報セット
            if (itemName) {
                itemName.value = data.itemName ?? "";
            }

            if (itemMaker) {
                itemMaker.value = data.itemMaker ?? "";
            }

            if (itemModel) {
                itemModel.value = data.itemModel ?? "";
            }

            // 商品情報取得成功 → 数量へ
            if (itemQuantity) {
                itemQuantity.focus();
                itemQuantity.select();
            }
        } catch (e) {
            console.error(e);
            idInput.value = "";
            requestAnimationFrame(() => {
                idInput.focus();
                idInput.select();
            });
        }
    }
};

const workResolver = {
    async resolve({ id, idInput, group }) {
        try {
            const res = await api.post("/api/work/get/workcode", { value: id });
            const data = res?.data;

            const panel = group.closest(".tab-panel");
            const workName = panel?.querySelector('[name="order-work-name"]');
            const workPrice = panel?.querySelector('[name="order-work-price"]');
            const workQuantity = panel?.querySelector('[name="order-work-quantity"]');

            // データなし
            if (!data) {
                // 作業情報をクリア
                if (workName) {
                    workName.value = "";
                }

                if (workPrice) {
                    workPrice.value = "";
                }

                if (workQuantity) {
                    workQuantity.value = "";
                }

                // 作業コードもクリア
                idInput.value = "";

                requestAnimationFrame(() => {
                    idInput.focus();
                    idInput.select();
                });

                return;
            }

            // 作業情報セット
            if (workName) {
                workName.value = data.workName ?? "";
            }

            if (workPrice) {
                workPrice.value = data.workPrice ?? "";
            }

            // 作業情報取得成功 → 数量へ
            if (workQuantity) {
                workQuantity.focus();
                workQuantity.select();
            }
        } catch (e) {
            console.error(e);
            idInput.value = "";
            requestAnimationFrame(() => {
                idInput.focus();
                idInput.select();
            });
        }
    }
};

const resolvers = {
    postal: postalResolver,
    item: itemResolver,
    work: workResolver,
    query: queryResolver,
    default: defaultResolver
};