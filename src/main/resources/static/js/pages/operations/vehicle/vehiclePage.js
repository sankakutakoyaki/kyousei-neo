"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { createVehicleColumns } from "./columns.js";
import { VehicleRepository } from "../../../repositories/operations/vehicle/VehicleRepository.js";
import { createMasterPage } from "../../../core/page/createMasterPage.js";
import { filterFactory } from "../../../util/filterFactory.js";
import { registerController } from "../../../application/controllerRegistry.js";

export async function init() {
    await initCommon();
    await initPageCache("/api/vehicle/init/cache");

    const vehicle = vehiclePage();

    registerController("vehicle", vehicle);

    vehicle.init();
    await vehicle.refresh();
}

export const vehiclePage = () =>
    createMasterPage({
        key: "vehicle",
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        idKey: "vehicleId",
        repository: VehicleRepository,

        columns: createVehicleColumns(),

        components: {
            combo: true
        },

        buildDetailParams: (id) => ({
            state: APP.cache.common.state.INITIAL,
            vehicleId: id
        }),

        model: {
            filters: {
                officeId: filterFactory.equals("officeId")
            }
        }
    });