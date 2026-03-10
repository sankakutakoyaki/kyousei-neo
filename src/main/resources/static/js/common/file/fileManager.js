"use strict";

import {FileService} from "./fileService.js";
import {FileRenderer} from "./fileRenderer.js";
// import {FileStore} from "./fileStore.js";

export class FileManager {

    constructor(config){

        this.config = config;

        this.container = document.getElementById(config.listId);

    }

    async load(){

        const parentId = this.config.groupValue();

        if(!parentId) return;

        const files =
            await FileService.fetchFiles(
                this.config,
                parentId
            );

        // FileStore.set(files);

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
        // const files = FileStore.get();

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

    // setFiles(files){

    //     FileStore.set(files);
    //     this.render();

    // }

}
// export class FileManager{

//     constructor(config){

//         this.config = config;
//         this.files = [];

//     }

//     async load(){

//         const parentId =
//             this.config.parentValue();

//         if(!parentId) return;

//         this.files =
//             await FileService.fetchFiles(
//                 this.config,
//                 parentId
//             );

//         this.render();
//     }

//     async upload(files){

//         const parentId =
//             this.config.parentValue();

//         await FileService.upload(
//             files,
//             this.config,
//             parentId
//         );

//         await this.load();
//     }

//     render(){

//         const groups =
//             FileGroupManager.groupFiles(this.files);

//         FileRenderer.renderGroups(
//             this.config,
//             groups
//         );

//     }

// }