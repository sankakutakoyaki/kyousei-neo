"use strict"

import {
    openMsgDialog,
    openConfirmDialog,
    closeFormDialog,
    closeMsgDialog,
    setInertState
} from "./dialogCore.js";

export const DialogService = {
    info(message){
        openMsgDialog({
            message,
            color:"blue"
        });
    },

    error(message){
        openMsgDialog({
            message,
            color:"red"
        });
    },

    async confirm(message){
        return new Promise(resolve => {
            openConfirmDialog({
                message,
                onSubmit: () => {
                    closeMsgDialog();
                    resolve(true);
                },
                onClose: () => {
                    closeMsgDialog();
                    resolve(false);
                }
            });
        });
    },

    async prompt(message, value = ""){
        return new Promise(resolve => {
            const area = document.createElement("div");
            area.className = "dialog input-dialog-area";

            const form = document.createElement("form");
            form.className = "form-dialog input-dialog";

            const main = document.createElement("div");
            main.className = "dialog-main";

            const header = document.createElement("header");
            header.className = "dialog-header blue";
            const title = document.createElement("strong");
            title.textContent = "入力";
            header.append(title);

            const content = document.createElement("section");
            content.className = "dialog-content input-dialog-content";
            const label = document.createElement("label");
            label.textContent = message;
            const input = document.createElement("input");
            input.type = "text";
            input.className = "normal-input";
            input.maxLength = 255;
            input.value = value;
            label.append(input);
            content.append(label);

            const footer = document.createElement("footer");
            footer.className = "dialog-footer";
            const cancel = document.createElement("button");
            cancel.type = "button";
            cancel.className = "normal-btn";
            cancel.textContent = "キャンセル";
            const submit = document.createElement("button");
            submit.type = "submit";
            submit.className = "normal-btn ok";
            submit.textContent = "決定";
            footer.append(cancel, submit);

            main.append(header, content, footer);
            form.append(main);
            area.append(form);
            document.body.append(area);
            setInertState(true);

            let settled = false;
            const finish = result => {
                if (settled) return;
                settled = true;
                area.remove();
                setInertState(false);
                resolve(result);
            };
            form.addEventListener("submit", event => {
                event.preventDefault();
                finish(input.value);
            });
            cancel.addEventListener("click", () => finish(null));
            area.addEventListener("click", event => {
                if (event.target === area) finish(null);
            });
            area.addEventListener("keydown", event => {
                if (event.key === "Escape") finish(null);
            });
            requestAnimationFrame(() => {
                input.focus();
                input.select();
            });
        });
    },

    close(formId){
        closeFormDialog(formId);
    }
};
