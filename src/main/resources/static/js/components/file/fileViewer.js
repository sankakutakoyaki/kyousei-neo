"use strict";

export const FileViewer = {

    state:{
        files:[],
        index:0
    },

    async open(files, index, config){

        this.state.files = files;
        this.state.index = index;

        this.showFile(index,config);

        openFormDialog(config.viewerId);
    },

    showFile(index,config){

        this.state.index = index;

        const file = this.state.files[index];

        const viewerBody = document.getElementById(config.viewerBodyId);

        viewerBody.innerHTML="";

        const fileNameEl = document.getElementById(config.viewerFileNameId);

        if(fileNameEl){
            fileNameEl.textContent = file.displayName ?? "";
        }

        const url = `${config.fileViewUrl}/${file.parentId}/${file.fileId}`;

        if(file.mimeType.startsWith("image/")){

            const img = document.createElement("img");
            img.src = url;
            img.className="viewer-image";

            viewerBody.appendChild(img);
        }

        else if(file.mimeType==="application/pdf"){

            const iframe=document.createElement("iframe");
            iframe.src=url+"#zoom=page-width";
            iframe.className="viewer-pdf";

            viewerBody.appendChild(iframe);
        }

        this.updateNavButtons(config);
    },

    updateNavButtons(config){

        const prevBtn =
            document.getElementById(config.prevBtnId);

        const nextBtn =
            document.getElementById(config.nextBtnId);

        const single =
            this.state.files.length <= 1;

        prevBtn.classList.toggle(
            "nav-hidden",
            single || this.state.index === 0
        );

        nextBtn.classList.toggle(
            "nav-hidden",
            single || this.state.index === this.state.files.length-1
        );

    },


    next(config){

        if(this.state.index < this.state.files.length-1){

            this.showFile(this.state.index+1,config);

        }

    },

    prev(config){

        if(this.state.index>0){

            this.showFile(this.state.index-1,config);

        }

    }
};