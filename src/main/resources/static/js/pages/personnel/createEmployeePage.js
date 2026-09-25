"use strict";

import { filterFactory } from "../../util/filterFactory.js";
import { EmployeeRepository } from "../../repositories/personnel/employee/EmployeeRepository.js";
import { createMasterPage } from "../../core/page/createMasterPage.js";

let originalWorkCategoryIds = [];

export function createEmployeePage(config){
    return createMasterPage({
        ...config,
        idKey: "employeeId",
        repository: EmployeeRepository,
        buildDetailParams:
        (id) => ({
            state: APP.cache.common.state.INITIAL,
            employeeId: id
        }),
        model: config.model ?? {
            filters: { officeId: filterFactory.equals("officeId")}
        },
        onOpen: async (data, form) => {
            await loadWorkCategories(data.employeeId);
            if(config.onOpen) await config.onOpen(data,form);
        },
        beforeSave: (payload) => {
            const isInsert = !payload.employeeId || Number(payload.employeeId) === 0;
            if(isInsert){
                payload.category = config.category;
                if(payload.code == null || payload.code === ""){
                    payload.code = 0;
                }
            }
            if(config.beforeSave){
                config.beforeSave(payload);
            }
        },
        buildAdditionalPayload: () => ({
            workCategoryIds: getSelectedWorkCategoryIds()
        }),
        hasAdditionalChanges: () => {
            const current = getSelectedWorkCategoryIds();
            return JSON.stringify(current) !== JSON.stringify(originalWorkCategoryIds);
        },
    });
}

async function loadWorkCategories(employeeId){
    const area = document.getElementById("employee-work-category-area");
    if(!area){
        return;
    }

    area.replaceChildren();
    const [categoryResult, memberResult] =
        await Promise.all([
            EmployeeRepository.findWorkCategories({state:APP.cache.common.state.INITIAL}),
            employeeId ? EmployeeRepository.findWorkCategoryMembers({employeeId, state:APP.cache.common.state.INITIAL}): Promise.resolve({data: []})
        ]);
    const categories = categoryResult.data ?? [];
    const members = memberResult.data ?? [];
    const selectedIds = new Set(members.filter(row => Number(row.state) === APP.cache.common.state.INITIAL).map(row => Number(row.employeeWorkCategoryId)));
    categories.forEach(category => {
            const label = document.createElement("label");
            const input = document.createElement("input");
            input.type = "checkbox";
            input.name = "employee-work-category";
            input.dataset.submit = "none";
            input.value = String(category.employeeWorkCategoryId);
            input.checked = selectedIds.has(Number(category.employeeWorkCategoryId));
            const text = document.createElement("span");
            text.textContent = category.name;
            label.append(input, text);
            area.append(label);
        }
    );
    originalWorkCategoryIds = getSelectedWorkCategoryIds();
}

function getSelectedWorkCategoryIds(){
    return [
        ...document.querySelectorAll('#employee-work-category-area input[name="employee-work-category"]:checked')
    ].map(input => Number(input.value)).sort((a, b) => a - b);
}