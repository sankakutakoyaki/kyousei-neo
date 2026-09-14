import {createDispatchBoard} from './dispatchBoard.js';
import {loadOwnOffices,fillOwnOffices} from './ownOffice.js';
import { initCommon } from '../../bootstrap/initPage.js';
import { FormController } from '../../application/FormController.js';
import { DialogService } from '../../core/ui/dialog/DialogService.js';
import { isMobileDevice,isMobileReadOnly,refreshMobileReadOnly } from '../../core/access/mobileReadOnly.js';
import { query,text,localDay,datePart,historyView } from './operationUi.js';
export async function init() {
    await initCommon();
    const search=document.getElementById('plan-search'),formEl=document.getElementById('operation-dispatch-form');
    const status=document.getElementById('plan-status'),code=document.getElementById('plan-vehicle-code'),leader=document.getElementById('plan-leader-code');
    status.setAttribute('role','status');let current={},vehicles=[],original='',rows=[],generation=0,allVehicles=[],crews=[];
    const editable=()=>Number(current.state)===0&&!isMobileReadOnly('operations');
    const selectedLeader=()=>vehicles.flatMap(v=>v.members??[]).find(m=>String(m.employeeCode)===leader.value.trim());
    const data=()=>({vehicles:vehicles.map(v=>({crewId:v.crewId,version:v.version})),leaderId:selectedLeader()?.employeeId??null});
    const signature=()=>JSON.stringify({...data(),leaderCode:leader.value.trim()});
    const safe=fn=>async(...args)=>{try{return await fn(...args);}catch(e){DialogService.error(e.message);}};
    function renderVehicles() {
        const root=document.getElementById('plan-vehicles');root.replaceChildren();
        for(const vehicle of vehicles) {
            const section=text('section');section.append(text('h3',`${vehicle.vehicleCode} ${vehicle.vehicleName??''}`));
            section.append(text('p',vehicle.members.map(m=>`${m.employeeCode} ${m.employeeName}${m.isDriver?'（運転）':''}`).join('、')));
            for(const m of vehicle.members) if(m.qualifications?.length) section.append(text('p',`${m.employeeName}：${m.qualifications.map(q=>`${q.qualificationName}（${q.expiryDate??'期限設定なし'}）`).join('、')}`));
            for(const warning of vehicle.warnings??[]) {const p=text('p',warning);p.className='operation-warnings';section.append(p);}
            const remove=text('button','この車両を外す');remove.type='button';remove.className='normal-btn';remove.disabled=!editable();
            remove.addEventListener('click',()=>{vehicles=vehicles.filter(v=>v!==vehicle);renderVehicles();form.updateSubmitState();});section.append(remove);root.append(section);
        }
        document.getElementById('plan-leader-name').textContent=selectedLeader()?.employeeName??(leader.value?'選択した車両の乗車メンバーに該当しません。':'乗車メンバーから責任者を指定してください。');
    }
    const form=new FormController({formId:'operation-dispatch-form',key:'id',idKey:'id',controller:{key:'operations',setBulkMode(){}},changeTargetSelector:'#plan-fields',submitText:'保存',cancelText:'閉じる',
        hasAdditionalChanges:()=>editable()&&signature()!==original,
        buildAdditionalPayload:()=>({orderId:current.orderId,orderVersion:current.orderVersion,mobile:isMobileDevice(),...data()}),
        onOpen:async item=>{
            current=item;code.value='';vehicles=(item.vehicles??[]).map(v=>({...v,members:item.members.filter(m=>Number(m.crewId)===Number(v.crewId))}));
            leader.value=item.members.find(m=>Number(m.employeeId)===Number(item.leaderId))?.employeeCode??'';
            document.getElementById('plan-summary').textContent=`${item.requestNumber??''} ${item.title??''} ／ ${datePart(item.visitDate)||'日程未定'} ${item.visitTime??''}`;
            document.getElementById('plan-readonly').textContent=editable()?'':'完了済みの伝票、またはこの端末の権限では閲覧のみです。';
            code.disabled=leader.disabled=Number(item.state)!==0;document.getElementById('plan-add').disabled=!editable();renderVehicles();original=signature();
            historyView(document.getElementById('plan-history'),item.history);
            document.getElementById('plan-legacy').replaceChildren(...item.legacyAssignments.map(a=>text('p',`${a.employeeName} ／ ${a.assignedAt}${a.cancelledAt?'（解除済み）':''}`)));
        },
        validateBusiness:()=>{if(vehicles.length&&!selectedLeader())throw new Error('乗車メンバーの担当者コードで現場責任者を指定してください。');},
        saveHandler:async payload=>{if(!editable())throw new Error('配車を編集できません。');return {data:await query('operationDispatchSave',payload),count:1};},
        afterSave:()=>load()
    });
    form.confirmSave=()=>DialogService.confirm(vehicles.length?'配車と現場責任者を保存しますか？':'この伝票の車両の割り当てをすべて解除しますか？');
    const base=form.canSubmit.bind(form);form.canSubmit=()=>editable()&&base();
    leader.addEventListener('input',()=>{renderVehicles();form.updateSubmitState();});
    async function add() {
        if(!editable())return;if(!current.visitDate)throw new Error('受注画面で訪問日を先に登録してください。');
        const found=await query('operationCrewDetail',{vehicleCode:code.value.trim(),workDate:datePart(current.visitDate)});
        if(vehicles.some(v=>Number(v.crewId)===Number(found.id)))throw new Error('すでに追加した車両です。');
        vehicles.push({...found,crewId:found.id});code.value='';code.focus();renderVehicles();form.updateSubmitState();
    }
    document.getElementById('plan-add').addEventListener('click',safe(add));code.addEventListener('keydown',e=>{if(e.key==='Enter'){e.preventDefault();safe(add)();}});
    formEl.addEventListener('submit',e=>{e.preventDefault();form.save(formEl);});
    const openOrder=safe(async row=>{await form.open(await query('operationDispatchDetail',{orderId:row.orderId}));refreshMobileReadOnly();});
    const board=createDispatchBoard(document.getElementById('dispatch-board'),{
        canEdit:()=>!isMobileReadOnly('operations'),onOpen:openOrder,
        onAssign:safe(async ({orderId,vehicleCode})=>{
            if(isMobileReadOnly('operations'))return;
            const detail=await query('operationDispatchDetail',{orderId});
            if(Number(detail.state)!==0 || datePart(detail.visitDate)!==search.elements.workDate.value)throw new Error('伝票の日付・状態が変更されました。再検索してください。');
            await form.open(detail);refreshMobileReadOnly();
            code.value=vehicleCode;await add();
        })
    });
    const render=()=>{
        const term=search.elements.keyword.value.trim().toLowerCase();
        const result=rows.filter(r=>(!search.elements.unassigned.checked||!r.vehicleNames)&&Object.values(r).join(' ').toLowerCase().includes(term));
        board.render({vehicles:allVehicles,crews,orders:result,allOrders:rows,day:search.elements.workDate.value});
        status.textContent=`${result.length}件`;
    };
    const load=async()=>{
        const request=++generation;status.textContent='読み込み中…';
        // 古い日付のカードを操作できないよう検索開始時に外す。
        rows=[];allVehicles=[];crews=[];
        document.getElementById('dispatch-board').replaceChildren(text('p','読み込み中…'));
        try {
            const day=search.elements.workDate.value;
            const [result,vehicleRows,crewRows]=await Promise.all([
                query('operationDispatchList',{dateFrom:day,dateTo:day,ownOfficeId:search.elements.ownOfficeId.value}),
                query('operationList',{entity:'VEHICLE'}),query('operationList',{entity:'CREW',workDate:day})
            ]);
            if(request!==generation||!search.isConnected)return;
            rows=result;allVehicles=vehicleRows;crews=crewRows;render();
        }catch(e){if(request===generation){rows=[];allVehicles=[];crews=[];status.textContent='読み込みに失敗しました。';DialogService.error(e.message);}}
    };
    fillOwnOffices(search.elements.ownOfficeId,await loadOwnOffices(),{unassigned:true});
    search.elements.workDate.value=localDay();
    search.addEventListener('submit',e=>{e.preventDefault();load();});
    search.elements.workDate.addEventListener('change',load);search.elements.ownOfficeId.addEventListener('change',load);
    search.elements.keyword.addEventListener('input',render);search.elements.unassigned.addEventListener('change',render);await load();
    const initialId=document.querySelector('main[data-order-id]')?.dataset.orderId;
    if(Number(initialId)>0)await safe(async()=>{await form.open(await query('operationDispatchDetail',{orderId:Number(initialId)}));refreshMobileReadOnly();})();
}
