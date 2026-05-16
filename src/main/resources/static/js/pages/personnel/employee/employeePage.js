"use strict"

import { initCommon } from "../../../core/init/initPage.js";
import { createEmployeeColumns } from "./columns.js";
import { registerController } from "../../../controllers/controllers.js";
import { initPageCache } from "../../../core/init/initPageCache.js";
import { createEmployeePage } from "../createEmployeePage.js";

export async function init() {
    await initCommon();
    await initPageCache("/api/employee/init/cache");

    // tab1
    const fulltime = fulltimeEmployeePage();
    registerController("fulltime", fulltime);
    fulltime.init();
    await fulltime.refresh();

    // tab2
    const parttime = parttimeEmployeePage();
    registerController("parttime", parttime);
    parttime.init();
    await parttime.refresh();
}

export const fulltimeEmployeePage = () =>
    createEmployeePage({
        key: "fulltime",
        components: {combo: true},
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        columns: createEmployeeColumns(),
        category:APP.cache.common.employeeCategory.FULLTIME
    });

export const parttimeEmployeePage = () =>
    createEmployeePage({
        key: "parttime",
        components: {combo: true},
        tableId: "table-02",
        footerId: "footer-02",
        formId: "form-01",
        columns: createEmployeeColumns(),
        category: APP.cache.common.employeeCategory.PARTTIME
    });