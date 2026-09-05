/* 勤怠修正履歴。timeworks の打刻原本は更新しない。 */
IF OBJECT_ID(N'dbo.timework_edits', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.timework_edits (
        timework_edit_id BIGINT IDENTITY(1, 1) NOT NULL,
        timework_id BIGINT NOT NULL,
        edit_start_time DATETIME2 NULL,
        edit_end_time DATETIME2 NULL,
        regist_date DATETIME2 NOT NULL CONSTRAINT DF_neo_timework_edit_regist_date DEFAULT (SYSDATETIME()),
        update_date DATETIME2 NOT NULL CONSTRAINT DF_neo_timework_edit_update_date DEFAULT (SYSDATETIME()),
        regist_user NVARCHAR(255) NOT NULL,
        update_user NVARCHAR(255) NOT NULL,
        version INT NOT NULL CONSTRAINT DF_neo_timework_edit_version DEFAULT (1),
        state INT NOT NULL CONSTRAINT DF_neo_timework_edit_state DEFAULT (0),
        CONSTRAINT PK_neo_timework_edits PRIMARY KEY (timework_edit_id),
        CONSTRAINT FK_neo_timework_edit_timework FOREIGN KEY (timework_id)
            REFERENCES dbo.timeworks(timework_id),
        CONSTRAINT CK_neo_timework_edit_has_value CHECK (edit_start_time IS NOT NULL OR edit_end_time IS NOT NULL)
    );

    CREATE UNIQUE INDEX UX_neo_timework_edit_active
        ON dbo.timework_edits(timework_id)
        WHERE state = 0;

    CREATE INDEX IX_neo_timework_edit_history
        ON dbo.timework_edits(timework_id, timework_edit_id DESC);
END;
