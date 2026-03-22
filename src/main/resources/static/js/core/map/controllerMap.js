"use strict"

import { partnerCompanyController } from "../../pages/corporation/partner/partnerController.js";
import { partnerEmployeeController } from "../../pages/corporation/partner/partnerController.js";

const controllerMap = {
    partnerCompany: partnerCompanyController,
    partnerEmployee: partnerEmployeeController
};

export function getController(target){
    return controllerMap[target];
}