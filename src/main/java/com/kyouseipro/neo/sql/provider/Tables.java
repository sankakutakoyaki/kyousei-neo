package com.kyouseipro.neo.sql.provider;

import com.kyouseipro.neo.sql.model.TableMeta;

public class Tables {

        public static final TableMeta COMPANY_BY_IDS =
            new TableMeta("companies", "companyId", "state", "version");

        public static final TableMeta EMPLOYEE_BY_IDS =
            new TableMeta("employees", "employeeId", "state", "version");

        public static final TableMeta VEHICLE_BY_IDS =
            new TableMeta("vehicles", "vehicleId", "state", "version");

        public static final TableMeta DAILY_CREW_BY_IDS =
            new TableMeta("daily_crews", "dailyCrewId", "state", "version");

        public static final TableMeta DAILY_CREW_MEMBER_BY_IDS =
            new TableMeta("daily_crew_members", "dailyCrewMemberId", "state", "version");

        public static final TableMeta DISPATCH_ASSIGNMENT_BY_IDS =
            new TableMeta("dispatch_assignments", "dispatchAssignmentId", "state", "version");

        public static final TableMeta EMPLOYEE_SHIFT_BY_IDS =
            new TableMeta("employee_shifts", "employeeShiftId", "state", "version");

        public static final TableMeta VEHICLE_DEFAULT_MEMBER_BY_IDS =
            new TableMeta("vehicle_default_members", "vehicleDefaultMemberId", "state", "version");

        public static final TableMeta VEHICLE_DISPATCH_CATEGORY_BY_IDS =
            new TableMeta("vehicle_dispatch_categories", "vehicleDispatchCategoryId", "state", "version");

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

        public static final TableMeta ITEM_MASTER_BY_IDS =
            new TableMeta("item_masters", "itemMasterId", "state", "version");

        public static final TableMeta WORK_MASTER_BY_IDS =
            new TableMeta("work_masters", "workMasterId", "state", "version");

        public static final TableMeta EMPLOYEE_WORK_CATEGORY_BY_IDS =
            new TableMeta("employee_work_categories", "employeeWorkCategoryId", "state", "version");

        public static final TableMeta EMPLOYEE_WORK_CATEGORY_MEMBER_BY_IDS =
            new TableMeta(
                "employee_work_category_members", "employeeWorkCategoryMemberId", "state", "version");
}
