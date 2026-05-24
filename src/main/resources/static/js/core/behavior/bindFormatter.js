"use strict";

import { formatters } from "./formatters.js";

export function bindFormatters(area = document){
    const fields = area.querySelectorAll("[data-format]");
    fields.forEach(field => {
        const type = field.dataset.format;
        const formatter = formatters[type];
        if(!formatter) return;

        field.addEventListener("input", () => {
            const caret = field.selectionStart;
            field.value = formatter(field.value);
            // 必要ならcaret復元
        });
    });
}