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

const FORM_CONFIG = [
  { name: 'employee-id', key: 'employee_id' },

  { name: 'code', key: 'code', emptyIf: 0 },

  { name: 'category', key: 'category' },
  { name: 'account', key: 'account' },

  { name: 'last-name', key: 'last_name' },
  { name: 'first-name', key: 'first_name' },
  { name: 'last-name-kana', key: 'last_name_kana' },
  { name: 'first-name-kana', key: 'first_name_kana' },

  { name: 'phone-number', key: 'phone_number' },
  { name: 'postal-code', key: 'postal_code' },
  { name: 'full-address', key: 'full_address' },
  { name: 'email', key: 'email' },

  { name: 'birthday', key: 'birthday', emptyIf: '9999-12-31' },
  { name: 'date-of-hire', key: 'date_of_hire', emptyIf: '9999-12-31' },

  { name: 'emergency-contact', key: 'emergency_contact' },
  { name: 'emergency-contact-number', key: 'emergency_contact_number' },

  { name: 'version', key: 'version' }
];

const SAVE_FORM_CONFIG = [
  { name: 'employee-id', key: 'employee_id' },

  { name: 'code', key: 'code', emptyTo: 0, number: true },

  { name: 'category', key: 'category', number: true },
  { name: 'account', key: 'account', trim: true },

  { name: 'company', key: 'company_id', number: true },
  { name: 'office', key: 'office_id', number: true },

  { name: 'last-name', key: 'last_name', trim: true },
  { name: 'first-name', key: 'first_name', trim: true },
  { name: 'last-name-kana', key: 'last_name_kana', trim: true },
  { name: 'first-name-kana', key: 'first_name_kana', trim: true },

  { name: 'phone-number', key: 'phone_number', trim: true },
  { name: 'postal-code', key: 'postal_code', trim: true },
  { name: 'full-address', key: 'full_address', trim: true },
  { name: 'email', key: 'email', trim: true },

  { name: 'gender', key: 'gender' },
  { name: 'blood-type', key: 'blood_type' },

  { name: 'birthday', key: 'birthday', emptyTo: '9999-12-31' },
  { name: 'date-of-hire', key: 'date_of_hire', emptyTo: '9999-12-31' },

  { name: 'emergency-contact', key: 'emergency_contact', trim: true },
  { name: 'emergency-contact-number', key: 'emergency_contact_number', trim: true },

  { name: 'version', key: 'version', number: true }
];

const COMBO_CONFIG = [
  {
    selector: 'select[name="company"]',
    list: companyComboList,
    key: 'company_id',
    onChange: (form, el) => createOfficeComboBox(form, el, officeList)
  },
  {
    selector: 'select[name="gender"]',
    list: genderComboList,
    key: 'gender'
  },
  {
    selector: 'select[name="blood-type"]',
    list: bloodTypeComboList,
    key: 'blood_type'
  }
];