"use strict"

import { updateField } from "../../util/utils.js";
import { api } from "../api/apiService.js";
import { normalizeValue, getOptions } from "./valueNormalizer.js";

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

            /* ID入力中はNameクリア */
            idInput.addEventListener("input", () => {
                this.clear(nameField);
            });

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

    // ★ 共通normalize
    return normalizeValue(
        v,
        getOptions(el)
    );
}

/**
 * 共通処理
 */
// const defaultResolver = {
//     async resolve({ type, id, nameField, idInput, clear }) {
//         try {
//             const res = await api.get(`/api/${type}/${id}`);
//             const data = res?.data;

//             // if (!data || !data.data) {
//             //     clear(nameField);
//             //     return;
//             // }
//             if (!data || !data.data) {
//                 clear(nameField);

//                 requestAnimationFrame(() => {
//                     idInput.focus();
//                     idInput.select();
//                 });

//                 return;
//             }
//             nameField.value = data.name ?? "";

//         } catch (e) {
//             console.error(e);
//             clear(nameField);
//         }
//     }
// };
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

// const queryResolver = {
//     cacheMap: {},

//     async resolve({ group, id, idInput, nameField, clear }) {
//         const queryId = group.dataset.queryId;
//         const builderName = group.dataset.paramBuilder;
//         const params = builderName && paramBuilders[builderName]
//             ? paramBuilders[builderName]()
//             : {};
//         const cacheKey = queryId + ":" + JSON.stringify(params);
//         try {
//             if (!this.cacheMap[cacheKey]) {
//                 const res = await api.request({ queryId, params, showProcessing: false });
//                 this.cacheMap[cacheKey] = res?.data || [];
//             }
//             const list = this.cacheMap[cacheKey];
//             const idKey = group.dataset.idKey || "id";
//             const nameKey = group.dataset.nameKey || "name";
//             const found = list.find(x =>
//                 String(x[idKey]) === String(id)
//             );
//             if (!found) {
//                 clear(nameField);

//                 requestAnimationFrame(() => {
//                     idInput.focus();
//                     idInput.select();
//                 });

//                 return;
//             }
//             nameField.value = found?.[nameKey] ?? "";
//         } catch (e) {
//             console.error(e);
//             clear(nameField);
//         }
//     }
// };
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
            // // ★ 住所欄の末尾にフォーカス
            // addressInput.focus();
            // const len = full.length;
            // addressInput.setSelectionRange(len, len);
        }
        delete postalInput.dataset.resolving;
    }
};

const resolvers = {
    postal: postalResolver,
    query: queryResolver,
    default: defaultResolver
};


    // async resolveApi(type, id, input) {
    //     try {
    //         const res = await fetch(`/api/${type}/${id}`);
    //         if (!res.ok) {
    //             input.value = "";
    //             return;
    //         }
    //         const data = await res.json();
    //         input.value = data.name ?? "";
    //     } catch (e) {
    //         console.error(e);
    //         input.value = "";
    //     }
    // },

// export const DataResolver = {

//     /**
//      * 初期化
//      * @param {HTMLElement} area 検索対象の親エリア（省略時は document）
//      */
//     init(area = document) {
//         const groups = area.querySelectorAll("[data-resolve]");

//         groups.forEach(group => {
//             const type = group.dataset.resolve;
//             const idInput = group.querySelector("[data-resolve-id]");
//             if (!idInput) return;

//             // Enter で次に移動する処理は enterFocus 側に任せる
//             // blur / focusout でのみ解決
//             idInput.addEventListener("blur", async () => {
//                 await this.resolve(group, type, {alwaysCheck: true});
//             });

//             // 入力中に Name をクリア
//             idInput.addEventListener("input", () => {
//                 this.clear(group);
//             });
//         });
//     },

//     /**
//      * ID → Name 解決処理
//      * @param {HTMLElement} group 
//      * @param {string} type 
//      * @param {object} options
//      */
//     async resolve(group, type, options = {}) {
//         const idInput = group.querySelector("[data-resolve-id]");
//         const nameField = group.querySelector("[data-resolve-name]");
//         if (!nameField) return;

//         const id = idInput.value.trim();

//         // 前回IDと同じでも blur時は強制チェック
//         if (!options.alwaysCheck && idInput.dataset.lastId === id) return;
//         idInput.dataset.lastId = id;

//         // ID が空なら Name もクリア
//         if (!id) {
//             this.clear(group);
//             return;
//         }

//         if (nameField.tagName === "SELECT") {
//             const found = this.resolveSelect(nameField, id);
//             if (!found) idInput.value = "";
//         } else {
//             await this.resolveApi(type, id, nameField);
//         }
//     },

//     /**
//      * SELECT 用解決
//      * @param {HTMLSelectElement} select 
//      * @param {string} id 
//      */
//     resolveSelect(select, id) {
//         for (const option of select.options) {
//             if (option.value === id) {
//                 select.value = id;
//                 return true;
//             }
//         }
//         select.value = "";
//         return false;
//     },

//     /**
//      * API 解決
//      * @param {string} type 
//      * @param {string} id 
//      * @param {HTMLInputElement} input 
//      */
//     async resolveApi(type, id, input) {
//         try {
//             const res = await fetch(`/api/${type}/${id}`);
//             if (!res.ok) {
//                 input.value = "";
//                 return;
//             }
//             const data = await res.json();
//             input.value = data.name ?? "";
//         } catch (e) {
//             console.error(e);
//             input.value = "";
//         }
//     },

//     /**
//      * Name フィールドクリア
//      * @param {HTMLElement} group 
//      */
//     clear(group) {
//         const nameField = group.querySelector("[data-resolve-name]");
//         if (!nameField) return;
//         nameField.value = "";
//     }
// };