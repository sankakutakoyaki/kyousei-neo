"use strict";

export const FileStore = {

    files: [],

    set(files){
        this.files = files;
    },

    get(){
        return this.files;
    },

    clear(){
        this.files = [];
    }

};