import test from 'node:test';
import assert from 'node:assert/strict';
import {qualificationInView as matches} from '../../main/resources/static/js/pages/operations/qualificationViews.js';
test('qualification tabs distinguish renewal deadlines and expired records',()=>{
 const today='2026-09-13';
 assert.equal(matches({expiryDate:'2026-09-12'},'expired',today),true);
 assert.equal(matches({expiryDate:today},'expired',today),false);
 assert.equal(matches({expiryDate:today},'renewal',today),true);
 assert.equal(matches({expiryDate:'2026-09-12'},'renewal',today),false);
 assert.equal(matches({renewalDate:'2026-09-01'},'renewal',today),true);
 assert.equal(matches({expiryDate:'2027-09-13'},'renewal',today),false);
 assert.equal(matches({},'renewal',today),false);
 assert.equal(matches({},'all',today),true);
});
