"use strict"

export const LookupEngine = {

    init() {

        const groups = document.querySelectorAll("[data-lookup]");

        groups.forEach(group => {

            const type = group.dataset.lookup;
            const idInput = group.querySelector("[data-lookup-id]");

            if (!idInput) return;

            idInput.addEventListener("keydown", async (e) => {

                if (e.key !== "Enter") return;

                e.preventDefault();

                await this.resolve(group, type);

            });

            idInput.addEventListener("blur", async () => {

                await this.resolve(group, type);

            });

            idInput.addEventListener("input", () => {

                this.clear(group);

            });

        });

    },

    async resolve(group, type) {

        const idInput = group.querySelector("[data-lookup-id]");
        const nameField = group.querySelector("[data-lookup-name]");

        const id = idInput.value.trim();

        // 同じIDを再検索しない
        if (idInput.dataset.lastId === id) return;
        idInput.dataset.lastId = id;

        if (!id) {

            this.clear(group);
            return;

        }

        if (nameField.tagName === "SELECT") {

            this.resolveSelect(nameField, id);

        } else {

            await this.resolveApi(type, id, nameField);

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

        const res = await fetch(`/api/${type}/${id}`);

        if (!res.ok) {

            input.value = "";
            return;

        }

        const data = await res.json();

        input.value = data.name ?? "";

    },

    clear(group) {

        const nameField = group.querySelector("[data-lookup-name]");

        if (!nameField) return;

        nameField.value = "";

    }

};