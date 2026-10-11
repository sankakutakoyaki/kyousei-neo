"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { registerController } from "../../../application/controllerRegistry.js";
import { createMasterPage } from "../../../core/page/createMasterPage.js";
import { TimeworksRepository } from "../../../repositories/management/TimeworksRepository.js";
import { createTimeworksListColumns } from "./columns.js";

import {
    loadManagementEmployees,
    refreshManagement,
    downloadManagementCsv
} from "./management.js";

import {
    initStamp,
    handleEmployeeResolved,
    handleStamp
} from "./stamp.js";

export async function init() {
    await initPageCache("/api/timeworks/init/cache");
    await initCommon();

    const timeworks = timeworksPage();
    registerController("timeworks", timeworks);

    initStamp(timeworks);
}

export const timeworksPage = () =>
    createMasterPage({
        key: "timeworks",
        tableId: "table-01",
        footerId: "footer-01",
        idKey: "timeworkId",
        repository: TimeworksRepository,
        columns: createTimeworksListColumns(),
        components: {combo: true},
        forms: {},
        checkable: false,
        buildParams: () => ({}),
        onDoubleClick: () => {},
        actions: {
            "employee-resolved": handleEmployeeResolved,
            stamp: handleStamp,
            "load-management-employees": async () => {
                await loadManagementEmployees();
                await refreshManagement();
            },
            "search-management": async () => {await refreshManagement();},
            "download-management-csv": () => {downloadManagementCsv();}
        },
        model: {
            pageSize: 50
        }
    });