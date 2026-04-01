"use strict"

import { initCommon } from "../../../core/init/initPage.js";
import { initControllers } from "../../../core/init/initControllers.js";
import { controllerFactory } from "./controllerFactory.js";

let controllers = {};

window.addEventListener("load", () => {

    controllers = initControllers(controllerFactory);
    initCommon();

});