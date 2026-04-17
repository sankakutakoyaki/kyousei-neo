"use strict"

// import { runAction } from "../../util/utils.js";
import { dispatchAction } from "../../util/actionDispatcher.js";

export function initInput(){
    document.addEventListener("input", dispatchAction);
    document.addEventListener("search", dispatchAction);
}

export function createInputComponent(controller){

    function getInputs(){
        return document.querySelectorAll(
            `[data-controller="${controller.key}"] input`
        );
    }

    function clear(){
        getInputs().forEach(el => el.value = "");
    }

    return {
        clear
    };
}