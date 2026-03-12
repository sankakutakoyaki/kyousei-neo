"use strict";

import {FileService} from "../../common/core/file/fileService.js";
import {FileRenderer} from "./fileRenderer.js";

export class FileManager {

    // constructor(config){

    //     this.config = config;

    //     this.container = document.getElementById(config.listId);

    // }
    constructor({
        parentType,
        parentId,
        groupId,
        listId
    }) {
        this.config = config;

        this.parentType = parentType;
        this.parentId   = parentId;
        this.groupId = groupId;

        this.container = document.getElementById(listId);
    }

    async load(){

        const parentId = this.config.groupValue();

        if(!parentId) return;

        const files =
            await FileService.fetchFiles(
                this.config,
                parentId
            );

        this.render(files);

    }

    async upload(files){

        const groupId = this.config.groupValue();

        await FileService.upload(
            files,
            this.config,
            groupId
        );

        await this.load();

    }

    render(files){

        this.container.innerHTML = "";

        if(!files || files.length===0){
            this.container.innerHTML="";
            return;
        }

        FileRenderer.renderGroups(
            this.container,
            files,
            this.config,
            this
        );
    }
}