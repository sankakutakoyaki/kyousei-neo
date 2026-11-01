SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF COL_LENGTH('dbo.orders','order_status') IS NULL
BEGIN
    ALTER TABLE dbo.orders ADD order_status VARCHAR(20) NOT NULL CONSTRAINT DF_orders_status DEFAULT 'ACTIVE',
        outcome_type VARCHAR(30) NULL, outcome_reason NVARCHAR(1000) NULL, cancel_reason VARCHAR(30) NULL,
        last_work_date DATE NULL, outcome_request_id UNIQUEIDENTIFIER NULL;
    ALTER TABLE dbo.orders_log ADD order_status VARCHAR(20) NOT NULL CONSTRAINT DF_orders_log_status DEFAULT 'ACTIVE',
        outcome_type VARCHAR(30) NULL, outcome_reason NVARCHAR(1000) NULL, cancel_reason VARCHAR(30) NULL,
        last_work_date DATE NULL, outcome_request_id UNIQUEIDENTIFIER NULL;
END;
IF COL_LENGTH('dbo.attachments','photo_category') IS NULL
BEGIN
    ALTER TABLE dbo.attachments ADD photo_category VARCHAR(30) NULL, work_date DATE NULL, uploaded_by NVARCHAR(255) NULL;
END;
IF COL_LENGTH('dbo.attachments','upload_request_id') IS NULL
    ALTER TABLE dbo.attachments ADD upload_request_id UNIQUEIDENTIFIER NULL, upload_index INT NULL;
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID('dbo.attachments') AND name='UX_attachment_photo_request')
    EXEC(N'CREATE UNIQUE INDEX UX_attachment_photo_request ON dbo.attachments(upload_request_id,upload_index) WHERE upload_request_id IS NOT NULL');
-- 営業所共用アカウントには従業員IDがない。操作者はログのeditorへ記録する。
ALTER TABLE dbo.order_work_results ALTER COLUMN result_employee_id BIGINT NULL;
ALTER TABLE dbo.order_work_results_log ALTER COLUMN result_employee_id BIGINT NULL;
COMMIT TRANSACTION;
