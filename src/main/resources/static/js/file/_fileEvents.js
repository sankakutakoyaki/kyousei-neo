"use strict";

export const FileEvents = {

    attachGroupEvents(
        groupDiv,
        titleDiv,
        group,
        config,
        component
    ){

        groupDiv.addEventListener(
            "dragover",
            e=>{
                e.preventDefault();
            }
        );

        groupDiv.addEventListener(
            "drop",
            async e=>{

                e.preventDefault();

                await FileService.uploadFiles(
                    e.dataTransfer.files,
                    config,
                    group.groupId
                );

                component.render();

            }
        );

    }

};