"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { createRecycleMakerColumns, createRecycleManufacturerColumns, createRecyclePriceColumns } from "./columns.js";
import { registerController } from "../../../application/controllerRegistry.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { RecycleMakerRepository } from "../../../repositories/corporation/recyclemaker/RecycleMakerRepository.js";
import { RecycleManufacturerRepository } from "../../../repositories/corporation/recyclemaker/RecycleManufacturerRepository.js";
import { RecyclePriceRepository } from "../../../repositories/corporation/recyclemaker/RecyclePriceRepository.js";
import { RecycleItemRepository } from "../../../repositories/corporation/recyclemaker/RecycleItemRepository.js";
import { getController } from "../../../application/controllerRegistry.js";
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

    // tab3
    const price = await recyclePricePage();
    registerController("recyclePrice", price);
    price.init();
    await price.refresh();
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
        submitText: "保存",
        cancelText: "キャンセル",
        components: {combo: true, input: true},
        afterSave: refreshMakerChildren,
        model: {
            pageSize: 50
        },
        validateBusiness: async (payload, currentEntity) => {
            const code =
                payload.code !== undefined
                    ? payload.code
                    : currentEntity?.code;

            if (code != null && Number(code) === 0) {
                throw {
                    message: "コードは0以外の数字を入力してください",
                    fields: ["code"]
                };
            }
        }
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
        submitText: "保存",
        cancelText: "キャンセル",
        components: {combo: true, input: true},
        model: {
            filters: {code: filterFactory.equals("code")},
            pageSize: 50
        },
        // validateBusiness: async (payload) => {
        //     if(!payload.recycleMakerId){
        //         throw {
        //             message: "略称を選択してください",
        //             fields: ["code"]
        //         };
        //     }
        // }
        validateBusiness: async (payload) => {
            const isInsert =
                !payload.recycleManufacturerId ||
                Number(payload.recycleManufacturerId) === 0;

            if (isInsert && (
                payload.recycleMakerId == null ||
                Number(payload.recycleMakerId) === 0
            )) {
                throw {
                    message: "略称を選択してください",
                    fields: ["code"]
                };
            }
        }
    });

export const recyclePricePage = async () => {
    const items = await RecycleItemRepository.search({
        state: APP.cache.common.state.INITIAL
    });

    return createMasterPage({
        key: "recyclePrice",
        tableId: "table-03",
        footerId: "footer-03",
        formId: "form-03",
        idKey: "recycleMakerId",
        repository: RecyclePriceRepository,
        saveHandler: RecyclePriceRepository.save,
        columns: createRecyclePriceColumns(items),
        submitText: "保存",
        cancelText: "キャンセル",
        components: {combo: true, input: true},
        checkable: false,
        model: {
            filters: {code: filterFactory.equals("code")},
            pageSize: 50
        },
        beforeSave: payload => {
            payload.details = [];
            for(let i = 1; i <= 8; i++){
                const recyclePriceId = payload[`recycle-price-id-${i}`];
                const price = Number(String(payload[`price${i}`] ?? 0).replace(/,/g, ""));
                // 新規で0円なら送らない
                if(!recyclePriceId && price <= 0) continue;
                payload.details.push({
                    recyclePriceId,
                    recycleMakerId: payload.recycleMakerId,
                    recycleItemId: i,
                    price
                });
            }
        }
    });
};

async function refreshMakerChildren(){
    const keys = [
        "recycleManufacturer",
        "recyclePrice"
    ];
    for(const key of keys){
        const controller = getController(key);
        await controller?.refresh();
    }
}
