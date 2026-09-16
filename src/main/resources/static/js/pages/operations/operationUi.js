import { RequestClient } from '../../core/api/RequestClient.js';
export const query = async (queryId, params={}) => {
    const result=await RequestClient.request({queryId,params});
    if(result.ok===false) throw new Error('処理に失敗しました。内容を確認して再度お試しください。');
    return result.data;
};
export const text=(tag,value='')=>{const el=document.createElement(tag);el.textContent=value??'';return el;};
export const localDay=()=>{const d=new Date();return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;};
export const datePart=value=>value?String(value).slice(0,10):'';
export const display=value=>value===null || value===undefined ? '' : typeof value==='boolean' ? (value?'有':'無') : String(value);
export function drawTable(heading,body,labels,rows,onOpen) {
    const header=text('tr');header.setAttribute('name','table-header');
    labels.forEach(label=>{const th=text('th',label);th.scope='col';header.append(th);});header.append(text('th','操作'));heading.replaceChildren(header);
    body.replaceChildren();
    rows.forEach(({data,values})=>{
        const tr=text('tr');tr.setAttribute('name','data-row');
        values.forEach((value,i)=>{const td=text('td',display(value));td.dataset.label=labels[i];tr.append(td);});
        const actions=text('td'),button=text('button','詳細');button.type='button';button.className='normal-btn';
        button.addEventListener('click',async()=>{button.disabled=true;try{await onOpen(data);}finally{button.disabled=false;}});
        actions.append(button);tr.append(actions);body.append(tr);
    });
}
export function historyView(root,history) {
    root.replaceChildren();
    if(!history?.length) root.append(text('p','履歴はありません。'));
    for(const row of history??[]) {
        const entry=text('details');entry.className='operation-history-entry';
        const processes={INSERT:'登録',UPDATE:'更新',DELETE:'無効化',BEFORE:'変更前',CREW_UPDATE:'編成変更'};
        entry.append(text('summary',`${row.logDate??''} ${row.editor??''} ${processes[row.process]??'変更'}（更新番号 ${row.version??''}）`));
        const labels={ownOfficeId:'営業所ID',code:'コード',name:'名称',plateNumber:'ナンバー',vehicleType:'車種',capacity:'乗車定員',recorderId:'ドラレコ識別情報',remarks:'備考',vehicleCode:'車両コード',vehicleName:'車両名',kind:'点検区分',dueDate:'期限',scheduledDate:'予定日',performedDate:'実施日',unavailableFrom:'使用不可開始',unavailableTo:'使用不可終了',odometer:'走行距離',cost:'費用',workDate:'運行日',employeeCode:'担当者コード',employeeName:'担当者名',isDriver:'ドライバー',score:'得点',qualificationCode:'資格コード',qualificationName:'資格名',category:'種類',grade:'区分',expiryRequired:'有効期限あり',driverLicense:'運転免許',acquiredDate:'取得日',expiryDate:'有効期限',renewalDate:'更新予定日',certificateNumber:'証番号',effectiveFrom:'適用開始',effectiveTo:'適用終了',healthInsurance:'健康保険',healthLastFour:'健康保険番号末尾4桁',pension:'年金',employmentInsurance:'雇用保険',employmentLastFour:'雇用保険番号末尾4桁',retirementBook:'退職金共済',confirmedDate:'確認日',examDate:'健診日',examType:'健診種類',nextDate:'次回予定日',systolic:'最高血圧',diastolic:'最低血圧',leaderName:'現場責任者'};
        const appendFields=(record,target)=>{
            const list=text('dl');
            for(const [key,value] of Object.entries(record)) {
                const camel=key.replace(/_([a-z])/g,(_,letter)=>letter.toUpperCase());
                if(!labels[camel])continue;
                list.append(text('dt',labels[camel]),text('dd',display(value)||'未入力'));
            }
            target.append(list);
        };
        appendFields(row,entry);
        for(const [key,label] of [['vehiclesJson','車両'],['membersJson','乗車メンバー']]) {
            if(!row[key])continue;
            try {const records=JSON.parse(row[key]);entry.append(text('h4',label));records.forEach(record=>appendFields(record,entry));}
            catch {entry.append(text('p','履歴の詳細を表示できませんでした。'));}
        }
        root.append(entry);
    }
}
