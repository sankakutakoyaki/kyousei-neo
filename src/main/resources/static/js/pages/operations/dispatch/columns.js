const escapeHtml=value=>String(value??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const button=(label,action,orderId)=>`<button type="button" class="normal-btn" data-dispatch-action="${action}" ${orderId?`data-order-id="${Number(orderId)}"`:''}>${label}</button>`;
export const createVehicleColumns=()=>[{
    field:'code',label:'車両・乗車メンバー',sortable:true,
    render:v=>`<h3>${escapeHtml(v.code)} ${escapeHtml(v.name)}</h3>
        <p>${v.crew?`${escapeHtml(v.crew.driverName)}（運転）／${escapeHtml(v.crew.memberNames)}`:'日別編成が未登録です。'}</p>
        ${v.assignedOrders.map(o=>button(escapeHtml(`${o.visitTime??''} ${o.requestNumber??''} ${o.title??''}`),'detail',o.orderId)).join('')}
        ${v.crew?button('選択','select'):''}`
}];
export const createDispatchColumns=()=>[{
    field:'visitTime',label:'訪問時間・伝票・配車',sortable:true,
    render:o=>`<h3>${escapeHtml(o.requestNumber)} ${escapeHtml(o.title)}</h3>
        <p>${escapeHtml(o.visitDate)} ${escapeHtml(o.visitTime)} ／ ${escapeHtml(o.fullAddress)}</p>
        <p>${escapeHtml(o.vehicleNames||'未配車')}</p>
        <p>現場責任者：${escapeHtml(o.leaderName||'未指定')}${Number(o.state)===2?'（完了）':''}</p>
        ${o.scheduleMismatch?'<p>訪問日が変更されています。配車を確認してください。</p>':''}
        ${button('詳細・割当解除','detail',o.orderId)}${Number(o.state)===0?button('選択','select'):''}`
}];
