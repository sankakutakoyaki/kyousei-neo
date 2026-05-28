"use strict"

export function clearFormExceptSkipped(form){
    if(!form) return;

    // checkboxで指定された保持対象
    const skipTargets = new Set(
        [...form.querySelectorAll("[data-skip-target]")]
            .filter(chk => chk.checked)
            .map(chk => chk.dataset.skipTarget)
    );
    const fields = form.querySelectorAll("input, select, textarea");
    for(const field of fields){
        // checkbox指定skip
        if(skipTargets.has(field.id)){
            continue;
        }
        // field固定skip
        if(field.dataset.clearSkip === "true"){
            continue;
        }
        if(field.type === "checkbox"
            || field.type === "radio"){
            continue;
        }
        if(field.tagName === "SELECT"){
            field.selectedIndex = 0;
            continue;
        }
        if(field.type === "hidden"){
            field.value = "";
            continue;
        }
        field.value = "";
    }
    form.dispatchEvent(
        new Event("input", { bubbles: true })
    );
}