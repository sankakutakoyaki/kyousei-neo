"use strict"

const FileViewer = (() => {

    if (typeof FILE_CONFIG === "undefined") return {};

    const config = FILE_CONFIG;
    let files = [];
    let index = 0;

    // async function open(url, clickedIndex) {

    //     const res = await fetch(url);
    //     files = await res.json();
    //     index = clickedIndex;

    //     openFormDialog("form-fileviewer");
    //     render();
    // }
async function open(url, clickedIndex) {

    console.log("fetch url:", url);

    const res = await fetch(url);

    console.log("status:", res.status);
    console.log("content-type:", res.headers.get("content-type"));

    const text = await res.text();
    console.log("raw response:", text);

    try {
        files = JSON.parse(text);
    } catch (e) {
        console.error("JSON parse error");
        files = [];
    }

    console.log("files:", files);

    index = clickedIndex;

    if (!files || files.length === 0) {
        console.warn("files is empty");
        return;
    }

    openFormDialog("form-fileviewer");
    render();
}
    async function render() {

        const file = files[index];
        const body = document.getElementById("viewer-body");
        body.innerHTML = "";

        console.log(file); // ← デバッグ

        const url =
            `${config.fileViewUrl}/${file.parentId}/${file.fileId}`;

        if (file.mimeType?.startsWith("image/")) {

            const img = document.createElement("img");
            img.src = url;
            img.style.maxWidth = "90vw";
            img.style.maxHeight = "90vh";
            body.appendChild(img);

        } else if (file.mimeType === "application/pdf") {

            const iframe = document.createElement("iframe");
            iframe.src = url;
            iframe.style.width = "90vw";
            iframe.style.height = "90vh";
            iframe.style.border = "none";
            body.appendChild(iframe);

        } else {

            const link = document.createElement("a");
            link.href = url;
            link.textContent = "ダウンロード";
            link.download = file.displayName;
            body.appendChild(link);
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
        closeFormDialog("form-fileviewer");
    }

    return { open, next, prev, close };
})();

async function loadFiles(config) {

    const ul = document.getElementById(config.listId);
    const parentId = document.getElementById(config.parentId).value;

    if (!parentId) {
        alert("IDを入力してください");
        return;
    }

    const response = await fetch(`${config.selectUrl}/${parentId}`);
    const files = await response.json();

    ul.innerHTML = "";
    if (!files || files.length === 0) return;

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

        group.files.forEach((file, i) => {

            const li = createListItemWithSelection(
                file.fileId,
                {
                    area: fileUl,
                    onSecondClick: () => startInlineEdit(file, config),
                    onDoubleClick: () =>
                        FileViewer.open(`${config.selectUrl}/${parentId}`, i)
                        // FileViewer.open(`${config.fileViewUrl}/${file.parentId}/${file.fileId}`, i)
                }
            );

            li.classList.add("file-item");

            const nameSpan = createNameSpan(file);
            const deleteBtn = createDeleteButton(file, config);

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

function createNameSpan(file) {
    const span = document.createElement("span");
    span.className = "file-name";
    span.textContent = file.displayName;
    span.style.cursor = "default";
    return span;
}

function createDeleteButton(file, config) {

    const btn = document.createElement("button");
    btn.innerHTML = "✖️";
    btn.className = "remove-btn";

    btn.onclick = async (e) => {
        e.stopPropagation();

        await fetch(`/api/files/file/delete/${file.fileId}`, {
            method: "POST",
            headers: {
                "X-CSRF-TOKEN": config.csrfToken
            }
        });

        const li = btn.closest("li");
        li.remove();
    };

    return btn;
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
