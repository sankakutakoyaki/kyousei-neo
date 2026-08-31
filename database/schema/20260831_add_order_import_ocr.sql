ALTER TABLE order_imports ADD
    ocr_status NVARCHAR(20) NOT NULL CONSTRAINT DF_order_imports_ocr_status DEFAULT 'PENDING',
    ocr_result NVARCHAR(MAX) NULL,
    ocr_error NVARCHAR(1000) NULL,
    ocr_finished_date DATETIME2 NULL;
