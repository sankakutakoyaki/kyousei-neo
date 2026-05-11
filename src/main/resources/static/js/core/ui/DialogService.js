"use strict"

import {
    openMsgDialog,
    openConfirmDialog,
    closeFormDialog
} from "./dialog.js";

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
                onSubmit: () => resolve(true),
                onCancel: () => resolve(false)
            });
        });
    },

    close(formId){
        closeFormDialog(formId);
    }
};