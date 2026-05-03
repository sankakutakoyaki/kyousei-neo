"use strict"

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