"use strict"

export function smartFilterHandler(controller, el){

    const key = el.dataset.key;
    const type = el.dataset.type;
    const value = el.value;

    if(type){
        const current = controller.dataTable.model.state[key] || {};

        controller.dataTable.set(key, {
            ...current,
            [type]: value
        });
        return;
    }

    // controller.state[key] = value;

    // controller.dataTable.set(key, value);
    controller.state[key] = value;
    controller.dataTable.reload();
}