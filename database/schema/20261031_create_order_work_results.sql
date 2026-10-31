-- 第4段階の配車履歴拡張の後、アプリ更新前に適用する。
SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF OBJECT_ID(N'dbo.order_work_results', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.order_work_results (
        work_result_id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        dispatch_assignment_id INT NOT NULL REFERENCES dbo.dispatch_assignments(dispatch_assignment_id),
        result_type VARCHAR(32) NOT NULL CHECK (result_type IN ('COMPLETED','DELIVERY_FAILED','POSTPONED')),
        reason NVARCHAR(1000) NOT NULL,
        work_date DATE NOT NULL,
        result_employee_id BIGINT NOT NULL,
        request_id UNIQUEIDENTIFIER NOT NULL UNIQUE,
        version INT NOT NULL DEFAULT 0,
        state INT NOT NULL DEFAULT 0,
        regist_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        update_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT UQ_order_work_results_visit UNIQUE(dispatch_assignment_id,work_date)
    );
END;
IF OBJECT_ID(N'dbo.order_work_results_log', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.order_work_results_log (
        editor NVARCHAR(255) NOT NULL,
        process NVARCHAR(50) NOT NULL,
        log_date DATETIME2 NOT NULL,
        work_result_id BIGINT NOT NULL,
        dispatch_assignment_id INT NOT NULL,
        result_type VARCHAR(32) NOT NULL,
        reason NVARCHAR(1000) NOT NULL,
        work_date DATE NOT NULL,
        result_employee_id BIGINT NOT NULL,
        request_id UNIQUEIDENTIFIER NOT NULL,
        version INT NOT NULL,
        state INT NOT NULL,
        regist_date DATETIME2 NOT NULL,
        update_date DATETIME2 NOT NULL,
        UNIQUE(work_result_id, version),
        UNIQUE(request_id)
    );
END;
IF COL_LENGTH(N'dbo.order_work_results', N'work_date') IS NULL
BEGIN
    IF EXISTS(SELECT 1 FROM dbo.order_work_results) OR EXISTS(SELECT 1 FROM dbo.order_work_results_log)
        THROW 50001, N'訪問日のない既存結果があります。移行内容の確認が必要です。', 1;
    ALTER TABLE dbo.order_work_results ADD work_date DATE NOT NULL;
    ALTER TABLE dbo.order_work_results_log ADD work_date DATE NOT NULL;
    DECLARE @old_visit_key SYSNAME;
    SELECT @old_visit_key=kc.name FROM sys.key_constraints kc
    JOIN sys.index_columns ic ON ic.object_id=kc.parent_object_id AND ic.index_id=kc.unique_index_id
    JOIN sys.columns c ON c.object_id=ic.object_id AND c.column_id=ic.column_id
    WHERE kc.parent_object_id=OBJECT_ID(N'dbo.order_work_results') AND kc.type='UQ'
        AND c.name='dispatch_assignment_id';
    IF @old_visit_key IS NOT NULL
    BEGIN
        DECLARE @drop_visit_key NVARCHAR(MAX)=N'ALTER TABLE dbo.order_work_results DROP CONSTRAINT '+QUOTENAME(@old_visit_key);
        EXEC sys.sp_executesql @drop_visit_key;
    END;
    EXEC(N'ALTER TABLE dbo.order_work_results ADD CONSTRAINT UQ_order_work_results_visit UNIQUE(dispatch_assignment_id,work_date)');
END;
-- 公開前の空の検証テーブルに残った予定日列だけを整理する。
IF COL_LENGTH(N'dbo.order_work_results', N'next_visit_date') IS NOT NULL
BEGIN
    IF EXISTS(SELECT 1 FROM dbo.order_work_results) OR EXISTS(SELECT 1 FROM dbo.order_work_results_log)
        THROW 50002, N'予定日の列に関連する既存結果があります。移行確認が必要です。', 1;
    DECLARE @date_check SYSNAME;
    SELECT @date_check=name FROM sys.check_constraints WHERE parent_object_id=OBJECT_ID(N'dbo.order_work_results') AND definition LIKE N'%next_visit_date%';
    IF @date_check IS NOT NULL
    BEGIN
        DECLARE @drop_date_check NVARCHAR(MAX)=N'ALTER TABLE dbo.order_work_results DROP CONSTRAINT '+QUOTENAME(@date_check);
        EXEC sys.sp_executesql @drop_date_check;
    END;
    EXEC(N'ALTER TABLE dbo.order_work_results DROP COLUMN next_visit_date');
    EXEC(N'ALTER TABLE dbo.order_work_results_log DROP COLUMN next_visit_date');
END;
IF COL_LENGTH(N'dbo.order_histories', N'work_result_id') IS NULL
    ALTER TABLE dbo.order_histories ADD work_result_id BIGINT NULL;
IF COL_LENGTH(N'dbo.order_histories', N'work_result_version') IS NULL
    ALTER TABLE dbo.order_histories ADD work_result_version INT NULL;
EXEC sys.sp_executesql N'
IF EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N''dbo.order_histories'') AND name=N''UX_order_histories_save'')
    DROP INDEX UX_order_histories_save ON dbo.order_histories;
CREATE UNIQUE INDEX UX_order_histories_save ON dbo.order_histories(order_id, order_version)
    WHERE dispatch_assignment_id IS NULL AND work_result_id IS NULL;
IF EXISTS (SELECT 1 FROM sys.check_constraints WHERE parent_object_id=OBJECT_ID(N''dbo.order_histories'') AND name=N''CK_order_histories_event'')
    ALTER TABLE dbo.order_histories DROP CONSTRAINT CK_order_histories_event;
ALTER TABLE dbo.order_histories WITH CHECK ADD CONSTRAINT CK_order_histories_event CHECK (
    (event_type IN (''ORDER_CREATED'',''ORDER_SAVED'') AND dispatch_assignment_id IS NULL AND dispatch_version IS NULL AND work_result_id IS NULL AND work_result_version IS NULL)
    OR (event_type IN (''DISPATCH_CREATED'',''DISPATCH_MOVED'',''DISPATCH_REMOVED'',''DISPATCH_RESET'',''DISPATCH_REORDERED'')
        AND dispatch_assignment_id IS NOT NULL AND dispatch_version IS NOT NULL AND work_result_id IS NULL AND work_result_version IS NULL)
    OR (event_type IN (''WORK_COMPLETED'',''DELIVERY_FAILED'',''POSTPONED'',''WORK_RESULT_CORRECTED'')
        AND work_result_id IS NOT NULL AND work_result_version IS NOT NULL AND dispatch_assignment_id IS NULL AND dispatch_version IS NULL)
);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N''dbo.order_histories'') AND name=N''UX_order_histories_work_result'')
    CREATE UNIQUE INDEX UX_order_histories_work_result ON dbo.order_histories(work_result_id,work_result_version) WHERE work_result_id IS NOT NULL;
';
COMMIT TRANSACTION;
