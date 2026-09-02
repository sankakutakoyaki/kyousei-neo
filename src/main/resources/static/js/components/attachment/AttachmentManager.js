"use strict";
import { AttachmentRepository } from "../../repositories/attachment/AttachmentRepository.js";
import { DialogService } from "../../core/ui/dialog/DialogService.js";

export class AttachmentManager {
    constructor(root,{parentType="ORDER"}={}) {
        this.root=root; this.parentType=parentType; this.parentId=null; this.groups=[]; this.activeGroupId=null;
        this.openGroupIds=new Set();
        this.groupsEl=root?.querySelector("[data-attachment-groups]");
        this.emptyEl=root?.querySelector("[data-attachment-empty]");
        this.fileInput=root?.querySelector("[data-attachment-files]");
        this.cameraInput=root?.querySelector("[data-attachment-camera]");
        this.bind();
    }
    bind() {
        if(!this.root || this.root.dataset.initialized) return;
        this.root.dataset.initialized="true";
        this.root.querySelector('[data-attachment-action="create-group"]')?.addEventListener("click",()=>this.createGroup());
        this.fileInput?.addEventListener("change",()=>this.handleFiles(this.fileInput.files));
        this.cameraInput?.addEventListener("change",()=>this.handleFiles(this.cameraInput.files));
    }
    async open(parentId) {
        const nextParentId=Number(parentId)||null;
        if(this.parentId!==nextParentId) this.openGroupIds.clear();
        this.parentId=nextParentId; this.activeGroupId=null;
        this.root.querySelector('[data-attachment-action="create-group"]')?.toggleAttribute("disabled",!this.parentId);
        if(!this.parentId){ this.groups=[]; this.render(); return; }
        await this.refresh();
    }
    async refresh(){ this.groups=await AttachmentRepository.groups(this.parentType,this.parentId); this.render(); }
    render() {
        this.emptyEl.classList.toggle("none",Boolean(this.parentId));
        this.groupsEl.replaceChildren();
        for(const group of this.groups) this.groupsEl.append(this.renderGroup(group));
        if(this.parentId && !this.groups.length) {
            const p=document.createElement("p"); p.className="attachment-empty"; p.textContent="フォルダはまだありません。「＋ フォルダ」から作成してください。"; this.groupsEl.append(p);
        }
    }
    renderGroup(group) {
        const section=document.createElement("section"); section.className="attachment-group";
        const header=document.createElement("header"); header.className="attachment-group-header";
        const expanded=this.openGroupIds.has(group.attachmentGroupId);
        const title=document.createElement("button"); title.type="button"; title.className="attachment-group-toggle";
        title.setAttribute("aria-expanded",String(expanded));
        const chevron=document.createElement("span"); chevron.className="attachment-group-chevron"; chevron.textContent="›";
        const folder=document.createElement("span"); folder.textContent="📁";
        const label=document.createElement("strong"); label.textContent=`${group.groupName} (${group.files.length})`;
        title.append(chevron,folder,label);
        const actions=document.createElement("div"); actions.className="attachment-group-actions";
        actions.append(this.button("ファイル追加",()=>this.chooseFiles(group.attachmentGroupId)),this.button("📷 撮影",()=>this.takePhoto(group.attachmentGroupId)),
            this.button("名前変更",()=>this.renameGroup(group)),this.button("削除",()=>this.deleteGroup(group)));
        header.append(title,actions); section.append(header);
        const body=document.createElement("div"); body.className="attachment-group-body"; body.classList.toggle("none",!expanded);
        title.addEventListener("click",()=>{
            const isOpen=this.openGroupIds.has(group.attachmentGroupId);
            if(isOpen) this.openGroupIds.delete(group.attachmentGroupId); else this.openGroupIds.add(group.attachmentGroupId);
            body.classList.toggle("none",isOpen); title.setAttribute("aria-expanded",String(!isOpen));
        });
        const drop=document.createElement("div"); drop.className="attachment-drop-zone"; drop.textContent="写真・図面・PDFをここにドロップ";
        this.setDragAndDrop(drop,group.attachmentGroupId);
        body.append(drop);
        const grid=document.createElement("div"); grid.className="attachment-grid";
        group.files.forEach((file,index)=>grid.append(this.renderFile(group,file,index))); body.append(grid); section.append(body);
        return section;
    }
    renderFile(group,file,index) {
        const card=document.createElement("article"); card.className="attachment-card";
        const preview=document.createElement("button"); preview.type="button"; preview.className="attachment-preview";
        if(file.fileType==="IMAGE") { const img=document.createElement("img"); img.loading="lazy"; img.alt=file.displayName; img.src=AttachmentRepository.contentUrl(file.attachmentId); preview.append(img); preview.onclick=()=>this.openViewer(group,index); }
        else { const badge=document.createElement("span"); badge.className="attachment-pdf"; badge.textContent="PDF"; preview.append(badge); preview.onclick=()=>window.open(AttachmentRepository.contentUrl(file.attachmentId),"_blank","noopener"); }
        const name=document.createElement("span"); name.className="attachment-name"; name.title=file.displayName; name.textContent=file.displayName;
        const menu=document.createElement("div"); menu.className="attachment-file-actions";
        menu.append(this.button("変更",()=>this.renameFile(file)),this.button("保存",()=>location.href=AttachmentRepository.contentUrl(file.attachmentId,true)),this.button("削除",()=>this.deleteFile(file)));
        card.append(preview,name,menu); return card;
    }
    button(label,handler){ const b=document.createElement("button");b.type="button";b.className="attachment-small-btn";b.textContent=label;b.addEventListener("click",handler);return b; }
    chooseFiles(id){this.activeGroupId=id;this.fileInput.value="";this.fileInput.click();}
    takePhoto(id){this.activeGroupId=id;this.cameraInput.value="";this.cameraInput.click();}
    setDragAndDrop(drop,groupId) {
        drop.addEventListener("dragover",e=>{e.preventDefault();drop.classList.add("is-dragover");});
        drop.addEventListener("dragleave",()=>drop.classList.remove("is-dragover"));
        drop.addEventListener("drop",e=>{
            e.preventDefault();
            drop.classList.remove("is-dragover");
            this.handleFiles(e.dataTransfer.files,groupId);
        });
    }
    async handleFiles(fileList,groupId=this.activeGroupId) {
        const files=Array.from(fileList ?? []);
        if(!groupId||!files.length)return;
        try {
            for(const file of files) await AttachmentRepository.upload(this.parentType,this.parentId,groupId,[file]);
            await this.refresh();
        } catch(e) {
            await this.refresh();
            DialogService.error(e.message);
        } finally {
            if(this.fileInput)this.fileInput.value="";
            if(this.cameraInput)this.cameraInput.value="";
        }
    }
    async createGroup(){const name=await DialogService.prompt("フォルダ名を入力してください");if(!name?.trim())return;try{await AttachmentRepository.createGroup(this.parentType,this.parentId,name.trim());await this.refresh();}catch(e){DialogService.error(e.message);}}
    async renameGroup(group){const name=await DialogService.prompt("新しいフォルダ名",group.groupName);if(!name?.trim()||name.trim()===group.groupName)return;try{await AttachmentRepository.renameGroup(group.attachmentGroupId,name.trim());await this.refresh();}catch(e){DialogService.error(e.message);}}
    async deleteGroup(group){if(!await DialogService.confirm(`「${group.groupName}」と中のファイルを削除しますか？`))return;try{await AttachmentRepository.deleteGroup(group.attachmentGroupId);await this.refresh();}catch(e){DialogService.error(e.message);}}
    async renameFile(file){const name=await DialogService.prompt("新しいファイル名",file.displayName);if(!name?.trim()||name.trim()===file.displayName)return;try{await AttachmentRepository.renameFile(file.attachmentId,name.trim());await this.refresh();}catch(e){DialogService.error(e.message);}}
    async deleteFile(file){if(!await DialogService.confirm(`「${file.displayName}」を削除しますか？`))return;try{await AttachmentRepository.deleteFile(file.attachmentId);await this.refresh();}catch(e){DialogService.error(e.message);}}
    openViewer(group,startIndex) {
        const images=group.files.filter(f=>f.fileType==="IMAGE"); const selected=group.files[startIndex]; let index=Math.max(0,images.findIndex(f=>f.attachmentId===selected.attachmentId));
        const overlay=document.createElement("div"); overlay.className="attachment-viewer"; overlay.tabIndex=0;
        const img=document.createElement("img"); const caption=document.createElement("div"); caption.className="attachment-viewer-caption";
        const show=()=>{const file=images[index];img.src=AttachmentRepository.contentUrl(file.attachmentId);img.alt=file.displayName;caption.textContent=`${file.displayName}　${index+1} / ${images.length}`;};
        const close=()=>overlay.remove(); const move=delta=>{index=(index+delta+images.length)%images.length;show();};
        overlay.append(this.button("×",close),this.button("‹",()=>move(-1)),img,this.button("›",()=>move(1)),caption);
        overlay.querySelectorAll("button")[0].className="attachment-viewer-close"; overlay.querySelectorAll("button")[1].className="attachment-viewer-prev"; overlay.querySelectorAll("button")[2].className="attachment-viewer-next";
        overlay.addEventListener("keydown",e=>{if(e.key==="Escape")close();if(e.key==="ArrowLeft")move(-1);if(e.key==="ArrowRight")move(1);});
        overlay.addEventListener("click",e=>{if(e.target===overlay)close();}); document.body.append(overlay);show();overlay.focus();
    }
}
