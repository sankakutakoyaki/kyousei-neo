"use strict"

const controllers = new Map();

export function registerController(name, controller){
    const previous = controllers.get(name);
    if(previous !== controller) previous?.dispose?.();
    controllers.set(name, controller);
}

export function getController(name){
    return controllers.get(name);
}
export function clearControllers(){
    controllers.forEach(controller => controller.dispose?.());
    controllers.clear();
}
