import {installOrderEnumCache} from './fixtures/orderEnumCache.mjs';
import {orderEnumLabel,workResultLabel,orderStatusLabel,photoCategoryLabel,isSimpleWorkResult} from '../../main/resources/static/js/components/order/orderEnums.js';
installOrderEnumCache();
import test from 'node:test';
import assert from 'node:assert/strict';
import vm from 'node:vm';
import {readFileSync} from 'node:fs';
const source=readFileSync(new URL('../../main/resources/static/js/components/order/OrderPhotoPanel.js',import.meta.url),'utf8').replace(/^import .*;\n/gm,'').replace('export class OrderPhotoPanel','class OrderPhotoPanel');
class Element {
    constructor(){this.value='';this.textContent='';this.isConnected=true;this.listeners={};}
    addEventListener(type,fn){this.listeners[type]=fn;}
    querySelectorAll(){return [];}
    replaceChildren(){}
    close(){}
}
function harness(fetcher=async()=>({data:[]})) {
    const root=new Element(),nodes=new Map(),calls=[];
    root.querySelector=key=>{if(!nodes.has(key))nodes.set(key,new Element());return nodes.get(key);};
    class FormData {constructor(){this.values=[];}append(k,v){this.values.push([k,v]);}}
    let serial=0;const context=vm.createContext({photoCategoryLabel,URLSearchParams,FormData,crypto:{randomUUID:()=>`request-${++serial}`},DialogService:{confirm:async()=>true},apiFetch:async(url,options)=>{calls.push({url,options});return fetcher(url,options);}});
    vm.runInContext(source+'\nglobalThis.create=root=>new OrderPhotoPanel(root);',context);
    return {root,panel:context.create(root),calls};
}
test('late photo response cannot replace photos for another order',async()=>{
    let first;const h=harness(url=>url.includes('orderId=1&')?new Promise(r=>first=r):Promise.resolve({data:[]}));
    const pending=h.panel.open(1,'2026-10-18');
    await h.panel.open(2,'2026-10-19');first({data:[]});await pending;
    assert.equal(h.panel.orderId,2);assert.equal(h.panel.el('day').value,'2026-10-19');
    assert.equal(h.calls.length,2);assert.match(h.calls[1].url,/orderId=2/);
});
test('upload keeps the original order and date when the user switches details',async()=>{
    let uploaded;const h=harness((url,options)=>options.method==='GET'?Promise.resolve({data:[]}):new Promise(r=>uploaded=r));
    await h.panel.open(1,'2026-10-18');h.panel.el('category').value='COMPLETED';
    const pending=h.panel.upload([{size:10}]);
    await h.panel.open(2,'2026-10-19');uploaded({data:[1]});await pending;
    const post=h.calls.find(call=>call.options.method!=='GET');
    assert.equal(post.url,'/api/order-photos/1');
    assert.deepEqual(Array.from(post.options.data.values).find(([k])=>k==='workDate'),['workDate','2026-10-18']);
    assert.equal(h.panel.el('status').textContent,'写真はありません。');
});
test('invalid selection and oversize batches do not submit files',async()=>{
    const h=harness();await h.panel.open(1,'2026-10-18');
    await h.panel.upload([{size:10}]);assert.equal(h.calls.length,1);
    h.panel.el('category').value='ESTIMATE';
    await h.panel.upload([{size:21*1024*1024}]);assert.equal(h.calls.length,1);
});

test('failed photos are retained and retry keeps the same request ID',async()=>{
    const h=harness((url,options)=>options.method==='GET'?Promise.resolve({data:[]}):Promise.reject(new Error('timeout')));
    await h.panel.open(1,'2026-10-18');h.panel.el('category').value='ESTIMATE';
    const files=[{size:10}];await h.panel.upload(files);assert.equal(h.panel.pending.files[0],files[0]);
    const request=h.panel.pending.requestId;await h.panel.upload(h.panel.pending.files,true);
    assert.equal(h.panel.pending.requestId,request);assert.match(h.panel.el('status').textContent,/保持/);
});
