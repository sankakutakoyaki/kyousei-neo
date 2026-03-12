"use strict";

export const FileService = {

    async fetchFiles(config, groupId){

        const res =
            await fetch(
                `/api/files/${config.parentType}/${config.parentValue()}?groupId=${groupId}`
            );

        return await res.json();

    },

    async upload(files, config, groupId){

        const formData =
            new FormData();

        for(const file of files){
            formData.append("files", file);
        }

        if(groupId)
            formData.append("groupId", groupId);

        await fetch(
            `${config.uploadUrl}/${config.parentValue()}`,
            {
                method:"POST",
                body:formData,
                headers: {
                    "X-CSRF-TOKEN": token
                }
            },

        );

    },

    async delete(config, fileId) {

        const res = await fetch(
            `${config.deleteUrl}/${fileId}`,
            {
                method: "POST",
                headers: { "X-CSRF-TOKEN": token }
            }
        );

        return await res.json();
    }

};