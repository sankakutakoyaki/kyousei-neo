"use strict";
import { isSimpleWorkResult } from "./orderEnums.js";
import { apiFetch } from "../../core/api/apiFetch.js";
import { DialogService } from "../../core/ui/dialog/DialogService.js";
export class OrderOutcomeDialog {
    constructor(root,onSaved) {
        this.root=root;this.onSaved=onSaved;this.el=key=>root.querySelector(`[data-outcome-${key}]`);
        this.el("cancel").addEventListener("change",()=>this.update());
        this.el("type").addEventListener("change",()=>this.update());
        this.el("close").addEventListener("click",()=>{if(!this.saving)root.close();});
        root.addEventListener("cancel",e=>{if(this.saving)e.preventDefault();});
        root.querySelector("form").addEventListener("submit",e=>{e.preventDefault();void this.save();});
    }
    async open(entity) {
        try {
            const response=await apiFetch(`/api/order-outcomes/${entity.orderId}`,{method:"GET"});
            if(!this.root.isConnected)return;
            this.order=response.data;
            if(this.order.version!==entity.version)throw new Error("受注が更新されています。受注詳細を開き直してください。");
            if(this.order.orderStatus==="CANCELLED")throw new Error("キャンセル済みの受注です。経緯は履歴で確認できます。");
            this.el("summary").textContent=`受注 ${this.order.orderId}　${this.order.title || ""}`;
            this.el("type").value="COMPLETED";this.el("reason").value="";this.el("cancel").value="";
            this.el("day").value=this.order.visitDate || this.order.lastWorkDate || "";
            this.el("status").textContent="";this.signature=null;this.update();this.root.showModal();
        }catch(error){DialogService.error(error.message);}
    }
    update() {
        const cancel=this.el("type").value==="CANCELLED";
        const correction=this.order?.outcomeType!=="ABSENT" && (!!this.order?.outcomeType || !!this.order?.hasWorkResult);
        const reason=correction || !isSimpleWorkResult(this.el("type").value);
        this.el("cancel-field").hidden=!cancel;this.el("cancel").required=cancel;
        this.el("day-field").hidden=cancel;this.el("day").required=!cancel;
        this.el("reason-field").hidden=!reason;this.el("reason").required=cancel?this.el("cancel").value==="OTHER":reason;
        this.el("confirm").hidden=reason;
        this.el("confirm").textContent=this.el("type").value==="ABSENT"?"不在として訪問したことを記録しますか？記録時刻も保存されます。":"作業を完了として報告しますか？";
    }
    async save() {
        if(this.saving)return;
        const payload={version:this.order.version,type:this.el("type").value,reason:this.el("reason-field").hidden?"":this.el("reason").value.trim(),
            cancelReason:this.el("type").value==="CANCELLED"?this.el("cancel").value:null,workDate:this.el("day").value || null};
        const signature=JSON.stringify(payload);if(signature!==this.signature){this.signature=signature;this.requestId=crypto.randomUUID();}
        this.saving=true;
        const controls=this.root.querySelectorAll("button,input,select,textarea");controls.forEach(el=>el.disabled=true);
        try{await apiFetch(`/api/order-outcomes/${this.order.orderId}`,{data:{...payload,requestId:this.requestId}});this.root.close();await this.onSaved();}
        catch(error){this.el("status").textContent=error.message;}
        finally{this.saving=false;controls.forEach(el=>el.disabled=false);}
    }
}
