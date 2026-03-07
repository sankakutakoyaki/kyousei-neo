"use strict";

export class FileListComponent {

    constructor(config) {
        this.config = config;
        this.container =
            document.getElementById(config.listId);
    }

    async render() {

        const parentId =
            this.config.parentValue();

        if (!parentId) return;

        const files =
            await FileService.fetchFiles(
                this.config,
                parentId
            );

        this.container.innerHTML = "";

        if (!files || files.length === 0) return;

        if (this.config.grouping) {

            const groups =
                FileGroup.groupFiles(files);

            FileRenderer.renderGroups(
                this.container,
                groups,
                this.config,
                this
            );

        } else {

            FileRenderer.renderFiles(
                this.container,
                files,
                this.config,
                this
            );

        }
    }

}