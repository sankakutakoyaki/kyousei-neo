"use strict";

import { DailyCrewRepository }
    from "../../../repositories/operations/dispatch/DailyCrewRepository.js";

import { VehicleDefaultMemberRepository }
    from "../../../repositories/operations/vehicle/VehicleDefaultMemberRepository.js";

import { DialogService }
    from "../../../core/ui/dialog/DialogService.js";


export function createDispatchVehicle({
    onChanged
} = {}) {

    return {

        async loadCandidates() {

            const select =
                document.getElementById(
                    "dispatch-add-vehicle"
                );

            if(!select){
                return;
            }

            select.replaceChildren();

            const emptyOption =
                new Option(
                    "車両を選択",
                    ""
                );

            select.add(emptyOption);

            const conditions =
                getDispatchConditions();

            if(
                !conditions.workDate ||
                !conditions.officeId ||
                !conditions.dispatchCategory
            ){
                select.disabled = true;
                return;
            }

            const result =
                await VehicleDefaultMemberRepository
                    .findDispatchCandidateList({

                        workDate:
                            conditions.workDate,

                        officeId:
                            conditions.officeId,

                        dispatchCategory:
                            conditions.dispatchCategory,

                        state:
                            APP.cache.common
                                .state.INITIAL
                    });

            const rows =
                result.data ?? [];

            rows.forEach(vehicle => {

                select.add(
                    new Option(
                        buildVehicleLabel(vehicle),
                        vehicle.vehicleId
                    )
                );
            });

            select.disabled =
                rows.length === 0;
        },


        async add() {

            const conditions =
                getDispatchConditions();

            const vehicleId =
                Number(
                    document.getElementById(
                        "dispatch-add-vehicle"
                    )?.value || 0
                );

            if(
                !conditions.workDate ||
                !conditions.officeId ||
                !conditions.dispatchCategory ||
                !vehicleId
            ){
                return;
            }

            try {

                await DailyCrewRepository
                    .bulkCreate({

                        workDate:
                            conditions.workDate,

                        officeId:
                            conditions.officeId,

                        dispatchCategory:
                            conditions.dispatchCategory,

                        vehicleIds:
                            [vehicleId],

                        // 手動追加なので基本乗務員はコピーしない
                        copyDefaultMembers:
                            false
                    });

                await onChanged?.();

            } catch(error) {

                DialogService.error(
                    error.message ??
                    "車両の追加に失敗しました。"
                );
            }
        }
    };
}


function getDispatchConditions(){

    return {

        workDate:
            document.getElementById(
                "dispatch-work-date"
            )?.value ?? "",

        officeId:
            Number(
                document.getElementById(
                    "dispatch-office"
                )?.value || 0
            ),

        dispatchCategory:
            Number(
                document.getElementById(
                    "dispatch-category"
                )?.value || 0
            )
    };
}


function buildVehicleLabel(vehicle){

    return [
        vehicle.vehicleName,
        vehicle.registrationArea,
        vehicle.registrationClass,
        vehicle.registrationKana,
        vehicle.registrationNumber
    ]
        .filter(Boolean)
        .join(" ");
}