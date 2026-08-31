CREATE TABLE order_imports (
    order_import_id INT IDENTITY(1, 1) NOT NULL,
    order_id INT NULL,
    prime_constractor_id INT NOT NULL,
    original_file_name NVARCHAR(255) NOT NULL,
    stored_file_name NVARCHAR(255) NOT NULL,
    file_path NVARCHAR(500) NOT NULL,
    mime_type NVARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    ocr_status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
    ocr_result NVARCHAR(MAX) NULL,
    ocr_error NVARCHAR(1000) NULL,
    ocr_finished_date DATETIME2 NULL,
    regist_date DATETIME2 NOT NULL,
    update_date DATETIME2 NOT NULL,
    version INT NOT NULL,
    state INT NOT NULL,
    CONSTRAINT PK_order_imports PRIMARY KEY (order_import_id)
);

CREATE INDEX IX_order_imports_prime_constractor_id_regist_date
    ON order_imports (prime_constractor_id, regist_date DESC);
