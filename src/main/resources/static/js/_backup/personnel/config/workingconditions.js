"use strict"

const MODE_CONFIG = {
    "01": {
        tableId: "table-01-content",
        footerId: "footer-01",
        searchId: "search-box-01",
        category: categoryCodes.FULLTIME
    },
    "02": {
        tableId: "table-02-content",
        footerId: "footer-02",
        searchId: "search-box-02",
        category: categoryCodes.PARTTIME
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
    { name: 'working-conditions-id', key: 'workingConditionsId' },
    { name: 'employee-id', key: 'employeeId' },
    { name: 'category', key: 'category' },

    { name: 'base-salary', key: 'baseSalary' },
    { name: 'trans-cost', key: 'transCost' },

    { name: 'version', key: 'version' }
];

const COMBO_CONFIG = [
    {
        selector: 'select[name="payment-method"]',
        list: paymentMethodComboList,
        key: 'paymentMethod'
    },
    {
        selector: 'select[name="pay-type"]',
        list: payTypeComboList,
        key: 'payType'
    }
];

const SAVE_CONFIG = [
    {
        name: 'working-conditions-id',
        key: 'workingConditionsId',
        parse: v => Number(v)
    },
    {
        name: 'employee-id',
        key: 'employeeId',
        parse: v => Number(v)
    },
    {
        name: 'category',
        key: 'category',
        parse: v => Number(v)
    },
    {
        name: 'payment-method',
        key: 'paymentMethod'
    },
    {
        name: 'pay-type',
        key: 'payType'
    },
    {
        name: 'base-salary',
        key: 'baseSalary',
        parse: v => v === '' ? 0 : Number(v)
    },
    {
        name: 'trans-cost',
        key: 'transCost',
        parse: v => v === '' ? 0 : Number(v)
    },
    {
        name: 'version',
        key: 'version'
    }
];