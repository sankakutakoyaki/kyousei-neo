"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { createRecycleMakerColumns, createRecycleManufacturerColumns } from "./columns.js";
import { registerController } from "../../../applcation/controllerRegistry.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { RecycleMakerRepository } from "../../../repositories/corporation/recyclemaker/recycleMakerRepository.js";
import { RecycleManufacturerRepository } from "../../../repositories/corporation/recyclemaker/RecycleManufacturerRepository.js";
import { getController } from "../../../applcation/controllerRegistry.js";
import { createMasterPage } from "../../../core/page/createMasterPage.js";

export async function init() {
    await initCommon();
    await initPageCache("/api/recyclemaker/init/cache");

    // tab1
    const maker = recycleMakerPage();
    registerController("recycleMaker", maker);
    maker.init();
    await maker.refresh();

    // tab2
    const manufacturer = recycleManufacturerPage();
    registerController("recycleManufacturer", manufacturer);
    manufacturer.init();
    await manufacturer.refresh();
}

export const recycleMakerPage = () =>
    createMasterPage({
        key: "recycleMaker",
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        idKey: "recycleMakerId",
        repository: RecycleMakerRepository,
        saveHandler: RecycleMakerRepository.save,
        columns: createRecycleMakerColumns(),
        afterSave: refreshMakerChildren,
        model: {
            pageSize: 300
        },
    });

export const recycleManufacturerPage = () =>
    createMasterPage({
        key: "recycleManufacturer",
        tableId: "table-02",
        footerId: "footer-02",
        formId: "form-02",
        idKey: "recycleManufacturerId",
        repository: RecycleManufacturerRepository,
        saveHandler: RecycleManufacturerRepository.save,
        columns: createRecycleManufacturerColumns(),
        components: {combo: true, input: true},
        model: {
            filters: {code: filterFactory.equals("code")},
            pageSize: 300
        },
        validateBusiness: async (payload) => {
            if(!payload.recycleMakerId){
                throw {
                    message: "略称を選択してください",
                    fields: ["code"]
                };
            }
        }
    });

async function refreshMakerChildren(){
    const keys = [
        "recycleManufacturer"
    ];
    for(const key of keys){
        const controller = getController(key);
        await controller?.refresh();
    }
}