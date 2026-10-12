"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { registerController } from "../../../application/controllerRegistry.js";
import { createMasterPage } from "../../../core/page/createMasterPage.js";
import { TimeworksRepository } from "../../../repositories/management/TimeworksRepository.js";
import { createTimeworksListColumns } from "./columns.js";

import { createManagement } from "./management.js";
import { createStamp } from "./stamp.js";
import { createPageSession } from "../../../core/page/PageSession.js";

export async function init() {
    const session = createPageSession(document.querySelector("main[data-page$='timeworksPage.js']"));
    const management = createManagement(session);
    const stamp = createStamp(session, management);
    const timeworks = timeworksPage(management, stamp);
    const dispose = timeworks.dispose.bind(timeworks);
    timeworks.dispose = () => {session.dispose(); dispose();};
    registerController("timeworks", timeworks);
    try {
        await initPageCache("/api/timeworks/init/cache", {isActive: session.isActive});
        if(!session.isActive()) return;
        await initCommon();
        stamp.initStamp(timeworks);
    } catch(error) {
        timeworks.dispose();
        throw error;
    }
}

export const timeworksPage = (management, stamp) =>
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
            "employee-resolved": stamp.handleEmployeeResolved,
            stamp: stamp.handleStamp,
            "load-management-employees": async () => {
                await management.loadManagementEmployees();
                await management.refreshManagement();
            },
            "search-management": async () => {await management.refreshManagement();},
            "download-management-csv": () => {management.downloadManagementCsv();}
        },
        model: {
            pageSize: 50
        }
    });