"use strict"

/******************************************************************************************************* 初期化時 */

window.addEventListener("load", async () => {

    document.getElementById("load-btn").addEventListener("click", () => loadFiles(FILE_CONFIG));
        
    const fileInput = document.getElementById('file-input');
    const fileList =  document.getElementById('file-list');
    setDragAndDrop(fileList, fileInput, FILE_CONFIG);

    setFileViewerBtns();
    setViewerKeyboardEvents();
});