-- アプリ停止・バックアップ後、対象DBで実行。再実行可能。
SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF OBJECT_ID('dbo.order_dispatch_versions', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.order_dispatch_versions (
        order_id INT NOT NULL PRIMARY KEY REFERENCES dbo.orders(order_id),
        version INT NOT NULL DEFAULT 0
    );
END;
IF OBJECT_ID('dbo.order_dispatch_assignments', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.order_dispatch_assignments (
        assignment_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        order_id INT NOT NULL REFERENCES dbo.orders(order_id),
        employee_id INT NOT NULL REFERENCES dbo.employees(employee_id),
        role VARCHAR(12) NOT NULL CHECK (role IN ('DELIVERY', 'INSTALL')),
        employee_name NVARCHAR(255) NOT NULL,
        assigned_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        assigned_by NVARCHAR(255) NOT NULL,
        cancelled_at DATETIME2 NULL,
        cancelled_by NVARCHAR(255) NULL
    );
    CREATE UNIQUE INDEX UX_dispatch_active ON dbo.order_dispatch_assignments(order_id, role, employee_id)
        WHERE cancelled_at IS NULL;
    CREATE INDEX IX_dispatch_employee ON dbo.order_dispatch_assignments(employee_id, cancelled_at, order_id);
END;
COMMIT;
