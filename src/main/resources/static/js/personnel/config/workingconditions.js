"use strict"

const MODE_CONFIG = {
    "01": {
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01",
        category: categoryEmployeeCode
    },
    "02": {
        tableId: "table-02-content",
        footerId: "footer-02",
        searchId: "search-box-02",
        category: categoryParttimeCode
    }
};

const VIEW_CONFIG = [
  {
    id: 'employee-name',
    value: (entity, ctx) =>
      String(ctx.employeeId).padStart(4, '0') + ' : ' + ctx.employeeName
  }
];

const FORM_CONFIG = [
  { name: 'working-conditions-id', key: 'working_conditions_id' },
  { name: 'employee-id', value: (e, ctx) => ctx.employeeId },
  { name: 'category', key: 'category' },

  { name: 'base-salary', key: 'base_salary' },
  { name: 'trans-cost', key: 'trans_cost' },
  { name: 'basic-start-time', key: 'basic_start_time' },
  { name: 'basic-end-time', key: 'basic_end_time' },

  { name: 'version', key: 'version' }
];

const COMBO_CONFIG = [
  {
    selector: 'select[name="payment-method"]',
    list: paymentMethodComboList,
    key: 'payment_method'
  },
  {
    selector: 'select[name="pay-type"]',
    list: payTypeComboList,
    key: 'pay_type'
  }
];

const SAVE_CONFIG = [
  {
    name: 'working-conditions-id',
    key: 'working_conditions_id',
    parse: v => Number(v)
  },
  {
    name: 'employee-id',
    key: 'employee_id',
    parse: v => Number(v)
  },
  {
    name: 'category',
    key: 'category',
    parse: v => Number(v)
  },
  {
    name: 'payment-method',
    key: 'payment_method'
  },
  {
    name: 'pay-type',
    key: 'pay_type'
  },
  {
    name: 'base-salary',
    key: 'base_salary',
    parse: v => v === '' ? 0 : Number(v)
  },
  {
    name: 'trans-cost',
    key: 'trans_cost',
    parse: v => v === '' ? 0 : Number(v)
  },
  {
    name: 'basic-start-time',
    key: 'basic_start_time',
    parse: v => v ? v : '00:00'
  },
  {
    name: 'basic-end-time',
    key: 'basic_end_time',
    parse: v => v ? v : '00:00'
  },
  {
    name: 'version',
    key: 'version'
  }
];