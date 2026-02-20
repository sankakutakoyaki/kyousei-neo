"use strict"

// const dropZone = document.getElementById("dropZone");
// const fileInput = document.getElementById("fileInput");

// dropZone.addEventListener("click", () => fileInput.click());

// dropZone.addEventListener("dragover", e => {
//   e.preventDefault();
//   dropZone.classList.add("dragover");
// });

// dropZone.addEventListener("dragleave", () => {
//   dropZone.classList.remove("dragover");
// });

// dropZone.addEventListener("drop", e => {
//   e.preventDefault();
//   dropZone.classList.remove("dragover");

//   uploadFiles(e.dataTransfer.files);
// });

// fileInput.addEventListener("change", () => {
//   uploadFiles(fileInput.files);
// });

// function uploadFiles(files) {
//   const formData = new FormData();
//   for (let file of files) {
//     formData.append("files", file);
//   }

//   fetch("/api/files/upload/1", {   // ← groupId
//     method: "POST",
//     body: formData
//   })
//   .then(res => res.json())
//   .then(data => {
//     console.log("アップロード完了", data);
//     location.reload();
//   });
// }
/******************************************************************************************************* アップロード */

async function uploadFiles(files) {console.log(files)
    const formData = new FormData();
    for (let file of files) {
        formData.append("files", file);
    }

    const response = await updateFetch("/api/construction/upload", formData, token);
    const result = await response.json();

    // fetch("/api/construction/upload/1", {   // ← groupId
    //     method: "POST",
    //     body: formData
    // })
    // .then(res => res.json())
    // .then(data => {
    //     console.log("アップロード完了", data);
    //     location.reload();
    // });
}
// async function uploadFile(files) {
//     // const id = document.getElementById('qualifications-id-02');
//     const formData = new FormData();
//     for (let i = 0; i < files.length; i++) {
//         formData.append('files', files[i]); // name="files" がサーバと一致する必要あり
//     }
//     formData.append("folder_name", "qualification/" + String(id.value) + "/");
//     formData.append("id", parseInt(id.value));

//     const response = await fetch("/api/files/upload/", {
//         headers: {  // リクエストヘッダを追加
//             'X-CSRF-TOKEN': token,
//         },
//         method: "POST",
//         body: formData,
//     });

//     const result = await response.json();
//     return result;
// }

function setDragAndDrop() {
    fileInput.addEventListener('change', () => {
        handleFiles(fileInput.files);
    });

    fileList.addEventListener('dragover', (e) => {
        e.preventDefault();
        fileList.classList.add('dragover');
    });

    fileList.addEventListener('dragleave', () => {
        fileList.classList.remove('dragover');
    });

    fileList.addEventListener('drop', (e) => {
        e.preventDefault();
        fileList.classList.remove('dragover');
        handleFiles(e.dataTransfer.files);
    });
}

async function handleFiles(files) {

    const result = await uploadFiles(files);
    if (result.length > 0) {
        fileList.innerHTML = "";
        for (const file of result) {
            // リストに登録する
            setListItem(id.value, file.file_name, file.internal_name);
        }
    }
    updatePlaceholder(fileList);
}

// リストを登録する
function setListItem(folderName, fileName, internalName) {
    const path = folderName + "/" + encodeURIComponent(internalName);

    const li = createListItem("/api/files/qualification/" + path, fileName);

    const removeBtn = setFileRemoveEventListner(createRemoveBtn(), li, path, '/api/files/delete/qualifications');
    li.appendChild(removeBtn);

    fileList.appendChild(li);

    updatePlaceholder(fileList);
}

/******************************************************************************************************* 取得 */

// 保持資格のPDFファイルリストを取得して表示する
async function getQualificationsFiles(id) {
    const folderName = id;
    const result = await postFetch('/api/files/get/qualifications', JSON.stringify({id:id}), token);

    if (result.data.length > 0) {
        for (const file of result.data) {
            // リストに登録する
            setPdfListItem(folderName, file.file_name, file.internal_name);
        }
    }
    updatePlaceholder(fileList);
}

/******************************************************************************************************* 初期化時 */

window.addEventListener("load", async () => {

  setDragAndDrop();
    // document.addEventListener('DOMContentLoaded', () => {

    // const lightbox = new PhotoSwipeLightbox({
    //     gallery: '.gallery',
    //     children: 'a'
    // });

    // lightbox.init();
    // });
});