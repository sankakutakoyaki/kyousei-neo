import test from 'node:test';
import assert from 'node:assert/strict';
import vm from 'node:vm';
import {readFileSync} from 'node:fs';
import {installOrderEnumCache} from './fixtures/orderEnumCache.mjs';
import {workResultLabel,isReportableWorkResult} from '../../main/resources/static/js/components/order/orderEnums.js';
import {absenceLabel} from '../../main/resources/static/js/components/order/workResultSummary.js';
installOrderEnumCache();
const code=readFileSync(new URL('../../main/resources/static/js/pages/business/myorder/myOrderPage.js',import.meta.url),'utf8').replace(/^import .*;\n/gm,'').replace('export async function init','async function init');
class Element {
    constructor(){this.children=[];this.dataset={};this.isConnected=true;this.value='';}
    append(...children){this.children.push(...children);}
    appendChild(child){this.children.push(child);return child;}
    replaceChildren(...children){this.children=children;}
    addEventListener(){}
}
test('my-order counter and cards use cached enum definitions after initialization',()=>{
    const root=new Element(),nodes=new Map();root.querySelector=key=>{if(!nodes.has(key))nodes.set(key,new Element());return nodes.get(key);};
    const document={querySelector:()=>root,getElementById:()=>new Element(),createElement:()=>new Element()};
    const context=vm.createContext({document,workResultLabel,isReportableWorkResult,absenceLabel,
        FormController:class {},OrderPhotoPanel:class {},WorkResultDialog:class {}});
    vm.runInContext(code+'\nglobalThis.create=()=>new MyOrderPageController();',context);
    const page=context.create();page.render([{orderId:1,dailyCrewId:1,resultType:'COMPLETED'},{orderId:2,dailyCrewId:1,resultType:null}]);
    assert.equal(page.count.textContent,'2件（未報告 1件）');
    const cards=page.list.children[0].children.slice(1);
    assert.equal(cards[0].children[0].children[1].textContent,'完了');
    assert.equal(cards[1].children[0].children[1].textContent,'未報告');
});
