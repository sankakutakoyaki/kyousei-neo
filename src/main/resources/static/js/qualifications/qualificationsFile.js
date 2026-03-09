"use strict"

import { FileManager } from "/js/file/FileManager.js";
import { FileUploader } from "/js/file/FileUploader.js";

let currentCfg = null;
let manager = null;
let qualificationsList = [];

/**
 * 初期化
 */
export function initFile(CONFIG) {

    currentCfg = CONFIG;
    manager = new FileManager(CONFIG);

    const dropArea = document.getElementById("drop-area");
    if(dropArea){
        FileUploader.attachDrop(dropArea, manager);
    }

    const fileInput = document.getElementById("file-input");
    if(fileInput){
        FileUploader.attachInput(fileInput, manager);
    }
}

/**
 *  表示
 */
export async function execOpen() {

    if (!currentCfg) return;

    await loadQualificationsList();

    const area = document.getElementById(currentCfg.groupArea);
    const list = qualificationsList.data.map(v => (
        {number: v.qualificationsId, text: v.number ?? "-----", data: v.files[0]?.groupId}));
    createComboBox(area, list);

    refreshFileList(currentCfg);

    if (!area.dataset.binded) {
        area.addEventListener("change", async () => {
            refreshFileList();
        });
        area.dataset.binded = "1";
    }
}

/**
 * ファイル一覧更新
 */
export function refreshFileList() {

    if (!currentCfg) return;

    const parentId = Number(currentCfg.parentValue());

    const list = qualificationsList.data
            .filter(v => { return v.qualificationsId === parentId })    
            .map(v => ({
                groupId: v.files[0]?.groupId,
                groupName: v.qualificationName,
                files: v.files
            }));

    manager.render(list);
}

/** 
 * 資格リスト取得
 */
export async function loadQualificationsList() {

    if (!currentCfg) return;

    const codeValue = currentCfg.codeValue();
    if (!codeValue || codeValue === "") return;

    const masterValue = currentCfg.masterValue();
    if (!masterValue || masterValue === "") return;

    const response = await fetch(`${currentCfg.getParentUrl}/${masterValue}/${codeValue}`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": token
            }
        }
    );

    qualificationsList = await response.json();

    refreshFileList();
}