IF OBJECT_ID(N'dbo.ai_document_reviews', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ai_document_reviews (
        document_ai_review_id BIGINT IDENTITY(1, 1) NOT NULL PRIMARY KEY,
        document_type NVARCHAR(50) NOT NULL,
        source_type NVARCHAR(50) NOT NULL,
        source_id BIGINT NOT NULL,
        prime_constractor_id BIGINT NULL,
        ai_engine NVARCHAR(100) NOT NULL,
        ai_model NVARCHAR(100) NULL,
        prompt_version NVARCHAR(100) NULL,
        ai_result NVARCHAR(MAX) NOT NULL,
        confirmed_result NVARCHAR(MAX) NULL,
        review_status NVARCHAR(30) NOT NULL CONSTRAINT DF_ai_document_reviews_review_status DEFAULT 'PENDING_REVIEW',
        reviewed_by NVARCHAR(100) NULL,
        reviewed_date DATETIME2 NULL,
        regist_date DATETIME2 NOT NULL,
        update_date DATETIME2 NOT NULL,
        version INT NOT NULL,
        state INT NOT NULL
    );

    CREATE INDEX IX_ai_document_reviews_source
        ON dbo.ai_document_reviews (source_type, source_id, review_status, document_ai_review_id DESC);
    CREATE INDEX IX_ai_document_reviews_training
        ON dbo.ai_document_reviews (document_type, review_status, state, document_ai_review_id);
END;
