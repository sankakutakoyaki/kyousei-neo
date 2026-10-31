-- アプリ更新前に適用する。過去ログからの自動移行は行わない。
IF OBJECT_ID(N'dbo.order_histories', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.order_histories (
        order_history_id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        order_id INT NOT NULL,
        order_version INT NOT NULL,
        event_type VARCHAR(32) NOT NULL,
        editor NVARCHAR(255) NOT NULL,
        occurred_at DATETIME2 NOT NULL CONSTRAINT DF_order_histories_occurred_at DEFAULT SYSDATETIME(),
        CONSTRAINT FK_order_histories_orders FOREIGN KEY (order_id) REFERENCES dbo.orders(order_id),
        CONSTRAINT UQ_order_histories_save UNIQUE (order_id, order_version),
        CONSTRAINT CK_order_histories_event CHECK (event_type IN ('ORDER_CREATED', 'ORDER_SAVED'))
    );
    CREATE INDEX IX_order_histories_order ON dbo.order_histories(order_id, order_history_id DESC);
END;
