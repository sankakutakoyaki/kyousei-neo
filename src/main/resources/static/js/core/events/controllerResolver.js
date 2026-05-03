"use strict"

import { getController } from "../../controllers/controllers.js";

export function resolveController(el){
    const name =
        el.dataset.controller ||
        el.closest("[data-controller]")?.dataset.controller;
    if(!name){
        // console.warn("controller not found", el);
        return null;
    }
    return getController(name);
}