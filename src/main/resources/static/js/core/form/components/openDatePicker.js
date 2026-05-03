"use strict"

export function openDatePicker(input){

    const picker = document.createElement("input");
    picker.type = "date";
    picker.style.position = "absolute";
    picker.style.opacity = 0;

    document.body.appendChild(picker);

    // 今の値をセット
    if(input.value){
        picker.value = input.value;
    }

    picker.addEventListener("change", () => {
        input.value = picker.value;
        picker.remove();
    });

    picker.addEventListener("blur", () => {
        picker.remove();
    });

    picker.focus();
}