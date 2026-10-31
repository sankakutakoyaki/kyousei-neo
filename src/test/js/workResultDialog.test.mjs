import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';
const code=readFileSync(new URL('../../main/resources/static/js/components/order/WorkResultDialog.js',import.meta.url),'utf8')
    .replace(/^import .*;\n/gm,'').replace('export const workResultLabels','const workResultLabels').replace('export class WorkResultDialog','class WorkResultDialog');
class Element {
    constructor(){this.listeners={};this.value='';this.disabled=false;this.hidden=false;this.isConnected=true;this.label={hidden:false};}
    addEventListener(type,fn){this.listeners[type]=fn;}
    closest(){return this.label;}
    focus(){}
    showModal(){this.open=true;}
    close(){this.open=false;this.listeners.close?.();}
}
function harness({snapshot={},post=async()=>({})}={}) {
    const root=new Element(), nodes=new Map(), calls=[];
    root.querySelector=key=>{if(!nodes.has(key))nodes.set(key,new Element());return nodes.get(key);};
    let serial=0,saved=0;
    const context=vm.createContext({crypto:{randomUUID:()=>`request-${++serial}`},
        apiFetch:async(url,options)=>{calls.push({url,options});return options.method==='GET'?{data:{assignmentVersion:3,resultVersion:null,workDate:'2026-10-31',...snapshot}}:post(url,options);}});
    vm.runInContext(code+'\nglobalThis.create=(root,cb)=>new WorkResultDialog(root,cb);',context);
    return {root,dialog:context.create(root,async()=>saved++),calls,get saved(){return saved;}};
}
test('reason requirements follow selected result and correction status',async()=>{
    const h=harness();await h.dialog.open(100,12,'COMPLETED');
    assert.equal(h.dialog.reason.required,false);
    h.dialog.type.value='POSTPONED';h.dialog.updateFields();
    assert.equal(h.dialog.reason.required,true);
    await h.dialog.save();assert.equal(h.calls.length,1);assert.match(h.dialog.status.textContent,/理由/);
    const c=harness({snapshot:{workResultId:9,resultVersion:1,resultType:'DELIVERY_FAILED'}});
    await c.dialog.open(100,12,'COMPLETED');assert.equal(c.dialog.reason.required,true);
});
test('successful report carries both versions and closes before refreshing',async()=>{
    const h=harness({snapshot:{workResultId:9,resultVersion:1}});await h.dialog.open(100,12,'POSTPONED');
    h.dialog.reason.value='希望日は後日相談';await h.dialog.save();
    const p=h.calls[1].options.data;
    assert.equal(p.assignmentVersion,3);assert.equal(p.resultVersion,1);assert.equal(p.workDate,'2026-10-31');assert.equal('nextVisitDate' in p,false);
    assert.equal(h.root.open,false);assert.equal(h.saved,1);
});
test('retry keeps request ID for the same payload and creates a new one for changed contents',async()=>{
    const h=harness({post:async()=>{throw new Error('timeout');}});await h.dialog.open(100,12,'COMPLETED');
    await h.dialog.save();await h.dialog.save();
    assert.equal(h.calls[1].options.data.requestId,h.calls[2].options.data.requestId);
    h.dialog.reason.value='補足';await h.dialog.save();
    assert.notEqual(h.calls[2].options.data.requestId,h.calls[3].options.data.requestId);
    assert.equal(h.saved,0);assert.equal(h.dialog.submit.disabled,false);
});
test('a switched or closed detail does not open an old response and save blocks double submissions',async()=>{
    const h=harness();await h.dialog.open(100,12,'COMPLETED',()=>false);assert.equal(h.root.open,undefined);
    let resolve;const pending=new Promise(r=>resolve=r);
    const s=harness({post:()=>pending});await s.dialog.open(100,12,'COMPLETED');
    const saving=s.dialog.save();await s.dialog.save();assert.equal(s.calls.length,2);
    let prevented=false;s.root.listeners.cancel({preventDefault(){prevented=true;}});assert.equal(prevented,true);
    resolve({});await saving;assert.equal(s.saved,1);
});
