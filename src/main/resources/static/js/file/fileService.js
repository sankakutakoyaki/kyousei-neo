"use strict";

const FileService = (() => {

    let viewerFiles = [];
    let currentIndex = 0;

    async function fetchFiles(config, parentId) {
        const res = await fetch(`${config.selectUrl}/${parentId}`);
        return await res.json();
    }

    async function uploadFiles(files, config, groupId = null) {

        const formData = new FormData();

        Array.from(files).forEach(f =>
            formData.append("files", f)
        );

        if (groupId) {
            formData.append("groupId", groupId);
        }

        const res = await fetch(
            `${config.uploadUrl}/${config.parentValue()}`,
            {
                method: "POST",
                headers: { "X-CSRF-TOKEN": token },
                body: formData
            }
        );

        return await res.json();
    }

    function groupFiles(files) {

        const grouped = {};

        files.forEach(file => {
            const key = file.groupId ?? "default";

            if (!grouped[key]) {
                grouped[key] = {
                    groupId: file.groupId,
                    groupName: file.groupName ?? "",
                    files: []
                };
            }

            grouped[key].files.push(file);
        });

        return Object.values(grouped);
    }

    /* =========================
       Viewer
    ========================= */

    function openViewer(files, startIndex = 0, config) {
        viewerFiles = files;
        currentIndex = startIndex;
        showFile(currentIndex, config);
        openFormDialog(config.viewerId);
    }

    function showFile(index, config) {

        currentIndex = index;
        const file = viewerFiles[index];

        const viewerBody = document.getElementById(config.viewerBodyId);
        viewerBody.innerHTML = "";

        const fileNameEl =
            document.getElementById(config.viewerFileNameId);

        if (fileNameEl) {
            fileNameEl.textContent = file.displayName ?? "";
        }

        const url =
            `${config.fileViewUrl}/${file.parentId}/${file.fileId}`;

        if (file.mimeType.startsWith("image/")) {
            const img = document.createElement("img");
            img.src = url;
            img.className = "viewer-image";
            viewerBody.appendChild(img);
        }
        else if (file.mimeType === "application/pdf") {
            const iframe = document.createElement("iframe");
            iframe.src = url + "#zoom=page-width";
            iframe.className = "viewer-pdf";
            viewerBody.appendChild(iframe);
        }

        updateNavButtons(config);
    }

    function updateNavButtons(config) {

        const prevBtn = document.getElementById(config.prevBtnId);
        const nextBtn = document.getElementById(config.nextBtnId);

        const single = viewerFiles.length <= 1;

        prevBtn.classList.toggle(
            "nav-hidden",
            single || currentIndex === 0
        );

        nextBtn.classList.toggle(
            "nav-hidden",
            single || currentIndex === viewerFiles.length - 1
        );
    }


    function next(config) {
        if (currentIndex < viewerFiles.length - 1) {
            showFile(currentIndex + 1, config);
        }
    }

    function prev(config) {
        if (currentIndex > 0) {
            showFile(currentIndex - 1, config);
        }
    }

    async function deleteFile(config, fileId) {

        const res = await fetch(
            `${config.deleteUrl}/${fileId}`,
            {
                method: "POST",
                headers: { "X-CSRF-TOKEN": token }
            }
        );

        return await res.json();
    }

    async function renameGroup(config, groupId, newName) {

        const res = await fetch(
            `${config.renameGroupUrl}/${groupId}`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": token
                },
                body: JSON.stringify({ name: newName })
            }
        );

        return await res.json();
    }
    
    async function renameFile(config, fileId, newName) {

        const res = await fetch(
            `${config.renameFileUrl}/${fileId}`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": token
                },
                body: JSON.stringify({ name: newName })
            }
        );

        return await res.json();
    }


    return {
        fetchFiles,
        uploadFiles,
        groupFiles,
        openViewer,
        next,
        prev,
        deleteFile,
        renameGroup,
        renameFile 
    };

})();

const FileUI = (() => {

    function init(config) {

        if (!config) {
            console.error("FILE_CONFIG is required");
            return;
        }

        /* =========================
           一覧読み込みボタン
        ========================= */

        const loadBtn = document.getElementById(config.loadBtnId);

        if (loadBtn) {
            loadBtn.addEventListener("click", () => {
                render(config);
            });
        }

        /* =========================
           アップロード input
        ========================= */

        const fileInput = document.getElementById(config.fileInputId);

        if (fileInput) {

            fileInput.addEventListener("change", async e => {

                if (!e.target.files.length) return;

                const groupId = document.getElementById(config.groupArea)?.value;

                await FileService.uploadFiles(e.target.files, config, groupId);
                e.target.value = "";

                renderFileList(config);
            });
        }

        /* =========================
           ドラッグ＆ドロップ
        ========================= */

        const dropArea = document.getElementById(config.dropAreaId);

        if (dropArea) {

            dropArea.addEventListener("drop", async e => {

                e.preventDefault();
                e.stopPropagation();

                // グループ上なら処理しない
                if (e.target.closest(".group-container")) return;

                const groupId = document.getElementById(config.groupArea)?.value;

                await FileService.uploadFiles(
                    e.dataTransfer.files,
                    config,
                    groupId
                );

                renderFileList(config);
            });
        }

        /* =========================
           Viewerボタン
        ========================= */

        const prevBtn = document.getElementById(config.prevBtnId);
        const nextBtn = document.getElementById(config.nextBtnId);
        const closeBtn = document.getElementById(config.closeBtnId);

        if (prevBtn) {
            prevBtn.addEventListener("click", () =>
                FileService.prev(config)
            );
        }

        if (nextBtn) {
            nextBtn.addEventListener("click", () =>
                FileService.next(config)
            );
        }

        if (closeBtn) {
            closeBtn.addEventListener("click", () =>
                closeFormDialog(config.viewerId)
            );
        }

        /* =========================
           キーボード
        ========================= */

        document.addEventListener("keydown", e => {

            if (e.key === "Escape") {
                closeFormDialog(config.viewerId);
            }

        });

    }

    /* =========================
       表示切替共通関数
    ========================= */

    async function render(config) {

        if (config.viewType === "table") {
            await renderFileTable(config);
        } else {
            await renderFileList(config);
        }
    }

    return { init };

})();

function highlightSelectedGroup() {

    document
        .querySelectorAll(".group-container")
        .forEach(div => {

            const isSelected =
                Number(div.dataset.groupId) === Number(selectedGroupId);

            div.classList.toggle("selected-group", isSelected);
        });
}
