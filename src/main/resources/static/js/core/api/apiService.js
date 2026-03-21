"use strict"

import { apiFetch } from "./apiFetch.js";

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