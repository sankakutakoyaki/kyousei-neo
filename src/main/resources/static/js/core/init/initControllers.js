"use strict"

import { controllerFactory } from "../factory/controllerFactory.js";

let controllerMap = {};

export function initControllers(){

    const elements = document.querySelectorAll("[data-controller]");
    controllerMap = {};

    elements.forEach(el => {
        const name = el.dataset.controller;
        const factory = controllerFactory[name];
        if(!factory){
            console.warn("controller not found:", name);
            return;
        }
        controllerMap[name] = factory();
    });
}

export function getController(target){
    return controllerMap[target];
}