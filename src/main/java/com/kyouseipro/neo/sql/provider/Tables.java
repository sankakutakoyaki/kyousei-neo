package com.kyouseipro.neo.sql.provider;

import com.kyouseipro.neo.sql.model.TableMeta;

public class Tables {

        public static final TableMeta PARTNER_COMPANY =
            new TableMeta("companies", "companyId", "state", "version");

        public static final TableMeta PARTNER_EMPLOYEE =
            new TableMeta("employees", "employeeId", "state", "version");

}
