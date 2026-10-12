"use strict"

import { handleEnterValidate } from "./actions/enterAction.js";
import { domActions } from "./actions/domActions.js";
import { resolveController } from "./controllerResolver.js";
import { DialogService } from "../ui/dialog/DialogService.js";
import { formatters } from "../behavior/formatters.js";

let initialized = false;

export function initEvents(){
    if(initialized) return;
    initialized = true;
    document.addEventListener("click", handleEvent);
    document.addEventListener("input", handleEvent);
    document.addEventListener("change", handleEvent);
    document.addEventListener("resolved", handleEvent);
    document.addEventListener("focusin", handleFocus);
    document.addEventListener("keydown", handleEvent);
    document.addEventListener("keydown", handleEnterValidate);
}

function handleEvent(e){
    // 入力時の共通フォーマット
    if(e.type === "input"){
        handleInputFormat(e);
    }

    const el = e.target.closest("[data-action]");
    if(!el) return;
    if(el.disabled || el.classList?.contains("disabled") || el.getAttribute?.("aria-disabled") === "true") return;
    const controller = resolveController(el);
    if(controller?.isEnabled?.(el.dataset.action, el) === false) return;

    const actions = el.dataset.action?.split(/\s+/).filter(Boolean) ?? [];
    // select-on-focus は focusin 専用なのでここでは処理しない
    const controllerActions = actions.filter(
        action => action !== "select-on-focus"
    );

    if(controllerActions.length === 0){
        return;
    }

    if(e.type === "keydown" && (e.isComposing || e.repeat)) return;
    if(e.type === "keydown" && e.key === "Enter" && el.dataset.enterSubmit === "true"){
        e.preventDefault();
        el.closest("form")?.querySelector('[name="submitBtn"]')?.click();
        return;
    }
    controllerActions.forEach(action => {
        if(!getActionEvents(el, action).includes(e.type)) return;
        if(e.type === "keydown" && el.dataset.key && e.key !== el.dataset.key) return;
        if(domActions[action]){
            domActions[action](e, el);
            return;
        }
        if(!controller) return;
        Promise.resolve(controller.executeAction(action, el)).catch(error => {
            handleValidationError(error);
        });
    });
}

const defaultActionEvents = {
    BUTTON: ["click"], A: ["click"], DIV: ["click"], SPAN: ["click"],
    INPUT: ["change"], SELECT: ["change"], TEXTAREA: ["change"]
};

export function getActionEvents(el, action){
    const custom = el.dataset.event?.trim();
    if(custom){
        if(!custom.includes(":")) return custom.split(",").map(value => value.trim()).filter(Boolean);
        const entry = custom.split(/\s+/).find(value => value.startsWith(`${action}:`));
        if(entry) return entry.slice(action.length + 1).split(",").filter(Boolean);
    }
    return defaultActionEvents[el.tagName] ?? ["click"];
}

function handleFocus(e){
    const el = e.target.closest("[data-action]");
    if (!el || !el.dataset.action.split(/\s+/).includes("select-on-focus")) return;

    if(el.tagName !== "INPUT" && el.tagName !== "TEXTAREA"){
        return;
    }
    if(el.tagName === "INPUT" || el.tagName === "TEXTAREA") el.select();
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

function handleInputFormat(e){
    const el = e.target;
    if(!(el instanceof HTMLInputElement)){
        return;
    }

    const type = el.dataset.format;
    if(!type){
        return;
    }

    const formatter = formatters[type];
    if(!formatter){
        return;
    }

    const value = el.value;
    const formatted = formatter(value);
    if(value !== formatted){
        el.value = formatted;
    }
}