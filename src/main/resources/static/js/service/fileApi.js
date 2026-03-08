"use strict"

export class FileApi {

    constructor(api){
        this.api = api;
    }

    upload(parentId,formData){
        return this.api.form(`/api/file/upload/${parentId}`,formData);
    }

    list(parentId){
        return this.api.get(`/api/file/list/${parentId}`);
    }

}