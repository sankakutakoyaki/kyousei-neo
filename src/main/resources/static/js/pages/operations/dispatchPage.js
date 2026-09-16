import {initCommon} from '../../bootstrap/initPage.js';
import {createCrudPage} from '../../core/page/createCrudPage.js';
import {registerController} from '../../application/controllerRegistry.js';
import {DialogService} from '../../core/ui/dialog/DialogService.js';
import {isMobileReadOnly,refreshMobileReadOnly} from '../../core/access/mobileReadOnly.js';
import {createDispatchRepositories} from '../../repositories/operations/DispatchRepository.js';
import {createVehicleColumns,createDispatchColumns} from './dispatch/columns.js';
import {createDispatchForm} from './dispatch/dispatchForm.js';
import {createDispatchBoard} from './dispatchBoard.js';
import {loadOwnOffices,fillOwnOffices} from './ownOffice.js';
import {localDay,datePart} from './operationUi.js';

export async function init() {
    await initCommon();
    const search=document.getElementById('plan-search'),status=document.getElementById('plan-status');
    fillOwnOffices(search.elements.ownOfficeId,await loadOwnOffices(),{unassigned:true});
    search.elements.workDate.value=localDay();
    const repositories=createDispatchRepositories();
    const safe=fn=>async(...args)=>{try{return await fn(...args);}catch(error){DialogService.error(error.message);}};
    const params=()=>({dateFrom:search.elements.workDate.value,dateTo:search.elements.workDate.value,ownOfficeId:search.elements.ownOfficeId.value});
    const openOrder=safe(async orderId=>{await orders.openForm('detail',orderId,{bulkMode:false});refreshMobileReadOnly();});
    const board=createDispatchBoard({
        message:document.getElementById('plan-instructions'),canEdit:()=>!isMobileReadOnly('operations'),workDate:()=>search.elements.workDate.value,onOpen:openOrder,
        onAssign:safe(async ({orderId,vehicleCode})=>{
            if(isMobileReadOnly('operations'))return;
            const detail=await repositories.orders.find({orderId});
            if(Number(detail.state)!==0||datePart(detail.visitDate)!==search.elements.workDate.value)throw new Error('伝票の日付・状態が変更されました。再検索してください。');
            await orders.openForm('detail',detail,{bulkMode:false});refreshMobileReadOnly();
            await orders.getDefaultForm().addVehicle(vehicleCode);
        })
    });
    let refreshRevision=0;
    const refresh=async()=>{
        const revision=++refreshRevision;
        repositories.invalidate();board.reset();status.textContent='読み込み中…';
        for(const controller of [vehicles,orders]){controller.dataTable.setData([]);controller.reload();}
        try{
            await Promise.all([vehicles.refresh(),orders.refresh()]);
            if(revision===refreshRevision)status.textContent='';
        }catch(error){if(revision===refreshRevision){status.textContent='読み込みに失敗しました。';DialogService.error(error.message);}}
    };
    const vehicles=createCrudPage({
        key:'dispatchVehicles',idKey:'id',tableId:'plan-vehicle-rows',footerId:'plan-vehicle-count',
        repository:repositories.vehicles,columns:createVehicleColumns(),buildParams:params,
        checkable:false,autoLoad:false,forms:{},rowClass:()=> 'dispatch-card',onDoubleClick:()=>{},
        onRendered:(table,rows)=>board.decorate(table,rows,'vehicle')
    });
    const orders=createCrudPage({
        key:'operationDispatch',idKey:'orderId',tableId:'plan-order-rows',footerId:'plan-order-count',
        repository:repositories.orders,columns:createDispatchColumns(),buildParams:params,
        checkable:false,autoLoad:false,rowClass:()=> 'dispatch-card',
        model:{filters:{unassigned:(row,value)=>!value||!row.vehicleNames}},
        forms:{detail:{create:createDispatchForm}},actions:{search:refresh,reload:refresh},
        onDoubleClick:row=>openOrder(row.orderId),
        onRendered:(table,rows)=>board.decorate(table,rows,'order')
    });
    registerController('dispatchVehicles',vehicles);registerController('operationDispatch',orders);
    vehicles.init();orders.init();
    search.addEventListener('submit',event=>{event.preventDefault();orders.executeAction('search');});
    search.elements.workDate.addEventListener('change',()=>orders.executeAction('search'));
    search.elements.ownOfficeId.addEventListener('change',()=>orders.executeAction('search'));
    search.elements.keyword.addEventListener('input',()=>orders.search(search.elements.keyword.value));
    search.elements.unassigned.addEventListener('change',()=>{orders.setFilter('unassigned',search.elements.unassigned.checked);orders.reload();});
    await orders.executeAction('search');
    const initialId=document.querySelector('main[data-order-id]')?.dataset.orderId;
    if(Number(initialId)>0)await openOrder(Number(initialId));
}
