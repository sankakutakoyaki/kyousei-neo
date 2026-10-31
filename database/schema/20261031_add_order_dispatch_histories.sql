-- 20261031_create_order_histories.sql の後、アプリ更新前に適用する。
SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF COL_LENGTH(N'dbo.order_histories', N'dispatch_assignment_id') IS NULL
    ALTER TABLE dbo.order_histories ADD dispatch_assignment_id INT NULL;
IF COL_LENGTH(N'dbo.order_histories', N'dispatch_version') IS NULL
    ALTER TABLE dbo.order_histories ADD dispatch_version INT NULL;
-- 追加列を同じバッチで参照するため、動的SQLでコンパイル境界を分ける。
EXEC sys.sp_executesql N'
IF EXISTS (SELECT 1 FROM sys.key_constraints WHERE parent_object_id=OBJECT_ID(N''dbo.order_histories'') AND name=N''UQ_order_histories_save'')
    ALTER TABLE dbo.order_histories DROP CONSTRAINT UQ_order_histories_save;
IF EXISTS (SELECT 1 FROM sys.check_constraints WHERE parent_object_id=OBJECT_ID(N''dbo.order_histories'') AND name=N''CK_order_histories_event'')
    ALTER TABLE dbo.order_histories DROP CONSTRAINT CK_order_histories_event;
ALTER TABLE dbo.order_histories WITH CHECK ADD CONSTRAINT CK_order_histories_event CHECK (
    (event_type IN (''ORDER_CREATED'', ''ORDER_SAVED'') AND dispatch_assignment_id IS NULL AND dispatch_version IS NULL)
    OR (event_type IN (''DISPATCH_CREATED'', ''DISPATCH_MOVED'', ''DISPATCH_REMOVED'', ''DISPATCH_RESET'', ''DISPATCH_REORDERED'')
        AND dispatch_assignment_id IS NOT NULL AND dispatch_version IS NOT NULL)
);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N''dbo.order_histories'') AND name=N''UX_order_histories_save'')
    CREATE UNIQUE INDEX UX_order_histories_save ON dbo.order_histories(order_id, order_version) WHERE dispatch_assignment_id IS NULL;
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N''dbo.order_histories'') AND name=N''UX_order_histories_dispatch'')
    CREATE UNIQUE INDEX UX_order_histories_dispatch ON dbo.order_histories(dispatch_assignment_id, dispatch_version) WHERE dispatch_assignment_id IS NOT NULL;
';
COMMIT TRANSACTION;
