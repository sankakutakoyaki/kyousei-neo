"use strict"

/******************************************************************************************************* 初期化時 */

window.addEventListener("load", async () => {

    document.getElementById("load-btn").addEventListener("click", () => loadFiles(FILE_CONFIG));
    document.getElementById("create-group-btn").addEventListener("click", () => createGroup(FILE_CONFIG.parentType, FILE_CONFIG.parentId, FILE_CONFIG.groupName));
        
    const fileInput = document.getElementById('file-input');
    const fileList =  document.getElementById('file-list');
    setDragAndDrop(fileList, fileInput, FILE_CONFIG);

    document.getElementById("viewer-next").onclick = FileViewer.next;
    document.getElementById("viewer-prev").onclick = FileViewer.prev;
    document.getElementById("viewer-close").onclick = FileViewer.close;
});