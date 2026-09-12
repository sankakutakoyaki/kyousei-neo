"use strict";
import {RequestClient} from '../../../core/api/RequestClient.js';
import {isMobileDevice} from '../../../core/access/mobileReadOnly.js';
import {openMsgDialog} from '../../../core/ui/dialog/dialogCore.js';

export const remainingQuantity = (item, entries) => Math.max(0, Number(item.itemQuantity) - entries.filter(r => !r.cancelledAt).reduce((n, r) => n + Number(r.quantity), 0));
// 社内LANのHTTP接続でも使えるよう、randomUUIDに依存しない。
export function newRequestId() {
    const bytes = crypto.getRandomValues(new Uint8Array(16));
    bytes[6] = (bytes[6] & 15) | 64;
    bytes[8] = (bytes[8] & 63) | 128;
    const hex = Array.from(bytes, b => b.toString(16).padStart(2, '0')).join('');
    return `${hex.slice(0,8)}-${hex.slice(8,12)}-${hex.slice(12,16)}-${hex.slice(16,20)}-${hex.slice(20)}`;
}
const today = () => { const d = new Date(); return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`; };
async function request(queryId, params) {
    const response = await RequestClient.request({queryId, params});
    if (!response.ok) throw new Error('処理に失敗しました。内容を確認してください。');
    return response.data;
}

export async function openArrivalHistory(controller, id) {
    const initial = await request('orderItemArrivalDetail', {orderItemId:id});
    const dialog = document.createElement('dialog');
    dialog.className = 'arrival-dialog';
    dialog.setAttribute('aria-label', '入荷登録・履歴');
    dialog.innerHTML = `<h2>入荷登録・履歴</h2><p class="arrival-product"></p><p class="arrival-summary"></p>
        <form class="arrival-entry"><label>入荷日<input name="arrivalDate" type="date" required></label>
        <label>今回の入荷数<input name="quantity" type="number" inputmode="numeric" min="1" step="1" required></label>
        <button type="submit" class="normal-btn">入荷登録</button><button type="button" class="arrival-reset normal-btn" hidden>訂正をやめる</button></form>
        <p class="arrival-error" role="alert"></p><div class="arrival-history"></div><button type="button" class="arrival-close normal-btn">閉じる</button>`;
    document.body.append(dialog);
    const form = dialog.querySelector('form');
    const error = dialog.querySelector('.arrival-error');
    let data = initial, correcting = null, busy = false, requestId = newRequestId();
    let uncertain = false;
    const reset = () => {
        correcting = null; requestId = newRequestId();
        form.elements.arrivalDate.value = today();
        form.elements.arrivalDate.max = today();
        form.elements.quantity.value = remainingQuantity(data.item, data.entries);
        form.elements.quantity.max = remainingQuantity(data.item, data.entries);
        form.querySelector('[type="submit"]').textContent = '入荷登録';
        form.elements.arrivalDate.disabled = false;
        form.elements.quantity.disabled = false;
        form.querySelector('.arrival-reset').hidden = true;
        form.hidden = remainingQuantity(data.item, data.entries) === 0;
    };
    const perform = async (queryId, params) => {
        if (busy || uncertain) return;
        busy = true; error.textContent = '';
        dialog.querySelectorAll('button').forEach(b => b.disabled = true);
        try {
            await request(queryId, {orderItemId:id, version:data.item.version, requestId, ...params});
            dialog.close();
            try { await controller.refresh(); } catch { /* 登録成功と再読込失敗を区別する。 */
                openMsgDialog({message:'登録は完了しました。一覧を再読み込みしてください。', color:'blue'}); return;
            }
            openMsgDialog({message: queryId === 'orderItemArrivalCancel' ? '入荷を取り消しました。' : queryId === 'orderItemArrivalCorrect' ? '入荷履歴を訂正しました。' : '入荷登録しました。', color:'blue'});
        } catch (e) {
            // 応答が失われた可能性もあるため、再読込で確定済み状態を確認してから次の入力を許可する。
            try { data = await request('orderItemArrivalDetail', {orderItemId:id}); render(); }
            catch { uncertain = true; form.hidden = true; }
            error.textContent = `${e.message} 最新状態を確認し、必要なら開き直してください。`;
        } finally {
            busy = false; dialog.querySelectorAll('button').forEach(b => b.disabled = false);
        }
    };
    const render = () => {
        const left = remainingQuantity(data.item, data.entries);
        dialog.querySelector('.arrival-product').textContent = `${data.item.itemName ?? ''} ${data.item.itemModel ?? ''}`;
        dialog.querySelector('.arrival-summary').textContent = `入荷済み ${Number(data.item.itemQuantity)-left} / ${data.item.itemQuantity}　残り ${left}`;
        const history = dialog.querySelector('.arrival-history'); history.replaceChildren();
        if (!data.entries.length) history.textContent = '入荷履歴はありません。';
        for (const row of data.entries) {
            const card = document.createElement('section');
            const description = document.createElement('p');
            description.textContent = `${row.arrivalDate}：${row.quantity}個　${row.cancelledAt ? '取消・訂正済み' : '有効'}（${row.registeredBy}）${row.replacesId ? ' 訂正登録' : ''}`;
            card.append(description);
            const actions = document.createElement("div");
            actions.className = "arrival-history-actions";
            if (!row.cancelledAt && !isMobileDevice()) {
                for (const [label, action] of [['訂正', () => {
                    if (isMobileDevice() || uncertain) return;
                    correcting = row; requestId = newRequestId(); form.hidden = false;
                    form.elements.arrivalDate.value = row.arrivalDate;
                    form.elements.quantity.value = row.quantity;
                    form.elements.quantity.max = left + Number(row.quantity);
                    form.querySelector('[type="submit"]').textContent = '訂正を保存';
                    form.querySelector('.arrival-reset').hidden = false;
                    form.elements.quantity.focus();
                }], ['取消', () => {
                    if (isMobileDevice() || uncertain) return;
                    if (window.confirm(`${row.arrivalDate} の ${row.quantity}個の入荷を取り消しますか？`)) {
                        requestId = newRequestId();
                        perform('orderItemArrivalCancel', {arrivalId:row.arrivalId});
                    }
                }]]) {
                    const button = document.createElement('button'); button.type = 'button'; button.textContent = label;
                    button.className = 'normal-btn arrival-maintenance'; button.addEventListener('click', action); actions.append(button);
                }
            }
            if (actions.childElementCount) card.append(actions);
            history.append(card);
        }
        reset();
    };
    form.addEventListener('submit', event => {
        event.preventDefault();
        if (correcting && isMobileDevice()) return;
        if (!form.reportValidity()) return;
        perform(correcting ? 'orderItemArrivalCorrect' : 'orderItemArrival', {
            arrivalId:correcting?.arrivalId, quantity:Number(form.elements.quantity.value), arrivalDate:form.elements.arrivalDate.value
        });
    });
    form.querySelector('.arrival-reset').addEventListener('click', reset);
    dialog.querySelector('.arrival-close').addEventListener('click', () => dialog.close());
    dialog.addEventListener('cancel', event => { if (busy) event.preventDefault(); });
    dialog.addEventListener('close', () => dialog.remove(), {once:true});
    const mobileMedia = window.matchMedia('(max-width: 560px), (pointer: coarse) and (max-width: 960px)');
    const onResize = () => { if (!busy) render(); };
    mobileMedia.addEventListener('change', onResize);
    dialog.addEventListener('close', () => mobileMedia.removeEventListener('change', onResize), {once:true});
    render(); dialog.showModal();
}
