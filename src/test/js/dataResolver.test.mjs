import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';
const source=path=>readFileSync(new URL('../../main/resources/static/js/'+path,import.meta.url),'utf8').replace(/^import .*;\n/gm,'').replace(/export /g,'');
function deferred(){let resolve,reject;const promise=new Promise((a,b)=>{resolve=a;reject=b;});return {promise,resolve,reject};}
class Input {
    constructor(value=''){this.value=value;this.dataset={};this.isConnected=true;this.events={};this.tagName='INPUT';}
    addEventListener(type,fn){(this.events[type]??=[]).push(fn);}emit(type){for(const fn of this.events[type]??[])fn();}
    focus(){this.focused=true;}select(){this.selected=true;}setSelectionRange(){}
}
function harness(api={}){
    const errors=[];const context=vm.createContext({api,APP:{cache:{common:{state:{INITIAL:0}}}},
        updateField:(el,value)=>{el.value=value;},DialogService:{error:e=>errors.push(e)},requestAnimationFrame:fn=>fn(),
        parsers:{},normalizeValue:v=>v,getOptions:()=>({}),HTMLInputElement:Input,HTMLTextAreaElement:Input,
        HTMLSelectElement:class{},RadioNodeList:class{},formatters:{},normalize:v=>v,
        convertKey:(key,from)=>from==='camel'?key.replace(/[A-Z]/g,c=>'-'+c.toLowerCase()):key.replace(/-([a-z])/g,(_,c)=>c.toUpperCase())});
    vm.runInContext(source('core/behavior/DataResolver.js')+'\nglobalThis.resolver = DataResolver;',context);
    const resolver=context.resolver;
    function group(type='postal'){
        const input=new Input(),name=new Input('既存値'),hidden=new Input('7');
        const g={isConnected:true,dataset:{resolve:type},querySelector:s=>s==='[data-resolve-id]'?input:s==='[data-resolve-name]'?name:s==="[data-role='hidden-id']"?hidden:s==='[name="full-address"]'?name:null,querySelectorAll:()=>[],closest:()=>null};
        resolver.init({querySelectorAll:()=>[g]});return {g,input,name,hidden};
    }
    return {context,resolver,errors,group};
}
test('failed postal lookup preserves input and can retry the same code',async()=>{
    let calls=0;const h=harness({post:async()=>{if(++calls===1)throw new Error('通信失敗');return {data:{postalCode:'123-4567',fullAddress:'住所'}};}});
    const {g,input,name}=h.group();input.value='1234567';
    assert.equal(await h.resolver.resolve(g,'postal'),false);assert.equal(input.value,'1234567');assert.equal(name.value,'既存値');
    assert.equal(await h.resolver.resolve(g,'postal'),true);assert.equal(name.value,'住所');assert.equal(input.dataset.lastId,'123-4567');assert.equal(h.errors.length,1);
    await h.resolver.resolve(g,'postal');assert.equal(calls,2);
});
test('response order cannot overwrite the latest postal input',async()=>{
    const a=deferred(),b=deferred();const h=harness({post:(_,p)=>p.value==='A'?a.promise:b.promise});const {g,input,name}=h.group();
    input.value='A';const first=h.resolver.resolve(g,'postal');input.value='B';input.emit('input');const second=h.resolver.resolve(g,'postal');
    b.resolve({data:{postalCode:'B',fullAddress:'新住所'}});await second;a.resolve({data:{postalCode:'A',fullAddress:'旧住所'}});await first;
    assert.equal(name.value,'新住所');assert.equal(input.value,'B');
});
test('input edits invalidate an old request even after returning to the same code',async()=>{
    const request=deferred();const h=harness({post:()=>request.promise});const {g,input,name}=h.group();input.value='A';const pending=h.resolver.resolve(g,'postal');
    input.value='B';input.emit('input');input.value='A';input.emit('input');request.resolve({data:{fullAddress:'古い住所'}});await pending;
    assert.equal(name.value,'既存値');assert.notEqual(input.dataset.lastId,'A');
});
test('repeated blur shares an in-flight lookup instead of sending twice',async()=>{
    let calls=0;const request=deferred();const h=harness({post:()=>{calls++;return request.promise;}});const {g,input}=h.group();input.value='A';
    const first=h.resolver.resolve(g,'postal'),second=h.resolver.resolve(g,'postal');request.resolve({data:{postalCode:'A',fullAddress:'住所'}});await Promise.all([first,second]);assert.equal(calls,1);
});
test('form clear/fill invalidates pending lookup and seeds the new entity values',async()=>{
    const request=deferred();const h=harness({post:()=>request.promise});const {g,input,name}=h.group();input.name='postal-code';name.name='full-address';
    const form={elements:{'postal-code':input,'full-address':name},querySelectorAll:s=>s==='[data-resolve]'?[g]:[],querySelector:s=>s.includes('postal-code')?input:s.includes('full-address')?name:null};
    form.elements[Symbol.iterator]=function*(){yield input;yield name;};
    vm.runInContext(source('core/form/FormModel.js')+'\nglobalThis.model = FormModel;',h.context);
    input.value='A';const pending=h.resolver.resolve(g,'postal');h.context.model.clear(form);h.context.model.fill(form,{postalCode:'A',fullAddress:'次の伝票の住所'});
    request.resolve({data:{postalCode:'A',fullAddress:'前の伝票の住所'}});await pending;assert.equal(name.value,'次の伝票の住所');assert.equal(input.dataset.lastId,'A');
});
test('disconnected or closed forms ignore late errors and field updates',async()=>{
    for(const closed of [true,false]){
        const request=deferred();const h=harness({post:()=>request.promise});const {g,input,name}=h.group();input.value='A';const pending=h.resolver.resolve(g,'postal');
        if(closed)g.closest=()=>({classList:{contains:()=>true}});else g.isConnected=false;
        request.reject(new Error('古い失敗'));await pending;assert.equal(name.value,'既存値');assert.equal(h.errors.length,0);
    }
});
test('query failure and not-found results allow a fresh fetch with hidden IDs cleared',async()=>{
    let calls=0;const h=harness({request:async()=>{calls++;if(calls===1)throw new Error('失敗');return {data:calls===2?[]:[{code:'5',name:'製造業者',recycleMakerId:15}]};}});
    const {g,input,name,hidden}=h.group('query');Object.assign(g.dataset,{queryId:'recycleMakerList',paramBuilder:'makerParams',idKey:'code',valueKey:'recycleMakerId'});input.value='5';
    await h.resolver.resolve(g,'query');assert.equal(hidden.value,'');await h.resolver.resolve(g,'query');await h.resolver.resolve(g,'query');
    assert.equal(calls,3);assert.equal(name.value,'製造業者');assert.equal(hidden.value,15);
});
test('clearing a previously resolved code also clears its hidden ID',async()=>{
    const h=harness({request:async()=>({data:[{id:'5',name:'名前',key:15}]})});const {g,input,name,hidden}=h.group('query');Object.assign(g.dataset,{queryId:'test',valueKey:'key'});input.value='5';await h.resolver.resolve(g,'query');
    input.value='';input.emit('input');await h.resolver.resolve(g,'query');assert.equal(name.value,'');assert.equal(hidden.value,'');
});
test('query caches are scoped to a group and reset with the form',async()=>{
    let calls=0;const h=harness({request:async()=>({data:[{id:'5',name:`取得${++calls}`} ]})});const a=h.group('query'),b=h.group('query');
    a.g.dataset.queryId=b.g.dataset.queryId='test';a.input.value=b.input.value='5';await h.resolver.resolve(a.g,'query');await h.resolver.resolve(b.g,'query');assert.equal(calls,2);
    h.resolver.reset({querySelectorAll:()=>[a.g]});a.input.value='6';await h.resolver.resolve(a.g,'query');assert.equal(calls,3);
});
test('default resolver reads the unwrapped API data object',async()=>{
    const h=harness({get:async()=>({data:{name:'名前',id:3}})});const {g,input,name,hidden}=h.group('custom');g.dataset.valueKey='id';input.value='3';await h.resolver.resolve(g,'custom');assert.equal(name.value,'名前');assert.equal(hidden.value,3);
});
test('work code resolves through the registered work master query',async()=>{
    const calls=[];const h=harness({request:async p=>{calls.push(p);return {data:[{workCode:'W01',workName:'設置',workPrice:1000}]};}});
    const {g,input}=h.group('work');const name=new Input(),price=new Input(),quantity=new Input();
    const scope={querySelector:s=>s.includes('order-work-name')?name:s.includes('order-work-price')?price:s.includes('order-work-quantity')?quantity:null};g.closest=s=>s==='form'?scope:null;input.value='W01';await h.resolver.resolve(g,'work');
    assert.equal(calls[0].queryId,'workMasterList');assert.equal(name.value,'設置');assert.equal(price.value,1000);assert.equal(quantity.focused,true);
});
test('API resolver emits only the current employee and invalidates input immediately',async()=>{
    const a=deferred(),b=deferred();let calls=0;const events=[];const h=harness({get:()=>++calls===1?a.promise:b.promise});
    h.context.URLSearchParams=URLSearchParams;
    h.context.CustomEvent=class {constructor(type,options){this.type=type;this.detail=options.detail;}};
    const {g,input,name}=h.group('api');g.dataset.resolveUrl='/api/timeworks/status';g.dataset.nameKey='fullName';g.dispatchEvent=e=>events.push(e.detail.data);
    input.value='A';const first=h.resolver.resolve(g,'api');input.value='B';input.emit('input');const second=h.resolver.resolve(g,'api');
    b.resolve({data:{employeeId:2,fullName:'B'}});await second;a.resolve({data:{employeeId:1,fullName:'A'}});await first;
    assert.equal(g._resolvedData.employeeId,2);assert.equal(name.value,'B');assert.equal(events.length,2);assert.equal(events[0],null);
});
