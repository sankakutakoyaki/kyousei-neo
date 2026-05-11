"use strict"

export class SaveBehavior {
    constructor(config = {}){
        this.beforeSave = config.beforeSave;
        this.validateBusiness = config.validateBusiness;
        this.confirmSave = config.confirmSave;
        this.executeSave = config.executeSave;
    }

    async save(payload, context = {}){
        if(this.beforeSave){
            await this.beforeSave(
                payload,
                context
            );
        }

        if(this.validateBusiness){
            await this.validateBusiness(
                payload,
                context
            );
        }

        if(this.confirmSave){
            const ok = await this.confirmSave(payload, context);
            if(!ok){
                return null;
            }
        }

        if(!this.executeSave){
            return null;
        }

        return await this.executeSave(
            payload,
            context
        );
    }
}