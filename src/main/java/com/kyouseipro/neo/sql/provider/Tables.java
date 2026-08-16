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

        public static final TableMeta RECYCLE_MANUFACTURER_BY_IDS =
            new TableMeta("recycle_manufacturers", "recycleManufacturerId", "state", "version");

        public static final TableMeta RECYCLE_PRICE_BY_IDS =
            new TableMeta("recycle_prices", "recyclePriceId", "state", "version");

        public static final TableMeta ORDER_BY_IDS =
            new TableMeta("orders", "orderId", "state", "version");

        public static final TableMeta ORDER_ITEM_BY_IDS =
            new TableMeta("order_items", "orderItemId", "state", "version");

        public static final TableMeta ORDER_WORK_BY_IDS =
            new TableMeta("order_works", "orderWorkId", "state", "version");

        public static final TableMeta WORK_MASTER_BY_IDS =
            new TableMeta("work_masters", "workMasterId", "state", "version");
}
