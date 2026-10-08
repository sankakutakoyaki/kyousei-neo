"use strict";

import { formatters } from "./formatters.js";

export function formatFields(area = document){
    const fields = area.querySelectorAll("[data-format]");
    fields.forEach(field => {
        const type = field.dataset.format;
        const formatter = formatters[type];
        if(!formatter){
            return;
        }
        field.value = formatter(field.value);
    });
}