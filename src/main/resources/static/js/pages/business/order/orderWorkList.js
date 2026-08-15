"use strict";

import { DataTable } from "../../../core/table/DataTable.js";
import { createOrderWorkListColumns } from "./columns.js";

export function createOrderWorkListController() {

    const controller = {
        key: "orderWorkList",
        state: {
            filters: {}
        },
        items: [],
        originalItems: [],
        table: null,

        // init(initialItems = []) {
        init(initialItems = [], formController = null) {
            this.formController = formController;

            this.items = structuredClone(initialItems).map(item => ({
                ...item,
                _tempId: item._tempId ?? crypto.randomUUID()
            }));

            this.originalItems = structuredClone(this.items);

            this.table = new DataTable({
                controller: this,
                tableId: "table-12",
                footerId: null,
                columns: createOrderWorkListColumns(this),
                idKey: "_tempId",
                checkable: false,
                data: this.items,
                model: {
                    filters: {}
                },
                pageTopButton: false,
                infiniteScroll: false,
            });

            this.initEvents();
            this.table.reload();
        },

        add(item) {

            const newItem = {
                ...structuredClone(item),
                _tempId: crypto.randomUUID()
            };

            this.items.push(newItem);

            this.table.setData(this.items);
            this.table.reload();
        },

        remove(id) {
            this.items = this.items.filter(
                item => String(item._tempId) !== String(id)
            );

            this.table.setData(this.items);
            this.table.reload();

            this.formController?.setSubmitEnabled(
                this.formController.canSubmit()
            );
        },

        getItems() {
            return structuredClone(this.items);
        },

        hasChanges() {
            const normalizeItems = (items) => {
                return items.map(item => {
                    const copy = structuredClone(item);
                    delete copy._tempId;
                    return copy;
                });
            };

            return JSON.stringify(normalizeItems(this.items))
                !== JSON.stringify(normalizeItems(this.originalItems));
        },

        reset() {
            this.items = structuredClone(this.originalItems);

            this.table.setData(this.items);
            this.table.reload();
        },

        initEvents() {
            const table = document.getElementById("table-12");

            if (!table || this._eventsInitialized) return;

            table.addEventListener("click", (e) => {

                const button =
                    e.target.closest('[data-action="delete-order-work"]');

                if (!button) return;

                e.preventDefault();
                e.stopPropagation();

                this.remove(button.dataset.id);
            });

            this._eventsInitialized = true;
        },

        updateButtons() {
        }
    };

    return controller;
}