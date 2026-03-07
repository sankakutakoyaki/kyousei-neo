"use strict";

import {FileService} from "./fileService.js";

export const FileActions = {

    async delete(file, manager){

        if(!confirm("削除しますか？"))
            return;

        await FileService.delete(
            file.fileId
        );

        await manager.load();

    }

};