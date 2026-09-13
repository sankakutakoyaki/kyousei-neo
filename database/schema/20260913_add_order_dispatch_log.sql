-- 先に20260913_create_order_dispatch.sqlを適用。アプリ停止・バックアップ後に実行。
SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF OBJECT_ID('dbo.order_dispatch_assignments', 'U') IS NULL
    THROW 51000, N'先に配車テーブル作成SQLを適用してください。', 1;
IF OBJECT_ID('dbo.order_dispatch_assignments_log', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.order_dispatch_assignments_log (
        log_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        assignment_id BIGINT NOT NULL,
        order_id INT NOT NULL,
        employee_id INT NOT NULL,
        role VARCHAR(12) NOT NULL,
        employee_name NVARCHAR(255) NOT NULL,
        assigned_at DATETIME2 NOT NULL,
        assigned_by NVARCHAR(255) NOT NULL,
        cancelled_at DATETIME2 NULL,
        cancelled_by NVARCHAR(255) NULL,
        editor NVARCHAR(255) NOT NULL,
        process VARCHAR(20) NOT NULL,
        log_date DATETIME2 NOT NULL,
        dispatch_version INT NOT NULL
    );
    CREATE INDEX IX_dispatch_log_order ON dbo.order_dispatch_assignments_log(order_id, dispatch_version, log_id);
    -- 適用前の割り当ては現在状態の初期スナップショット。過去の更新ログとは区別する。
    INSERT dbo.order_dispatch_assignments_log
        (assignment_id, order_id, employee_id, role, employee_name, assigned_at, assigned_by,
         cancelled_at, cancelled_by, editor, process, log_date, dispatch_version)
    SELECT a.assignment_id, a.order_id, a.employee_id, a.role, a.employee_name, a.assigned_at, a.assigned_by,
        a.cancelled_at, a.cancelled_by, N'配車ログ初期移行', 'MIGRATE', SYSDATETIME(), COALESCE(v.version,0)
    FROM dbo.order_dispatch_assignments a
    LEFT JOIN dbo.order_dispatch_versions v ON v.order_id=a.order_id;
END;
COMMIT;
