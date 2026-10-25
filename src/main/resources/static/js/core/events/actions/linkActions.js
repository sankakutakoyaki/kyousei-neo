"use strict";

function getTargetValue(el) {
    const name = el.dataset.target;
    const form = el.closest("form");
    return form?.elements.namedItem(name)?.value?.trim() ?? "";
}

export function handlePhone(e, el) {
    e.preventDefault();
    const phone = getTargetValue(el).replace(/[^\d+]/g, "");
    if (!phone) return;
    window.location.href = `tel:${phone}`;
}

export function handleMap(e, el) {
    e.preventDefault();
    const address = getTargetValue(el);
    if (!address) return;
    const url = `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(address)}`;
    window.open(url, "_blank", "noopener,noreferrer");
}