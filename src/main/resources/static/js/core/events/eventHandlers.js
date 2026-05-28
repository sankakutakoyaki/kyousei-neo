"use strict"

import { handleEnterValidate } from "./actions/enterAction.js";
import { domActions } from "./actions/domActions.js";
import { resolveController } from "./controllerResolver.js";
import { DialogService } from "../ui/dialog/DialogService.js";

let initialized = false;

export function initEvents(){
    if(initialized) return;
    initialized = true;
    document.addEventListener("click", handleEvent);
    document.addEventListener("input", handleEvent);
    document.addEventListener("change", handleEvent);
    document.addEventListener("focusin", handleFocus);
    document.addEventListener("keydown", handleEvent);
    document.addEventListener("keydown", handleEnterValidate);
}

function handleEvent(e){
    const el = e.target.closest("[data-action]");
    if(!el) return;

    const controller = resolveController(el);
    const actions = el.dataset.action?.split(/\s+/).filter(Boolean) ?? [];
    const keydownActions = [
        "date-arrow",
        "search"
    ];

    actions.forEach(action => {
        // keydown制限
        if(e.type === "keydown"){
            if(e.key !== "Enter") return;
            // Enter submit
            if(el.dataset.enterSubmit === "true"){
                e.preventDefault();
                const form = el.closest("form");
                const submitBtn = form?.querySelector('[name="submitBtn"]');
                submitBtn?.click();
                return;
            }
            // save は click のみ
            if(action === "save") return;
        }
        // DOM actions
        if(domActions[action]){
            domActions[action](e, el);
            return;
        }
        if(!controller){
            console.warn("controller not found", el);
            return;
        }
        controller.executeAction(action, el);
    });
}

function handleFocus(e){
    const el = e.target.closest("[data-action='select-on-focus']");
    if (!el) return;

    if(el.tagName !== "INPUT" && el.tagName !== "TEXTAREA"){
        return;
    }
    el.select();
}

export function handleValidationError(error){
    // 一旦全部解除
    document.querySelectorAll(".error").forEach(el => {
        el.classList.remove("error");
    });

    // エラー項目へ付与
    error.fields?.forEach(id => {
        document.getElementById(id)?.classList.add("error");
    });

    // 最初へfocus
    if(error.fields?.length){
        requestAnimationFrame(() => {
            document.getElementById(error.fields[0])?.focus();
        });
    }
    DialogService.error(error.message);
}