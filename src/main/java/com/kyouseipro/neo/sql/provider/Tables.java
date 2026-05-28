package com.kyouseipro.neo.sql.provider;

import com.kyouseipro.neo.sql.model.TableMeta;

public class Tables {

        public static final TableMeta COMPANY_BY_IDS =
            new TableMeta("companies", "companyId", "state", "version");

        public static final TableMeta EMPLOYEE_BY_IDS =
            new TableMeta("employees", "employeeId", "state", "version");

        public static final TableMeta OFFICE_BY_IDS =
            new TableMeta("offices", "officeId", "state", "version");

        public static final TableMeta STAFF_BY_IDS =
            new TableMeta("staffs", "staffId", "state", "version");

        public static final TableMeta RECYCLE_BY_IDS =
            new TableMeta("recycles", "recycleId", "state", "version");

        public static final TableMeta RECYCLE_MAKER_BY_IDS =
            new TableMeta("recycle_makers", "recycleMakerId", "state", "version");

        public static final TableMeta RECYCLE_PRICE_BY_IDS =
            new TableMeta("recycle_prices", "recyclePriceId", "state", "version");
}
