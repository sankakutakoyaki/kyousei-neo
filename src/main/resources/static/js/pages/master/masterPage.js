"use strict"

import { initCommon } from "../../bootstrap/initPage.js";
import { initPageCache } from "../../bootstrap/initPageCache.js";
import { registerController } from "../../applcation/controllerRegistry.js";
import { filterFactory } from "../../util/filterFactory.js";
import { ItemMasterRepository } from "../../repositories/master/item/itemMasterRepository.js";
import { WorkMasterRepository } from "../../repositories/master/work/workMasterRepository.js";
import { createItemMasterColumns, createWorkMasterColumns } from "./columns.js";
import { getController } from "../../applcation/controllerRegistry.js";
import { createMasterPage } from "../../core/page/createMasterPage.js";

export async function init() {
    await initCommon();
    await initPageCache("/api/master/init/cache");

    // tab1
    const item = itemMasterPage();
    registerController("itemMaster", item);
    item.init();
    await item.refresh();

    // tab2
    const work = workMasterPage();
    registerController("workMaster", work);
    work.init();
    await work.refresh();
}

export const itemMasterPage = () =>
    createMasterPage({
        key: "itemMaster",
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        idKey: "itemMasterId",
        repository: ItemMasterRepository,
        saveHandler: ItemMasterRepository.save,
        columns: createItemMasterColumns(),
        submitText: "保存",
        cancelText: "キャンセル",
        components: {combo: true, input: true},
        model: {
            pageSize: 50
        },
    });

export const workMasterPage = () =>
    createMasterPage({
        key: "workMaster",
        tableId: "table-02",
        footerId: "footer-02",
        formId: "form-02",
        idKey: "workMasterId",
        repository: WorkMasterRepository,
        saveHandler: WorkMasterRepository.save,
        columns: createWorkMasterColumns(),
        submitText: "保存",
        cancelText: "キャンセル",
        components: {combo: true, input: true},
        model: {
            pageSize: 50
        }
    });