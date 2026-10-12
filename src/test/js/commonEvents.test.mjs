import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';
const resource = '../../main/resources/static/js/';
function source(path){return readFileSync(new URL(resource + path, import.meta.url),'utf8').replace(/^import .*;\n/gm,'').replace(/export /g,'');}
function events(){
    const listeners = {}, calls = [];
    const context = vm.createContext({document:{addEventListener:(name,fn)=>(listeners[name]??=[]).push(fn)},
        resolveController:()=>({executeAction:async action=>calls.push(action)}),
        domActions:{'date-arrow':event=>calls.push(event.key)}, handleEnterValidate:()=>{},
        HTMLInputElement: class {}, formatters:{}, DialogService:{error:()=>{}},console});
    vm.runInContext(source('core/events/eventHandlers.js'),context);
    context.initEvents();context.initEvents();
    return {listeners,calls,emit(type,el,extra={}){for(const fn of listeners[type]??[])fn({type,target:{closest:()=>el},...extra});}};
}
test('save button dispatches once on click, never on keydown/input/change',()=>{
    const h=events(),el={tagName:'BUTTON',dataset:{action:'save'}};
    h.emit('keydown',el,{key:'Enter'});h.emit('input',el);h.emit('change',el);h.emit('click',el);
    assert.deepEqual(h.calls,['save']);assert.equal(h.listeners.click.length,1);
});
test('per-action event mapping separates date arrows from search',()=>{
    const h=events(),el={tagName:'INPUT',dataset:{action:'date-arrow search',event:'date-arrow:keydown search:change'}};
    h.emit('keydown',el,{key:'ArrowRight',shiftKey:true});h.emit('change',el);h.emit('click',el);
    assert.deepEqual(h.calls,['ArrowRight','search']);
});
test('custom resolved event and Enter key filtering are supported',()=>{
    const h=events(),el={tagName:'INPUT',dataset:{action:'search',event:'keydown',key:'Enter'}};
    h.emit('keydown',el,{key:'a'});h.emit('keydown',el,{key:'Enter',isComposing:true});h.emit('keydown',el,{key:'Enter'});
    const resolved={tagName:'DIV',dataset:{action:'employee-resolved',event:'resolved'}};
    h.emit('click',resolved);h.emit('resolved',resolved);assert.deepEqual(h.calls,['search','employee-resolved']);
});
test('select-on-focus also works in multiple action attributes',()=>{
    const h=events();let count=0;
    h.emit('focusin',{tagName:'INPUT',dataset:{action:'select-on-focus search'},select:()=>count++});
    assert.equal(count,1);
});
test('Enter submission happens once for multiple actions',()=>{
    const h=events();let clicks=0;
    const el={tagName:'INPUT',dataset:{action:'search other',enterSubmit:'true'},closest:()=>({querySelector:()=>({click:()=>clicks++})})};
    h.emit('keydown',el,{key:'Enter',preventDefault:()=>{}});assert.equal(clicks,1);assert.deepEqual(h.calls,[]);
});
test('DataResolver initialization is idempotent on retained DOM',()=>{
    let listeners=0;
    const input={value:'',dataset:{},addEventListener:()=>listeners++},select={tagName:'SELECT',addEventListener:()=>listeners++};
    const group={dataset:{resolve:'query'},querySelector:selector=>selector.includes('resolve-id')?input:select};
    const context=vm.createContext({document:{querySelectorAll:()=>[group]},parsers:{},api:{},updateField:()=>{},normalizeValue:()=>{},getOptions:()=>{}});
    vm.runInContext(source('core/behavior/DataResolver.js')+'\nglobalThis.resolver = DataResolver;',context);
    context.resolver.init();context.resolver.init();assert.equal(listeners,4);
});
test('controller replacement and page departure dispose retained controllers',()=>{
    const context=vm.createContext({});
    vm.runInContext(source('application/controllerRegistry.js'),context);
    let disposals=0;
    context.registerController('page',{dispose:()=>disposals++});
    const next={dispose:()=>disposals++};
    context.registerController('page',next);context.registerController('page',next);
    assert.equal(disposals,1);context.clearControllers();assert.equal(disposals,2);
    assert.equal(context.getController('page'),undefined);
});
