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
        // 当日の勤怠一覧は社員IDで行を識別する
        idKey: "employeeId",
        repository: TimeworksRepository,
        columns: createTimeworksListColumns(),
        // 打刻の有無に応じて行の表示スタイルを切り替える
        rowClass: item => item.startTime || item.endTime
            ? "timework-stamped"
            : "timework-pending",
        components: {combo: true},
        forms: {},
        checkable: false,
        buildParams: () => ({}),
        // 勤怠一覧のダブルクリックで社員を打刻欄にセットする
        onDoubleClick: item => stamp.selectEmployeeFromList(item),
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