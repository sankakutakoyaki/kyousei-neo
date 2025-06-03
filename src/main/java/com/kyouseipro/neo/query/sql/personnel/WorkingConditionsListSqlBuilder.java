package com.kyouseipro.neo.query.sql.personnel;

public class WorkingConditionsListSqlBuilder {
    
    public static String buildFindAllSql() {
        return "SELECT * FROM working_conditions WHERE NOT (state = ?)";
    }
}
