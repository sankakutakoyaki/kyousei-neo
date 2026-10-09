"use strict";

export function initLoginOffice(area = document){
    const loginOfficeId = APP.cache.page?.loginOfficeId;
    if(!loginOfficeId){ return; }

    area.querySelectorAll("[data-login-office]").forEach(office => {
        const value = String(loginOfficeId);
        const exists = [...office.options].some(option => String(option.value) === value);
        if(exists){ office.value = value; }
    });
}