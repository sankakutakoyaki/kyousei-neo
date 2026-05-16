"use strict"

export function smartFilterHandler(controller, el){
    const key = el.dataset.field || el.dataset.key;
    const type = el.dataset.type;
    const value = el.value;

    // ★ filters初期化
    if(!controller.state.filters){
        controller.state.filters = {};
    }

    // 複合条件（from / to）
    if(type){
        const current = controller.state.filters[key] || {};

        controller.state.filters[key] = {
            ...current,
            [type]: value
        };
    }
    // 単一条件
    else {
        controller.state.filters[key] = value;
    }

    controller.dataTable.reload();
}