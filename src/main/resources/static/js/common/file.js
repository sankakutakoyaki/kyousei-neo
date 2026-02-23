"use strict"

const FileViewer = (() => {

    let files = [];
    let index = 0;

    async function open(parentType, parentId, clickedIndex) {

        const res = await fetch(
            `/api/files?parentType=${parentType}&parentId=${parentId}`
        );

        files = await res.json();
        index = clickedIndex;

        // document.getElementById("file-viewer")
        //         .classList.remove("hidden");
        openFormDialog("form-fileviewer");

        render();
    }

    function render() {

        const file = files[index];
        const body = document.getElementById("viewer-body");
        body.innerHTML = "";

        const url = `/files/${file.internalName}`;

        if (file.contentType?.startsWith("image/")) {

            const img = document.createElement("img");
            img.src = url;
            img.style.maxWidth = "90vw";
            img.style.maxHeight = "90vh";
            body.appendChild(img);

        } else if (file.contentType === "application/pdf") {

            const iframe = document.createElement("iframe");
            iframe.src = url;
            iframe.style.width = "90vw";
            iframe.style.height = "90vh";
            body.appendChild(iframe);

        } else {

            body.innerHTML = `
                <p>この形式はプレビュー非対応です</p>
                <a href="${url}" download>ダウンロード</a>
            `;
        }
    }

    function next() {
        if (index < files.length - 1) {
            index++;
            render();
        }
    }

    function prev() {
        if (index > 0) {
            index--;
            render();
        }
    }

    function close() {
        document.getElementById("file-viewer")
                .classList.add("hidden");
    }

    return { open, next, prev, close };
})();

async function loadFiles(config) {

    const ul = document.getElementById(config.listId);

    const entityId = document.getElementById(config.parentId).value;

    if (!entityId) {
        alert("IDを入力してください");
        return;
    }

    const response = await fetch(`${config.selectUrl}/${entityId}`);
    const files = await response.json();

    if (!files || files.length === 0) {
        ul.innerHTML = "";
        return;
    }

    ul.innerHTML = "";

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

    Object.values(grouped).forEach(group => {

        const groupContainer = document.createElement("li");
        groupContainer.classList.add("group-container");

        const groupId = group.files[0]?.groupId;
        groupContainer.dataset.groupId = groupId;

        const groupTitle = document.createElement("div");
        groupTitle.classList.add("group-title");

        const toggleBtn = document.createElement("span");
        toggleBtn.textContent = "▼";
        toggleBtn.style.cursor = "pointer";
        toggleBtn.style.marginRight = "5px";

        const groupNameSpan = document.createElement("span");
        groupNameSpan.textContent = group.groupName;

        const fileUl = document.createElement("ul");
        fileUl.style.display = "block";

        toggleBtn.onclick = () => {
            const isOpen = fileUl.style.display === "block";
            fileUl.style.display = isOpen ? "none" : "block";
            toggleBtn.textContent = isOpen ? "▶" : "▼";
        };

        // ===== グループ名編集 =====
        groupNameSpan.onclick = () => {
            const input = document.createElement("input");
            input.type = "text";
            input.value = group.groupName;

            groupTitle.replaceChild(input, groupNameSpan);
            input.focus();

            async function save(newName) {

                if (!newName || newName === group.groupName) {
                    groupTitle.replaceChild(groupNameSpan, input);
                    return;
                }

                await fetch(
                    `${config.renameUrl}/${groupId}`,
                    {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            "X-CSRF-TOKEN": config.csrfToken
                        },
                        body: JSON.stringify({
                            groupName: newName
                        })
                    }
                );

                groupNameSpan.textContent = newName;
                groupTitle.replaceChild(groupNameSpan, input);
            }

            input.addEventListener("keydown", e => {
                if (e.key === "Enter") save(input.value);
            });

            input.addEventListener("blur", () => {
                save(input.value);
            });
        };

        // ===== ファイル描画 =====
        group.files.forEach((file, i) => {

            const li = document.createElement("li");
            li.classList.add('file-item');
console.log(file)
            li.onclick = () => {
                FileViewer.open(file.parentType, file.parentId, i);
            };
                    const nameSpan =
                createNameSpan(file, config);

            const deleteBtn =
                createDeleteButton(file, config);

            li.appendChild(nameSpan);
            li.appendChild(deleteBtn);

            fileUl.appendChild(li);
        });

        groupTitle.appendChild(toggleBtn);
        groupTitle.appendChild(groupNameSpan);

        groupContainer.appendChild(groupTitle);
        groupContainer.appendChild(fileUl);

        ul.appendChild(groupContainer);
    });
}

