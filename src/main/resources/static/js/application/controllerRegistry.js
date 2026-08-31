"use strict"

const controllers = new Map();

export function registerController(name, controller){
    controllers.set(name, controller);
}

export function getController(name){
    return controllers.get(name);
}