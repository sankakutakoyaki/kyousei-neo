"use strict";
import {RequestClient} from '../../../core/api/RequestClient.js';
import {isMobileDevice} from '../../../core/access/mobileReadOnly.js';
import {openMsgDialog} from '../../../core/ui/dialog/dialogCore.js';
import {DialogService} from '../../../core/ui/dialog/DialogService.js';

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

// 共通の確認画面をネイティブdialogの前面に置き、終了後は元の場所へ戻す。
export async function confirmArrivalCancellation(dialog, message) {
    const area = document.getElementById('msg-dialog-area');
    if (!area) throw new Error('確認画面が見つかりません。画面を再読み込みしてください。');
    const marker = document.createComment('arrival-confirm');
    const focused = document.activeElement;
    area.before(marker);
    dialog.append(area);
    try {
        return await DialogService.confirm(message);
    } finally {
        marker.replaceWith(area);
        if (focused?.isConnected) focused.focus();
    }
}

export async function openArrivalHistory(controller, id) {
    const initial = await request('orderItemArrivalDetail', {orderItemId:id});
    const template = document.getElementById('arrival-dialog-template');
    if (!template) throw new Error('入荷画面が見つかりません。画面を再読み込みしてください。');
    const dialog = template.content.querySelector('dialog').cloneNode(true);
    const footer = dialog.querySelector('.dialog-footer');
    footer.classList.add('arrival-history-actions');
    const actionTemplates = {
        '訂正': footer.querySelector('[name="submitBtn"]'),
        '取消': footer.querySelector('[name="cancelBtn"]')
    };
    footer.querySelector('[name="footerCloseBtn"]').classList.add('arrival-close');
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
        dialog.querySelector('.arrival-product').textContent = data.item.itemName ?? '';
        dialog.querySelector('.arrival-model').textContent = data.item.itemModel ?? '';
        const footer = dialog.querySelector('.arrival-history-actions');
        const close = dialog.querySelector('.arrival-close');
        footer.replaceChildren(close);
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
                }], ['取消', async () => {
                    if (isMobileDevice() || uncertain || busy) return;
                    busy = true;
                    let confirmed = false;
                    try {
                        confirmed = await confirmArrivalCancellation(dialog, `${row.arrivalDate} の ${row.quantity}個の入荷を取り消しますか？`);
                    } catch (e) {
                        error.textContent = e.message;
                    } finally { busy = false; }
                    if (confirmed) {
                        requestId = newRequestId();
                        perform('orderItemArrivalCancel', {arrivalId:row.arrivalId});
                    }
                }]]) {
                    const button = actionTemplates[label].cloneNode(true);
                    button.classList.add('arrival-maintenance'); button.addEventListener('click', action); actions.append(button);
                }
            }
            if (actions.childElementCount) {
                const buttons = Array.from(actions.children);
                const label = document.createElement('label');
                label.className = 'arrival-history-selection';
                const radio = document.createElement('input');
                radio.type = 'radio'; radio.name = 'arrival-history-selection';
                radio.setAttribute('aria-label', `${row.arrivalDate}の${row.quantity}個の履歴を選択`);
                label.append(radio, document.createTextNode('この履歴を操作'));
                card.prepend(label);
                radio.addEventListener('change', () => {
                    if (busy) return;
                    reset();
                    footer.replaceChildren(...buttons, close);
                });
                radio.checked = true;
                footer.replaceChildren(...buttons, close);
            }
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
    const closeHistory = () => { if (!busy) dialog.close(); };
    dialog.querySelector('.arrival-close').addEventListener('click', closeHistory);
    dialog.querySelector('.dialog-header [name="closeBtn"]').addEventListener('click', closeHistory);
    dialog.addEventListener('cancel', event => { if (busy) event.preventDefault(); });
    dialog.addEventListener('close', () => dialog.remove(), {once:true});
    const mobileMedia = window.matchMedia('(max-width: 560px), (pointer: coarse) and (max-width: 960px)');
    const onResize = () => { if (!busy) render(); };
    mobileMedia.addEventListener('change', onResize);
    dialog.addEventListener('close', () => mobileMedia.removeEventListener('change', onResize), {once:true});
    render(); dialog.showModal();
}
