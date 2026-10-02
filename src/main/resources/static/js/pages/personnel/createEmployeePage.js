"use strict";

import { filterFactory } from "../../util/filterFactory.js";
import { EmployeeRepository } from "../../repositories/personnel/employee/EmployeeRepository.js";
import { createMasterPage } from "../../core/page/createMasterPage.js";

let originalWorkCategoryIds = [];
let originalEmployeeOfficeIds = [];

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
            await loadEmployeeOfficeMembers(data.employeeId);
            if(config.onOpen) await config.onOpen(data,form);
        },
        beforeSave: (payload) => {
            const isInsert = !payload.employeeId || Number(payload.employeeId) === 0;
            if(isInsert){
                payload.category = config.category;
                if(payload.code == null || payload.code === ""){
                    payload.code = 0;
                }
                if((payload.companyId == null || Number(payload.companyId) === 0) && config.companyId != null && Number(config.companyId) !== 0){
                    payload.companyId = config.companyId;
                }
            }
            if(config.beforeSave){
                config.beforeSave(payload);
            }
        },
        buildAdditionalPayload: () => ({
            workCategoryIds: getSelectedWorkCategoryIds(),
            employeeOfficeIds: getSelectedEmployeeOfficeIds()
        }),
        hasAdditionalChanges: () => {
            const currentWorkCategoryIds = getSelectedWorkCategoryIds();
            const currentEmployeeOfficeIds = getSelectedEmployeeOfficeIds();
            return (
                JSON.stringify(currentWorkCategoryIds) !== JSON.stringify(originalWorkCategoryIds) 
                || JSON.stringify(currentEmployeeOfficeIds ) !== JSON.stringify(originalEmployeeOfficeIds)
            );
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
    });
    originalWorkCategoryIds = getSelectedWorkCategoryIds();
}

async function loadEmployeeOfficeMembers(employeeId){
    const area = document.getElementById("employee-office-member-area");
    if(!area){
        return;
    }
    area.replaceChildren();

    const result = employeeId ? await EmployeeRepository.findOfficeMembers({employeeId}) : {data: []};
    const members = result.data ?? [];
    const offices = APP.cache.page.officeComboList ?? [];
    const selectedIds = new Set(
        members.filter(row => Number(row.state) === APP.cache.common.state.INITIAL)
            .map(row => Number(row.officeId))
    );
    offices.forEach(office => {
            const officeId = Number(office.value);
            if(!officeId){
                return;
            }

            const label = document.createElement("label");
            const input = document.createElement("input");
            input.type = "checkbox";
            input.name = "employee-office-member";
            input.dataset.submit = "none";
            input.value = String(officeId);
            input.checked = selectedIds.has(officeId);
            const text = document.createElement("span");
            text.textContent = office.label;
            label.append(input, text);
            area.appendChild(label);
        }
    );

    originalEmployeeOfficeIds =
        getSelectedEmployeeOfficeIds();
}

function getSelectedWorkCategoryIds(){
    return [
        ...document.querySelectorAll('#employee-work-category-area input[name="employee-work-category"]:checked')
    ].map(input => Number(input.value)).sort((a, b) => a - b);
}

function getSelectedEmployeeOfficeIds(){
    return [
        ...document.querySelectorAll('#employee-office-member-area input[name="employee-office-member"]:checked')
    ].map(input => Number(input.value)).sort((a, b) => a - b);
}