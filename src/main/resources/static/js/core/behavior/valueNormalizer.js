"use strict"

export function normalizeValue(v, opt = {}){

    // trim
    if(typeof v === "string" && opt.trim !== false){
        v = v.trim();
    }

    // 空 → null
    if(v === ""){
        v = null;
    }

    // number
    if(opt.number && v !== null){
        v = Number(v);
    }

    // zeroToNull
    if(opt.zeroToNull && (v === 0 || v === "0")){
        v = null;
    }

    return v;
}

export function getOptions(el){
    return {
        trim: !("noTrim" in el.dataset),
        number: "number" in el.dataset,
        zeroToNull: "zeroToNull" in el.dataset
    };
}

export function normalize(v){
    return v === "" || v === undefined ? null : v;
}

export function normalizeParentChild(form){

    let updated;

    do {
        updated = false;

        const children = form.querySelectorAll("[data-parent-name]");

        children.forEach(child => {

            const parentName = child.dataset.parentName;
            const parent = form.querySelector(`[name="${parentName}"]`);
            if (!parent) return;

            const selected = child.selectedOptions[0];
            if (!selected) return;

            const parentValue = selected.dataset.parent;
            if (!parentValue) return;

            if (!parent.value) {
                parent.value = parentValue;
                parent.dispatchEvent(new Event("change"));
                updated = true;
            }
        });

    } while (updated);
}

// export function normalizeParentChild(form){

//     const child = form.querySelector("[data-parent-target]");
//     if (!child) return;

//     const parentId = child.dataset.parentTarget;
//     const parent = form.querySelector(`#${parentId}`);
//     if (!parent) return;

//     const selected = child.selectedOptions[0];
//     if (!selected) return;

//     const parentValue = selected.dataset.parent;

//     // 親が空なら復元
//     if (!parent.value && parentValue) {
//         parent.value = parentValue;
//         parent.dispatchEvent(new Event("change"));
//     }
// }