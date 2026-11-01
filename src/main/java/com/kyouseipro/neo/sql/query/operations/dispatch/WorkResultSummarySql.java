package com.kyouseipro.neo.sql.query.operations.dispatch;

/** 訪問日当日の有効な配車だけを集計する。過去日の報告は履歴で参照する。 */
public final class WorkResultSummarySql {
    private WorkResultSummarySql() {}

    public static final String JOIN = """
        OUTER APPLY (
            SELECT
                COUNT(CASE WHEN wr.result_type = 'COMPLETED' THEN 1 END) AS completed_count,
                COUNT(CASE WHEN wr.result_type = 'DELIVERY_FAILED' THEN 1 END) AS failed_count,
                COUNT(CASE WHEN wr.result_type = 'POSTPONED' THEN 1 END) AS postponed_count,
                COUNT(CASE WHEN wr.work_result_id IS NULL THEN 1 END) AS unreported_count
            FROM dbo.dispatch_assignments result_assignment
            INNER JOIN dbo.daily_crews result_crew
                ON result_crew.daily_crew_id = result_assignment.daily_crew_id
                AND result_crew.state = 0 AND result_crew.work_date = o.visit_date
            LEFT JOIN dbo.order_work_results wr
                ON wr.dispatch_assignment_id = result_assignment.dispatch_assignment_id
                AND wr.work_date = result_crew.work_date AND wr.state = 0
            WHERE result_assignment.order_id = o.order_id AND result_assignment.state = 0
        ) result_summary
        """;
}
