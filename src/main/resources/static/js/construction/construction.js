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

// async function uploadFiles(files) {
//     const formData = new FormData();
//     for (let file of files) {
//         formData.append("files", file);
//     }

//     fetch("/api/construction/upload", {   // ← groupId
//         method: "POST",
//         body: formData
//     })
//     .then(res => res.json())
//     .then(data => {
//         console.log("アップロード完了", data);
//         location.reload();
//     });
// }
async function uploadFiles(files) {
    const id = document.getElementById('construction-id');
    if (!id || id.value === "") return;

    const groupId = document.getElementById('group-id');
    
    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
        formData.append('files', files[i]); // name="files" がサーバと一致する必要あり
    }

    // formData.append("fileName", name);
    formData.append("constructionId", Number(id.value));
    formData.append("groupId", Number(groupId.value));
    // formData.append("folder_name", "construction/" + String(id.value) + "/");
    // formData.append("id", parseInt(id.value));

    const response = await fetch("/api/construction/upload", {
        headers: {  // リクエストヘッダを追加
            'X-CSRF-TOKEN': token,
        },
        method: "POST",
        body: formData,
    });

    const result = await response.json();
    return result;
}

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

    loadFiles();
}

async function loadFiles() {

    const constructionId = document.getElementById('construction-id').value;
    if (!constructionId) {
        alert("管理IDを入力してください");
        return;
    }

    const response = await fetch(`/api/construction/${constructionId}/files`);
    const files = await response.json();
    
    const ul = document.getElementById("file-list");
    if (files.length === 0) {
        ul.innerHTML = "";
        return;
    }

    ul.innerHTML = "";

    // ① groupごとにまとめる
    const grouped = {};

    files.forEach(file => {
        if (!grouped[file.groupId]) {
            grouped[file.groupId] = {
                groupName: file.groupName,
                files: []
            };
        }
        grouped[file.groupId].files.push(file);
    });
    
    // ② グループ単位で描画
    Object.values(grouped).forEach(group => {
        const groupContainer = document.createElement("li");
        groupContainer.classList.add("group-container");
        const groupId = group.files[0]?.groupId;
        groupContainer.dataset.groupId = groupId;

        const groupTitle = document.createElement("div");
        groupTitle.classList.add("group-title");

        // --- 三角ボタン（開閉専用） ---
        const toggleBtn = document.createElement("span");
        toggleBtn.classList.add("toggle-btn");
        toggleBtn.textContent = "▼"; // 初期は開いている
        toggleBtn.style.cursor = "pointer";
        toggleBtn.style.marginRight = "5px";

        // --- グループ名テキスト（編集専用） ---
        const groupNameSpan = document.createElement("span");
        groupNameSpan.classList.add("group-name");
        groupNameSpan.textContent = group.groupName;
        groupNameSpan.style.cursor = "text";

        // --- 三角ボタンで折りたたみ ---
        toggleBtn.onclick = () => {
            const isOpen = fileUl.style.display === "block";
            fileUl.style.display = isOpen ? "none" : "block";
            toggleBtn.textContent = isOpen ? "▶" : "▼";
        };

        // --- クリックで編集モードに切替 ---
        groupNameSpan.onclick = () => {
            const input = document.createElement("input");
            input.type = "text";
            input.value = groupNameSpan.textContent;
            input.classList.add("group-name-input");
            input.style.width = "200px";

            groupTitle.replaceChild(input, groupNameSpan);
            input.focus();

            // Enterで確定
            input.addEventListener("keydown", async (e) => {
                if (e.key === "Enter") {
                    await saveGroupName(input.value);
                }
            });

            // フォーカスアウトで確定
            input.addEventListener("blur", async () => {
                await saveGroupName(input.value);
            });

            async function saveGroupName(newName) {
                if (!newName || newName === group.groupName) {
                    groupTitle.replaceChild(groupNameSpan, input);
                    return;
                }

                await fetch(`/api/construction/group/${groupId}/rename`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "X-CSRF-TOKEN": token
                    },
                    body: JSON.stringify({ groupName: newName })
                });

                groupNameSpan.textContent = newName;
                groupTitle.replaceChild(groupNameSpan, input);
            }
        };

        groupTitle.appendChild(toggleBtn);
        groupTitle.appendChild(groupNameSpan);

        // --- ファイルリスト ---
        const fileUl = document.createElement("ul");
        fileUl.style.display = "block";

        // ファイル描画
        group.files.forEach(file => {
            const li = document.createElement("li");
            li.classList.add("file-item");

            const nameSpan = createNameSpan(file.fileId, file.displayName);
            const deleteBtn = createDeleteButton(file.fileId);

            li.appendChild(nameSpan);
            li.appendChild(deleteBtn);
            fileUl.appendChild(li);
        });

        groupContainer.appendChild(groupTitle);
        groupContainer.appendChild(fileUl);

        // return groupContainer;
        ul.appendChild(groupContainer);
    });
}

// function createGroupContainer(ul, grouped) {
//     Object.values(grouped).forEach(group => {
//         const groupContainer = document.createElement("li");
//         groupContainer.classList.add("group-container");
//         groupContainer.dataset.groupId = group.groupId;

//         const groupTitle = document.createElement("div");
//         groupTitle.classList.add("group-title");

//         // --- 三角ボタン（開閉専用） ---
//         const toggleBtn = document.createElement("span");
//         toggleBtn.classList.add("toggle-btn");
//         toggleBtn.textContent = "▼"; // 初期は開いている
//         toggleBtn.style.cursor = "pointer";
//         toggleBtn.style.marginRight = "5px";

