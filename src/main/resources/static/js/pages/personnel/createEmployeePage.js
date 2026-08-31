"use strict";

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
