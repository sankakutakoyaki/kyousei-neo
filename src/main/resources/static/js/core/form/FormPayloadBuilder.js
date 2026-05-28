"use strict"

import { FormModel } from "./FormModel.js";

export const FormPayloadBuilder = {
    build({
        form,
        currentEntity,
        key,
        isBulkMode,
        getTargetIds
    }){
        const payload = FormModel.buildPayload(form, currentEntity, key);
        if(payload == null){
            return null;
        }
        if(isBulkMode){
            const ids = getTargetIds(payload);
            payload.ids = ids;
        }
        return payload;
    }
};