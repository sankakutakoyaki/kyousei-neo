import test from 'node:test';
import assert from 'node:assert/strict';
import {orderEnumLabel,isSimpleWorkResult} from '../../main/resources/static/js/components/order/orderEnums.js';
test('labels are read from the current server cache rather than duplicated client definitions',()=>{
    globalThis.APP={cache:{common:{orderEnums:{workResult:{COMPLETED:'サーバーの表示名'}}}}};
    assert.equal(orderEnumLabel('workResult','COMPLETED'),'サーバーの表示名');
    globalThis.APP.cache.common.orderEnums.workResult.COMPLETED='更新した表示名';
    assert.equal(orderEnumLabel('workResult','COMPLETED'),'更新した表示名');
    assert.equal(orderEnumLabel('workResult','UNKNOWN'),'UNKNOWN');assert.equal(orderEnumLabel('workResult',null),'');
});
test('confirmation rules are also supplied by the server',()=>{
    globalThis.APP={cache:{common:{workResultRules:{ABSENT:{confirmationOnly:true}}}}};
    assert.equal(isSimpleWorkResult('ABSENT'),true);assert.equal(isSimpleWorkResult('UNKNOWN'),false);
});
