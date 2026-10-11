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
    document.addEventListener("focusin", handleFocus);
    document.addEventListener("keydown", handleEvent);
    document.addEventListener("resolved", handleEvent);
    document.addEventListener("keydown", handleEnterValidate);
}

const defaultActionEvents = {
    BUTTON: ["click"],
    SELECT: ["change"],
    INPUT: ["change"]
};

function getActionEvents(el, action) {
    const custom = el.dataset.event;
    if(custom){
        const map = Object.fromEntries(
            custom.split(/\s+/).filter(value => value.includes(":")).map(value => value.split(":"))
        );
        if(map[action]){
            return map[action].split(",");
        }
        if(!custom.includes(":")){
            return custom.split(",").map(value => value.trim());
        }
    }
    const events = [...(defaultActionEvents[el.tagName] ?? [])];
    if(el.dataset.enterSubmit === "true" && !events.includes("keydown")){
        events.push("keydown");
    }
    return events;
}

function handleEvent(e){
    // 入力時の共通フォーマット
    if(e.type === "input"){
        handleInputFormat(e);
    }
    const el = e.target.closest("[data-action]");
    if(!el) return;
    const actions = el.dataset.action?.split(/\s+/).filter(Boolean) ?? [];
    // select-on-focus は focusin 専用なのでここでは処理しない
    const controllerActions = actions.filter(
        action => action !== "select-on-focus"
    );
    if(controllerActions.length === 0){return;}
    const controller = resolveController(el);
    controllerActions.forEach(action => {
        const allowedEvents = getActionEvents(el, action);
        if(!allowedEvents.includes(e.type)){return;}

        if(e.type === "keydown"){
            const key = el.dataset.key;
            if(key && e.key !== key){return;}
            if(el.dataset.enterSubmit === "true"){
                if(e.key !== "Enter"){return;}
                e.preventDefault();
                const form = el.closest("form");
                const submitBtn = form?.querySelector('[name="submitBtn"]');
                submitBtn?.click();
                return;
            }
        }
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
    const el = e.target.closest("[data-action]");
    if(!el) return;

    const actions = el.dataset.action?.split(/\s+/) ?? [];
    if(!actions.includes("select-on-focus")) return;
    if(el.tagName !== "INPUT" && el.tagName !== "TEXTAREA") return;

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
        requestAnimationFrame(() => {document.getElementById(error.fields[0])?.focus();});
    }
    DialogService.error(error.message);
}

function handleInputFormat(e){
    const el = e.target;
    if(!(el instanceof HTMLInputElement)){return;}
    const type = el.dataset.format;
    if(!type){return;}
    const formatter = formatters[type];
    if(!formatter){return;}
    const value = el.value;
    const formatted = formatter(value);
    if(value !== formatted){
        el.value = formatted;
    }
}