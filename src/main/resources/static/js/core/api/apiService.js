"use strict"

import {apiFetch} from "./apiFetch.js";

// export class ApiService {

    // constructor(getToken){
    //     this.getToken = getToken;
    // }
export const api = {
    get: (url) => {
        return apiFetch(url,{
            method:"GET"
        });
    },

    post: (url, data) => {
        return apiFetch(url,{
            method:"POST",
            data
        });
    },

    form: (url,formData) => {
        return apiFetch(url,{
            method:"POST",
            data:formData
        });
    }
}

// /**
//  * 
//  * @returns token取得
//  */
// export function getCsrfToken() {
//     return {
//         token: document.querySelector('meta[name="_csrf"]').content,
//         header: document.querySelector('meta[name="_csrf_header"]').content
//     };
// }