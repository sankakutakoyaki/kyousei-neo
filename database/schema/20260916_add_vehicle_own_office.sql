-- アプリ停止・バックアップ後に適用。既存車両の営業所は未設定で保持。
SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF COL_LENGTH('dbo.operation_vehicle','own_office_id') IS NULL
BEGIN
 ALTER TABLE dbo.operation_vehicle ADD own_office_id INT NULL;
 ALTER TABLE dbo.operation_vehicle ADD CONSTRAINT FK_operation_vehicle_office FOREIGN KEY(own_office_id) REFERENCES dbo.offices(office_id);
END;
IF COL_LENGTH('dbo.operation_vehicle_log','own_office_id') IS NULL
 ALTER TABLE dbo.operation_vehicle_log ADD own_office_id INT NULL;
COMMIT;
