"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { createRecycleMakerColumns } from "./columns.js";
import { registerController } from "../../../applcation/controllerRegistry.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { RecycleMakerRepository } from "../../../repositories/corporation/recyclemaker/recycleMakerRepository.js";
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
        // category: APP.cache.common.companyCategory.PARTNER,
        columns: createRecycleMakerColumns(),
        // components: {combo: true},
        model: {
            pageSize: 300
        },
        // validateBusiness: async (payload) => {
        //     if(!payload.category){
        //         throw {
        //             message: "分類を選択してください",
        //             fields: ["category"]
        //         };
        //     }
        // }
    });