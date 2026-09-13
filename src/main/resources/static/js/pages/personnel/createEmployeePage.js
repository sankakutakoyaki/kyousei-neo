"use strict";

import { RequestClient } from "../../core/api/RequestClient.js";
import { filterFactory } from "../../util/filterFactory.js";
import { EmployeeRepository } from "../../repositories/personnel/employee/EmployeeRepository.js";
import { createMasterPage } from "../../core/page/createMasterPage.js";

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
            const root=document.getElementById(form.formId)?.querySelector('.dialog-content');
            if (!root) return;
            let summary=root.querySelector('[data-employee-qualifications]');
            if(!summary){summary=document.createElement('details');summary.dataset.employeeQualifications='';root.append(summary);}
            summary.replaceChildren();const title=document.createElement('summary');title.textContent='保有資格・免許・教育';summary.append(title);
            if(data.employeeId) {
                try {
                    const result=await RequestClient.request({queryId:'operationEmployeeQualificationsList',params:{employeeId:data.employeeId}});
                    for(const q of result.data??[]){const line=document.createElement('p');line.textContent=`${q.qualificationName} ／ 取得 ${q.acquiredDate??''} ／ 有効期限 ${q.expiryDate??'設定なし'}`;summary.append(line);}
                    if(!result.data?.length){const line=document.createElement('p');line.textContent='登録されている資格はありません。';summary.append(line);}
                } catch {const line=document.createElement('p');line.textContent='資格情報を読み込めませんでした。';summary.append(line);}
            }
            if(config.onOpen) await config.onOpen(data,form);
        },
        beforeSave: (payload) => {
            const isInsert =
                !payload.employeeId ||
                Number(payload.employeeId) === 0;

            if(isInsert){
                payload.category = config.category;
                if(payload.code == null || payload.code === ""){
                    payload.code = 0;
                }
            }

            if(config.beforeSave){
                config.beforeSave(payload);
            }
        }
    });
}
