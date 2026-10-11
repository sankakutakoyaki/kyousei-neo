"use strict";

export function initLoginOffice(area = document){
    const loginOfficeId = APP.cache.common?.loginOfficeId;
    if(!loginOfficeId){return;}

    area.querySelectorAll("[data-login-office]").forEach(office => {
        if(office.value && office.value !== "0"){return;}

        const value = String(loginOfficeId);
        const exists = [...office.options].some(option => String(option.value) === value);
        if(exists){office.value = value;}
    });
}