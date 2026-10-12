import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';
const read = path => readFileSync(new URL('../../main/resources/static/js/'+path,import.meta.url),'utf8');
const source = path => read(path).replace(/^import\s+[\s\S]*?;\n/gm,'').replace(/export /g,'');
const deferred = () => {let resolve,reject;const promise=new Promise((a,b)=>{resolve=a;reject=b;});return {promise,resolve,reject};};
const flush = async () => {for(let i=0;i<12;i++) await Promise.resolve();};
function session(elements={}) {
    let active=true;const cleanups=[];
    return {root:{querySelector:s=>elements[s.slice(1)],querySelectorAll:()=>[]},byId:id=>elements[id],isActive:()=>active,
        onDispose:fn=>cleanups.push(fn),dispose(){active=false;cleanups.splice(0).forEach(fn=>fn());}};
}
function dispatchHarness(overrides={}) {
    const renders=[];
    class Controller {constructor(config){this.config=config;this.state={};this.forms={};}init(){this.config.onInit(this);}dispose(){Object.values(this.forms).forEach(f=>f.dispose?.());}}
    const c=vm.createContext({PageController:Controller,APP:{cache:{common:{state:{INITIAL:0}}}},
        createCrewMemberForm:()=>({dispose(){},updateSubmitState(){}}),createDispatchVehicle:()=>({loadCandidates:async()=>{}}),
        readConditions:root=>({workDate:root.querySelector('#dispatch-work-date').value,officeId:1,dispatchCategory:1}),
        hasDispatchConditions:x=>!!x.workDate,DailyCrewRepository:{findBoardList:async()=>({data:[]})},
        DispatchOrderRepository:{findList:async()=>({data:[]})},DispatchAssignmentRepository:{findList:async()=>({data:[]})},
        groupDispatchCrews:x=>x,renderDispatchCrews:x=>renders.push(['crews',x]),renderDispatchOrders:x=>renders.push(['orders',x]),
        document:{getElementById:()=>({value:'all'})},...overrides});
    vm.runInContext(source('pages/operations/dispatch/dispatchPage.js')+'\nglobalThis.create=createDispatchPage;',c);
    const pageSession=session({'dispatch-work-date':{value:'2026-10-12'}});const page=c.create(pageSession);page.init();
    return {page,pageSession,renders};
}
test('dispatch owns the member form through the shared controller',()=>{
    const h=dispatchHarness();assert.ok(h.page.forms.members);assert.equal(h.page.config.defaultFormName,'members');
});
test('dispatch ignores a board response after departure',async()=>{
    const req=deferred();const h=dispatchHarness({DailyCrewRepository:{findBoardList:()=>req.promise}});
    const load=h.page.loadBoard();await flush();h.page.dispose();req.resolve({data:[]});await load;assert.deepEqual(h.renders,[]);
});
test('dispatch discards an older condition and applies the newer board',async()=>{
    const req=deferred();let calls=0;
    const h=dispatchHarness({DailyCrewRepository:{findBoardList:()=>++calls===1?req.promise:Promise.resolve({data:[]})}});
    const first=h.page.loadBoard();await flush();h.pageSession.byId('dispatch-work-date').value='2026-10-13';
    const second=h.page.loadBoard();req.resolve({data:[]});await Promise.all([first,second]);assert.equal(h.renders.length,2);
});
test('two dispatch page instances do not share pending board requests',async()=>{
    const req=deferred();const first=dispatchHarness({DailyCrewRepository:{findBoardList:()=>req.promise}});
    const load=first.page.loadBoard();await flush();first.page.dispose();const second=dispatchHarness();await second.page.loadBoard();
    assert.equal(second.renders.length,2);req.resolve({data:[]});await load;assert.equal(first.renders.length,0);
});
test('assignment move sends one versioned update without insert/delete',async()=>{
    const writes=[];const c=vm.createContext({DispatchAssignmentRepository:{findNextVisitOrder:async()=>({data:[{nextVisitOrder:4}]}),save:async x=>writes.push(x),deleteByIds:()=>assert.fail('delete must not run')},APP:{cache:{common:{state:{INITIAL:0}}}}});
    vm.runInContext(source('pages/operations/dispatch/dispatchDragDrop.js'),c);
    await c.moveOrderToCrew({dispatchAssignmentId:12,version:3},8);
    assert.deepEqual(JSON.parse(JSON.stringify(writes)),[{dispatchAssignmentId:12,version:3,dailyCrewId:8,visitOrder:4}]);
});
test('assignment move refuses a payload missing its optimistic version',async()=>{
    const c=vm.createContext({});vm.runInContext(source('pages/operations/dispatch/dispatchDragDrop.js'),c);
    await assert.rejects(c.moveOrderToCrew({dispatchAssignmentId:12},8),/再読み込み/);
});
function managementHarness(repository={}) {
    const listeners={},renders=[],timers=new Map(),errors=[],saves=[];let id=0;
    const list={addEventListener:(type,fn)=>listeners[type]=fn,removeEventListener:type=>delete listeners[type]};
    const elements={'management-list':list,'management-month':{value:'2026-10'},'management-office':{value:'1'},'management-closing-type':{value:'1'},'management-employee':{value:'',replaceChildren(){},add(){}}};
    const pageSession=session(elements);
    const c=vm.createContext({TimeworksRepository:{findEmployeeCombo:async()=>[],searchManagement:async()=>[],updateTimes:async p=>saves.push(p),...repository},
        Option:class {},renderManagementList:x=>renders.push(x),renderManagementPeriod(){},openMsgDialog:x=>errors.push(x),formatDate:()=>'',
        window:{setTimeout:fn=>{timers.set(++id,fn);return id;},clearTimeout:id=>timers.delete(id)}});
    vm.runInContext(source('pages/management/timeworks/management.js'),c);
    return {management:c.createManagement(pageSession),pageSession,listeners,renders,timers,saves,errors};
}
test('management events initialize on each new page and detach on dispose',async()=>{
    const a=managementHarness();a.management.initManagement();await flush();assert.equal(typeof a.listeners.change,'function');
    a.pageSession.dispose();assert.equal(a.listeners.change,undefined);
    const b=managementHarness();b.management.initManagement();await flush();assert.equal(typeof b.listeners.change,'function');assert.equal(b.renders.length,1);
});
test('management latest search wins when responses arrive out of order',async()=>{
    const a=deferred(),b=deferred();let calls=0;const h=managementHarness({searchManagement:()=>++calls===1?a.promise:b.promise});
    const first=h.management.refreshManagement(),second=h.management.refreshManagement();b.resolve(['new']);await second;a.resolve(['old']);await first;
    assert.deepEqual(h.renders,[['new']]);
});
test('management does not render detached page responses',async()=>{
    const req=deferred();const h=managementHarness({searchManagement:()=>req.promise});const pending=h.management.refreshManagement();
    h.pageSession.dispose();req.resolve(['old']);await pending;assert.deepEqual(h.renders,[]);
});
test('accepted management edits save their snapshot on departure',async()=>{
    const h=managementHarness();h.management.initManagement();await flush();
    const start={value:'09:00'},end={value:'17:00'},nextDay={checked:false};
    const row={dataset:{timeworkId:'7',workDate:'2026-10-12'},classList:{remove(){},add(){}},querySelector:s=>s.includes('editStart')?start:s.includes('editEnd')?end:nextDay};
    h.listeners.change({target:{matches:()=>true,closest:()=>row}});start.value='10:00';h.pageSession.dispose();await flush();
    assert.equal(h.saves.length,1);assert.equal(h.saves[0].editStartTime,'2026-10-12T09:00:00');assert.equal(h.timers.size,0);
});
test('management reports a save failure even after departure',async()=>{
    const h=managementHarness({updateTimes:async()=>{throw Error('save failed');}});h.management.initManagement();await flush();
    const row={dataset:{timeworkId:'7',workDate:'2026-10-12'},classList:{remove(){},add(){}},querySelector:()=>({value:'09:00'})};
    h.listeners.change({target:{matches:()=>true,closest:()=>row}});h.pageSession.dispose();await flush();assert.equal(h.errors[0].message,'save failed');
});
function formHarness(config={}) {
    const listeners={},opened=[],closed=[],info=[];const form={isConnected:true,dataset:{},querySelector:()=>null,querySelectorAll:()=>[],
        addEventListener:(type,fn)=>listeners[type]=fn,removeEventListener:type=>delete listeners[type]};
    const c=vm.createContext({document:{getElementById:()=>form,querySelector:()=>null},structuredClone,
        FormModel:{clear(){},fill(){}},FormStateBehavior:{hasChanges:()=>false,hasValidInput:()=>true},
        openFormDialog:x=>opened.push(x),DialogService:{close:x=>closed.push(x),info:x=>info.push(x),error(){}},
        isMobileReadOnly:()=>false,refreshMobileReadOnly(){},formatFields(){},initParentChildLink(){},setEnterFocus(){},PageCacheService:{get:()=>null}});
    vm.runInContext(source('core/save/SaveBehavior.js')+'\nglobalThis.SaveBehavior = SaveBehavior;',c);
    vm.runInContext(source('application/FormController.js')+'\nglobalThis.Controller = FormController;',c);
    const controller=new c.Controller({formId:'member',key:'dailyCrewId',idKey:'dailyCrewId',controller:{state:{},setBulkMode(){}},...config});
    return {controller,form,listeners,opened,closed,info};
}
test('form opening applies only the latest candidate response',async()=>{
    const a=deferred(),b=deferred(),applied=[];const h=formHarness({onOpen:async(data,form,canApply)=>{
        await (data.dailyCrewId===1?a.promise:b.promise);if(canApply()) applied.push(data.dailyCrewId);
    }});
    const first=h.controller.open({dailyCrewId:1}),second=h.controller.open({dailyCrewId:2});b.resolve();await second;a.resolve();await first;
    assert.deepEqual(applied,[2]);assert.equal(h.opened.length,1);assert.equal(h.controller.currentEntity.dailyCrewId,2);
});
test('pending form open is cancelled by dispose and listeners are removed',async()=>{
    const req=deferred();const h=formHarness({onOpen:()=>req.promise});const pending=h.controller.open({dailyCrewId:1});
    h.controller.dispose();req.resolve();await pending;assert.equal(h.opened.length,0);assert.deepEqual(h.listeners,{});
});
test('save completion after departure does not close the new page dialog',async()=>{
    const req=deferred();const h=formHarness({saveHandler:()=>req.promise});h.controller.preparePayload=()=>({dailyCrewId:1});
    const pending=h.controller.save(h.form);await flush();h.controller.dispose();req.resolve({data:1});await pending;
    assert.deepEqual(h.closed,[]);assert.deepEqual(h.info,[]);
});
function stampHarness(repository={}) {
    const starts={disabled:true,focus(){}},ends={disabled:true,focus(){}},input={value:'',dataset:{},focus(){}},group={};
    const mediaListeners=new Set(),timers=new Set();let personal=false;
    const media={matches:false,addEventListener:(_,fn)=>mediaListeners.add(fn),removeEventListener:(_,fn)=>mediaListeners.delete(fn)};
    const elements={'stamp-identifier':input,'stamp-user-name':{value:''},'stamp-date':{isConnected:true},'stamp-time':{isConnected:true}};
    const pageSession=session(elements);pageSession.root.classList={toggle:(_,v)=>personal=v};
    const byId=pageSession.root.querySelector;
    pageSession.root.querySelector=s=>s.includes('data-stamp-type="START"')?starts:s.includes('data-stamp-type="END"')?ends:s.includes('stamp-employee')?group:byId(s);
    pageSession.root.querySelectorAll=()=>[starts,ends];
    let refreshes=0;const controller={dataTable:{refresh:async()=>refreshes++}};
    const c=vm.createContext({mobileQuery:'mobile',APP:{cache:{page:{}}},TimeworksRepository:repository,
        formatDate:()=> '2026-10-12',openMsgDialog(){},window:{matchMedia:()=>media,setInterval:fn=>{timers.add(fn);return fn;},clearInterval:fn=>timers.delete(fn),requestAnimationFrame:fn=>fn()}});
    vm.runInContext(source('pages/management/timeworks/stamp.js'),c);
    const stamp=c.createStamp(pageSession,{initManagement(){}});stamp.initStamp(controller);
    return {stamp,pageSession,starts,ends,input,controller,mediaListeners,timers,refreshes:()=>refreshes,personal:()=>personal};
}
test('stamp owns its media listener and clock and releases both on departure',()=>{
    const h=stampHarness();assert.equal(h.mediaListeners.size,1);assert.equal(h.timers.size,1);h.pageSession.dispose();
    assert.equal(h.mediaListeners.size,0);assert.equal(h.timers.size,0);
});
test('a stamp completion cannot reset a newly selected employee',async()=>{
    const req=deferred(),calls=[];const h=stampHarness({stamp:(id,type)=>{calls.push([id,type]);return req.promise;}});
    await h.stamp.handleEmployeeResolved(h.controller,{_resolvedData:{employeeId:1,canStart:true}});
    const button=h.starts;button.dataset={stampType:'START'};const pending=h.stamp.handleStamp(h.controller,button);
    await h.stamp.handleEmployeeResolved(h.controller,{_resolvedData:{employeeId:2,canEnd:true}});
    req.resolve();await pending;assert.deepEqual(calls,[[1,'START']]);assert.equal(h.ends.disabled,false);assert.equal(h.refreshes(),0);
});
test('repeated stamp events send only one request while saving',async()=>{
    const req=deferred();let calls=0;const h=stampHarness({stamp:()=>{calls++;return req.promise;}});
    await h.stamp.handleEmployeeResolved(h.controller,{_resolvedData:{employeeId:1,canStart:true}});
    h.starts.dataset={stampType:'START'};const first=h.stamp.handleStamp(h.controller,h.starts);
    // Simulate a repeated programmatic event even if another operation enabled the element.
    h.starts.disabled=false;await h.stamp.handleStamp(h.controller,h.starts);assert.equal(calls,1);
    h.pageSession.dispose();req.resolve();await first;assert.equal(h.refreshes(),0);
});
test('member form renders existing members from the HTML template',async()=>{
    const rows=[];
    const list={replaceChildren:()=>rows.splice(0),appendChild:fragment=>rows.push(fragment.row),querySelector:s=>s==='[data-member-row]'?rows[0]:null};
    const template={content:{cloneNode:()=>{
        const role={value:''},name={textContent:''};const row={dataset:{},querySelector:s=>s.includes('role')?role:name};return {row,querySelector:()=>row};
    }}};
    const candidate={replaceChildren(){},add(){},options:[]};
    const pageSession=session({'dispatch-member-current-list':list,'dispatch-member-row-template':template,'dispatch-member-candidate':candidate,
        'dispatch-member-add-role':{},'dispatch-member-add-button':{},'dispatch-work-date':{value:'2026-10-12'},'dispatch-office':{value:'1'},'dispatch-category':{value:'1'}});
    pageSession.root.querySelectorAll=()=>rows;
    const c=vm.createContext({structuredClone,Option:class {},APP:{cache:{common:{state:{INITIAL:0}}}},
        FormController:class {constructor(config){this.config=config;}},DailyCrewRepository:{findMemberCandidates:async()=>({data:[]})}});
    vm.runInContext(source('pages/operations/dispatch/dispatchCrewMemberForm.js'),c);
    const form=c.createCrewMemberForm({}, {session:pageSession});
    await form.config.onOpen({dailyCrewId:1,members:[{employeeId:7,employeeName:'乗務員',role:1}]},form,()=>true);
    assert.equal(rows.length,1);assert.equal(form.config.buildAdditionalPayload().members[0].employeeId,7);
    assert.equal(candidate.disabled,true);assert.equal(pageSession.byId('dispatch-member-add-button').disabled,true);
});
test('an earlier row save cannot mark a newer pending edit as saved',async()=>{
    const req=deferred();const h=managementHarness({updateTimes:()=>req.promise});h.management.initManagement();await flush();
    const classes=new Set(),start={value:'09:00'};
    const row={isConnected:true,dataset:{timeworkId:'7',workDate:'2026-10-12'},
        classList:{remove:(...names)=>names.forEach(n=>classes.delete(n)),add:(...names)=>names.forEach(n=>classes.add(n))},
        querySelector:s=>s.includes('editStart')?start:{value:'17:00'}};
    const event={target:{matches:()=>true,closest:()=>row}};
    h.listeners.change(event);const [timer,run]=h.timers.entries().next().value;h.timers.delete(timer);run();await flush();
    start.value='10:00';h.listeners.change(event);req.resolve();await flush();assert.equal(classes.has('is-saved'),false);assert.equal(classes.has('is-saving'),true);
});