//         // --- グループ名テキスト（編集専用） ---
//         const groupNameSpan = document.createElement("span");
//         groupNameSpan.classList.add("group-name");
//         groupNameSpan.textContent = group.groupName;
//         groupNameSpan.style.cursor = "text";

//         // --- 三角ボタンで折りたたみ ---
//         toggleBtn.onclick = () => {
//             const isOpen = fileUl.style.display === "block";
//             fileUl.style.display = isOpen ? "none" : "block";
//             toggleBtn.textContent = isOpen ? "▶" : "▼";
//         };

//         // --- クリックで編集モードに切替 ---
//         groupNameSpan.onclick = () => {
//             const input = document.createElement("input");
//             input.type = "text";
//             input.value = groupNameSpan.textContent;
//             input.classList.add("group-name-input");
//             input.style.width = "200px";

//             groupTitle.replaceChild(input, groupNameSpan);
//             input.focus();

//             // Enterで確定
//             input.addEventListener("keydown", async (e) => {
//                 if (e.key === "Enter") {
//                     await saveGroupName(input.value);
//                 }
//             });

//             // フォーカスアウトで確定
//             input.addEventListener("blur", async () => {
//                 await saveGroupName(input.value);
//             });

//             async function saveGroupName(newName) {
//                 if (!newName || newName === group.groupName) {
//                     groupTitle.replaceChild(groupNameSpan, input);
//                     return;
//                 }

//                 await fetch(`/api/construction/group/${group.groupId}/rename`, {
//                     method: "POST",
//                     headers: {
//                         "Content-Type": "application/json",
//                         "X-CSRF-TOKEN": token
//                     },
//                     body: JSON.stringify({ groupName: newName })
//                 });

//                 groupNameSpan.textContent = newName;
//                 groupTitle.replaceChild(groupNameSpan, input);
//             }
//         };

//         groupTitle.appendChild(toggleBtn);
//         groupTitle.appendChild(groupNameSpan);

//         // --- ファイルリスト ---
//         const fileUl = document.createElement("ul");
//         fileUl.style.display = "block";

//         // ファイル描画
//         group.files.forEach(file => {
//             const li = document.createElement("li");
//             li.classList.add("file-item");

//             const nameSpan = createNameSpan(file.fileId, file.displayName);
//             const deleteBtn = createDeleteButton(file.fileId);

//             li.appendChild(nameSpan);
//             li.appendChild(deleteBtn);
//             fileUl.appendChild(li);
//         });

//         groupContainer.appendChild(groupTitle);
//         groupContainer.appendChild(fileUl);

//         // return groupContainer;
//         ul.appendChild(groupContainer);
//     });
// }

function createDeleteButton(fileId) {
    const deleteBtn = document.createElement("button");
    // deleteBtn.textContent = "削除";
    deleteBtn.innerHTML = '✖️';
    deleteBtn.className = 'remove-btn';
    deleteBtn.onclick = async () => {

        await fetch(`/api/construction/file/${fileId}`, {
            headers: {
                'X-CSRF-TOKEN': token,
            },
            method: "POST"
        });

        loadFiles();
    };

    return deleteBtn;
}

function createNameSpan(fileId, displayName) {
    const nameSpan = document.createElement("span");
    nameSpan.textContent = displayName;
    nameSpan.style.cursor = "pointer";

    nameSpan.onclick = () => {

        const input = document.createElement("input");
        input.type = "text";
        input.value = displayName;

        input.onblur = async () => {

            const newName = input.value.trim();
            if (!newName) {
                loadFiles();
                return;
            }

            await fetch(`/api/construction/file/${fileId}/rename`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": token
                },
                body: JSON.stringify({
                    displayName: newName
                })
            });

            loadFiles();
        };

        nameSpan.replaceWith(input);
        input.focus();
    };

    return nameSpan;
}

async function createGroup() {

    const constructionId =
        document.getElementById("construction-id").value;

    const groupName =
        document.getElementById("group-name").value;

    const response = await fetch(
        `/api/construction/${constructionId}/group`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": token
            },
            body: JSON.stringify({ groupName })
        }
    );

    await response.json();

    loadFiles(); // 再描画
}

// // リストを登録する
// function setListItem(folderName, fileName, internalName) {
//     const path = folderName + "/" + encodeURIComponent(internalName);

//     const li = createListItem("/api/files/qualification/" + path, fileName);

//     const removeBtn = setFileRemoveEventListner(createRemoveBtn(), li, path, '/api/files/delete/qualifications');
//     li.appendChild(removeBtn);

//     fileList.appendChild(li);

//     updatePlaceholder(fileList);
// }

/******************************************************************************************************* 取得 */

// // 保持資格のPDFファイルリストを取得して表示する
// async function getQualificationsFiles(id) {
//     const folderName = id;
//     const result = await postFetch('/api/files/get/qualifications', JSON.stringify({id:id}), token);

//     if (result.data.length > 0) {
//         for (const file of result.data) {
//             // リストに登録する
//             setPdfListItem(folderName, file.file_name, file.internal_name);
//         }
//     }
//     updatePlaceholder(fileList);
// }

/******************************************************************************************************* 初期化時 */

window.addEventListener("load", async () => {

    document.getElementById("load-btn").addEventListener("click", () => {

        // const constructionId =
        //     document.getElementById("construction-id").value;

        // if (!constructionId) {
        //     alert("管理IDを入力してください");
        //     return;
        // }

        loadFiles();
    });

    setDragAndDrop();
    // document.addEventListener('DOMContentLoaded', () => {

    // const lightbox = new PhotoSwipeLightbox({
    //     gallery: '.gallery',
    //     children: 'a'
    // });

    // lightbox.init();
    // });
});