"use strict"

const FileViewer = (() => {

    if (typeof FILE_CONFIG === "undefined") return {};

    const config = FILE_CONFIG;
    let files = [];
    let index = 0;

    async function open(url, clickedIndex) {

        const res = await fetch(url);
        files = await res.json();

        if (!files || files.length === 0) return;
        index = clickedIndex ?? 0;

        // index が範囲外の可能性もある
        if (index >= files.length)index = files.length - 1;

        openFileViewer(files, index); // 0番目から表示
    }

    return { open };
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
    const groupCombo = [];

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

        const groupContainer = document.createElement("div");
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

        groupCombo.push({number:groupId, text:group.groupName});

        group.files.forEach((file, i) => {

            const li = createListItemWithSelection(
                file.fileId,
                {
                    area: document.getElementById("file-list"),
                    onSecondClick: () => startInlineEdit(file, config),
                    onDoubleClick: () =>
                        FileViewer.open(`${config.groupUrl}/${file.groupId}`, i)
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

    const groupComboBox = document.getElementById(config.groupId);
    if (groupComboBox) createComboBox(groupComboBox, groupCombo);
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

        const response = await fetch(`/api/files/file/delete/${file.fileId}`, {
            method: "POST",
            headers: {
                "X-CSRF-TOKEN": config.csrfToken
            }
        });

        const result = await response.json();

        const li = btn.closest("li");

        if (result.data.groupDeleted) {
            const groupContainer = li.closest(".group-container");
            groupContainer?.remove();
        } else {
            li.remove();
        }
    };

    return btn;
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

let viewerFiles = [];
let currentIndex = 0;

function openFileViewer(files, startIndex = 0) {
    if (!files || files.length === 0) return;

    viewerFiles = files;
    currentIndex = startIndex;
    showFile(currentIndex);

    openFormDialog("form-fileviewer");
}

function showFile(index) {
    if (!viewerFiles[index]) return;

    const file = viewerFiles[index];console.log(file)
    document.getElementById("viewer-group-name").textContent = file.groupName;
    document.getElementById("viewer-file-name").textContent = file.displayName;
    const config = FILE_CONFIG;

    const viewerBody = document.getElementById("viewer-body");
    viewerBody.innerHTML = `<img src="${config.fileViewUrl}/${file.parentId}/${file.fileId}" alt="${file.fileName}">`;

    currentIndex = index;

    updateNavButtons();
}

function setFileViewerBtns() {
    // ナビゲーション
    document.getElementById("viewer-prev").onclick = () => {
        if (currentIndex > 0) {
            showFile(currentIndex - 1);
        }
    };

    document.getElementById("viewer-next").onclick = () => {
        if (currentIndex < viewerFiles.length - 1) {
            showFile(currentIndex + 1);
        }
    };

    document.getElementById("viewer-close").onclick = () => {
        closeFormDialog("form-fileviewer");
    };
}

/*　矢印を制御 */
function updateNavButtons() {
    const prevBtn = document.getElementById("viewer-prev");
    const nextBtn = document.getElementById("viewer-next");

    const isSingle = viewerFiles.length <= 1;

    prevBtn.classList.toggle(
        "nav-hidden",
        isSingle || currentIndex === 0
    );

    nextBtn.classList.toggle(
        "nav-hidden",
        isSingle || currentIndex === viewerFiles.length - 1
    );
}

function setViewerKeyboardEvents() {

    document.addEventListener("keydown", (e) => {

        const viewer = document.getElementById("file-viewer");

        // viewer が閉じていたら何もしない
        if (viewer.closest(".form-dialog").classList.contains("none")) return;

        if (e.key === "ArrowLeft" && currentIndex > 0) {
            showFile(currentIndex - 1);
        }

        if (e.key === "ArrowRight" && currentIndex < viewerFiles.length - 1) {
            showFile(currentIndex + 1);
        }

        if (e.key === "Escape") {
            closeFormDialog("form-fileviewer");
        }
    });
}