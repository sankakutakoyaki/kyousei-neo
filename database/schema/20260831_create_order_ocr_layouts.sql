IF OBJECT_ID(N'dbo.order_ocr_layouts', N'U') IS NULL
BEGIN
    CREATE TABLE order_ocr_layouts (
        order_ocr_layout_id INT IDENTITY(1, 1) NOT NULL,
        prime_constractor_id INT NOT NULL,
        field_key NVARCHAR(50) NOT NULL,
        x INT NOT NULL,
        y INT NOT NULL,
        width INT NOT NULL,
        height INT NOT NULL,
        update_date DATETIME2 NOT NULL,
        CONSTRAINT PK_order_ocr_layouts PRIMARY KEY (order_ocr_layout_id),
        CONSTRAINT UQ_order_ocr_layouts UNIQUE (prime_constractor_id, field_key)
    );
END;
