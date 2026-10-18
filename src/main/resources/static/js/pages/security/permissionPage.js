"use strict";

import { initCommon } from "../../bootstrap/initPage.js";
import { registerController } from "../../application/controllerRegistry.js";
import { PageController } from "../../application/PageController.js";
import { cloneTemplate } from "../../core/dom/cloneTemplate.js";
import { DialogService } from "../../core/ui/dialog/DialogService.js";

export async function init() {
    const root = document.getElementById("permission-page");
    if (!root) return;

    await initCommon();
    if (!root.isConnected) return;

    const page = createPermissionPage(root);
    registerController("permission", page);
    page.init();
    await page.load();
}

function createPermissionPage(root) {
    const roleSelect = root.querySelector("#permission-role");
    const scopeSelect = root.querySelector("#permission-scope");
    const saveButton = root.querySelector("#permission-save");
    const tableArea = root.querySelector("#permission-table-list");
    const operationArea = root.querySelector("#permission-operation-list");

    let detail = null;
    let snapshot = "";
    let requestId = 0;
    let disposed = false;
    let saving = false;

    const active = () => !disposed && root.isConnected;

    const controller = new PageController({
        key: "permission",
        autoLoad: false,
        actions: {
            "change-permission-role": async () => {
                await loadDetail();
            },
            "save-permissions": async () => {
                await save();
            }
        }
    });

    const dispose = controller.dispose.bind(controller);
    controller.dispose = () => {
        disposed = true;
        requestId++;
        root.removeEventListener("change", updateSubmitState);
        dispose();
    };

    root.addEventListener("change", updateSubmitState);

    controller.load = load;
    return controller;

    async function load() {
        const roles = await api("/api/admin/permissions/roles");
        if (!active()) return;

        roleSelect.replaceChildren(new Option("選択してください", ""));

        for (const role of roles) {
            roleSelect.add(new Option(
                `${role.roleName} (${role.roleCode})`,
                String(role.roleId)
            ));
        }

        // admin以外の最初のロールを初期表示
        const first = roles.find(role =>
            role.roleCode !== "APPROLE_admin"
        );

        if (first) {
            roleSelect.value = String(first.roleId);
            await loadDetail();
        }
    }

    async function loadDetail() {
        const current = ++requestId;
        const roleId = roleSelect.value;

        detail = null;
        snapshot = "";
        saveButton.disabled = true;
        tableArea.replaceChildren();
        operationArea.replaceChildren();

        if (!roleId) return;

        try {
            const result = await api(
                `/api/admin/permissions/roles/${roleId}`
            );

            if (!active() || current !== requestId) return;

            detail = result;
            scopeSelect.value = detail.scopeCode ?? "OWN";
            renderTables(detail.tables ?? []);
            renderOperations(detail.operations ?? []);

            snapshot = JSON.stringify(buildRequest());
            updateSubmitState();
        } catch (error) {
            if (active() && current === requestId) {
                DialogService.error(error.message);
            }
        }
    }

    function renderTables(tables) {
        const fragment = document.createDocumentFragment();

        for (const item of tables) {
            const row = cloneTemplate("permission-table-row-template");
            row.dataset.tableName = item.tableName;

            row.querySelector('[data-field="tableName"]')
                .textContent = item.tableName;

            for (const field of [
                "canSelect", "canInsert", "canUpdate", "canDelete"
            ]) {
                row.querySelector(`[data-field="${field}"]`)
                    .checked = Boolean(item[field]);
            }

            fragment.appendChild(row);
        }

        tableArea.replaceChildren(fragment);
    }

    function renderOperations(operations) {
        const fragment = document.createDocumentFragment();

        for (const item of operations) {
            const row = cloneTemplate("permission-operation-row-template");
            row.dataset.operationCode = item.operationCode;

            row.querySelector('[data-field="operationCode"]')
                .textContent = item.operationCode;

            row.querySelector('[data-field="allowed"]')
                .checked = Boolean(item.allowed);

            fragment.appendChild(row);
        }

        operationArea.replaceChildren(fragment);
    }

    function buildRequest() {
        const tables = detail.tables.map(item => {
            const row = [...tableArea.children].find(
                element => element.dataset.tableName === item.tableName
            );

            return {
                tableName: item.tableName,
                permissionId: item.permissionId ?? null,
                version: item.version ?? null,
                canSelect: row.querySelector(
                    '[data-field="canSelect"]'
                ).checked,
                canInsert: row.querySelector(
                    '[data-field="canInsert"]'
                ).checked,
                canUpdate: row.querySelector(
                    '[data-field="canUpdate"]'
                ).checked,
                canDelete: row.querySelector(
                    '[data-field="canDelete"]'
                ).checked
            };
        });

        const operations = detail.operations.map(item => {
            const row = [...operationArea.children].find(
                element =>
                    element.dataset.operationCode === item.operationCode
            );

            return {
                operationCode: item.operationCode,
                permissionId: item.permissionId ?? null,
                version: item.version ?? null,
                allowed: row.querySelector(
                    '[data-field="allowed"]'
                ).checked
            };
        });

        return {
            version: detail.version,
            scopeCode: scopeSelect.value,
            scopeId: detail.scopeId ?? null,
            scopeVersion: detail.scopeVersion ?? null,
            tables,
            operations
        };
    }

    function updateSubmitState() {
        if (!detail || !active() || saving) {
            saveButton.disabled = true;
            return;
        }

        saveButton.disabled =
            detail.roleCode === "APPROLE_admin"
            || JSON.stringify(buildRequest()) === snapshot;
    }

    async function save() {
        if (!detail || !active() || saving || saveButton.disabled) return;

        const current = requestId;
        const roleId = roleSelect.value;
        const payload = buildRequest();

        saving = true;
        saveButton.disabled = true;

        try {
            const result = await api(
                `/api/admin/permissions/roles/${roleId}`,
                {
                    method: "PUT",
                    body: JSON.stringify(payload)
                }
            );

            if (!active() || current !== requestId) return;

            detail = result;
            scopeSelect.value = detail.scopeCode;
            renderTables(detail.tables);
            renderOperations(detail.operations);

            snapshot = JSON.stringify(buildRequest());
            DialogService.info("権限設定を保存しました。");
        } catch (error) {
            if (active() && current === requestId) {
                DialogService.error(error.message);
            }
        } finally {
            saving = false;
            if (active() && current === requestId) {
                updateSubmitState();
            }
        }
    }
}

async function api(url, options = {}) {
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    const headers = {"Accept": "application/json"};
    if (options.body != null) {
        headers["Content-Type"] = "application/json";
    }

    if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
    }

    const response = await fetch(url, {
        credentials: "same-origin",
        ...options,
        headers: {
            ...headers,
            ...options.headers
        }
    });

    if (!response.ok) {
        const result = await response.json().catch(() => null);
        const message = result?.message
            || `通信エラーが発生しました。(${response.status})`;

        throw new Error(message);
    }

    return response.json();
}