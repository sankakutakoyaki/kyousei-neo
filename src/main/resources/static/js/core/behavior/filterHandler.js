"use strict"

// export function smartFilterHandler(controller, el){

//     const key = el.dataset.key;
//     const type = el.dataset.type;
//     const value = el.value;

//     if(type){
//         const current = controller.dataTable.model.state[key] || {};

//         controller.dataTable.set(key, {
//             ...current,
//             [type]: value
//         });
//         return;
//     }

//     // controller.state[key] = value;

//     // controller.dataTable.set(key, value);
//     controller.state[key] = value;
//     controller.dataTable.reload();
// }

// export function smartFilterHandler(controller, el){

//     const key = el.dataset.field || el.dataset.key;
//     const type = el.dataset.type;
//     const value = el.value;

//     // 複合条件（例：from/to）
//     if(type){
//         const current = controller.state[key] || {};

//         controller.state[key] = {
//             ...current,
//             [type]: value
//         };
//     }
//     else {
//         controller.state[key] = value;
//     }

//     // ★ 再描画
//     controller.dataTable.reload();
// }

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