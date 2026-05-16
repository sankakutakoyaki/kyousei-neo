"use strict";

import {FileUploader} from "./fileUploader.js";
import {FileViewer} from "./fileViewer.js";
import { FileService } from "../../common/file/fileService.js";

export const FileRenderer = {

    renderGroups(container, groups, config, manager){

        groups.forEach(group=>{

            const groupDiv =
                this.createGroup(
                    group,
                    config,
                    manager
                );

            container.appendChild(groupDiv);

        });

    },

    createGroup(group, config, manager){

        const div =
            document.createElement("div");

        div.className="file-group";
        div.dataset.groupId = group.groupId;

        const title =
            document.createElement("div");

        title.className="file-group-title";
        title.textContent=group.groupName;

        div.appendChild(title);

        FileUploader.attachDrop(
            div,
            config,
            manager,
            group.groupId
        );

        const ul =
            document.createElement("ul");

        group.files.forEach((file,i)=>{

            const li =
                this.createFileRow(
                    file,
                    group.files,
                    i,
                    config,
                    manager
                );

            ul.appendChild(li);

        });

        div.appendChild(ul);

        return div;

    },

    createFileRow(file,list,index,config,manager){

        const li = document.createElement("li");

        li.className="file-row";

        li.onclick=()=>{
            FileViewer.open(
                list,
                index,
                config                
            );
        };

        const name = document.createElement("span");
        name.className = "file-name";
        name.textContent=file.displayName;

        li.appendChild(name);

        const deleteBtn = document.createElement("button");

        deleteBtn.className = "delete-btn";

        const img = document.createElement("img");
        img.src = "/icons/dust.png";
        img.title = "削除";
        img.alt = "削除";
        img.className = "delete-icon";

        deleteBtn.appendChild(img);

        deleteBtn.onclick= async (e) => {

            e.stopPropagation();
            await FileService.delete(config, file.fileId);
            if(config.afterRender){
                config.afterRender();
            }
        };

        li.appendChild(deleteBtn);

        return li;

    }

};