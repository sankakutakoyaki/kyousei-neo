package com.kyouseipro.neo.sql.query.business.order;

/** 訪問予定日、予定未定なら最終報告日に配車された有効な班・乗務員を参照する。 */
final class OrderAssigneeSql {
    private OrderAssigneeSql() {}
    static final String JOIN = """
        OUTER APPLY (
            SELECT COUNT(*) AS assigned_crew_count
            FROM dbo.dispatch_assignments crew_assignment
            JOIN dbo.daily_crews crew ON crew.daily_crew_id=crew_assignment.daily_crew_id AND crew.state=0
            WHERE crew_assignment.order_id=o.order_id AND crew_assignment.state=0 AND crew.work_date=COALESCE(o.visit_date,o.last_work_date)
        ) order_crews
        OUTER APPLY (
            SELECT STRING_AGG(CAST(NULLIF(LTRIM(RTRIM(person.full_name)),N'') AS NVARCHAR(MAX)),N'・')
                WITHIN GROUP (ORDER BY person.employee_id) AS assigned_employee_names
            FROM (
                SELECT DISTINCT e.employee_id,e.full_name
                FROM dbo.dispatch_assignments people_assignment
                JOIN dbo.daily_crews people_crew ON people_crew.daily_crew_id=people_assignment.daily_crew_id AND people_crew.state=0
                JOIN dbo.daily_crew_members member ON member.daily_crew_id=people_crew.daily_crew_id AND member.state=0
                JOIN dbo.employees e ON e.employee_id=member.employee_id AND e.state=0
                WHERE people_assignment.order_id=o.order_id AND people_assignment.state=0 AND people_crew.work_date=COALESCE(o.visit_date,o.last_work_date)
            ) person
        ) order_people
        """;
}
