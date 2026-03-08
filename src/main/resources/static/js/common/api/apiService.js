"use strict"

import {apiFetch} from "./apiFetch.js";

export class ApiService {

    constructor(token){
        this.token = token;
    }

    get(url){
        return apiFetch(url,{
            method:"GET",
            token:this.token
        });
    }

    post(url,data){
        return apiFetch(url,{
            method:"POST",
            data,
            token:this.token
        });
    }

    form(url,formData){
        return apiFetch(url,{
            method:"POST",
            data:formData,
            token:this.token
        });
    }

}