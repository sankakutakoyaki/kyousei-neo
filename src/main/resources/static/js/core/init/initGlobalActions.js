"use strict"

import { dispatchAction } from "../../util/actionDispatcher.js";

export function initGlobalActions(){
    document.addEventListener("click", dispatchAction);
    document.addEventListener("focusin", dispatchAction);
}