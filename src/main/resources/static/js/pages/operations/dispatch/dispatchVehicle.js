"use strict";

import { DailyCrewRepository } from "../../../repositories/operations/dispatch/DailyCrewRepository.js";
import { VehicleDefaultMemberRepository } from "../../../repositories/operations/vehicle/VehicleDefaultMemberRepository.js";
import { DialogService } from "../../../core/ui/dialog/DialogService.js";

import { getDispatchConditions as readConditions } from "./dispatchConditions.js";

export function createDispatchVehicle({session, onChanged} = {}) {
    let candidateRequest = 0;
    let adding = false;
    const getDispatchConditions = () => readConditions(session.root);
    return {
        async loadCandidates() {
            if(!session.isActive()) return;
            const request = ++candidateRequest;
            const select = session.byId("dispatch-add-vehicle");
            if(!select){return;}
            select.replaceChildren();

            const emptyOption = new Option("車両を選択", "");
            select.add(emptyOption);

            const conditions = getDispatchConditions();
            if(!conditions.workDate || !conditions.officeId || !conditions.dispatchCategory){
                select.disabled = true;
                return;
            }

            const result = await VehicleDefaultMemberRepository.findDispatchCandidateList({
                workDate: conditions.workDate,
                officeId: conditions.officeId,
                dispatchCategory: conditions.dispatchCategory,
                state: APP.cache.common.state.INITIAL
            });
            if(!session.isActive() || request !== candidateRequest
                || JSON.stringify(conditions) !== JSON.stringify(getDispatchConditions())) return;
            const rows = result.data ?? [];
            rows.forEach(vehicle => {select.add(new Option(buildVehicleLabel(vehicle), vehicle.vehicleId));});
            select.disabled = rows.length === 0;
        },

        async add() {
            if(adding || !session.isActive()) return;
            const conditions = getDispatchConditions();
            const vehicleId = Number(session.byId("dispatch-add-vehicle")?.value || 0);
            if(!conditions.workDate || !conditions.officeId || !conditions.dispatchCategory || !vehicleId){return;}
            adding = true;
            try {
                await DailyCrewRepository.bulkCreate({
                    workDate: conditions.workDate,
                    officeId: conditions.officeId,
                    dispatchCategory: conditions.dispatchCategory,
                    vehicleIds: [vehicleId],
                    // 手動追加なので基本乗務員はコピーしない
                    copyDefaultMembers: false
                });
                await onChanged?.();
            } catch(error) {
                DialogService.error(error.message ?? "車両の追加に失敗しました。");
            } finally {adding = false;}
        }
    };
}

function buildVehicleLabel(vehicle){
    return [
        vehicle.vehicleName,
        vehicle.registrationArea,
        vehicle.registrationClass,
        vehicle.registrationKana,
        vehicle.registrationNumber
    ].filter(Boolean).join(" ");
}