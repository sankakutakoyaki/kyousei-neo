"use strict";

export const FileGroup = {

    groupFiles(files) {

        const map = {};

        files.forEach(file => {

            const gid =
                file.groupId || 0;

            if (!map[gid]) {

                map[gid] = {
                    groupId: gid,
                    groupName: file.groupName || "未分類",
                    files: []
                };

            }

            map[gid].files.push(file);

        });

        return Object.values(map);
    }

};