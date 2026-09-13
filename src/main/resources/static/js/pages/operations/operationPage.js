import { initCommon } from '../../bootstrap/initPage.js';
import { FormController } from '../../application/FormController.js';
import { DialogService } from '../../core/ui/dialog/DialogService.js';
import { isMobileDevice, isMobileReadOnly, refreshMobileReadOnly } from '../../core/access/mobileReadOnly.js';
import { AttachmentRepository } from '../../repositories/attachment/AttachmentRepository.js';
import { qualificationInView } from './qualificationViews.js';
import { query,text,localDay,datePart,drawTable,historyView } from './operationUi.js';

export async function init() {
    await initCommon();
    const page=document.querySelector('main[data-op-entity]'),entity=page.dataset.opEntity;
    const canWrite=page.dataset.opWrite==='true',privateAccess=page.dataset.opPrivate==='true';
    const formEl=document.getElementById('operation-form'),fields=[...document.querySelectorAll('#operation-fields input')];
    const search=document.getElementById('operation-search'),status=document.getElementById('operation-status');status.setAttribute('role','status');
    const getField=key=>fields.find(f=>f.dataset.key===key);
    let rows=[],current={},members=[],original='',generation=0,groups=[];
    const editable=()=>canWrite && !isMobileReadOnly('operations');
    const safe=fn=>async(...args)=>{try{return await fn(...args);}catch(e){DialogService.error(e.message);}};
    const showMembers=()=>{
        const root=document.getElementById('crew-members');if(!root)return;root.replaceChildren();
        for(const m of members) {
            const line=text('div');line.className='operation-member';
            const radio=document.createElement('input');radio.type='radio';radio.name='crew-driver';radio.dataset.submit='none';radio.checked=m.isDriver;radio.disabled=!canWrite;
            radio.addEventListener('change',()=>{members.forEach(x=>x.isDriver=x===m);showMembers();form.updateSubmitState();});
            const label=text('label');label.append(radio,text('span','ドライバー'));
            const remove=text('button','外す');remove.type='button';remove.className='normal-btn';remove.disabled=!canWrite;remove.dataset.mobileWrite='';
            remove.addEventListener('click',()=>{members=members.filter(x=>x!==m);showMembers();form.updateSubmitState();});
            line.append(text('span',`${m.employeeCode} ${m.employeeName}`),label,remove);root.append(line);
        }
    };
    const payloadMembers=()=>members.map(m=>({employeeCode:m.employeeCode,isDriver:m.isDriver===true}));
    const parentType=entity==='QUALIFICATION'?'OP_QUALIFICATION':'OP_INSPECTION';
    const attachmentRoot=document.getElementById('operation-files');
    const refreshFiles=async()=>{
        if(!attachmentRoot)return;attachmentRoot.replaceChildren();
        if(!current.id){attachmentRoot.append(text('p','先に記録を保存し、詳細を開き直してください。'));return;}
        groups=await AttachmentRepository.groups(parentType,current.id);
        for(const group of groups) for(const file of group.files??[]) {
            const p=text('p'),a=text('a',file.displayName);a.href=AttachmentRepository.contentUrl(file.attachmentId);a.target='_blank';a.rel='noopener';p.append(a);attachmentRoot.append(p);
        }
    };
    const form=new FormController({
        formId:'operation-form',key:'id',idKey:'id',controller:{key:'operations',setBulkMode(){}},
        changeTargetSelector:'#operation-fields',submitText:'保存',cancelText:'閉じる',
        hasAdditionalChanges:()=>entity==='CREW' && JSON.stringify(payloadMembers())!==original,
        buildAdditionalPayload:()=>({entity,mobile:isMobileDevice(),...(entity==='CREW'?{members:payloadMembers(),impacts:current.impacts??[]}: {})}),
        onOpen:async data=>{
            current=data;members=structuredClone(data.members??[]);original=JSON.stringify(payloadMembers());
            fields.forEach(f=>{
                f.disabled=!canWrite;
                f.readOnly=Boolean(data.id) && ((['VEHICLE','QUALIFICATION_TYPE'].includes(entity) && f.dataset.key==='code') || (entity==='CREW' && ['workDate','vehicleCode'].includes(f.dataset.key)) || (entity==='SCORE' && ['workDate','vehicleCode','employeeCode'].includes(f.dataset.key)));
                if(f.type==='date')f.value=datePart(data[f.dataset.key]);
            });
            showMembers();
            const impact=document.getElementById('crew-impacts');if(impact)impact.textContent=(data.impacts??[]).length?`変更が反映される未完了伝票：${data.impacts.map(x=>x.requestNumber||x.orderId).join('、')}`:'未完了伝票への影響はありません。';
            historyView(document.getElementById('operation-history'),[...(data.history??[]),...(data.memberHistory??[])]);
            const del=document.getElementById('operation-delete');del.hidden=!data.id||!canWrite;del.disabled=!editable();
            await refreshFiles();
        },
        resetAdditional:()=>{members=structuredClone(current.members??[]);showMembers();},
        saveHandler:async payload=>{if(!editable())throw new Error('編集権限がありません。');return {data:await query('operationSave',payload),count:1};},
        afterSave:()=>load()
    });
    const baseCanSubmit=form.canSubmit.bind(form);form.canSubmit=()=>editable() && baseCanSubmit();
    form.confirmSave=()=>DialogService.confirm(entity==='CREW' && current.impacts?.length ? `${current.impacts.map(x=>x.requestNumber||x.orderId).join('、')} の乗車メンバーも更新します。保存しますか？`:'保存しますか？');
    formEl.addEventListener('submit',e=>{e.preventDefault();form.save(formEl);});
    document.getElementById('operation-delete').addEventListener('click',safe(async()=>{
        if(!editable() || !current.id)return;
        if(!await DialogService.confirm('この登録を無効にしますか？変更履歴は残ります。'))return;
        await query('operationSave',{entity,id:current.id,version:current.version,delete:true,mobile:isMobileDevice()});
        DialogService.close('operation-form');await load();
    }));
    document.getElementById('operation-create')?.addEventListener('click',safe(()=>{
        if(!editable())return;
        return form.open({id:0,...(entity==='CREW'||entity==='SCORE'?{workDate:search.elements.workDate.value}:{}),members:[]});
    }));
    for(const key of ['vehicleCode','employeeCode','qualificationCode']) {
        const input=getField(key);if(!input)continue;
        const label=text('small');input.after(label);
        input.addEventListener('change',safe(async()=>{
            label.textContent='';if(!input.value.trim())return;
            const row=await query('operationCodeDetail',{kind:key==='vehicleCode'?'vehicle':key==='employeeCode'?'employee':'qualification',code:input.value.trim()});
            label.textContent=row.name;
        }));
    }
    document.getElementById('crew-add')?.addEventListener('click',safe(addMember));
    document.getElementById('crew-employee-code')?.addEventListener('keydown',e=>{if(e.key==='Enter'){e.preventDefault();safe(addMember)();}});
    async function addMember() {
        if(!editable())return;
        const input=document.getElementById('crew-employee-code'),code=input.value.trim();if(!code)return;
        const row=await query('operationCodeDetail',{kind:'employee',code});
        if(members.some(m=>Number(m.employeeId)===Number(row.id)))throw new Error('登録済みの担当者です。');
        members.push({employeeId:row.id,employeeCode:row.code,employeeName:row.name,isDriver:members.length===0});input.value='';input.focus();showMembers();form.updateSubmitState();
    }
    document.getElementById('crew-copy')?.addEventListener('click',safe(async()=>{
        if(!editable())return;
        const day=getField('workDate').value,vehicleCode=getField('vehicleCode').value.trim();
        if(!day||!vehicleCode)throw new Error('運行日と車両コードを入力してください。');
        const previous=new Date(day+'T12:00:00');previous.setDate(previous.getDate()-1);
        const d=`${previous.getFullYear()}-${String(previous.getMonth()+1).padStart(2,'0')}-${String(previous.getDate()).padStart(2,'0')}`;
        const data=await query('operationCrewDetail',{workDate:d,vehicleCode});
        if(members.length && !await DialogService.confirm('入力中の乗車メンバーを前日の編成に置き換えますか？'))return;
        members=data.members;showMembers();form.updateSubmitState();
    }));
    document.getElementById('operation-upload')?.addEventListener('click',safe(async()=>{
        if(!editable()||!current.id)throw new Error('先に保存済みの記録を開いてください。');
        const input=document.getElementById('operation-file');if(!input.files.length)return;
        let group=groups[0];
        if(!group){await AttachmentRepository.createGroup(parentType,current.id,'書類');groups=await AttachmentRepository.groups(parentType,current.id);group=groups[0];}
        const button=document.getElementById('operation-upload');button.disabled=true;
        try{for(const file of input.files){const result=await AttachmentRepository.upload(parentType,current.id,group.attachmentGroupId,[file]);if(result.ok===false)throw new Error("書類の保存に失敗しました。");}input.value='';await refreshFiles();}finally{button.disabled=false;}
    }));
    const columns={
        VEHICLE:[['code','車両コード'],['name','車両名'],['plateNumber','ナンバー'],['vehicleType','車種'],['capacity','定員']],
        INSPECTION:[['vehicleCode','車両コード'],['kind','点検種別'],['dueDate','期限'],['scheduledDate','予定日'],['performedDate','実施日']],
        CREW:[['workDate','運行日'],['vehicleCode','車両コード'],['driverName','ドライバー'],['memberNames','乗車メンバー'],['remarks','備考']],
        SCORE:[['workDate','運行日'],['vehicleCode','車両コード'],['employeeName','ドライバー'],['score','得点'],['remarks','備考']],
        QUALIFICATION_TYPE:[['code','資格コード'],['name','名称'],['category','区分'],['grade','等級'],['expiryRequired','有効期限あり']],
        QUALIFICATION:[['employeeCode','担当者コード'],['employeeName','氏名'],['qualificationName','資格'],['expiryDate','有効期限'],['renewalDate','更新予定']],
        LABOR:[['employeeCode','担当者コード'],['employeeName','氏名'],['effectiveFrom','適用開始'],['effectiveTo','適用終了'],['confirmedDate','確認日']],
        HEALTH:[['employeeCode','担当者コード'],['employeeName','氏名'],['examDate','診断日'],['examType','種類'],['nextDate','次回予定']]
    }[entity];
    const render=()=>{
        const term=search.elements.keyword.value.trim().toLowerCase();const filtered=rows.filter(r=>(entity!=='QUALIFICATION'||qualificationInView(r,page.dataset.opView,localDay()))).filter(r=>Object.values(r).join(' ').toLowerCase().includes(term));
        drawTable(document.getElementById('operation-heading'),document.getElementById('operation-rows'),columns.map(c=>c[1]),filtered.map(data=>({data,values:columns.map(([key])=>key==='score'&&data[key]==null?'未入力':data[key])})),safe(async row=>{
            if(entity==='QUALIFICATION'&&!privateAccess){DialogService.info(`${row.qualificationName}：${row.expiryDate?`有効期限 ${datePart(row.expiryDate)}`:'有効期限の設定なし'}`);return;}
            await form.open(await query('operationDetail',{entity,id:row.id}));refreshMobileReadOnly();
        }));status.textContent=`${filtered.length}件`;
    };
    const load=async()=>{
        const token=++generation;status.textContent='読み込み中…';
        try{const result=await query('operationList',{entity,...(search.elements.workDate?{workDate:search.elements.workDate.value}:{})});if(token!==generation||!page.isConnected)return;rows=result??[];render();}
        catch(e){if(token===generation){status.textContent='読み込みに失敗しました。';DialogService.error(e.message);}}
    };
    document.getElementById('operation-guide').textContent={CREW:'日付ごとに車両と乗車メンバーを登録します。配車は「運行 → 配車」で登録します。',SCORE:'得点は手入力です。未入力は0点と区別します。運転者は乗車メンバーからコードで指定してください。',INSPECTION:'未実施の予定と実施済みの履歴を管理します。使用不可期間は編成登録時にも確認されます。',LABOR:'保険・年金の適用期間を管理します。番号は提出用の末尾4桁以内です。',HEALTH:'健康診断の記録は管理担当者のみ参照できます。',QUALIFICATION:'資格・免許・講習・教育の履歴です。更新時は前の記録を残して新規登録してください。'}[entity]??'コードを指定して登録・検索できます。';
    if(entity==='QUALIFICATION' && page.dataset.opView==='renewal')document.getElementById('operation-guide').textContent='有効期限が本日から90日以内、または更新予定日が90日以内（予定超過を含む）の資格を表示します。期限切れは別タブで確認できます。';
    search.addEventListener('submit',e=>{e.preventDefault();load();});search.elements.keyword.addEventListener('input',render);
    if(search.elements.workDate)search.elements.workDate.value=localDay();
    await load();
}
