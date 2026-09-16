import test from 'node:test';
import assert from 'node:assert/strict';
import vm from 'node:vm';
import {readFileSync} from 'node:fs';
import {createVehicleColumns,createDispatchColumns} from '../../main/resources/static/js/pages/operations/dispatch/columns.js';
const source=path=>readFileSync(new URL('../../main/resources/static/js/'+path,import.meta.url),'utf8').replace(/^import .*;\n/gm,'').replaceAll('export ','');
test('paired repositories share a single snapshot and refresh explicitly',async()=>{
 const calls=[];
 const context=vm.createContext({RequestClient:{request:async({queryId,params})=>{calls.push({queryId,params});return {data:queryId==='operationDispatchList'?[{orderId:10,crewIds:'7'}]:params.entity==='VEHICLE'?[{id:1,code:'V1'}]:[{id:7,vehicleId:1}]};}}});
 const repositories=vm.runInContext(source('repositories/operations/DispatchRepository.js')+'\ncreateDispatchRepositories()',context);
 const params={dateFrom:'2026-09-16',dateTo:'2026-09-16',ownOfficeId:2};
 const [orders,vehicles]=await Promise.all([repositories.orders.search(params),repositories.vehicles.search(params)]);
 assert.equal(calls.length,3);assert.equal(orders.length,1);assert.equal(vehicles[0].crew.id,7);assert.equal(vehicles[0].assignedOrders[0].orderId,10);
 repositories.invalidate();await repositories.orders.search(params);assert.equal(calls.length,6);
});
test('DataTable rejects stale response and stale failure, preserving latest data',async()=>{
 const context=vm.createContext({filterFactory:{keyword:()=>()=>true}});
 const DataTable=vm.runInContext(source('core/table/DataTable.js')+'\nDataTable',context);
 const pending=[];
 const table=Object.create(DataTable.prototype);Object.assign(table,{fetchRevision:0,controller:{},model:{setOrigin(data){this.data=data;}},repository:{search:()=>new Promise((resolve,reject)=>pending.push({resolve,reject}))}});
 const first=table.fetch(),second=table.fetch();pending[1].resolve(['new']);assert.equal(await second,true);pending[0].resolve(['old']);assert.equal(await first,false);assert.deepEqual(table.model.data,['new']);
 const third=table.fetch(),fourth=table.fetch();pending[3].resolve(['latest']);await fourth;pending[2].reject(new Error('old failure'));assert.equal(await third,false);assert.deepEqual(table.model.data,['latest']);
});
test('columns escape database text while retaining buttons',()=>{
 const malicious='<img src=x onerror=alert(1)>';
 const html=createDispatchColumns()[0].render({orderId:1,state:0,title:malicious});
 assert.ok(html.includes('&lt;img'));assert.ok(!html.includes('<img'));assert.ok(html.includes('data-dispatch-action="select"'));
 const vehicle=createVehicleColumns()[0].render({id:1,code:malicious,name:'車両',crew:null,assignedOrders:[]});
 assert.ok(vehicle.includes('&lt;img'));assert.ok(!vehicle.includes('data-dispatch-action="select"'));
});
