SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF COL_LENGTH('dbo.orders','outcome_reported_at') IS NULL
BEGIN
    ALTER TABLE dbo.orders ADD outcome_reported_at DATETIME2 NULL;
    ALTER TABLE dbo.orders_log ADD outcome_reported_at DATETIME2 NULL;
END;
DECLARE @constraint SYSNAME;
SELECT @constraint=name FROM sys.check_constraints WHERE parent_object_id=OBJECT_ID('dbo.order_work_results') AND definition LIKE '%result_type%';
IF @constraint IS NOT NULL
BEGIN
    DECLARE @drop_sql NVARCHAR(MAX)=N'ALTER TABLE dbo.order_work_results DROP CONSTRAINT '+QUOTENAME(@constraint);
    EXEC sys.sp_executesql @drop_sql;
END;
ALTER TABLE dbo.order_work_results WITH CHECK ADD CONSTRAINT CK_order_work_results_type CHECK(result_type IN ('COMPLETED','DELIVERY_FAILED','POSTPONED','ABSENT'));
IF EXISTS(SELECT 1 FROM sys.check_constraints WHERE parent_object_id=OBJECT_ID('dbo.order_histories') AND name='CK_order_histories_event')
    ALTER TABLE dbo.order_histories DROP CONSTRAINT CK_order_histories_event;
ALTER TABLE dbo.order_histories WITH CHECK ADD CONSTRAINT CK_order_histories_event CHECK (
    (event_type IN ('ORDER_CREATED','ORDER_SAVED') AND dispatch_assignment_id IS NULL AND dispatch_version IS NULL AND work_result_id IS NULL AND work_result_version IS NULL)
    OR (event_type IN ('DISPATCH_CREATED','DISPATCH_MOVED','DISPATCH_REMOVED','DISPATCH_RESET','DISPATCH_REORDERED') AND dispatch_assignment_id IS NOT NULL AND dispatch_version IS NOT NULL AND work_result_id IS NULL AND work_result_version IS NULL)
    OR (event_type IN ('WORK_COMPLETED','DELIVERY_FAILED','POSTPONED','ABSENT','WORK_RESULT_CORRECTED') AND work_result_id IS NOT NULL AND work_result_version IS NOT NULL AND dispatch_assignment_id IS NULL AND dispatch_version IS NULL)
);
COMMIT TRANSACTION;