function createDeleteButton(file, config) {
    const deleteBtn = document.createElement("button");
    deleteBtn.innerHTML = '✖️';
    deleteBtn.className = 'remove-btn';
    deleteBtn.onclick = async () => {

        await fetch(`/api/files/file/delete/${file.fileId}`, {
            headers: {
                'X-CSRF-TOKEN': config.csrfToken,
            },
            method: "POST"
        });

        loadFiles(config);
    };

    return deleteBtn;
}

function createNameSpan(file, config) {

    const nameSpan = document.createElement("span");
    nameSpan.textContent = file.displayName;
    nameSpan.style.cursor = "pointer";

    nameSpan.onclick = () => {

        const input = document.createElement("input");
        input.type = "text";
        input.value = file.displayName;
        input.classList.add("file-name-input");

        let committed = false; // 二重実行防止

        async function commit() {

            if (committed) return;
            committed = true;

            const newName = input.value.trim();

            if (!newName || newName === file.displayName) {
                updateFileDisplay(config);
                return;
            }

            await fetch(`/api/files/file/rename/${file.fileId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": config.csrfToken
                },
                body: JSON.stringify({
                    displayName: newName
                })
            });

            updateFileDisplay(config);
        }

        // Enterで確定
        input.addEventListener("keydown", (e) => {
            if (e.key === "Enter") {
                commit();
            }
            if (e.key === "Escape") {
                committed = true;
                updateFileDisplay(config);
            }
        });

        // フォーカスアウトで確定
        input.addEventListener("blur", commit);

        nameSpan.replaceWith(input);
        input.focus();
        input.select();
    };

    return nameSpan;
}

async function createGroup(config) {
    const parentType = document.getElementById(config.parentType)?.value;
    if (!parentType || parentType ==="") return;
    const parentId = document.getElementById(config.parentId)?.value;
    if (!parentId || parentId ==="") return;
    const groupName = document.getElementById(config.groupName)?.value;
    if (!groupName || groupName ==="") return;

    const response = await fetch(
        `/api/files/${parentType}/${parentId}/create/group`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": config.csrfToken
            },
            body: JSON.stringify({ groupName })
        }
    );

    await response.json();

    loadFiles(config); // 再描画
}

// 追加画面と追加ボタンの処理
function setDragAndDrop(fileList, fileInput, config) {
    fileInput.addEventListener('change', () => {
        handleFiles(fileInput.files, config);
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
        handleFiles(e.dataTransfer.files, config);
    });
}

async function handleFiles(files, config) {

    const result = await uploadFiles(files, config);
    loadFiles(config);
}

async function uploadFiles(files, config) {
    const id = document.getElementById(config.parentId);
    if (!id || id.value === "") return;

    const groupId = document.getElementById(config.groupId);
    
    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
        formData.append('files', files[i]); // name="files" がサーバと一致する必要あり
    }

    const gid = groupId?.value;

    if (gid && !isNaN(gid)) {
        formData.append("groupId", gid);
    }

    const response = await fetch(
        `${config.uploadUrl}/${Number(id.value)}`, {
            headers: {  // リクエストヘッダを追加
                "X-CSRF-TOKEN": config.csrfToken
            },
            method: "POST",
            body: formData,
        }
    );

    const result = await response.json();
    return result;
}