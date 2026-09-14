import {text,datePart} from './operationUi.js';

import {assignmentPair} from './dispatchAssignment.js';
export function createDispatchBoard(root,{onAssign,onOpen,canEdit}) {
    let selected=null,busy=false;
    const message=text('p','伝票と車両をドラッグ＆ドロップ、または両方の「選択」ボタンで指定してください。');
    message.setAttribute('role','status');
    let activeButton=null;
    async function choose(source,target) {
        const pair=assignmentPair(source,target);
        if(!pair||busy||!canEdit())return;
        busy=true;
        try{await onAssign(pair);}finally{busy=false;selected=null;activeButton?.setAttribute('aria-pressed','false');activeButton=null;}
    }
    function wire(card,item,enabled) {
        card.draggable=enabled;
        if(!enabled)return;
        card.addEventListener('dragstart',event=>{
            selected=item;
            event.dataTransfer.setData('application/x-kyousei-dispatch',JSON.stringify(item));
            event.dataTransfer.effectAllowed='link';
        });
        card.addEventListener('dragend',()=>{selected=null;card.classList.remove('is-drop-target');});
        card.addEventListener('dragover',event=>{if(assignmentPair(selected,item)){event.preventDefault();event.dataTransfer.dropEffect='link';card.classList.add('is-drop-target');}});
        card.addEventListener('dragleave',()=>card.classList.remove('is-drop-target'));
        card.addEventListener('drop',event=>{event.preventDefault();event.stopPropagation();card.classList.remove('is-drop-target');choose(selected,item);});
        const button=text('button','選択');button.type='button';button.className='normal-btn';button.setAttribute('aria-pressed','false');
        button.addEventListener('click',()=>{
            if(assignmentPair(selected,item)){choose(selected,item);return;}
            activeButton?.setAttribute('aria-pressed','false');selected=item;activeButton=button;button.setAttribute('aria-pressed','true');
            message.textContent=item.kind==='vehicle'?'右の伝票を選択してください。':'左の車両を選択してください。';
        });card.append(button);
    }
    return {
        render({vehicles,crews,orders,allOrders=orders,day}) {
            selected=null;activeButton=null;root.replaceChildren(message);
            message.textContent='伝票と車両をドラッグ＆ドロップ、または両方の「選択」ボタンで指定してください。最後に確認画面で保存します。';
            const columns=text('div');columns.className='dispatch-board-columns';
            const left=text('section'),right=text('section');left.append(text('h2','車両一覧'));right.append(text('h2',`${day} の伝票一覧（${orders.length}件）`));
            if(!vehicles.length)left.append(text('p','車両が登録されていません。'));
            for(const vehicle of vehicles) {
                const crew=crews.find(c=>String(c.vehicleId)===String(vehicle.id));
                const card=text('article');card.className='dispatch-card';card.dataset.vehicleCode=vehicle.code;
                card.append(text('h3',`${vehicle.code} ${vehicle.name}`),text('p',crew?`${crew.driverName??''}（運転）／${crew.memberNames??''}`:'日別編成が未登録です。先に乗車メンバーを登録してください。'));
                const assigned=allOrders.filter(o=>String(o.crewIds??'').split(',').includes(String(crew?.id)));
                for(const order of assigned){const detail=text('button',`${order.visitTime??''} ${order.requestNumber??''} ${order.title??''}`);detail.type='button';detail.className='normal-btn dispatch-assigned';detail.addEventListener('click',()=>onOpen(order));card.append(detail);}
                wire(card,{kind:'vehicle',code:vehicle.code},!!crew&&canEdit());left.append(card);
            }
            if(!orders.length)right.append(text('p','対象の伝票はありません。'));
            for(const order of orders) {
                const card=text('article');card.className='dispatch-card';card.dataset.orderId=order.orderId;
                card.append(text('h3',`${order.requestNumber??''} ${order.title??''}`),text('p',`${datePart(order.visitDate)} ${order.visitTime??''} ／ ${order.fullAddress??''}`),text('p',order.vehicleNames||'未配車'),text('p',`現場責任者：${order.leaderName??'未指定'}${Number(order.state)===2?'（完了）':''}`));
                if(order.scheduleMismatch)card.append(text('p','訪問日が変更されています。配車の確認が必要です。'));
                const detail=text('button','詳細・割当解除');detail.type='button';detail.className='normal-btn';detail.addEventListener('click',()=>onOpen(order));card.append(detail);
                wire(card,{kind:'order',id:order.orderId},Number(order.state)===0&&datePart(order.visitDate)===day&&canEdit());right.append(card);
            }
            columns.append(left,right);root.append(columns);
        }
    };
}
