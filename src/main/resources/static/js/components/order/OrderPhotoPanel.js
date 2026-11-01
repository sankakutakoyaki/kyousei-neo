"use strict";
import { DialogService } from "../../core/ui/dialog/DialogService.js";
import { apiFetch } from "../../core/api/apiFetch.js";

import { photoCategoryLabel } from "./orderEnums.js";
export class OrderPhotoPanel {
    constructor(root) {
        this.root=root; this.generation=0; this.rows=[];
        this.el=key=>root.querySelector(`[data-photo-${key}]`);
        root.addEventListener("keydown",event=>{
            if(event.key==="Enter" && !event.target.closest("button,a") && event.target.type!=="file") {
                event.preventDefault();event.stopPropagation();void this.load(true);
            }
        });
        this.el("search").addEventListener("click",()=>void this.load(true));
        this.el("more").addEventListener("click",()=>void this.load());
        for(const [button,input] of [["shoot","camera"],["select","files"]]) {
            this.el(button)?.addEventListener("click",()=>{
                if(!this.orderId || !this.el("category").value || !this.el("day").value){this.el("status").textContent="写真分類と対象訪問日を指定してください。";return;}
                this.el(input).value="";this.el(input).click();
            });
            this.el(input)?.addEventListener("change",event=>void this.upload(event.target.files));
        }
        this.el("retry")?.addEventListener("click",()=>{if(this.pending?.orderId===this.orderId)void this.upload(this.pending.files,true);});
        this.el("discard")?.addEventListener("click",async()=>{
            if(this.saving || !await DialogService.confirm("未保存の写真を破棄しますか？"))return;
            this.pending=null;this.updateRetry();this.el("status").textContent="未保存の写真を破棄しました。";
        });
        this.el("close").addEventListener("click",()=>this.el("viewer").close());
        this.el("prev").addEventListener("click",()=>this.show(this.index-1));
        this.el("next").addEventListener("click",()=>this.show(this.index+1));
    }
    async open(orderId=null,day="") {
        this.generation++;this.orderId=orderId;this.rows=[];this.query=null;
        this.el("global").hidden=!!orderId;
        if(this.el("upload"))this.el("upload").hidden=!orderId;
        if(this.el("day"))this.el("day").value=day || "";
        this.el("viewer").close();
        await this.load(true);this.updateRetry();
    }
    updateRetry() {
        const visible=!!this.pending && this.pending.orderId===this.orderId;
        for(const key of ["retry","discard"])if(this.el(key))this.el(key).hidden=!visible;
        if(this.pending){this.pendingNotice=true;this.el("status").textContent=visible?"未保存の写真があります。再送できます。":"別の受注の未保存写真があります。元の担当伝票を開いて再送してください。";}
        else if(this.pendingNotice){this.pendingNotice=false;this.el("status").textContent=this.rows.length?`${this.rows.length}枚表示しています。`:"写真はありません。";}
    }
    close(){this.generation++;this.el("viewer").close();}
    async load(reset=false) {
        const generation=reset?++this.generation:this.generation;
        if(!reset && this.loading)return;
        if(reset){
            this.rows=[];this.el("grid").replaceChildren();
            this.query=new URLSearchParams();
            const id=this.orderId || this.el("order").value;
            if(id)this.query.set("orderId",id);
            for(const key of ["from","to","category"])if(this.el(key).value)this.query.set(key,this.el(key).value);
        }
        if(!this.query)return;
        this.loading=true;this.el("more").hidden=true;this.el("status").textContent="写真を読み込み中…";
        const query=new URLSearchParams(this.query);query.set("offset",this.rows.length);
        try {
            const response=await apiFetch(`/api/order-photos?${query}`,{method:"GET",showProcessing:false});
            if(generation!==this.generation || !this.root.isConnected)return;
            const rows=response.data ?? [];this.el("more").hidden=rows.length<=100;
            for(const row of rows.slice(0,100))this.append(row);
            this.el("status").textContent=this.rows.length?`${this.rows.length}枚表示しています。`:"写真はありません。";
        }catch(error){if(generation===this.generation)this.el("status").textContent=error.message || "写真を取得できませんでした。";}
        finally{if(generation===this.generation)this.loading=false;}
    }
    append(row) {
        const index=this.rows.length;this.rows.push(row);
        const card=this.el("card").content.firstElementChild.cloneNode(true);
        const image=card.querySelector("img");image.src=`/api/order-photos/preview/${row.attachmentId}`;image.alt=row.displayName;
        card.querySelector("[data-photo-name]").textContent=row.displayName;
        card.querySelector("[data-photo-context]").textContent=`受注 ${row.orderId} ${row.title || ""} ／ ${photoCategoryLabel(row.photoCategory) || "既存添付"} ／ ${row.workDate || "対象日未設定"}`;
        card.querySelector("[data-photo-meta]").textContent=`保存：${row.registDate || ""} ${row.uploadedBy || "保存者未記録"}`;
        card.querySelector("button").addEventListener("click",()=>{this.show(index);this.el("viewer").showModal();});
        this.el("grid").append(card);
    }
    show(index) {
        if(index<0 || index>=this.rows.length)return;
        this.index=index;const row=this.rows[index];
        const url=`/api/attachments/files/${row.attachmentId}/content`;
        this.el("large").src=url;this.el("large").alt=row.displayName;
        this.el("original").href=url;
        this.el("caption").textContent=`${index+1} / ${this.rows.length}　${row.displayName}　${row.workDate || ""}　${photoCategoryLabel(row.photoCategory) || "既存添付"}`;
        this.el("prev").disabled=index===0;this.el("next").disabled=index===this.rows.length-1;
    }
    async upload(fileList,retry=false) {
        if(this.saving)return;
        if(this.pending && !retry){this.updateRetry();return;}
        const files=Array.from(fileList || []);if(!files.length)return;
        const generation=this.generation,orderId=retry?this.pending.orderId:this.orderId,
            category=retry?this.pending.category:this.el("category").value,day=retry?this.pending.day:this.el("day").value;
        if(!orderId || !category || !day){this.el("status").textContent="写真分類と対象訪問日を指定してください。";return;}
        if(files.length>20 || files.some(file=>file.size>20*1024*1024) || files.reduce((total,file)=>total+file.size,0)>90*1024*1024){this.el("status").textContent="写真は1回20枚まで、1枚20MB・合計90MB以内で選択してください。";return;}
        this.pending ??= {files,orderId,category,day,requestId:crypto.randomUUID()};
        const data=new FormData();data.append("requestId",this.pending.requestId);data.append("category",category);data.append("workDate",day);files.forEach(file=>data.append("files",file));
        const controls=this.el("upload").querySelectorAll("button,input");controls.forEach(el=>el.disabled=true);
        this.saving=true;this.el("status").textContent="写真を保存しています…";
        try {
            await apiFetch(`/api/order-photos/${orderId}`,{data,timeout:120000});
            this.pending=null;this.updateRetry();
            if(generation!==this.generation || !this.root.isConnected)return;
            await this.load(true);this.el("status").textContent=`写真${files.length}枚を保存しました。`;
        }catch(error){if(generation===this.generation){this.updateRetry();this.el("status").textContent=`${error.message || "保存できませんでした。"} 写真は保持しています。再送できます。`; }}
        finally{this.saving=false;controls.forEach(el=>el.disabled=false);for(const key of ["files","camera"])if(!this.pending && this.el(key))this.el(key).value="";}
    }
}
