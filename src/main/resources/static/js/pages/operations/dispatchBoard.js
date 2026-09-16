import {assignmentPair} from './dispatchAssignment.js';

// 一覧描画・検索・状態管理はDataTableに任せ、配車固有の操作だけを追加する。
export function createDispatchBoard({message,onAssign,onOpen,canEdit,workDate}) {
    let selected=null,activeButton=null,busy=false;
    const reset=()=>{selected=null;activeButton?.setAttribute('aria-pressed','false');activeButton=null;};
    async function choose(source,target) {
        const pair=assignmentPair(source,target);
        if(!pair||busy||!canEdit())return;
        busy=true;
        try{await onAssign(pair);}finally{busy=false;reset();}
    }
    function decorate(table,items,kind) {
        reset();
        table.querySelectorAll('tr[data-id]').forEach(row=>{
            const record=items.find(item=>String(kind==='order'?item.orderId:item.id)===row.dataset.id);
            const item=kind==='order'?{kind,id:record.orderId}:{kind,code:record.code};
            const enabled=canEdit()&&(kind==='vehicle'?!!record.crew:Number(record.state)===0&&String(record.visitDate).slice(0,10)===workDate());
            row.draggable=enabled;
            if(kind==='vehicle')row.dataset.vehicleCode=record.code;
            else row.dataset.orderId=record.orderId;
            row.querySelectorAll('[data-dispatch-action="detail"]').forEach(button=>button.addEventListener('click',event=>{event.stopPropagation();onOpen(Number(button.dataset.orderId));}));
            const button=row.querySelector('[data-dispatch-action="select"]');
            if(button){
                button.disabled=!enabled;button.setAttribute('aria-pressed','false');
                button.addEventListener('click',event=>{
                    event.stopPropagation();if(!canEdit()||!enabled)return;
                    if(assignmentPair(selected,item)){choose(selected,item);return;}
                    reset();selected=item;activeButton=button;button.setAttribute('aria-pressed','true');
                    message.textContent=kind==='vehicle'?'右の伝票を選択してください。':'左の車両を選択してください。';
                });
            }
            if(!enabled)return;
            row.addEventListener('dragstart',event=>{selected=item;event.dataTransfer.setData('application/x-kyousei-dispatch',JSON.stringify(item));event.dataTransfer.effectAllowed='link';});
            row.addEventListener('dragend',()=>{reset();row.classList.remove('is-drop-target');});
            row.addEventListener('dragover',event=>{if(assignmentPair(selected,item)){event.preventDefault();event.dataTransfer.dropEffect='link';row.classList.add('is-drop-target');}});
            row.addEventListener('dragleave',()=>row.classList.remove('is-drop-target'));
            row.addEventListener('drop',event=>{event.preventDefault();event.stopPropagation();row.classList.remove('is-drop-target');choose(selected,item);});
        });
    }
    return {reset,decorate};
}
