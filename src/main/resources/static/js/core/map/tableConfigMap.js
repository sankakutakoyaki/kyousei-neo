"use strict"

import { tableConfig } from "../../pages/corporation/partner/partnerConfig.js";

const tableConfigMap = {
    partnerCompany: tableConfig.partnerCompany,
    partnerEmployee: tableConfig.partnerEmployee
};

export function getTableConfig(name){
    return tableConfigMap[name];
}