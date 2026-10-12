import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';
const base = '../../main/resources/static/js/';
function load(path, name, dependencies){
    const code=readFileSync(new URL(base+path,import.meta.url),'utf8')
        .replace(/^import .*;\n/gm,'').replace(/export /g,'');
    const context=vm.createContext(dependencies);
    vm.runInContext(code+`\nglobalThis.loaded = ${name};`,context);
    return context.loaded;
}
function repositories(api){
    const dependencies={api,RequestClient:{request:()=>{throw new Error('コンボはquery APIを使用しない');}}};
    return {
        company:load('repositories/corporation/company/CompanyRepository.js','CompanyRepository',dependencies),
        office:load('repositories/corporation/company/OfficeRepository.js','OfficeRepository',dependencies)
    };
}
test('client and partner company combos use matching APIs and preserve ComboDto fields',async()=>{
    const urls=[],rows=[{value:7,label:'会社'}];
    const {company}=repositories({get:async url=>{urls.push(url);return {data:rows};}});
    assert.equal(await company.fetchCombo('client'),rows);
    assert.equal(await company.fetchCombo('partner'),rows);
    assert.deepEqual(urls,['/api/company/client/combo','/api/company/partner/combo']);
    await assert.rejects(company.fetchCombo('unknown'),/未対応/);
    assert.equal(urls.length,2);
});
test('company services select the category used by their page cache',async()=>{
    const scopes=[];
    const dependencies={CompanyRepository:{fetchCombo:async scope=>scopes.push(scope)}};
    const partner=load('services/corporation/company/CompanyService.js','CompanyService',dependencies);
    const client=load('services/corporation/company/ClientCompanyService.js','ClientCompanyService',dependencies);
    await partner.fetchCombo();await client.fetchCombo();assert.deepEqual(scopes,['partner','client']);
});
test('office combo refresh preserves parent linkage and updates page cache after success',async()=>{
    const urls=[],rows=[{value:2,label:'営業所',parent:7}],cache={};
    const {office}=repositories({get:async url=>{urls.push(url);return {data:rows};}});
    const service=load('services/corporation/company/OfficeService.js','OfficeService',{
        OfficeRepository:office,PageCacheService:{set:(key,value)=>cache[key]=value}
    });
    assert.equal(await service.refreshCombo(),rows);assert.equal(cache.officeComboList,rows);
    assert.deepEqual(urls,['/api/office/client/combo']);
});
test('failed combo refresh leaves the existing page cache intact',async()=>{
    const previous=[{value:1,label:'旧営業所',parent:7}],cache={officeComboList:previous};
    const {office}=repositories({get:async()=>{throw new Error('通信失敗');}});
    const service=load('services/corporation/company/OfficeService.js','OfficeService',{
        OfficeRepository:office,PageCacheService:{set:(key,value)=>cache[key]=value}
    });
    await assert.rejects(service.refreshCombo(),/通信失敗/);assert.equal(cache.officeComboList,previous);
});
