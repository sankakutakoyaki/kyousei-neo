import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';
const deferred=()=>{let resolve,reject;const promise=new Promise((a,b)=>{resolve=a;reject=b;});return {promise,resolve,reject};};
const settle=async()=>{for(let i=0;i<12;i++)await Promise.resolve();};
function harness(){
    const styles=[],requests=[],menus=[],modules=new Map(),timers=new Map();let serial=0,clears=0,appInits=0;
    const h={};
    class Root {
        constructor(name,page=`/${name}.js`,css=`/${name}.css`){this.name=name;this.dataset={page,css};this.isConnected=true;}
        querySelector(){return null;}querySelectorAll(){return [];}
        cloneNode(){const copy=new Root(this.name,this.dataset.page,this.dataset.css);copy.dataset={...this.dataset};return copy;}
        replaceWith(other){this.isConnected=false;other.isConnected=true;h.root=other;}
    }
    h.root=new Root('old');
    const oldStyle={media:'',dataset:{pageCss:'true'},href:'/old.css',remove(){styles.splice(styles.indexOf(this),1);}};styles.push(oldStyle);
    h.css=link=>queueMicrotask(()=>link.onload?.());
    const document={querySelector:selector=>['.normal-body','.normal-main','.no-side-body'].includes(selector)?h.root:null,
        querySelectorAll:selector=>selector==='link[data-page-css]'?styles:[],
        head:{appendChild:link=>{styles.push(link);h.css(link);}},
        createElement:()=>({dataset:{},remove(){const i=styles.indexOf(this);if(i>=0)styles.splice(i,1);}})};
    const app={currentPage:'/old',currentTarget:'body',cache:{common:{old:true},page:{old:true}}};
    const context=vm.createContext({APP:app,AbortController,DOMException,
        window:{setTimeout:fn=>{timers.set(++serial,fn);return serial;},clearTimeout:id=>timers.delete(id)},
        fetch:(url,options)=>{const d=deferred();requests.push({url,options,...d});return d.promise;},
        DOMParser:class{parseFromString(html){return {querySelector:selector=>html==='missing'?null:new Root(html)};}},
        document,clearControllers:()=>clears++,menuActive:(url,target)=>menus.push([url,target]),initApp:()=>appInits++,
        loadModule:async path=>{const module=modules.get(path);if(!module)throw new Error('module not found: '+path);return module;}});
    const source=readFileSync(new URL('../../main/resources/static/js/core/dom/loadPage.js',import.meta.url),'utf8')
        .replace(/^import .*;\n/gm,'').replace(/export /g,'').replace(/import\(/g,'loadModule(');
    vm.runInContext(source,context);
    modules.set('/old.js',{init:async()=>{app.cache.page.restored=true;}});
    h.page=(name,init=async()=>{})=>modules.set(`/${name}.js`,{init});
    h.respond=(index,name,status=200)=>requests[index].resolve({ok:status>=200&&status<300,status,text:async()=>name});
    return Object.assign(h,{context,app,styles,oldStyle,requests,menus,modules,timers,get clears(){return clears;},get appInits(){return appInits;}});
}
test('HTTP failure preserves old page/cache/styles and permits the same URL retry',async()=>{
    const h=harness(),cache=h.app.cache;const failed=h.context.loadPage('/a');h.respond(0,'a',500);await assert.rejects(failed,/HTTP 500/);
    assert.equal(h.root.name,'old');assert.equal(h.app.cache,cache);assert.equal(h.app.currentPage,'/old');assert.deepEqual(h.styles,[h.oldStyle]);
    h.page('a');const retry=h.context.loadPage('/a');h.respond(1,'a');assert.equal(await retry,true);assert.equal(h.app.currentPage,'/a');
});
test('network errors, missing DOM, JS import failures and CSS failures occur before page exchange',async()=>{
    for(const reason of ['network','missing','module','css']){
        const h=harness(),cache=h.app.cache;if(reason!=='module')h.page('a');if(reason==='css')h.css=link=>queueMicrotask(()=>link.onerror?.());
        const pending=h.context.loadPage('/a');
        if(reason==='network')h.requests[0].reject(new Error('network'));else h.respond(0,reason==='missing'?'missing':'a');
        await assert.rejects(pending);assert.equal(h.root.name,'old');assert.equal(h.app.cache,cache);assert.equal(h.styles.length,1);
    }
});
test('only latest requested page commits even when old fetch ignores abort',async()=>{
    const h=harness();h.page('a');h.page('b');const first=h.context.loadPage('/a'),second=h.context.loadPage('/b');
    assert.equal(h.requests[0].options.signal.aborted,true);h.respond(1,'b');await second;h.respond(0,'a');assert.equal(await first,false);
    assert.equal(h.root.name,'b');assert.equal(h.app.currentPage,'/b');assert.deepEqual(h.menus,[['/b','sidebar']]);
});
test('next DOM/cache exchange waits for the existing page initializer',async()=>{
    const h=harness(),init=deferred(),started=deferred();h.page('a',async()=>{started.resolve();await init.promise;h.app.cache.page.value='A';});h.page('b',async()=>{h.app.cache.page.value='B';});
    const first=h.context.loadPage('/a');h.respond(0,'a');await started.promise;assert.equal(h.root.name,'a');assert.equal(h.app.currentPage,'/old');
    const second=h.context.loadPage('/b');h.respond(1,'b');await settle();assert.equal(h.root.name,'a');
    init.resolve();await Promise.all([first,second]);assert.equal(h.root.name,'b');assert.equal(h.app.cache.page.value,'B');assert.equal(h.app.currentPage,'/b');
});
test('failed initialization restores old DOM/CSS/cache and reinitializes its module',async()=>{
    const h=harness();h.page('a',async()=>{h.app.cache.page.partial=true;throw new Error('init failed');});
    const pending=h.context.loadPage('/a');h.respond(0,'a');await assert.rejects(pending,/init failed/);
    assert.equal(h.root.name,'old');assert.equal(h.app.currentPage,'/old');assert.equal(h.app.cache.page.old,true);
    assert.equal(h.app.cache.page.partial,undefined);assert.equal(h.app.cache.page.restored,true);assert.equal(h.styles.length,1);assert.equal(h.oldStyle.media,'');
});
test('duplicate in-flight navigation shares a single request',async()=>{
    const h=harness();h.page('a');const first=h.context.loadPage('/a'),second=h.context.loadPage('/a');assert.equal(first,second);assert.equal(h.requests.length,1);
    h.respond(0,'a');await first;assert.equal(h.timers.size,0);
});
test('returning to currently displayed page cancels a pending fetch',async()=>{
    const h=harness();h.page('a');const pending=h.context.loadPage('/a');assert.equal(await h.context.loadPage('/old'),true);assert.equal(await pending,false);
    assert.equal(h.root.name,'old');assert.equal(h.app.currentPage,'/old');
});
test('timeout aborts loading and leaves the current page intact',async()=>{
    const h=harness();const pending=h.context.loadPage('/a');[...h.timers.values()][0]();await assert.rejects(pending,/タイムアウト/);
    assert.equal(h.root.name,'old');assert.equal(h.requests[0].options.signal.aborted,true);
});
test('layout transition activates header menu and tracks target separately',async()=>{
    const h=harness();h.page('a');const pending=h.context.loadPage('/a','layout');h.respond(0,'a');await pending;
    assert.equal(h.app.currentTarget,'layout');assert.deepEqual(h.menus,[['/a','header']]);
    await assert.rejects(h.context.loadPage('/b','invalid'),/不正/);
});
test('initialization failure clears copied binding markers before recovery',async()=>{
    const h=harness();Object.assign(h.root.dataset,{initialized:'true',linkInitialized:'true',enterFocusInitialized:'true'});
    let recovered=false;h.modules.set('/old.js',{init:async()=>{assert.equal(h.root.dataset.initialized,undefined);assert.equal(h.root.dataset.linkInitialized,undefined);assert.equal(h.root.dataset.enterFocusInitialized,undefined);recovered=true;}});
    h.page('a',async()=>{throw new Error('failed');});const pending=h.context.loadPage('/a');h.respond(0,'a');await assert.rejects(pending,/failed/);assert.equal(recovered,true);
});
test('failed page-cache response leaves cache unchanged',async()=>{
    const app={cache:{page:{old:true},common:{old:true}}};
    const context=vm.createContext({APP:app,AbortController,setTimeout,clearTimeout,fetch:async()=>({ok:false,status:503})});
    const source=readFileSync(new URL('../../main/resources/static/js/bootstrap/initPageCache.js',import.meta.url),'utf8').replace(/export /g,'');
    vm.runInContext(source,context);await assert.rejects(context.initPageCache('/cache'),/HTTP 503/);assert.deepEqual(app.cache.page,{old:true});
});
