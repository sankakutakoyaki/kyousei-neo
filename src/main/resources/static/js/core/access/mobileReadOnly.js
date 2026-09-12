"use strict";

export const mobileQuery = "(max-width: 560px), (pointer: coarse) and (max-width: 960px)";
export const isMobileDevice = () => typeof window !== "undefined" && window.matchMedia(mobileQuery).matches;
export function isRecycleScope(scope) {
    if (typeof scope === "string") return /^recycle(Use|Delivery|Shipping|Loss)$/.test(scope);
    return !!scope?.closest?.('main[data-page$="/recyclePage.js"]') && !!scope?.closest?.('#header-02, #header-03, #header-04, #header-05');
}
export const isMobileReadOnly = scope => isMobileDevice() && !isRecycleScope(scope);
export const mobileWriteQueries = new Set(["orderItemArrival", "recycleSave", "recycleDeliverySave", "recycleShippingSave", "recycleLossSave"]);
export const isWriteAction = action => /^(create|delete|save|bulkEdit|delete-order-item|delete-order-work)$/i.test(action);
export function isWriteRequest(url, method, data) {
    if (/^(GET|HEAD|OPTIONS)$/i.test(method)) return false;
    const path = String(url).split("?")[0];
    if (path === "/api/query") return !/(Detail|List|Csv|ListByItemModel|FindByJanCode)$/.test(data?.queryId ?? "");
    return /^\/api\/(attachments|order|timeworks)(\/|$)/.test(path);
}
export function assertMobileWriteAllowed(url, method, data) {
    if (isMobileDevice() && isWriteRequest(url, method, data) && !(String(url).split("?")[0] === "/api/query" && mobileWriteQueries.has(data?.queryId) && !(data?.queryId === "recycleSave" && Number(data?.params?.recycleId ?? 0) !== 0))) throw new Error("スマホでは入荷登録・リサイクルの登録以外は閲覧のみ利用できます。");
}
const writeSelector = '[data-mobile-write], [data-action~="create"], [data-action~="delete"], [data-action~="save"], [data-action~="bulkEdit"], [data-action~="delete-order-item"], [data-action~="delete-order-work"], #order-pdf-file-button, #add-item-btn, #add-work-btn, [data-attachment-action="create-group"], [data-stamp-type]';
let initialized = false;
const previousDisabled = new WeakMap();
export function refreshMobileReadOnly() {
    const mobile = isMobileDevice();
    document.documentElement.classList.toggle("mobile-read-only", mobile && !document.querySelector?.('main[data-page$="/recyclePage.js"]'));
    document.querySelectorAll('#form-dialog-area input, #form-dialog-area select, #form-dialog-area textarea, .normal-table input:not([type="checkbox"]), .normal-table select, .normal-table textarea, [name="editStartTime"], [name="editEndTime"], [name="endNextDay"]').forEach(el => {
        if (mobile && !isRecycleScope(el)) {
            if (!previousDisabled.has(el)) previousDisabled.set(el, el.disabled);
            el.disabled = true;
        } else if (previousDisabled.has(el)) {
            el.disabled = previousDisabled.get(el);
            previousDisabled.delete(el);
        }
    });
}
export function initMobileReadOnly() {
    if (initialized) return;
    initialized = true;
    window.matchMedia(mobileQuery).addEventListener("change", () => {
        refreshMobileReadOnly();
        document.querySelectorAll("#form-dialog-area form").forEach(form =>
            form.dispatchEvent(new Event("input", {bubbles: true})));
    });
    new MutationObserver(refreshMobileReadOnly).observe(document.body, {childList: true, subtree: true});
    document.addEventListener("click", event => {
        if (!isMobileReadOnly(event.target)) return;
        if (event.target.closest(writeSelector) || event.target.closest('#form-dialog-area [name="submitBtn"]')) {
            event.preventDefault(); event.stopImmediatePropagation();
        }
    }, true);
    document.addEventListener("drop", event => {
        if (isMobileReadOnly(event.target) && event.target.closest("main")) {
            event.preventDefault(); event.stopImmediatePropagation();
        }
    }, true);
    refreshMobileReadOnly();
}
