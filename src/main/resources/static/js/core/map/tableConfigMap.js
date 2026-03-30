"use strict"

import { tableConfig as partnerConfig } from "../../pages/corporation/partner/partnerConfig.js";

const tableConfigMap = {
    partnerCompany: partnerConfig.partnerCompany,
    partnerEmployee: partnerConfig.partnerEmployee
};

export function getTableConfig(name){
    return tableConfigMap[name];
}