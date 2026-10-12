import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';
const source=path=>readFileSync(new URL('../../main/resources/static/js/'+path,import.meta.url),'utf8').replace(/^import .*;\n/gm,'').replace(/export /g,'');
class Element {
    constructor(action,{disabled=false,pointerEvents=''}={}){
        this.tagName='BUTTON';this.dataset={action};this.disabled=disabled;this.style={pointerEvents};this.attributes={};
        const classes=new Set();this.classList={toggle:(name,on)=>on?classes.add(name):classes.delete(name),contains:name=>classes.has(name)};
    }
    setAttribute(key,value){this.attributes[key]=value;}getAttribute(key){return this.attributes[key];}
}
function harness(config={}){
    const elements=[],listeners={},calls=[];let controller;
    const document={querySelectorAll:selector=>selector==='[data-action]'?elements:[],getElementById:()=>null,
        addEventListener:(type,fn)=>(listeners[type]??=[]).push(fn),removeEventListener(){}};
    const context=vm.createContext({isMobileReadOnly:()=>false, isWriteAction:()=>false, document,resolveController:el=>el.foreign?null:controller,
        smartFilterHandler(){},openMsgDialog(){},closeMsgDialog(){},openConfirmDialog(){},
        initCombo(){},createInputComponent(){},domActions:{'date-arrow':()=>calls.push('date-arrow')},
        handleEnterValidate(){},DialogService:{error(){}},formatters:{},HTMLInputElement:class{}});
    vm.runInContext(source('application/PageController.js')+'\nglobalThis.Controller = PageController;',context);
    controller=new context.Controller({key:'page',...config});controller.init();
    vm.runInContext(source('core/events/eventHandlers.js'),context);context.initEvents();
    return {controller,elements,calls,emit:(type,el,extra={})=>{for(const fn of listeners[type]??[])fn({type,target:{closest:()=>el},...extra});}};
}
test('compound controls require all declared conditions, independent of whitespace/order',()=>{
    let allowed=false;const h=harness({conditions:{edit:()=>true,download:()=>allowed}});const el=new Element(' edit  download ');h.elements.push(el);
    h.controller.updateButtons();assert.equal(el.disabled,true);assert.equal(el.getAttribute('aria-disabled'),'true');
    assert.equal(h.controller.isEnabled('download edit',el),false);
    allowed=true;h.controller.updateButtons();assert.equal(el.disabled,false);assert.equal(el.getAttribute('aria-disabled'),'false');
});
test('actions without conditions do not bypass a declared condition',()=>{
    const h=harness({conditions:{save:()=>false}});const el=new Element('save custom');h.elements.push(el);h.controller.updateButtons();assert.equal(el.disabled,true);
    assert.equal(h.controller.isEnabled(' custom save ',el),false);
});
test('elements without conditions preserve HTML disabled and component-managed styles',()=>{
    const h=harness();const el=new Element('select-on-focus search',{disabled:true,pointerEvents:'inherit'});el.tagName='INPUT';h.elements.push(el);
    h.controller.updateButtons();assert.equal(el.disabled,true);assert.equal(el.style.pointerEvents,'inherit');assert.equal(el.getAttribute('aria-disabled'),undefined);
});
test('enabling a conditional control restores its original pointer style',()=>{
    let allowed=false;const h=harness({conditions:{custom:()=>allowed}});const el=new Element('custom',{pointerEvents:'inherit'});h.elements.push(el);
    h.controller.updateButtons();assert.equal(el.style.pointerEvents,'none');allowed=true;h.controller.updateButtons();assert.equal(el.style.pointerEvents,'inherit');
});
test('foreign controller controls are untouched',()=>{
    const h=harness({conditions:{edit:()=>false}});const el=new Element('edit');el.foreign=true;h.elements.push(el);h.controller.updateButtons();assert.equal(el.disabled,false);
});
test('single-action selection conditions retain their behavior',()=>{
    let selected=false;const h=harness();h.controller.hasSelection=()=>selected;
    const el=new Element('download');h.elements.push(el);h.controller.updateButtons();assert.equal(el.disabled,true);selected=true;h.controller.updateButtons();assert.equal(el.disabled,false);
});
test('dispatch checks current conditions even when UI has not updated yet',async()=>{
    let allowed=true;const calls=[];const h=harness({conditions:{save:()=>allowed},actions:{save:()=>calls.push('save'),other:()=>calls.push('other')}});
    const el=new Element('save other');h.elements.push(el);h.controller.updateButtons();allowed=false;h.emit('click',el);await Promise.resolve();assert.deepEqual(calls,[]);
    allowed=true;h.emit('click',el);await Promise.resolve();assert.deepEqual(calls,['save','other']);
});
test('native, CSS and aria disabled states all block delegated action execution',()=>{
    const calls=[];const h=harness({actions:{custom:()=>calls.push('custom')}});
    const native=new Element('custom',{disabled:true}),css=new Element('custom'),aria=new Element('custom');
    css.classList.toggle('disabled',true);aria.setAttribute('aria-disabled','true');
    for(const el of [native,css,aria])h.emit('click',el);assert.deepEqual(calls,[]);
});
test('disabled compound inputs cannot trigger Enter submit or DOM actions',()=>{
    let submitted=0;const h=harness({conditions:{save:()=>false}});
    const el=new Element('date-arrow save');el.dataset.event='keydown';el.dataset.enterSubmit='true';el.closest=()=>({querySelector:()=>({click:()=>submitted++})});
    h.emit('keydown',el,{key:'Enter',preventDefault(){}});h.emit('keydown',el,{key:'ArrowRight'});
    assert.equal(submitted,0);assert.deepEqual(h.calls,[]);
});
test('HTML action lock prevents duplicate mutations and releases after failure',async()=>{
    let reject;let calls=0;const pending=new Promise((_,b)=>reject=b);
    const h=harness({actions:{custom:()=>{calls++;return calls===1?pending:Promise.resolve();}}});
    const el=new Element('custom');el.dataset.actionLock='true';
    const first=h.controller.executeAction('custom',el);
    await h.controller.executeAction('custom',el);assert.equal(calls,1);
    reject(Error('failed'));await assert.rejects(first,/failed/);
    await h.controller.executeAction('custom',el);assert.equal(calls,2);
});
