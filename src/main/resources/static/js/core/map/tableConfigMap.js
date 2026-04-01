"use strict"

import { tableConfig as partnerConfig } from "../../pages/corporation/partner/config.js";

const tableConfigMap = {
    partnerCompany: partnerConfig.partnerCompany,
    partnerEmployee: partnerConfig.partnerEmployee
};

export function getTableConfig(name){
    return tableConfigMap[name];
}