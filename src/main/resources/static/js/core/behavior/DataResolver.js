"use strict"

import { updateField } from "../../util/utils.js";
import { api } from "../api/apiService.js";

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
                await this.resolve(group, type);
            });

            /* Name(select) → ID */
            if (nameField.tagName === "SELECT") {
                nameField.addEventListener("change", () => {
                    // idInput.value = nameField.value;
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

        // if (idInput?.dataset.resolving) return;
        
        const id = idInput.value.trim();

        /* 空ならクリア */
        if (!id) {
            this.clear(nameField);
            return;
        }

        /* 同じIDなら処理しない */
        if (idInput.dataset.lastId === id) return;
        idInput.dataset.lastId = id;

        if (nameField.tagName === "SELECT") {
            const found = this.resolveSelect(nameField, id);
            if (!found) {
                idInput.value = "";
            }
        } else {
            await this.resolveApi(type, id, idInput, nameField);

        }
    },

    resolveSelect(select, id) {
        for (const option of select.options) {
            if (option.value === id) {

                updateField(select, id);
                // select.value = id;
                return true;
            }
        }
        // select.value = "";
        updateField(select, "");
        return false;
    },

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
    async resolveApi(type, id, idInput, nameField) {

        try {

            // ======================
            // 郵便番号
            // ======================
            if(type === "postal"){

                const res = await api.post(
                    "/api/address/get/postalcode",
                    { value: id }
                );

                const data = res?.data;
                if (!data) return;

                const area = idInput.closest('[data-resolve="postal"]');
                if (!area) return;

                const postalInput = idInput;
                const addressInput = area.querySelector('[name="full-address"]');

                // ★ フラグON（ループ防止）
                postalInput.dataset.resolving = "true";

                // 郵便番号
                if (postalInput) {
                    postalInput.value = data.postalCode ?? "";
                }

                // 住所
                if (addressInput) {
                    addressInput.value = data.fullAddress ?? "";
                }

                // ★ フラグOFF
                delete postalInput.dataset.resolving;

                return;
            }

            // ======================
            // 通常（重要）
            // ======================
            const res = await api.get(`/api/${type}/${id}`);
            const data = res?.data;

            if (!data) {
                nameField.value = "";
                return;
            }

            nameField.value = data.name ?? "";

        } catch (e) {
            console.error(e);
            nameField.value = "";
        }
    },

    clear(field) {
        updateField(field, "");
        // if (field.tagName === "SELECT") {
        //     field.value = "";
        // } else {
        //     field.value = "";
        // }
    }
};

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