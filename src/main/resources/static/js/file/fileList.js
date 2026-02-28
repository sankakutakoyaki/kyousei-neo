"use strict";

let selectedGroupId = null;

async function renderFileList(config) {

    const parentId = config.parentValue();
    if (!parentId) return;

    const files =
        await FileService.fetchFiles(config, parentId);

    const container =
        document.getElementById(config.listId);

    container.innerHTML = "";

    if (!files || files.length === 0) return;

    if (config.grouping) {

        const groups = FileService.groupFiles(files);

        /* --------------------------
           コンボ生成
        --------------------------- */

        const comboList = groups.map(g => ({
            number: g.groupId,
            text: g.groupName
        }));

        const area = document.getElementById(config.groupArea);

        if (area) {

            createComboBoxWithFunc(
                area,
                comboList,
                (value) => {
                    selectedGroupId = value;
                    highlightSelectedGroup();
                },
                "新規グループ"
            );

            // 再描画後に選択値を復元
            if (selectedGroupId) {
                area.value = selectedGroupId;
            }
        }

        /* --------------------------
           グループ描画
        --------------------------- */

        groups.forEach(group => {

            const groupDiv =
                document.createElement("div");

            groupDiv.className = "group-container";
            groupDiv.dataset.groupId = group.groupId;

            groupDiv.innerHTML =
                `<div class="group-title">
                    ${group.groupName}
                 </div>`;

            // クリックで選択可能にする
            groupDiv.addEventListener("click", () => {
                selectedGroupId = group.groupId;

                if (area) area.value = selectedGroupId;

                highlightSelectedGroup();
            });

            groupDiv.addEventListener("dragover", e => {
                e.preventDefault();
                groupDiv.classList.add("dragover");
            });

            groupDiv.addEventListener("dragleave", () => {
                groupDiv.classList.remove("dragover");
            });

            groupDiv.addEventListener("drop", async e => {

                e.preventDefault();
                e.stopPropagation();
                groupDiv.classList.remove("dragover");

                await FileService.uploadFiles(
                    e.dataTransfer.files,
                    config,
                    group.groupId
                );

                await renderFileList(config);
            });

            group.files.forEach((file, i) => {
                const li = createFileRow(file, group.files, i, config);
                groupDiv.appendChild(li);
            });

            const titleDiv = groupDiv.querySelector(".group-title");

            groupDiv.tabIndex = 0;

            groupDiv.addEventListener("keydown", (e) => {

                // ★ 自分自身がフォーカスされている時だけ反応
                if (document.activeElement !== groupDiv) return;

                if (e.key === "F2") {
                    e.preventDefault();

                    startInlineRename({
                        element: titleDiv,
                        initialValue: group.groupName,
                        onSave: async (newName) => {
                            await FileService.renameGroup(
                                config,
                                group.groupId,
                                newName
                            );
                            await renderFileList(config);
                        }
                    });
                }
            });

            container.appendChild(groupDiv);
        });

        // 描画後にハイライト
        highlightSelectedGroup();
    }
    else {

        files.forEach((file, i) => {
            const li = createFileRow(file, files, i, config);
            container.appendChild(li);
        });
    }
}

function createFileRow(file, fileArray, index, config) {

    const li = document.createElement("li");
    li.className = "file-row";

    const nameSpan = document.createElement("span");
    nameSpan.textContent = file.displayName;
    nameSpan.className = "file-name";
    li.appendChild(nameSpan);

    if (config.isAdmin) {

        const deleteBtn = document.createElement("button");
        deleteBtn.className = "delete-btn";

        const img = document.createElement("img");
        img.src = "/icons/dust.png";
        img.title = "削除";
        img.alt = "削除";
        img.className = "delete-icon";

        deleteBtn.appendChild(img);

        deleteBtn.addEventListener("click", async (e) => {

            e.stopPropagation();

            await FileService.deleteFile(
                config,
                file.fileId
            );

            renderFileList(config);
        });

        li.appendChild(deleteBtn);
    }

    li.tabIndex = 0;

    li.addEventListener("keydown", (e) => {

        if (document.activeElement !== li) return;

        if (e.key === "F2") {
            e.preventDefault();

            startInlineRename({
                element: nameSpan,
                initialValue: file.displayName,
                onSave: async (newName) => {
                    await FileService.renameFile(
                        config,
                        file.fileId,
                        newName
                    );
                    await renderFileList(config);
                }
            });
        }
    });

    li.ondblclick = () =>
        FileService.openViewer(fileArray, index, config);

    return li;
}

function startInlineRename({
    element,
    initialValue,
    onSave
}) {

    const originalHTML = element.innerHTML;

    const input = document.createElement("input");
    input.type = "text";
    input.value = initialValue;
    input.className = "rename-input";

    element.innerHTML = "";
    element.appendChild(input);

    input.focus();
    input.select();

    let cancelled = false;

    async function commit() {
        if (cancelled) return;

        const newValue = input.value.trim();

        if (!newValue || newValue === initialValue) {
            cancel();
            return;
        }

        await onSave(newValue);
    }

    function cancel() {
        cancelled = true;
        element.innerHTML = originalHTML;
    }

    input.addEventListener("keydown", async (e) => {
        if (e.key === "Enter") {
            e.preventDefault();
            await commit();
        }
        if (e.key === "Escape") {
            cancel();
        }
    });

    input.addEventListener("blur", commit);
}