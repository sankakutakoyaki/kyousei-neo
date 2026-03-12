"use strict"

export const ResolveEngine = {
    init() {
        const groups = document.querySelectorAll("[data-resolve]");
        groups.forEach(group => {
            const type = group.dataset.resolve;
            const idInput = group.querySelector("[data-resolve-id]");
            if (!idInput) return;

            idInput.addEventListener("keydown", async (e) => {
                if (e.key !== "Enter") return;
                e.preventDefault();
                idInput.dataset.skipBlur = "1";
                await this.resolve(group, type);
            });

            idInput.addEventListener("blur", async () => {
                if (idInput.dataset.skipBlur) {
                    delete idInput.dataset.skipBlur;
                    return;
                }
                await this.resolve(group, type);
            });

            idInput.addEventListener("input", () => {
                this.clear(group);
            });
        });
    },

    async resolve(group, type) {
        const idInput = group.querySelector("[data-resolve-id]");
        const nameField = group.querySelector("[data-resolve-name]");
        if (!nameField) return;

        const id = idInput.value.trim();
        if (idInput.dataset.lastId === id) return;
        idInput.dataset.lastId = id;

        if (!id) {
            this.clear(group);
            return;
        }

        if (nameField.tagName === "SELECT") {
            this.resolveSelect(nameField, id);
        } else {
            return this.resolveApi(type, id, nameField);
        }
    },

    resolveSelect(select, id) {
        let found = false;
        for (const option of select.options) {
            if (option.value === id) {
                select.value = id;
                found = true;
                break;
            }
        }
        if (!found) select.value = "";
    },

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

    clear(group) {
        const nameField = group.querySelector("[data-resolve-name]");
        if (!nameField) return;
        nameField.value = "";
    }
};