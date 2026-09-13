import test from 'node:test';
import assert from 'node:assert/strict';
import vm from 'node:vm';
import {readFileSync} from 'node:fs';
const code = readFileSync(new URL('../../main/resources/static/js/pages/business/dispatch/dispatchPage.js', import.meta.url), 'utf8')
    .replace(/^import .*;\n/gm, '').replace('export async function init', 'async function init');
class Element {
    constructor(tag='div') { this.tag=tag; this.children=[]; this.events={}; this.dataset={}; this.value=''; this.isConnected=true; }
    setAttribute(name,value) { this[name]=value; }
    append(...nodes) { this.children.push(...nodes); }
    replaceChildren(...nodes) { this.children=[...nodes]; }
    addEventListener(name,fn) { this.events[name]=fn; }
    async click() { await this.events.click?.(); }
    querySelectorAll(selector) { return this.children.flatMap(c=>[...(c.tag==='input' && (selector!=='input:checked' || c.checked) ? [c] : []),...c.querySelectorAll(selector)]); }
}
async function harness({mobile=false,manager=false}={}) {
    const elements = new Map();
    const get = id => { if(!elements.has(id)) elements.set(id,new Element()); return elements.get(id); };
    get('dispatch-search').elements = Object.fromEntries(['dateFrom','dateTo','includeUndated','assignmentFilter','keyword'].map(k=>[k,new Element()]));
    const row = {orderId:12,version:2,dispatchVersion:3,state:0,title:'<img src=x onerror=alert(1)>',assignments:[{employeeId:10,role:'DELIVERY',employeeName:'配送担当'}],history:[]};
    const calls=[]; let config, form;
    const context = vm.createContext({console,Date,Number,String,JSON,Error,
        initCommon:async()=>{},isMobileDevice:()=>mobile,isDispatchManager:()=>manager,refreshMobileReadOnly:()=>{},
        document:{getElementById:get,createElement:tag=>new Element(tag)},
        DialogService:{confirm:async()=>true,error:message=>{throw new Error(message);}},
        RequestClient:{request:async request=>{calls.push(request); return {data:request.queryId==='dispatchList' ? [row] : request.queryId==='dispatchDetail' ? row : [{employeeId:10,fullName:'配送担当'},{employeeId:11,fullName:'請負担当'}]};}},
        FormController:class {constructor(c){config=c;form=this;} async open(data){await config.onOpen(data);} }
    });
    await vm.runInContext(code+'\ninit()',context);
    await get('dispatch-results').children[0].children.at(-1).children[0].click();
    return {get,config,form,calls,row};
}
test('dispatch renders untrusted titles as text and submits both roles with dispatch version',async()=>{
    const h=await harness();
    assert.equal(h.get('dispatch-results').children[0].children[0].textContent,' <img src=x onerror=alert(1)>');
    assert.equal(h.config.hasAdditionalChanges(),false);
    const fields=h.get('dispatch-members').querySelectorAll('input');
    fields[3].checked=true;
    assert.equal(h.config.hasAdditionalChanges(),true);
    const payload=h.config.buildAdditionalPayload();
    assert.equal(payload.dispatchVersion,3);
    assert.equal(payload.mobile,false);
    assert.equal(JSON.stringify(payload.assignments),JSON.stringify([{employeeId:10,role:'DELIVERY'},{employeeId:11,role:'INSTALL'}]));
    assert.equal(await h.form.confirmSave(),true);
    await h.config.saveHandler(payload);
    assert.equal(h.calls.at(-1).queryId,'dispatchSave');
    h.config.resetAdditional();
    assert.equal(h.config.hasAdditionalChanges(),false);
});
test('mobile ordinary user cannot submit but management user can',async()=>{
    const ordinary=await harness({mobile:true});
    assert.throws(()=>ordinary.config.saveHandler({}),/編集/);
    assert.equal(ordinary.get('dispatch-readonly').hidden,false);
    const manager=await harness({mobile:true,manager:true});
    assert.equal(manager.get('dispatch-readonly').hidden,true);
    assert.equal(manager.config.buildAdditionalPayload().mobile,true);
    await manager.config.saveHandler(manager.config.buildAdditionalPayload());
    assert.equal(manager.calls.at(-1).queryId,'dispatchSave');
});
