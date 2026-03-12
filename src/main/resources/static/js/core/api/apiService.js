"use strict"

import {apiFetch} from "./apiFetch.js";

export class ApiService {

    constructor(getToken){
        this.getToken = getToken;
    }

    get(url){
        return apiFetch(url,{
            method:"GET",
            token:this.getToken()
        });
    }

    post(url,data){
        return apiFetch(url,{
            method:"POST",
            data,
            token:this.getToken()
        });
    }

    form(url,formData){
        return apiFetch(url,{
            method:"POST",
            data:formData,
            token:this.getToken()
        });
    }

}