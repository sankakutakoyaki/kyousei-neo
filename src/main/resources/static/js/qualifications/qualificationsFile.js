"use strict"

import { FileManager } from "/js/file/FileManager.js";
import { FileUploader } from "/js/file/FileUploader.js";

let currentCfg;
let manager;
let qualificationsList;

/******************************************************************************************************* 表示 */

export async function execOpen() {

    const cfg = currentCfg;
    await getQualificationsList(cfg);

    const area = document.getElementById(cfg.groupArea);
    const list = qualificationsList.data.map(v => ({number: v.qualificationsId, text: v.number ?? "-----", data: v.files[0]?.groupId}));
    createComboBox(area, list);

    refreshFileList(cfg);

    if (!area.dataset.binded) {
        area.addEventListener("change", async () => {
            refreshFileList();
        });
        area.dataset.binded = "1";
    }
}

export function refreshFileList() {

    const list = qualificationsList.data
            .filter(v => { return v.qualificationsId === Number(currentCfg.parentValue()) })    
            .map(v => ({
                groupId: v.files[0]?.groupId,
                groupName: v.qualificationName,
                files: v.files
            }));

    manager.render(list);
}

/******************************************************************************************************* 取得 */

export async function getQualificationsList(cfg) {

    const codeValue = cfg.codeValue();
    if (!codeValue || codeValue === "") return;

    const masterValue = cfg.masterValue();
    if (!masterValue || masterValue === "") return;

    const response = await fetch(`${cfg.getParentUrl}/${masterValue}/${codeValue}`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": token
            }
        }
    );

    qualificationsList = await response.json();
}

export function setFileConfig(cate, CONFIG){

    const cfg = CONFIG.FILE[cate];

    currentCfg = cfg;
    manager = new FileManager(cfg);

    const dropArea = document.getElementById("drop-area");

    if(dropArea){
        FileUploader.attachDrop(
            dropArea,
            manager
        );
    }

    const fileInput = document.getElementById("file-input");

    if(fileInput){
        FileUploader.attachInput(
            fileInput,
            manager
        );
    }
}