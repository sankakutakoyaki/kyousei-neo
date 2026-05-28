"use strict"

export function init(area, config) {

    config.forEach(cfg => {
        const el = area.getElementById(cfg.comboId);
        if (!el) return;

        createComboBox({
            area: el,
            items: cfg.comboList,
            text: cfg.text ?? null,
            onChange: cfg.onChange ?? null
        });
    });
}

export function createComboBox({area, items, text = null, onChange = null}) {
    if (!area) return;
    area.replaceChildren();

    if (text !== null) {
        area.insertAdjacentHTML("beforeend",`<option value="0" data-id="0">${text}</option>`);
    }

    (items ?? []).forEach(item => {
        const v = item.value;

        if ((typeof v === "number" && v > -1) || (typeof v === "string" && v !== "")) {
            const option = document.createElement("option");
            option.value = item.value;
            option.dataset.parent = item.parent ?? "";
            option.dataset.id = item.data ?? "";
            option.textContent = item.label;
            area.appendChild(option);
        }
    });

    if (typeof onChange === "function") {
        area.addEventListener("change", onChange);
    }
}