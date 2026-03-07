"use strict";

async function renderFileTable(config) {

    const parentId = config.parentValue();
    if (!parentId) return;

    const files =
        await FileService.fetchFiles(config, parentId);

    const tbody =
        document.getElementById(config.tableBodyId);

    tbody.innerHTML = "";

    files.forEach((file, i) => {

        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${file.groupName ?? ""}</td>
            <td>${file.displayName}</td>
            <td>
                <button class="img-btn">表示</button>
            </td>
        `;

        tr.querySelector("button").onclick = () =>
            FileService.openViewer(files, i, config);

        tbody.appendChild(tr);
    });
}