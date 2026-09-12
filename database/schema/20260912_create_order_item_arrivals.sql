-- アプリ停止中に実行。既存入荷日を全数入荷の初期履歴へ移行する。
SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF COL_LENGTH('dbo.order_items', 'expected_arrival_date') IS NULL
    ALTER TABLE dbo.order_items ADD expected_arrival_date DATE NULL;
IF COL_LENGTH('dbo.order_items_log', 'expected_arrival_date') IS NULL
    ALTER TABLE dbo.order_items_log ADD expected_arrival_date DATE NULL;
IF OBJECT_ID('dbo.order_item_arrivals', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.order_item_arrivals (
        arrival_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        order_item_id INT NOT NULL REFERENCES dbo.order_items(order_item_id),
        arrival_date DATE NOT NULL,
        quantity INT NOT NULL CHECK (quantity > 0),
        request_id VARCHAR(36) NOT NULL UNIQUE,
        replaces_id BIGINT NULL REFERENCES dbo.order_item_arrivals(arrival_id),
        registered_by NVARCHAR(255) NOT NULL,
        registered_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        cancelled_at DATETIME2 NULL,
        cancelled_by NVARCHAR(255) NULL,
        cancel_request_id VARCHAR(36) NULL
    );
    CREATE INDEX IX_order_item_arrivals_item ON dbo.order_item_arrivals(order_item_id, cancelled_at);
    IF EXISTS (SELECT 1 FROM dbo.order_items WHERE arrival_date IS NOT NULL AND (item_quantity IS NULL OR item_quantity <= 0))
        THROW 51000, N'入荷済み商品の数量に空欄または0以下があります。数量を確認して再実行してください。', 1;
    INSERT dbo.order_item_arrivals(order_item_id, arrival_date, quantity, request_id, registered_by)
    SELECT order_item_id, arrival_date, item_quantity, CONVERT(VARCHAR(36), NEWID()), N'既存データ移行'
    FROM dbo.order_items WHERE arrival_date IS NOT NULL;
END;
COMMIT;
