"use strict"

export const ResolveEngine = {

    /**
     * 初期化
     * @param {HTMLElement} area 検索対象の親エリア（省略時は document）
     */
    init(area = document) {
        const groups = area.querySelectorAll("[data-resolve]");

        groups.forEach(group => {
            const type = group.dataset.resolve;
            const idInput = group.querySelector("[data-resolve-id]");
            if (!idInput) return;

            // Enter で次に移動する処理は enterFocus 側に任せる
            // blur / focusout でのみ解決
            idInput.addEventListener("blur", async () => {
                await this.resolve(group, type, {alwaysCheck: true});
            });

            // 入力中に Name をクリア
            idInput.addEventListener("input", () => {
                this.clear(group);
            });
        });
    },

    /**
     * ID → Name 解決処理
     * @param {HTMLElement} group 
     * @param {string} type 
     * @param {object} options
     */
    async resolve(group, type, options = {}) {
        const idInput = group.querySelector("[data-resolve-id]");
        const nameField = group.querySelector("[data-resolve-name]");
        if (!nameField) return;

        const id = idInput.value.trim();

        // 前回IDと同じでも blur時は強制チェック
        if (!options.alwaysCheck && idInput.dataset.lastId === id) return;
        idInput.dataset.lastId = id;

        // ID が空なら Name もクリア
        if (!id) {
            this.clear(group);
            return;
        }

        if (nameField.tagName === "SELECT") {
            const found = this.resolveSelect(nameField, id);
            if (!found) idInput.value = "";
        } else {
            await this.resolveApi(type, id, nameField);
        }
    },

    /**
     * SELECT 用解決
     * @param {HTMLSelectElement} select 
     * @param {string} id 
     */
    resolveSelect(select, id) {
        for (const option of select.options) {
            if (option.value === id) {
                select.value = id;
                return true;
            }
        }
        select.value = "";
        return false;
    },

    /**
     * API 解決
     * @param {string} type 
     * @param {string} id 
     * @param {HTMLInputElement} input 
     */
    async resolveApi(type, id, input) {
        try {
            const res = await fetch(`/api/${type}/${id}`);
            if (!res.ok) {
                input.value = "";
                return;
            }
            const data = await res.json();
            input.value = data.name ?? "";
        } catch (e) {
            console.error(e);
            input.value = "";
        }
    },

    /**
     * Name フィールドクリア
     * @param {HTMLElement} group 
     */
    clear(group) {
        const nameField = group.querySelector("[data-resolve-name]");
        if (!nameField) return;
        nameField.value = "";
    }
};