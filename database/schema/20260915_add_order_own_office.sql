-- アプリ停止・バックアップ後に適用。既存受注の所属は推測して埋めない。
SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF COL_LENGTH('dbo.orders','own_office_id') IS NULL
BEGIN
 ALTER TABLE dbo.orders ADD own_office_id INT NULL;
 ALTER TABLE dbo.orders ADD CONSTRAINT FK_orders_own_office FOREIGN KEY(own_office_id) REFERENCES dbo.offices(office_id);
END;
IF COL_LENGTH('dbo.orders_log','own_office_id') IS NULL
 ALTER TABLE dbo.orders_log ADD own_office_id INT NULL;
COMMIT;
