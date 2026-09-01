IF OBJECT_ID(N'dbo.attachment_groups', N'U') IS NULL
BEGIN
    CREATE TABLE attachment_groups (
        attachment_group_id BIGINT IDENTITY(1, 1) NOT NULL,
        parent_type NVARCHAR(40) NOT NULL,
        parent_id BIGINT NOT NULL,
        group_name NVARCHAR(100) NOT NULL,
        display_order INT NOT NULL DEFAULT 0,
        regist_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        update_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version INT NOT NULL DEFAULT 1,
        state INT NOT NULL DEFAULT 0,
        CONSTRAINT PK_attachment_groups PRIMARY KEY (attachment_group_id)
    );
    CREATE INDEX IX_attachment_groups_parent
        ON attachment_groups (parent_type, parent_id, state, display_order);
END;

IF OBJECT_ID(N'dbo.attachments', N'U') IS NULL
BEGIN
    CREATE TABLE attachments (
        attachment_id BIGINT IDENTITY(1, 1) NOT NULL,
        attachment_group_id BIGINT NOT NULL,
        stored_name NVARCHAR(255) NOT NULL,
        original_name NVARCHAR(255) NOT NULL,
        display_name NVARCHAR(255) NOT NULL,
        file_type NVARCHAR(20) NOT NULL,
        mime_type NVARCHAR(100) NOT NULL,
        file_size BIGINT NOT NULL,
        width INT NULL,
        height INT NULL,
        display_order INT NOT NULL DEFAULT 0,
        regist_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        update_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version INT NOT NULL DEFAULT 1,
        state INT NOT NULL DEFAULT 0,
        CONSTRAINT PK_attachments PRIMARY KEY (attachment_id),
        CONSTRAINT FK_attachments_group FOREIGN KEY (attachment_group_id)
            REFERENCES attachment_groups (attachment_group_id)
    );
    CREATE INDEX IX_attachments_group
        ON attachments (attachment_group_id, state, display_order);
END;
