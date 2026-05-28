"use strict"

export class SaveBehavior {
    constructor(config = {}){
        this.beforeSave = config.beforeSave;
        this.validateBusiness = config.validateBusiness;
        this.confirmSave = config.confirmSave;
        this.executeSave = config.executeSave;
        this.afterSave = config.afterSave;
    }

    async save(payload, context = {}){
        if(this.beforeSave){
            await this.beforeSave(
                payload,
                context
            );
        }
        if(this.validateBusiness){
            await this.validateBusiness(payload);
        }
        if(this.confirmSave){
            const ok = await this.confirmSave(payload);
            if(!ok) return null;
        }
        if(!this.executeSave) return null;

        const result = await this.executeSave(payload);
        if(this.afterSave){
            await this.afterSave(result, payload);
        }
        return result;
    }
}